package com.matrusneh.health.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matrusneh.health.data.db.dao.DailyKickCount
import com.matrusneh.health.data.repository.KickRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KickViewModel @Inject constructor(
    private val kickRepository: KickRepository
) : ViewModel() {

    // Debounce: store last tap timestamp
    private var lastTapMs: Long = 0L
    private val DEBOUNCE_MS = 500L

    val todayKickCount: StateFlow<Int> = kickRepository.getTodayKickCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val weeklyKicks: StateFlow<List<DailyKickCount>> = kickRepository.getWeeklyKicks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /**
     * Records a kick only if >= 500ms have passed since the last tap.
     * Returns true if the kick was recorded, false if debounced.
     */
    fun recordKick(): Boolean {
        val now = System.currentTimeMillis()
        if (now - lastTapMs < DEBOUNCE_MS) return false
        lastTapMs = now
        viewModelScope.launch {
            kickRepository.recordKick()
        }
        return true
    }
}
