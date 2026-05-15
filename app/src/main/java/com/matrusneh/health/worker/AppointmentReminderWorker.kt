package com.matrusneh.health.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.matrusneh.health.MainActivity
import com.matrusneh.health.MatruSnehApp
import com.matrusneh.health.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class AppointmentReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val type = inputData.getString(KEY_TYPE) ?: return Result.failure()
        val hoursBefore = inputData.getInt(KEY_HOURS_BEFORE, 24)

        val typeName = if (type == "SCAN") "Scan" else "Vaccination"
        val timeMsg = if (hoursBefore == 24) "tomorrow" else "in 1 hour"

        showNotification(
            title = "Appointment Reminder 🏥",
            body = "Your $typeName appointment is $timeMsg. Please be prepared."
        )
        return Result.success()
    }

    private fun showNotification(title: String, body: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, MatruSnehApp.CHANNEL_APPOINTMENT)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    companion object {
        const val KEY_TYPE = "appointment_type"
        const val KEY_HOURS_BEFORE = "hours_before"
    }
}
