package com.matrusneh.health.data.repository

import com.matrusneh.health.data.db.dao.DailyKickCount
import com.matrusneh.health.data.db.dao.KickLogDao
import com.matrusneh.health.data.db.entity.KickLogEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class KickRepository @Inject constructor(
    private val kickLogDao: KickLogDao
) {
    private val dateFmt = DateTimeFormatter.ISO_LOCAL_DATE

    suspend fun recordKick() {
        val now = System.currentTimeMillis()
        val today = LocalDate.now().format(dateFmt)
        kickLogDao.insert(KickLogEntity(timestampMs = now, date = today))
    }

    /** Last 7 days kick counts (date → count) */
    fun getWeeklyKicks(): Flow<List<DailyKickCount>> {
        val sevenDaysAgo = LocalDate.now().minusDays(6).format(dateFmt)
        return kickLogDao.getWeeklyKicks(sevenDaysAgo)
    }

    fun getTodayKickCount(): Flow<Int> {
        val today = LocalDate.now().format(dateFmt)
        return kickLogDao.getTodayKickCount(today)
    }
}
