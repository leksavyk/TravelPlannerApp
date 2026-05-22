package com.example.travelplanner.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppBottomNavigation(navController: NavHostController) {

    val items = listOf(
        Screen.Trips,
        Screen.AddTrip,
        Screen.Calendar,
        Screen.Profile
    )

    val currentRoute =
        navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        items.forEach { item ->

            NavigationBarItem(
                icon = {
                    item.icon?.let {
                        Icon(it, contentDescription = item.title)
                    }
                },
                label = {
                    item.title?.let {
                        Text(it)
                    }
                },

                selected =
                    currentRoute == item.route ||
                            (currentRoute?.startsWith(Screen.TripDetail.route) == true
                                    && item == Screen.Trips),

                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
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