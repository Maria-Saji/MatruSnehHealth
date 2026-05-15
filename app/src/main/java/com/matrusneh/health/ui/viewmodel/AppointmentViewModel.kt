package com.matrusneh.health.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matrusneh.health.data.db.entity.AppointmentEntity
import com.matrusneh.health.data.repository.AppointmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AppointmentViewModel @Inject constructor(
    private val appointmentRepository: AppointmentRepository
) : ViewModel() {

    val scanAppointment: StateFlow<AppointmentEntity?> =
        appointmentRepository.getScanAppointment()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val vaccinationAppointment: StateFlow<AppointmentEntity?> =
        appointmentRepository.getVaccinationAppointment()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun saveScanDate(date: LocalDate) {
        viewModelScope.launch {
            appointmentRepository.saveAppointment(AppointmentRepository.TYPE_SCAN, date)
        }
    }

    fun saveVaccinationDate(date: LocalDate) {
        viewModelScope.launch {
            appointmentRepository.saveAppointment(AppointmentRepository.TYPE_VACCINATION, date)
        }
    }

    fun daysRemaining(epochDay: Long): Long = appointmentRepository.daysRemaining(epochDay)
}
