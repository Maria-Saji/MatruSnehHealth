package com.matrusneh.health.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.matrusneh.health.data.db.entity.FoodItemEntity
import com.matrusneh.health.ui.viewmodel.NutritionViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(
    onBack: () -> Unit,
    viewModel: NutritionViewModel = hiltViewModel()
) {
    val foodItems by viewModel.allFoodItems.collectAsStateWithLifecycle()
    val nutritionLog by viewModel.todayNutritionLog.collectAsStateWithLifecycle()
    val checkedCount by viewModel.todayCheckedCount.collectAsStateWithLifecycle()
    val total = foodItems.size

    // Build a map of foodId → isChecked from today's log
    val checkedMap = remember(nutritionLog) {
        nutritionLog.associate { it.foodId to it.isChecked }
    }

    val todayDisplay = LocalDate.now()
        .format(DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🥗  Nutrition Plate") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Date & progress header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(16.dp)
            ) {
                Text(
                    text = todayDisplay,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Today's Progress",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "$checkedCount / $total",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(Modifier.height(8.dp))
                // Progress bar (FR-03-c)
                LinearProgressIndicator(
                    progress = { if (total > 0) checkedCount.toFloat() / total else 0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f)
                )
                if (checkedCount == total && total > 0) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "🎉 Great job! All foods consumed today!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            if (foodItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Food checklist (FR-03-a, FR-03-b)
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(foodItems, key = { it.id }) { item ->
                        FoodItemRow(
                            item = item,
                            isChecked = checkedMap[item.id] ?: false,
                            onToggle = { checked ->
                                viewModel.toggleFoodItem(item.id, checked)
                            }
                        )
                    }
                    item {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "ℹ️  Checklist resets automatically at midnight.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FoodItemRow(
    item: FoodItemEntity,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isChecked)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(if (isChecked) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = item.iconEmoji, fontSize = 28.sp)
            Spacer(Modifier.width(14.dp))
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isChecked)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface
            )
            Checkbox(
                checked = isChecked,
                onCheckedChange = onToggle,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}
