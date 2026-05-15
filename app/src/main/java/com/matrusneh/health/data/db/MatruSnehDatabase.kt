package com.matrusneh.health.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.matrusneh.health.data.db.dao.*
import com.matrusneh.health.data.db.entity.*

@Database(
    entities = [
        KickLogEntity::class,
        AppointmentEntity::class,
        FoodItemEntity::class,
        NutritionLogEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MatruSnehDatabase : RoomDatabase() {

    abstract fun kickLogDao(): KickLogDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun foodItemDao(): FoodItemDao
    abstract fun nutritionLogDao(): NutritionLogDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        const val DATABASE_NAME = "matru_sneh_db"

        /**
         * Seeds the 10 required food items from FR-03-a on first install.
         * Called from the Room Callback in DatabaseModule after the database is created.
         */
        suspend fun seedFoodItems(database: MatruSnehDatabase) {
            val dao = database.foodItemDao()
            if (dao.count() == 0) {
                val items = listOf(
                    FoodItemEntity(name = "Ragi",                  iconEmoji = "🌾"),
                    FoodItemEntity(name = "Greens (Palak/Methi)",  iconEmoji = "🥬"),
                    FoodItemEntity(name = "Pulses (Dal)",           iconEmoji = "🫘"),
                    FoodItemEntity(name = "Milk",                   iconEmoji = "🥛"),
                    FoodItemEntity(name = "Eggs",                   iconEmoji = "🥚"),
                    FoodItemEntity(name = "Banana",                 iconEmoji = "🍌"),
                    FoodItemEntity(name = "Drumstick (Moringa)",    iconEmoji = "🌿"),
                    FoodItemEntity(name = "Jaggery",                iconEmoji = "🟫"),
                    FoodItemEntity(name = "Sesame Seeds",           iconEmoji = "⚪"),
                    FoodItemEntity(name = "Groundnuts",             iconEmoji = "🥜")
                )
                dao.insertAll(items)
            }
        }
    }
}
