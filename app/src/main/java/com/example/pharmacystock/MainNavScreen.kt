package com.example.pharmacystock

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.compose.NavHost
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.statusBarsPadding

@Composable
fun MainNavScreen(
    navController: NavHostController,
    isDarkMode: Boolean
) {
    val viewModel: PharmacyViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val innerNavController = rememberNavController()

    val screenOrder = mapOf(
        "dashboard" to 0,
        "medicines" to 1,
        "stockInOut" to 2,
        "reports" to 3
    )

    val currentRoute =
        innerNavController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        containerColor = if (isDarkMode)
            MaterialTheme.colorScheme.background
        else
            Color.White,
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .height(60.dp),
                tonalElevation = 0.dp,
                containerColor = if (isDarkMode)
                    MaterialTheme.colorScheme.surface
                else
                    Color.White
            ) {

                NavigationBarItem(
                    selected = currentRoute == "dashboard",
                    onClick = {
                        innerNavController.navigate("dashboard") {
                            popUpTo("dashboard")
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.dashboard),
                            contentDescription = "Dashboard",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text("Dashboard", style = MaterialTheme.typography.labelSmall)
                    },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF1E2A78),
                        selectedTextColor = Color(0xFF1E2A78),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )

                NavigationBarItem(
                    selected = currentRoute == "medicines",
                    onClick = {
                        innerNavController.navigate("medicines") {
                            popUpTo("dashboard")
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.medicine),
                            contentDescription = "Medicines",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text("Medicines", style = MaterialTheme.typography.labelSmall)
                    },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF1E2A78),
                        selectedTextColor = Color(0xFF1E2A78),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )

                NavigationBarItem(
                    selected = currentRoute == "stockInOut",
                    onClick = {
                        innerNavController.navigate("stockInOut") {
                            popUpTo("dashboard")
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.stock),
                            contentDescription = "Stock",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text("Stock", style = MaterialTheme.typography.labelSmall)
                    },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF1E2A78),
                        selectedTextColor = Color(0xFF1E2A78),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )

                NavigationBarItem(
                    selected = currentRoute == "reports",
                    onClick = {
                        innerNavController.navigate("reports") {
                            popUpTo("dashboard")
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.reports),
                            contentDescription = "Reports",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text("Reports", style = MaterialTheme.typography.labelSmall)
                    },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF1E2A78),
                        selectedTextColor = Color(0xFF1E2A78),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    ) { padding ->

        NavHost(
            navController = innerNavController,
            startDestination = "dashboard",
            modifier = Modifier
                .padding(padding)
                .statusBarsPadding(),
            enterTransition = {

                val from = screenOrder[initialState.destination.route] ?: 0
                val to = screenOrder[targetState.destination.route] ?: 0

                if (to > from) {
                    slideInHorizontally { it }
                } else {
                    slideInHorizontally { -it }
                }
            },

            exitTransition = {

                val from = screenOrder[initialState.destination.route] ?: 0
                val to = screenOrder[targetState.destination.route] ?: 0

                if (to > from) {
                    slideOutHorizontally { -it }
                } else {
                    slideOutHorizontally { it }
                }
            },

            popEnterTransition = {

                val from = screenOrder[initialState.destination.route] ?: 0
                val to = screenOrder[targetState.destination.route] ?: 0

                if (to > from) {
                    slideInHorizontally { it }
                } else {
                    slideInHorizontally { -it }
                }
            },

            popExitTransition = {

                val from = screenOrder[initialState.destination.route] ?: 0
                val to = screenOrder[targetState.destination.route] ?: 0

                if (to > from) {
                    slideOutHorizontally { -it }
                } else {
                    slideOutHorizontally { it }
                }
            }
        ){

            composable("dashboard") {
                DashboardScreen(viewModel, innerNavController, navController)
            }
            composable("medicines") { MedicinesScreen(viewModel) }
            composable("stockInOut") { StockInOutScreen(viewModel) }
            composable("reports") { ReportsScreen(viewModel) }
        }
    }
}
@Composable
fun NavItem(
    route: String,
    label: String,
    icon: Int,
    currentRoute: String?,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    val isSelected = currentRoute == route
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxHeight()
            .clickable {
                navController.navigate(route) {
                    popUpTo("dashboard")
                    launchSingleTop = true
                }
            }
    ) {

        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            tint = if (isSelected) activeColor else inactiveColor,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            color = if (isSelected) activeColor else inactiveColor,
            style = MaterialTheme.typography.labelSmall
        )
    }
}