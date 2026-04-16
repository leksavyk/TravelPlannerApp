package com.example.travelplanner.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.travelplanner.ui.screen.AddTripScreen
import com.example.travelplanner.ui.screen.ProfileScreen
import com.example.travelplanner.ui.screen.TripDetailScreen
import com.example.travelplanner.ui.screen.TripsListScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {AppBottomNavigation(navController)}
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Trips.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Trips.route) {
                TripsListScreen(
                    navController = navController
                )
            }

            composable("${Screen.TripDetail.route}/{tripId}") { backStackEntry ->
                val tripId = backStackEntry.arguments?.getString("tripId")

                TripDetailScreen(
                    tripId = tripId,
                    navController = navController
                )
            }

//            composable("${Screen.PlaceDetail.route}/{placeId}") { backStackEntry ->
//                val placeId = backStackEntry.arguments?.getString("placeId")
//                PlaceDetailScreen(
//                    placeId = placeId
//                )
//            }

            composable(Screen.AddTrip.route) {
                AddTripScreen(navController = navController)
            }

            composable(Screen.Profile.route) {
                ProfileScreen(navController = navController)
            }
        }
    }
}