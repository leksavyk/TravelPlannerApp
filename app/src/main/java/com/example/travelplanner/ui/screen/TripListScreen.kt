package com.example.travelplanner.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController

import com.example.travelplanner.data.MockData
import com.example.travelplanner.ui.navigation.Screen
import com.example.travelplanner.ui.viewmodel.TripViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsListScreen(navController: NavController, viewModel: TripViewModel) {
    LaunchedEffect(Unit) {
        viewModel.startWebSocket()
    }

    val trips by viewModel.trips.collectAsState()
    val notifications by viewModel.notifications.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Мої подорожі",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate("notifications") }) {
                        if (notifications.isNotEmpty()) {
                            BadgedBox(
                                badge = {
                                    val notifications by viewModel.notifications.collectAsState()
                                    val unreadCount = notifications.count { !it.isRead }

                                    if (unreadCount > 0) {
                                        Badge {
                                            Text(unreadCount.toString())
                                        }
                                    }
                                }

                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Сповіщення"
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Сповіщення"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(trips) { trip ->
                TripItem(trip = trip) {
                    navController.navigate("${Screen.TripDetail.route}/${trip.id}")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TripsListPreview() {
    Column {
        Text(
            text = "Мої подорожі",
            style = MaterialTheme.typography.headlineMedium
        )

        LazyColumn {
            items(MockData.tripsList) { trip ->
                TripItem(
                    trip = trip,
                    onClick = {}
                )
            }
        }
    }
}
