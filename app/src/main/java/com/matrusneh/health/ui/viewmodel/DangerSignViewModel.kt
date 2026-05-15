package com.matrusneh.health.ui.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/** Danger signs as defined in FR-04-a */
enum class DangerSign(val label: String, val emoji: String) {
    SWELLING("Severe swelling of face/hands/feet", "🦵"),
    HEADACHE("Severe headache", "🤕"),
    BLURRED_VISION("Blurred vision", "👁️"),
    REDUCED_MOVEMENT("Reduced/absent fetal movement", "👶"),
    BLEEDING("Vaginal bleeding", "🩸"),
    HIGH_FEVER("High fever", "🌡️")
}

@HiltViewModel
class DangerSignViewModel @Inject constructor() : ViewModel() {

    // Map of DangerSign → toggled state
    private val _signStates = mutableStateMapOf<DangerSign, Boolean>().apply {
        DangerSign.entries.forEach { put(it, false) }
    }
    val signStates: Map<DangerSign, Boolean> get() = _signStates

    /** Returns true when ANY danger sign is active → triggers full-screen alert */
    val anySignActive: Boolean get() = _signStates.values.any { it }

    fun toggle(sign: DangerSign, active: Boolean) {
        _signStates[sign] = active
    }

    /** Explicit dismissal (FR-04-c: not back gesture) */
    fun dismissAll() {
        DangerSign.entries.forEach { _signStates[it] = false }
    }
}
