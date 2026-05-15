package com.matrusneh.health.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Warm, maternal color palette
val RoseRed      = Color(0xFFD32F2F)
val SoftPink     = Color(0xFFF8BBD9)
val WarmOrange   = Color(0xFFFF8F00)
val LightOrange  = Color(0xFFFFE0B2)
val TealGreen    = Color(0xFF00796B)
val LightTeal    = Color(0xFFB2DFDB)
val OffWhite     = Color(0xFFFFF8F0)
val DarkBrown    = Color(0xFF4E342E)
val MediumBrown  = Color(0xFF8D6E63)
val AlertRed     = Color(0xFFB71C1C)

private val MatruSnehColorScheme = lightColorScheme(
    primary          = TealGreen,
    onPrimary        = Color.White,
    primaryContainer = LightTeal,
    secondary        = WarmOrange,
    onSecondary      = Color.White,
    secondaryContainer = LightOrange,
    error            = RoseRed,
    background       = OffWhite,
    surface          = Color.White,
    onBackground     = DarkBrown,
    onSurface        = DarkBrown,
)

@Composable
fun MatruSnehTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MatruSnehColorScheme,
        content = content
    )
}
