package com.matrusneh.health.data.repository

import com.matrusneh.health.data.db.dao.FoodItemDao
import com.matrusneh.health.data.db.dao.NutritionLogDao
import com.matrusneh.health.data.db.entity.FoodItemEntity
import com.matrusneh.health.data.db.entity.NutritionLogEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class NutritionRepository @Inject constructor(
    private val foodItemDao: FoodItemDao,
    private val nutritionLogDao: NutritionLogDao
) {
    private val dateFmt = DateTimeFormatter.ISO_LOCAL_DATE

    fun getAllFoodItems(): Flow<List<FoodItemEntity>> = foodItemDao.getAll()

    fun getTodayNutritionLog(): Flow<List<NutritionLogEntity>> {
        val today = LocalDate.now().format(dateFmt)
        return nutritionLogDao.getForDate(today)
    }

    fun getTodayCheckedCount(): Flow<Int> {
        val today = LocalDate.now().format(dateFmt)
        return nutritionLogDao.getCheckedCountForDate(today)
    }

    suspend fun toggleFoodItem(foodId: Long, isChecked: Boolean) {
        val today = LocalDate.now().format(dateFmt)
        nutritionLogDao.upsert(
            NutritionLogEntity(
                foodId = foodId,
                date = today,
                isChecked = isChecked
            )
        )
    }

    /** Called by MidnightResetWorker — clears old checklist entries */
    suspend fun resetOldEntries() {
        val today = LocalDate.now().format(dateFmt)
        nutritionLogDao.deleteOlderThan(today)
    }
}
