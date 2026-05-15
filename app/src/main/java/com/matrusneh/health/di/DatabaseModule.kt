package com.matrusneh.health.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.matrusneh.health.data.db.MatruSnehDatabase
import com.matrusneh.health.data.db.dao.*
import com.matrusneh.health.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MatruSnehDatabase {
        lateinit var db: MatruSnehDatabase
        db = Room.databaseBuilder(
            context,
            MatruSnehDatabase::class.java,
            MatruSnehDatabase.DATABASE_NAME
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(sqliteDb: SupportSQLiteDatabase) {
                    super.onCreate(sqliteDb)
                    // Seed food items on first database creation
                    CoroutineScope(Dispatchers.IO).launch {
                        MatruSnehDatabase.seedFoodItems(db)
                    }
                }
            })
            .build()
        return db
    }

    @Provides fun provideKickLogDao(db: MatruSnehDatabase): KickLogDao = db.kickLogDao()
    @Provides fun provideAppointmentDao(db: MatruSnehDatabase): AppointmentDao = db.appointmentDao()
    @Provides fun provideFoodItemDao(db: MatruSnehDatabase): FoodItemDao = db.foodItemDao()
    @Provides fun provideNutritionLogDao(db: MatruSnehDatabase): NutritionLogDao = db.nutritionLogDao()
    @Provides fun provideUserProfileDao(db: MatruSnehDatabase): UserProfileDao = db.userProfileDao()
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideKickRepository(dao: KickLogDao): KickRepository = KickRepository(dao)

    @Provides
    @Singleton
    fun provideAppointmentRepository(
        dao: AppointmentDao,
        @ApplicationContext context: Context
    ): AppointmentRepository = AppointmentRepository(dao, context)

    @Provides
    @Singleton
    fun provideNutritionRepository(
        foodItemDao: FoodItemDao,
        nutritionLogDao: NutritionLogDao
    ): NutritionRepository = NutritionRepository(foodItemDao, nutritionLogDao)

    @Provides
    @Singleton
    fun provideUserProfileRepository(dao: UserProfileDao): UserProfileRepository =
        UserProfileRepository(dao)
}
