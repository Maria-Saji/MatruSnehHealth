package com.matrusneh.health.data.repository

import android.content.Context
import androidx.work.*
import com.matrusneh.health.data.db.dao.AppointmentDao
import com.matrusneh.health.data.db.entity.AppointmentEntity
import com.matrusneh.health.worker.AppointmentReminderWorker
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AppointmentRepository @Inject constructor(
    private val appointmentDao: AppointmentDao,
    private val context: Context
) {
    companion object {
        const val TYPE_SCAN = "SCAN"
        const val TYPE_VACCINATION = "VACCINATION"
    }

    fun getScanAppointment(): Flow<AppointmentEntity?> =
        appointmentDao.getByType(TYPE_SCAN)

    fun getVaccinationAppointment(): Flow<AppointmentEntity?> =
        appointmentDao.getByType(TYPE_VACCINATION)

    suspend fun saveAppointment(type: String, date: LocalDate) {
        val workTag = "reminder_${type.lowercase()}"
        val entity = AppointmentEntity(
            type = type,
            dateEpochDay = date.toEpochDay(),
            workTag = workTag
        )
        appointmentDao.upsert(entity)
        scheduleReminder(type, date, workTag)
    }

    /** Schedules a WorkManager one-time notification 24h and 1h before appointment */
    private fun scheduleReminder(type: String, date: LocalDate, workTag: String) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelAllWorkByTag(workTag)

        val appointmentMillis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val now = System.currentTimeMillis()

        // 24-hour reminder
        val delay24h = appointmentMillis - now - TimeUnit.HOURS.toMillis(24)
        if (delay24h > 0) {
            val request24h = OneTimeWorkRequestBuilder<AppointmentReminderWorker>()
                .setInitialDelay(delay24h, TimeUnit.MILLISECONDS)
                .setInputData(
                    workDataOf(
                        AppointmentReminderWorker.KEY_TYPE to type,
                        AppointmentReminderWorker.KEY_HOURS_BEFORE to 24
                    )
                )
                .addTag(workTag)
                .build()
            workManager.enqueue(request24h)
        }

        // 1-hour reminder
        val delay1h = appointmentMillis - now - TimeUnit.HOURS.toMillis(1)
        if (delay1h > 0) {
            val request1h = OneTimeWorkRequestBuilder<AppointmentReminderWorker>()
                .setInitialDelay(delay1h, TimeUnit.MILLISECONDS)
                .setInputData(
                    workDataOf(
                        AppointmentReminderWorker.KEY_TYPE to type,
                        AppointmentReminderWorker.KEY_HOURS_BEFORE to 1
                    )
                )
                .addTag("${workTag}_1h")
                .build()
            workManager.enqueue(request1h)
        }
    }

    /** Calculate days remaining from today */
    fun daysRemaining(epochDay: Long): Long {
        val today = LocalDate.now().toEpochDay()
        return epochDay - today
    }
}
