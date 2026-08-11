package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.localization.AppStrings
import com.example.model.ThemeMode
import com.example.ui.screens.AddEditProductScreen
import com.example.ui.screens.CategoryManagementScreen
import com.example.ui.screens.CollectionScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.FabricCollectionTheme
import com.example.viewmodel.FabricViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: FabricViewModel = viewModel()
            val settings by viewModel.appSettings.collectAsState()

            FabricCollectionTheme(
                accentColorTheme = settings.accentColor,
                themeMode = settings.themeMode,
                useDynamicColor = settings.useDynamicColor
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FabricApp(viewModel = viewModel)
                }
            }
        }
    }
}

sealed class BottomNavItem(val route: String, val stringKey: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "home", Icons.Default.Home)
    object Collection : BottomNavItem("collection", "collection", Icons.Default.Checkroom)
    object Categories : BottomNavItem("categories", "categories", Icons.Default.Category)
    object Settings : BottomNavItem("settings", "settings", Icons.Default.Settings)
}

@Composable
fun FabricApp(viewModel: FabricViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val settings by viewModel.appSettings.collectAsState()
    val currentLanguage = settings.language

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Collection,
        BottomNavItem.Categories,
        BottomNavItem.Settings
    )

    // Hide BottomBar on Add/Edit and Detail screens
    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .height(80.dp)
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        val labelText = AppStrings.get(item.stringKey, currentLanguage)

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = labelText,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = labelText,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("nav_item_${item.route}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onProductClick = { productId ->
                        navController.navigate("product_detail/$productId")
                    },
                    onAddProductClick = {
                        navController.navigate("add_edit_product?productId=0")
                    }
                )
            }

            composable(BottomNavItem.Collection.route) {
                CollectionScreen(
                    viewModel = viewModel,
                    onProductClick = { productId ->
                        navController.navigate("product_detail/$productId")
                    },
                    onAddProductClick = {
                        navController.navigate("add_edit_product?productId=0")
                    }
                )
            }

            composable(BottomNavItem.Categories.route) {
                CategoryManagementScreen(viewModel = viewModel)
            }

            composable(BottomNavItem.Settings.route) {
                SettingsScreen(viewModel = viewModel)
            }

            composable(
                route = "add_edit_product?productId={productId}",
                arguments = listOf(
                    navArgument("productId") {
                        type = NavType.LongType
                        defaultValue = 0L
                    }
                )
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
                AddEditProductScreen(
                    viewModel = viewModel,
                    productId = productId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = "product_detail/{productId}",
                arguments = listOf(
                    navArgument("productId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
                ProductDetailScreen(
                    viewModel = viewModel,
                    productId = productId,
                    onNavigateBack = { navController.popBackStack() },
                    onEditClick = { id ->
                        navController.navigate("add_edit_product?productId=$id")
                    }
                )
            }
        }
    }
}
