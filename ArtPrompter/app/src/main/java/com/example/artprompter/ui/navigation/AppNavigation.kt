package com.dcmoote.inkwell.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dcmoote.inkwell.InkwellApplication
import com.dcmoote.inkwell.ui.AppViewModel
import com.dcmoote.inkwell.ui.history.HistoryScreen
import com.dcmoote.inkwell.ui.home.HomeScreen
import com.dcmoote.inkwell.ui.onboarding.OnboardingScreen
import com.dcmoote.inkwell.ui.paywall.PaywallScreen
import com.dcmoote.inkwell.ui.settings.SettingsScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Today", Icons.Default.Home)
    object History : Screen("history", "History", Icons.Default.List)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

private val bottomNavScreens = listOf(Screen.Home, Screen.History, Screen.Settings)
private const val ROUTE_ONBOARDING = "onboarding"
private const val ROUTE_PAYWALL = "paywall"

@Composable
fun AppNavigation(appViewModel: AppViewModel) {
    val prefs = (LocalContext.current.applicationContext as InkwellApplication)
        .container.userPreferencesManager
    val startDestination = if (prefs.onboardingComplete) Screen.Home.route else ROUTE_ONBOARDING

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = bottomNavScreens.any {
        currentDestination?.hierarchy?.any { dest -> dest.route == it.route } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavScreens.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            selected = currentDestination?.hierarchy
                                ?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(ROUTE_ONBOARDING) {
                OnboardingScreen(
                    onComplete = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(ROUTE_ONBOARDING) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.History.route) { HistoryScreen() }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    appViewModel = appViewModel,
                    onGoProClick = { navController.navigate(ROUTE_PAYWALL) }
                )
            }
            composable(ROUTE_PAYWALL) {
                PaywallScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
