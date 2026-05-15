package com.matrusneh.health.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matrusneh.health.data.db.entity.UserProfileEntity
import com.matrusneh.health.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    /** The raw profile row; null while Room is loading OR genuinely not set. */
    val profile: StateFlow<UserProfileEntity?> =
        userProfileRepository.getProfile()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null
            )

    /**
     * Three-state flag used by NavGraph to decide start destination:
     *   null  → Room query still in-flight (show onboarding as safe default)
     *   false → Profile row does not exist (show onboarding)
     *   true  → Profile row exists (go straight to Home)
     *
     * We use a separate flow with a sentinel initial value so NavGraph can
     * distinguish "loading" from "genuinely absent".
     */
    val profileExists: StateFlow<Boolean?> =
        userProfileRepository.getProfile()
            .map { entity -> entity != null }        // Boolean once DB responds
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null                  // null = still loading
            )

    fun saveProfile(gestationalWeek: Int, name: String = "") {
        viewModelScope.launch {
            userProfileRepository.saveProfile(gestationalWeek, name)
        }
    }

    fun updateGestationalWeek(week: Int) {
        val currentName = profile.value?.name ?: ""
        saveProfile(week, currentName)
    }
}
