package com.dynogamer.studio.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.dynogamer.studio.ui.screens.backup.BackupScreen
import com.dynogamer.studio.ui.screens.cpm1.Cpm1Screen
import com.dynogamer.studio.ui.screens.cpm2.Cpm2Screen
import com.dynogamer.studio.ui.screens.conversion.ConversionScreen
import com.dynogamer.studio.ui.screens.diagnostics.DiagnosticsScreen
import com.dynogamer.studio.ui.screens.export.ExportScreen
import com.dynogamer.studio.ui.screens.home.HomeScreen
import com.dynogamer.studio.ui.screens.logs.LogsScreen
import com.dynogamer.studio.ui.screens.preview.PreviewScreen
import com.dynogamer.studio.ui.screens.projects.ProjectsScreen
import com.dynogamer.studio.ui.screens.settings.SettingsScreen
import com.dynogamer.studio.ui.screens.shizuku.ShizukuScreen
import com.dynogamer.studio.ui.theme.*

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Cpm1 : Screen("cpm1", "CPM1", Icons.Default.DirectionsCar)
    object Cpm2 : Screen("cpm2", "CPM2", Icons.Default.DirectionsCarFilled)
    object Projects : Screen("projects", "Projects", Icons.Default.Folder)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)

    // Sub-screens (no bottom nav)
    object Conversion : Screen("conversion", "Conversion", Icons.Default.Transform)
    object Preview : Screen("preview/{projectId}", "Preview", Icons.Default.Visibility)
    object Backup : Screen("backup", "Backup", Icons.Default.Backup)
    object Export : Screen("export", "Export", Icons.Default.Upload)
    object Diagnostics : Screen("diagnostics", "Diagnostics", Icons.Default.BugReport)
    object Logs : Screen("logs", "Logs", Icons.Default.List)
    object Shizuku : Screen("shizuku", "Shizuku", Icons.Default.Security)
}

val bottomNavItems = listOf(
    Screen.Home, Screen.Cpm1, Screen.Cpm2, Screen.Projects, Screen.Settings
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        containerColor = Background,
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val showBottomBar = bottomNavItems.any { it.route == currentDestination?.route }

            if (showBottomBar) {
                NavigationBar(containerColor = SurfaceSecondary, tonalElevation = 0.dp) {
                    bottomNavItems.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AccentRed,
                                selectedTextColor = AccentRed,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary,
                                indicatorColor = AccentRedGlow
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigate = { route -> navController.navigate(route) }
                )
            }
            composable(Screen.Cpm1.route) {
                Cpm1Screen(onNavigate = { navController.navigate(it) })
            }
            composable(Screen.Cpm2.route) {
                Cpm2Screen(onNavigate = { navController.navigate(it) })
            }
            composable(Screen.Projects.route) {
                ProjectsScreen(onNavigate = { navController.navigate(it) })
            }
            composable(Screen.Settings.route) {
                SettingsScreen(onNavigate = { navController.navigate(it) })
            }
            composable(Screen.Conversion.route) {
                ConversionScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.Preview.route) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
                PreviewScreen(projectId = projectId, onBack = { navController.popBackStack() })
            }
            composable(Screen.Backup.route) {
                BackupScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.Export.route) {
                ExportScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.Diagnostics.route) {
                DiagnosticsScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.Logs.route) {
                LogsScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.Shizuku.route) {
                ShizukuScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
