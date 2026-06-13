package com.aistudio.nexreceipt.pro

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PostAdd
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.aistudio.nexreceipt.pro.data.model.Receipt
import com.aistudio.nexreceipt.pro.ui.screens.DashboardScreen
import com.aistudio.nexreceipt.pro.ui.screens.HistoryScreen
import com.aistudio.nexreceipt.pro.ui.screens.PdfExportScreen
import com.aistudio.nexreceipt.pro.ui.screens.ReceiptEditorScreen
import com.aistudio.nexreceipt.pro.ui.screens.SettingsScreen
import com.aistudio.nexreceipt.pro.ui.theme.NexReceiptProTheme
import com.aistudio.nexreceipt.pro.ui.viewmodel.ReceiptViewModel
import kotlinx.serialization.Serializable

// Type Safe Navigation Destinations
@Serializable
object DashboardDest

@Serializable
object CreateDest

@Serializable
object HistoryDest

@Serializable
object SettingsDest

@Serializable
data class ExportDest(val id: Long)

class MainActivity : ComponentActivity() {

    private val viewModel: ReceiptViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }

            NexReceiptProTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = backStackEntry?.destination

                val receiptsList by viewModel.allReceipts.collectAsStateWithLifecycle()

                // NavItems for Bottom Navigation
                val navItems = listOf(
                    NavItem("Home", DashboardDest, Icons.Filled.Home, Icons.Outlined.Home, "nav_home_tab"),
                    NavItem("Create", CreateDest, Icons.Filled.PostAdd, Icons.Outlined.PostAdd, "nav_create_tab"),
                    NavItem("History", HistoryDest, Icons.Filled.History, Icons.Outlined.History, "nav_history_tab"),
                    NavItem("Settings", SettingsDest, Icons.Filled.Settings, Icons.Outlined.Settings, "nav_settings_tab")
                )

                // Hide bottom bar when previewing/exporting PDF files
                val shouldShowBottomBar = currentDestination?.hierarchy?.any { dest ->
                    dest.hasRoute(DashboardDest::class) ||
                    dest.hasRoute(CreateDest::class) ||
                    dest.hasRoute(HistoryDest::class) ||
                    dest.hasRoute(SettingsDest::class)
                } == true

                Scaffold(
                    modifier = Modifier.fillMaxSize().testTag("main_scaffold"),
                    bottomBar = {
                        if (shouldShowBottomBar) {
                            NavigationBar(
                                modifier = Modifier.testTag("app_navigation_bar")
                            ) {
                                navItems.forEach { item ->
                                    val isSelected = currentDestination?.hierarchy?.any {
                                        it.hasRoute(item.route::class)
                                    } == true

                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = {
                                            navController.navigate(item.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                                contentDescription = item.label
                                            )
                                        },
                                        label = { Text(item.label) },
                                        modifier = Modifier.testTag(item.testTag)
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = DashboardDest,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable<DashboardDest> {
                            DashboardScreen(
                                viewModel = viewModel,
                                onNavigateToCreate = {
                                    navController.navigate(CreateDest) {
                                        launchSingleTop = true
                                    }
                                },
                                onNavigateToHistory = {
                                    navController.navigate(HistoryDest) {
                                        launchSingleTop = true
                                    }
                                },
                                onPreviewReceipt = { r ->
                                    navController.navigate(ExportDest(id = r.id))
                                }
                            )
                        }

                        composable<CreateDest> {
                            ReceiptEditorScreen(
                                viewModel = viewModel,
                                onSaveSuccess = { savedReceipt ->
                                    navController.navigate(ExportDest(id = savedReceipt.id)) {
                                        popUpTo(CreateDest) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable<HistoryDest> {
                            HistoryScreen(
                                viewModel = viewModel,
                                onNavigateToEdit = { r ->
                                    viewModel.loadReceipt(r)
                                    navController.navigate(CreateDest) {
                                        launchSingleTop = true
                                    }
                                },
                                onPreviewReceipt = { r ->
                                    navController.navigate(ExportDest(id = r.id))
                                }
                            )
                        }

                        composable<SettingsDest> {
                            SettingsScreen(
                                viewModel = viewModel,
                                isDarkTheme = isDarkTheme,
                                onThemeToggle = { isDarkTheme = it }
                            )
                        }

                        composable<ExportDest> { backStackEntry ->
                            val route = backStackEntry.toRoute<ExportDest>()
                            val matchedReceipt = receiptsList.find { it.id == route.id }

                            if (matchedReceipt != null) {
                                PdfExportScreen(
                                    receipt = matchedReceipt,
                                    onBack = {
                                        viewModel.clearEditor()
                                        navController.navigateUp()
                                    }
                                )
                            } else {
                                LaunchedEffect(Unit) {
                                    Toast.makeText(this@MainActivity, "Document not found!", Toast.LENGTH_SHORT).show()
                                    navController.navigateUp()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class NavItem(
    val label: String,
    val route: Any,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)
