package com.example.travelplanner.ui.screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import com.example.travelplanner.data.MockData
import com.example.travelplanner.data.model.Place
import com.example.travelplanner.ui.viewmodel.TripViewModel
import com.example.travelplanner.utils.formatToUk
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(tripId: String?, navController: NavController, viewModel: TripViewModel) {
    val trips by viewModel.trips.collectAsState()
    val trip = trips.find { it.id.toString() == tripId }

    var showDialog by remember { mutableStateOf(false) }
    var newPlaceName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = trip?.title ?: "Деталі подорожі") },

                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Додати місце")
            }
        }
    ) { padding ->
        if (trip != null) {
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Нове місце") },
                    text = {
                        TextField(
                            value = newPlaceName,
                            onValueChange = { newPlaceName = it },
                            placeholder = { Text("Назва локації (напр. Парк)") }
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            if (newPlaceName.isNotBlank() && trip != null) {
                                val newPlace = Place(
                                    id = UUID.randomUUID(),
                                    name = newPlaceName,
                                    isVisited = false
                                )
                                val updatedTrip = trip.copy(places = trip.places + newPlace)

                                viewModel.addTrip(updatedTrip)

                                newPlaceName = ""
                                showDialog = false
                            }
                        }) {
                            Text("Додати")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Скасувати")
                        }
                    }
                )
            }

            LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
                item {
                    Text("Бюджет: ${trip.budget}", style = MaterialTheme.typography.bodyLarge)
                    Text("Дата: ${trip.startDate.formatToUk()}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Місця для відвідування:", style = MaterialTheme.typography.titleMedium)
                }

                items(trip.places) { place ->
                    PlaceItem(
                        place = place,
                        onCheckedChange = { isChecked ->
                            val updatedPlaces = trip.places.map {
                                if (it.id == place.id) it.copy(isVisited = isChecked) else it
                            }
                            val allVisited = updatedPlaces.all { it.isVisited }

                            val updatedTrip = trip.copy(
                                places = updatedPlaces,
                                isCompleted = allVisited
                            )
                            viewModel.addTrip(updatedTrip)
                        }
                    )
                }
            }
        } else {
            Text("Подорож не знайдена", modifier = Modifier.padding(padding))
        }
    }
}