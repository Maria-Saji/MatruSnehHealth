package com.matrusneh.health.ui.screen

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.matrusneh.health.ui.viewmodel.DangerSign
import com.matrusneh.health.ui.viewmodel.DangerSignViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DangerSignScreen(
    onBack: () -> Unit,
    viewModel: DangerSignViewModel = hiltViewModel()
) {
    val signStates = viewModel.signStates
    val anyActive = viewModel.anySignActive

    // Full-screen alert overlay when any sign is active (FR-04-b)
    if (anyActive) {
        DangerAlertOverlay(
            activeSign = signStates.entries.firstOrNull { it.value }?.key,
            onDismiss = { viewModel.dismissAll() }
        )
        return  // Don't render the normal screen behind the alert
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🚨  Danger Signs") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFD32F2F),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "⚠️ Important",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB71C1C)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "If you are experiencing any of the following symptoms, it may indicate a serious complication. Toggle the symptom you are experiencing to get guidance.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFB71C1C).copy(alpha = 0.85f)
                    )
                }
            }

            Text(
                text = "Are you experiencing any of these?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            // Six danger sign toggles (FR-04-a)
            DangerSign.entries.forEach { sign ->
                DangerSignRow(
                    sign = sign,
                    isActive = signStates[sign] ?: false,
                    onToggle = { active -> viewModel.toggle(sign, active) }
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "ℹ️  This app provides general guidance only. Always consult a qualified healthcare professional for medical advice.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DangerSignRow(
    sign: DangerSign,
    isActive: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) Color(0xFFFFCDD2) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(if (isActive) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = sign.emoji, fontSize = 28.sp)
            Spacer(Modifier.width(12.dp))
            Text(
                text = sign.label,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                color = if (isActive) Color(0xFFB71C1C) else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
            )
            Switch(
                checked = isActive,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFFD32F2F)
                )
            )
        }
    }
}

/**
 * Full-screen red alert overlay (FR-04-b, FR-04-c)
 * Cannot be dismissed by back gesture — only by explicit button press.
 */
@Composable
private fun DangerAlertOverlay(
    activeSign: DangerSign?,
    onDismiss: () -> Unit
) {
    // Intercept back gesture (FR-04-c: dismissible only by explicit action)
    BackHandler(enabled = true) {
        // Intentionally do nothing — back gesture blocked per spec
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB71C1C)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(text = "🚨", fontSize = 72.sp)

            Text(
                text = "GO TO HOSPITAL\nIMMEDIATELY",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 42.sp
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD32F2F))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (activeSign != null) {
                        Text(
                            text = "Detected: ${activeSign.emoji} ${activeSign.label}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                    Text(
                        text = "This symptom can be a sign of a serious pregnancy complication. Do not wait. Go to your nearest health centre or hospital right away.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Helpline number
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📞 Emergency Helpline",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB71C1C)
                    )
                    Text(
                        text = "104",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFB71C1C),
                        fontSize = 48.sp
                    )
                    Text(
                        text = "National Health Helpline (Free)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFB71C1C).copy(alpha = 0.7f)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Also: 108 (Ambulance) | 112 (Emergency)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFB71C1C).copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Explicit dismiss button (FR-04-c)
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFFB71C1C)
                )
            ) {
                Text(
                    text = "✓  I understand — I will go to hospital",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
