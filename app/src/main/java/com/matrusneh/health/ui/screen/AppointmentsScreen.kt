package com.matrusneh.health.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.matrusneh.health.data.db.entity.AppointmentEntity
import com.matrusneh.health.data.repository.AppointmentRepository
import com.matrusneh.health.ui.viewmodel.AppointmentViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(
    onBack: () -> Unit,
    viewModel: AppointmentViewModel = hiltViewModel()
) {
    val scanAppointment by viewModel.scanAppointment.collectAsStateWithLifecycle()
    val vaccinationAppointment by viewModel.vaccinationAppointment.collectAsStateWithLifecycle()

    var showScanDatePicker by remember { mutableStateOf(false) }
    var showVacDatePicker by remember { mutableStateOf(false) }

    // Date picker states
    val scanPickerState = rememberDatePickerState(
        initialSelectedDateMillis = scanAppointment?.dateEpochDay?.let {
            LocalDate.ofEpochDay(it)
                .atStartOfDay()
                .toInstant(java.time.ZoneOffset.UTC)
                .toEpochMilli()
        }
    )
    val vacPickerState = rememberDatePickerState(
        initialSelectedDateMillis = vaccinationAppointment?.dateEpochDay?.let {
            LocalDate.ofEpochDay(it)
                .atStartOfDay()
                .toInstant(java.time.ZoneOffset.UTC)
                .toEpochMilli()
        }
    )

    if (showScanDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showScanDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    scanPickerState.selectedDateMillis?.let { millis ->
                        val date = LocalDate.ofEpochDay(
                            java.util.concurrent.TimeUnit.MILLISECONDS.toDays(millis)
                        )
                        viewModel.saveScanDate(date)
                    }
                    showScanDatePicker = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showScanDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = scanPickerState)
        }
    }

    if (showVacDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showVacDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    vacPickerState.selectedDateMillis?.let { millis ->
                        val date = LocalDate.ofEpochDay(
                            java.util.concurrent.TimeUnit.MILLISECONDS.toDays(millis)
                        )
                        viewModel.saveVaccinationDate(date)
                    }
                    showVacDatePicker = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showVacDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = vacPickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📅  Appointments") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Set your upcoming appointment dates.\nYou will receive reminders 24 hours and 1 hour before.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            AppointmentCard(
                emoji = "🔬",
                type = "Scan Appointment",
                appointment = scanAppointment,
                daysRemaining = scanAppointment?.let { viewModel.daysRemaining(it.dateEpochDay) },
                onSetDate = { showScanDatePicker = true }
            )

            AppointmentCard(
                emoji = "💉",
                type = "Vaccination Appointment",
                appointment = vaccinationAppointment,
                daysRemaining = vaccinationAppointment?.let { viewModel.daysRemaining(it.dateEpochDay) },
                onSetDate = { showVacDatePicker = true }
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Text(text = "🔔", fontSize = 22.sp)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Reminders fire even when the app is closed. Make sure notifications are enabled for Matru-Sneh in your phone settings.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AppointmentCard(
    emoji: String,
    type: String,
    appointment: AppointmentEntity?,
    daysRemaining: Long?,
    onSetDate: () -> Unit
) {
    val fmt = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = emoji, fontSize = 32.sp)
                Spacer(Modifier.width(12.dp))
                Text(
                    text = type,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(12.dp))

            if (appointment != null) {
                val date = LocalDate.ofEpochDay(appointment.dateEpochDay)
                val formattedDate = date.format(fmt)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        val daysText = when {
                            daysRemaining == null -> ""
                            daysRemaining < 0 -> "This appointment has passed"
                            daysRemaining == 0L -> "Today! 🎉"
                            else -> "In $daysRemaining days"
                        }
                        Text(
                            text = daysText,
                            style = MaterialTheme.typography.bodySmall,
                            color = if ((daysRemaining ?: 99) <= 1 && (daysRemaining ?: 99) >= 0)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    OutlinedButton(onClick = onSetDate, shape = RoundedCornerShape(10.dp)) {
                        Text("Change")
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Not set yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Button(
                        onClick = onSetDate,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Set Date")
                    }
                }
            }
        }
    }
}
