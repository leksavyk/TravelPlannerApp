package com.example.travelplanner.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController

import com.example.travelplanner.data.MockData
import com.example.travelplanner.ui.navigation.Screen
import com.example.travelplanner.ui.viewmodel.TripViewModel

@Composable
fun TripsListScreen(navController: NavController, viewModel: TripViewModel) {

    val trips by viewModel.trips.collectAsState()

    Column {
        Text(
            text = "Мої подорожі",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn {
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
