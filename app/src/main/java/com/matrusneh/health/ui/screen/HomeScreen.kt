package com.matrusneh.health.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.matrusneh.health.data.repository.AppointmentRepository
import com.matrusneh.health.ui.viewmodel.AppointmentViewModel
import com.matrusneh.health.ui.viewmodel.KickViewModel
import com.matrusneh.health.ui.viewmodel.NutritionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    gestationalWeek: Int,
    onNavigateToKickCounter: () -> Unit,
    onNavigateToNutrition: () -> Unit,
    onNavigateToDangerSign: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToSettings: () -> Unit,
    kickViewModel: KickViewModel = hiltViewModel(),
    appointmentViewModel: AppointmentViewModel = hiltViewModel(),
    nutritionViewModel: NutritionViewModel = hiltViewModel()
) {
    val todayKicks by kickViewModel.todayKickCount.collectAsStateWithLifecycle()
    val scanAppointment by appointmentViewModel.scanAppointment.collectAsStateWithLifecycle()
    val vaccinationAppointment by appointmentViewModel.vaccinationAppointment.collectAsStateWithLifecycle()
    val checkedCount by nutritionViewModel.todayCheckedCount.collectAsStateWithLifecycle()
    val totalFoodItems by nutritionViewModel.allFoodItems.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Matru-Sneh 🤰",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Week $gestationalWeek of pregnancy",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
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
            // ── Appointment countdown row ──────────────────────────────
            Text(
                text = "Upcoming Appointments",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val scanDays = scanAppointment?.let {
                    appointmentViewModel.daysRemaining(it.dateEpochDay)
                }
                val vacDays = vaccinationAppointment?.let {
                    appointmentViewModel.daysRemaining(it.dateEpochDay)
                }

                CountdownCard(
                    modifier = Modifier.weight(1f),
                    emoji = "🔬",
                    label = "Next Scan",
                    daysRemaining = scanDays,
                    onClick = onNavigateToAppointments
                )
                CountdownCard(
                    modifier = Modifier.weight(1f),
                    emoji = "💉",
                    label = "Vaccination",
                    daysRemaining = vacDays,
                    onClick = onNavigateToAppointments
                )
            }

            // ── Feature cards grid ────────────────────────────────────
            Text(
                text = "Today's Tracking",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeatureCard(
                    modifier = Modifier.weight(1f),
                    emoji = "👶",
                    title = "Kick Counter",
                    subtitle = "$todayKicks kicks today",
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    onClick = onNavigateToKickCounter
                )
                FeatureCard(
                    modifier = Modifier.weight(1f),
                    emoji = "🥗",
                    title = "Nutrition",
                    subtitle = "$checkedCount / ${totalFoodItems.size} foods",
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = onNavigateToNutrition
                )
            }

            // ── Danger sign alert button ──────────────────────────────
            DangerSignBanner(onClick = onNavigateToDangerSign)

            Spacer(Modifier.height(8.dp))

            // ── Info card ─────────────────────────────────────────────
            InfoCard(gestationalWeek = gestationalWeek)
        }
    }
}

@Composable
private fun CountdownCard(
    modifier: Modifier = Modifier,
    emoji: String,
    label: String,
    daysRemaining: Long?,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .height(110.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 26.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            if (daysRemaining != null) {
                val display = when {
                    daysRemaining < 0 -> "Passed"
                    daysRemaining == 0L -> "Today! 🎉"
                    else -> "${daysRemaining}d left"
                }
                Text(
                    text = display,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (daysRemaining <= 1 && daysRemaining >= 0)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = "Tap to set",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun FeatureCard(
    modifier: Modifier = Modifier,
    emoji: String,
    title: String,
    subtitle: String,
    containerColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .height(130.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = emoji, fontSize = 36.sp)
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun DangerSignBanner(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "🚨", fontSize = 32.sp)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Danger Signs Check",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB71C1C)
                )
                Text(
                    text = "Tap to report any warning symptoms",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFB71C1C).copy(alpha = 0.8f)
                )
            }
            Text(text = "→", fontSize = 18.sp, color = Color(0xFFB71C1C))
        }
    }
}

@Composable
private fun InfoCard(gestationalWeek: Int) {
    val tip = when (gestationalWeek) {
        in 1..12 -> "First trimester: Your baby's major organs are forming. Folic acid is critical."
        in 13..26 -> "Second trimester: You may feel baby kicks! Stay hydrated and eat iron-rich foods."
        in 27..36 -> "Third trimester: Baby is growing fast. Monitor kick counts daily."
        in 37..42 -> "Full term! Watch for labour signs. Keep your hospital bag ready."
        else -> "Track your baby's health every day."
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Text(text = "💡", fontSize = 22.sp)
            Spacer(Modifier.width(10.dp))
            Text(
                text = tip,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
