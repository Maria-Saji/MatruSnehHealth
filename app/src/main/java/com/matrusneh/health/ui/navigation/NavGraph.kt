package com.matrusneh.health.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.matrusneh.health.ui.screen.AppointmentsScreen
import com.matrusneh.health.ui.screen.DangerSignScreen
import com.matrusneh.health.ui.screen.HomeScreen
import com.matrusneh.health.ui.screen.KickCounterScreen
import com.matrusneh.health.ui.screen.NutritionScreen
import com.matrusneh.health.ui.screen.OnboardingScreen
import com.matrusneh.health.ui.screen.SettingsScreen
import com.matrusneh.health.ui.viewmodel.UserProfileViewModel

sealed class Screen(val route: String) {
    object Onboarding   : Screen("onboarding")
    object Home         : Screen("home")
    object KickCounter  : Screen("kick_counter")
    object Nutrition    : Screen("nutrition")
    object DangerSign   : Screen("danger_sign")
    object Appointments : Screen("appointments")
    object Settings     : Screen("settings")
}

@Composable
fun MatruSnehNavGraph(
    navController: NavHostController = rememberNavController(),
    profileViewModel: UserProfileViewModel = hiltViewModel()
) {
    val profile       by profileViewModel.profile.collectAsStateWithLifecycle()
    val profileExists by profileViewModel.profileExists.collectAsStateWithLifecycle()

    // null  → DB still loading  → land on Onboarding (safe; it will be very brief)
    // false → no profile yet    → land on Onboarding
    // true  → profile present   → land on Home
    val startDestination = if (profileExists == true)
        Screen.Home.route
    else
        Screen.Onboarding.route

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onProfileSaved = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                gestationalWeek          = profile?.gestationalWeek ?: 1,
                onNavigateToKickCounter  = { navController.navigate(Screen.KickCounter.route) },
                onNavigateToNutrition    = { navController.navigate(Screen.Nutrition.route) },
                onNavigateToDangerSign   = { navController.navigate(Screen.DangerSign.route) },
                onNavigateToAppointments = { navController.navigate(Screen.Appointments.route) },
                onNavigateToSettings     = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.KickCounter.route) {
            KickCounterScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Nutrition.route) {
            NutritionScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.DangerSign.route) {
            DangerSignScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Appointments.route) {
            AppointmentsScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}