package com.matrusneh.health.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// --- kick_log table ---
@Entity(tableName = "kick_log")
data class KickLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestampMs: Long,      // epoch millis of the kick tap
    val date: String            // yyyy-MM-dd for easy daily grouping
)

// --- appointment table ---
@Entity(tableName = "appointment")
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,           // "SCAN" or "VACCINATION"
    val dateEpochDay: Long,     // LocalDate.toEpochDay() for easy arithmetic
    val workTag: String         // WorkManager unique work tag
)

// --- food_item table (static seed data) ---
@Entity(tableName = "food_item")
data class FoodItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val iconEmoji: String       // Using emoji as icon — no image assets needed
)

// --- nutrition_log table (daily check state) ---
@Entity(tableName = "nutrition_log")
data class NutritionLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val foodId: Long,
    val date: String,           // yyyy-MM-dd
    val isChecked: Boolean
)

// --- user_profile table (single-row) ---
@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1, // always row 1
    val gestationalWeek: Int,
    val name: String = ""
)
