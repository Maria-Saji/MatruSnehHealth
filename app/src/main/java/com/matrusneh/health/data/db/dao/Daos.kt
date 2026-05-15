package com.matrusneh.health.data.db.dao

import androidx.room.*
import com.matrusneh.health.data.db.entity.*
import kotlinx.coroutines.flow.Flow

// --------------- KickLog DAO ---------------
@Dao
interface KickLogDao {
    @Insert
    suspend fun insert(kick: KickLogEntity)

    /** Returns kicks grouped by date for the last 7 days */
    @Query("""
        SELECT date, COUNT(*) as kickCount
        FROM kick_log
        WHERE date >= :fromDate
        GROUP BY date
        ORDER BY date ASC
    """)
    fun getWeeklyKicks(fromDate: String): Flow<List<DailyKickCount>>

    @Query("SELECT COUNT(*) FROM kick_log WHERE date = :date")
    fun getTodayKickCount(date: String): Flow<Int>
}

/** Lightweight projection for weekly summary */
data class DailyKickCount(val date: String, val kickCount: Int)

// --------------- Appointment DAO ---------------
@Dao
interface AppointmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(appointment: AppointmentEntity)

    @Query("SELECT * FROM appointment WHERE type = :type LIMIT 1")
    fun getByType(type: String): Flow<AppointmentEntity?>

    @Query("SELECT * FROM appointment")
    fun getAll(): Flow<List<AppointmentEntity>>

    @Query("DELETE FROM appointment WHERE type = :type")
    suspend fun deleteByType(type: String)
}

// --------------- FoodItem DAO ---------------
@Dao
interface FoodItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<FoodItemEntity>)

    @Query("SELECT * FROM food_item ORDER BY id ASC")
    fun getAll(): Flow<List<FoodItemEntity>>

    @Query("SELECT COUNT(*) FROM food_item")
    suspend fun count(): Int
}

// --------------- NutritionLog DAO ---------------
@Dao
interface NutritionLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(log: NutritionLogEntity)

    @Query("SELECT * FROM nutrition_log WHERE date = :date")
    fun getForDate(date: String): Flow<List<NutritionLogEntity>>

    /** Midnight reset: delete all logs for previous dates */
    @Query("DELETE FROM nutrition_log WHERE date < :today")
    suspend fun deleteOlderThan(today: String)

    @Query("SELECT COUNT(*) FROM nutrition_log WHERE date = :date AND isChecked = 1")
    fun getCheckedCountForDate(date: String): Flow<Int>
}

// --------------- UserProfile DAO ---------------
@Dao
interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: UserProfileEntity)

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getProfile(): Flow<UserProfileEntity?>

    @Query("SELECT COUNT(*) FROM user_profile WHERE id = 1")
    suspend fun exists(): Int
}
