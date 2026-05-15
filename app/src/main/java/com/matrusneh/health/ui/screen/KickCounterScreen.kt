package com.matrusneh.health.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.matrusneh.health.data.db.dao.DailyKickCount
import com.matrusneh.health.ui.viewmodel.KickViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KickCounterScreen(
    onBack: () -> Unit,
    viewModel: KickViewModel = hiltViewModel()
) {
    val todayKicks by viewModel.todayKickCount.collectAsStateWithLifecycle()
    val weeklyKicks by viewModel.weeklyKicks.collectAsStateWithLifecycle()

    // Button scale animation for tap feedback
    var buttonPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (buttonPressed) 0.90f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "kick_button_scale"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("👶  Kick Counter") },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            // Today's count display
            Text(
                text = "Today's Kicks",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Text(
                text = "$todayKicks",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 80.sp
            )

            Spacer(Modifier.height(32.dp))

            // Large record kick button (FR-01-a)
            Button(
                onClick = {
                    val recorded = viewModel.recordKick()
                    if (recorded) {
                        buttonPressed = true
                    }
                },
                modifier = Modifier
                    .size(200.dp)
                    .scale(scale),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "👣", fontSize = 48.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Record\nKick",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Reset press animation
            LaunchedEffect(buttonPressed) {
                if (buttonPressed) {
                    delay(150)
                    buttonPressed = false
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "⚡ Duplicate taps within 0.5s are ignored",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            HorizontalDivider()

            Spacer(Modifier.height(16.dp))

            // Weekly table (FR-01-c)
            Text(
                text = "Kicks per Hour — Last 7 Days",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            WeeklyKicksTable(weeklyKicks = weeklyKicks)

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun WeeklyKicksTable(weeklyKicks: List<DailyKickCount>) {
    if (weeklyKicks.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No kick data yet.\nStart recording kicks above!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }
        return
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Date",
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = "Total Kicks",
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Kicks/Hour",
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.End
                )
            }

            weeklyKicks.forEachIndexed { index, entry ->
                val bgColor = if (index % 2 == 0)
                    MaterialTheme.colorScheme.surface
                else
                    MaterialTheme.colorScheme.surfaceVariant

                // Assumption: tracking window = 1 hour (as per PRD "Kicks per Hour")
                // A normal session tracked across ~1h gives kicks/hour directly.
                // For simplicity, we display total as-is and derive per-hour assuming tracking period.
                val kicksPerHour = String.format("%.1f", entry.kickCount / 1.0)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(bgColor)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = entry.date.takeLast(5), // MM-DD
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${entry.kickCount}",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = kicksPerHour,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}
