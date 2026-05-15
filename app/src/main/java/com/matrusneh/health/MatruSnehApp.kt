package com.matrusneh.health

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.matrusneh.health.worker.MidnightResetWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MatruSnehApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        // Schedule midnight nutrition-reset worker on every app launch
        // (REPLACE policy means it won't duplicate if already scheduled)
        MidnightResetWorker.scheduleMidnightReset(this)
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            // Appointment reminder channel
            val appointmentChannel = NotificationChannel(
                CHANNEL_APPOINTMENT,
                "Appointment Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for your upcoming scan and vaccination appointments"
            }

            // Nutrition reset channel
            val nutritionChannel = NotificationChannel(
                CHANNEL_NUTRITION,
                "Nutrition Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily nutrition checklist reminders"
            }

            manager.createNotificationChannels(listOf(appointmentChannel, nutritionChannel))
        }
    }

    companion object {
        const val CHANNEL_APPOINTMENT = "channel_appointment"
        const val CHANNEL_NUTRITION = "channel_nutrition"
    }
}
