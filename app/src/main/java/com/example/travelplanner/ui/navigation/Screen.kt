package com.example.travelplanner.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String? = null, val icon: ImageVector? = null) {
    object Trips : Screen(route = "trips", title = "Trips", icon = Icons.Default.Home)
    object AddTrip : Screen(route = "add_trip", title = "Add", icon = Icons.Default.Add)
    object Profile : Screen(route = "profile", title = "Profile", icon = Icons.Default.Person)

    object TripDetail : Screen("trip_detail")
    object Notifications : Screen(route = "notifications", title = "Notification", Icons.Default.Notifications)
}