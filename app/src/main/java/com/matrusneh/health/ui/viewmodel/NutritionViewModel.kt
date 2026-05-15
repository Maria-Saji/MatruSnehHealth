package com.matrusneh.health.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matrusneh.health.data.db.entity.FoodItemEntity
import com.matrusneh.health.data.db.entity.NutritionLogEntity
import com.matrusneh.health.data.repository.NutritionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val nutritionRepository: NutritionRepository
) : ViewModel() {

    val allFoodItems: StateFlow<List<FoodItemEntity>> =
        nutritionRepository.getAllFoodItems()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val todayNutritionLog: StateFlow<List<NutritionLogEntity>> =
        nutritionRepository.getTodayNutritionLog()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val todayCheckedCount: StateFlow<Int> =
        nutritionRepository.getTodayCheckedCount()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    fun toggleFoodItem(foodId: Long, isChecked: Boolean) {
        viewModelScope.launch {
            nutritionRepository.toggleFoodItem(foodId, isChecked)
        }
    }

    /** Returns true if the given foodId is checked today */
    fun isChecked(foodId: Long): Boolean {
        return todayNutritionLog.value.any { it.foodId == foodId && it.isChecked }
    }
}
