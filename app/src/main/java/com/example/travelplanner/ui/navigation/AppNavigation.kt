package com.example.travelplanner.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.travelplanner.data.local.AppDatabase
import com.example.travelplanner.data.remote.MockTripApiService
import com.example.travelplanner.data.repository.TripRepository
import com.example.travelplanner.data.repository.UserRepository
import com.example.travelplanner.ui.screen.AddTripScreen
import com.example.travelplanner.ui.screen.AuthScreen
import com.example.travelplanner.ui.screen.ProfileScreen
import com.example.travelplanner.ui.screen.TripDetailScreen
import com.example.travelplanner.ui.screen.TripsListScreen
import com.example.travelplanner.ui.viewmodel.AuthViewModel
import com.example.travelplanner.ui.viewmodel.TripViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val context = androidx.compose.ui.platform.LocalContext.current

    val database = remember { AppDatabase.getDatabase(context) }
    val apiService = remember { MockTripApiService() }
    val userRepository = remember { UserRepository(database.userDao()) }
    val tripRepository = remember { TripRepository(database.tripDao(), apiService, userRepository) }

    val tripViewModel = remember { TripViewModel(tripRepository, userRepository) }
    val authViewModel = remember { AuthViewModel(userRepository) }

    val currentUser by userRepository.currentUser.collectAsState(initial = null)

    Scaffold(
        bottomBar = {
            if (currentUser != null) {
                AppBottomNavigation(navController)
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = if (currentUser == null) "auth" else Screen.Trips.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("auth") {
                AuthScreen(navController = navController, viewModel = authViewModel)
            }

            composable(Screen.Trips.route) {
                TripsListScreen(
                    navController = navController,
                    viewModel = tripViewModel
                )
            }

            composable("${Screen.TripDetail.route}/{tripId}") { backStackEntry ->
                val tripId = backStackEntry.arguments?.getString("tripId")

                TripDetailScreen(
                    tripId = tripId,
                    navController = navController,
                    viewModel = tripViewModel
                )
            }

            composable(Screen.AddTrip.route) {
                AddTripScreen(navController = navController, viewModel = tripViewModel)
            }

            composable(Screen.Profile.route) {
                ProfileScreen(navController = navController, viewModel = tripViewModel, authViewModel = authViewModel)
            }
        }
    }
}