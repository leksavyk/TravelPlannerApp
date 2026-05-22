package com.example.travelplanner.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.travelplanner.data.biometric.AppLockController
import com.example.travelplanner.data.biometric.BiometricManagerImpl
import com.example.travelplanner.data.local.AppDatabase
import com.example.travelplanner.data.local.SharedSecurityStorage
import com.example.travelplanner.data.remote.MockTripApiService
import com.example.travelplanner.data.repository.TripRepository
import com.example.travelplanner.data.repository.UserRepository
import com.example.travelplanner.data.websocket.SocketManager
import com.example.travelplanner.ui.screen.AddTripScreen
import com.example.travelplanner.ui.screen.AuthScreen
import com.example.travelplanner.ui.screen.BiometricLoginScreen
import com.example.travelplanner.ui.screen.CalendarScreen
import com.example.travelplanner.ui.screen.CriticalActionScreen
import com.example.travelplanner.ui.screen.ProfileScreen
import com.example.travelplanner.ui.screen.TripDetailScreen
import com.example.travelplanner.ui.screen.TripsListScreen
import com.example.travelplanner.ui.screen.NotificationScreen
import com.example.travelplanner.ui.screen.PackingScreen
import com.example.travelplanner.ui.screen.SecuritySettingsScreen
import com.example.travelplanner.ui.viewmodel.AuthViewModel
import com.example.travelplanner.ui.viewmodel.SecurityViewModel
import com.example.travelplanner.ui.viewmodel.TripViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val context = androidx.compose.ui.platform.LocalContext.current

    val database = remember { AppDatabase.getDatabase(context) }
    val apiService = remember { MockTripApiService() }
    val userRepository = remember { UserRepository(database.userDao()) }
    val tripRepository = remember { TripRepository(database.tripDao(), database.packingDao(), apiService, userRepository) }
    val socketManager = remember { SocketManager() }

    val tripViewModel = remember { TripViewModel(tripRepository, userRepository, socketManager) }
    val authViewModel = remember { AuthViewModel(userRepository) }

    val securityViewModel = remember {
        val securityStorage = SharedSecurityStorage(context.applicationContext)
        val biometricManager = BiometricManagerImpl(context.applicationContext, securityStorage)
        val appLockController = AppLockController(securityStorage)
        SecurityViewModel(biometricManager, securityStorage, appLockController)
    }

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
                AuthScreen(navController = navController, viewModel = authViewModel, securityViewModel = securityViewModel)
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

            composable("packing_list/{tripId}") { backStackEntry ->
                val tripId = backStackEntry.arguments?.getString("tripId") ?: ""

                PackingScreen(
                    tripId = tripId,
                    viewModel = tripViewModel,
                    navController = navController
                )
            }

            composable(Screen.AddTrip.route) {
                AddTripScreen(navController = navController, viewModel = tripViewModel)
            }

            composable(Screen.Calendar.route) {
                CalendarScreen(viewModel = tripViewModel, navController = navController)
            }

            composable(Screen.Profile.route) {
                ProfileScreen(navController = navController, viewModel = tripViewModel, authViewModel = authViewModel, securityViewModel = securityViewModel)
            }

            composable("notifications") {
                NotificationScreen(navController = navController, viewModel = tripViewModel)
            }

            composable("security_settings") {
                SecuritySettingsScreen(viewModel = securityViewModel, tripViewModel = tripViewModel, onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = "critical_action_confirm/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""

                CriticalActionScreen(
                    viewModel = securityViewModel,
                    onActionConfirmed = {
                        if (userId.isNotEmpty()) {
                            authViewModel.deleteAccount(userId)
                        }
                        navController.navigate("auth") {
                            popUpTo(0)
                        }
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable("biometric_login") {
                val savedUserId = securityViewModel.getSavedUserId() ?: ""
                val coroutineScope = rememberCoroutineScope()

                val userDao = database.userDao()

                BiometricLoginScreen(
                    securityViewModel = securityViewModel,
                    userDao = userDao,
                    savedUserId = savedUserId,
                    onLoginSuccess = { userId ->
                        coroutineScope.launch(Dispatchers.IO) {
                            userDao.updateLoginStatus(userId, true)

                            withContext(Dispatchers.Main) {
                                navController.navigate("trips") {
                                    popUpTo("auth") { inclusive = true }
                                }
                            }
                        }
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}