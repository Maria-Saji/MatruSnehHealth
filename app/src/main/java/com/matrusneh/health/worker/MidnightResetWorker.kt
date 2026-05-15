package com.matrusneh.health.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.matrusneh.health.data.repository.NutritionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

@HiltWorker
class MidnightResetWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val nutritionRepository: NutritionRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        nutritionRepository.resetOldEntries()
        // Re-schedule for next midnight
        scheduleMidnightReset(applicationContext)
        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "midnight_nutrition_reset"

        fun scheduleMidnightReset(context: Context) {
            val now = LocalDateTime.now()
            val nextMidnight = LocalDate.now().plusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
            val delayMs = nextMidnight - System.currentTimeMillis()

            val request = OneTimeWorkRequestBuilder<MidnightResetWorker>()
                .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                .addTag(WORK_NAME)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    WORK_NAME,
                    ExistingWorkPolicy.REPLACE,
                    request
                )
        }
    }
}
