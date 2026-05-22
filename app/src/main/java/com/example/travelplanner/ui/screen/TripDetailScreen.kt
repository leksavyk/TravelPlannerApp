package com.example.travelplanner.ui.screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import com.example.travelplanner.data.model.Place
import com.example.travelplanner.ui.viewmodel.TripViewModel
import com.example.travelplanner.utils.formatToUk
import java.util.UUID
import com.example.travelplanner.ui.components.TripHeaderCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(tripId: String?, navController: NavController, viewModel: TripViewModel) {
    val trips by viewModel.trips.collectAsState()
    val trip = trips.find { it.id.toString() == tripId }

    var showDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var newPlaceName by remember { mutableStateOf("") }

    if (showDeleteConfirm && trip != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Видалити подорож?") },
            text = { Text("Ви впевнені, що хочете видалити подорож \"${trip.title}\" та всі пов'язані локації? Цю дію неможливо скасувати") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTrip(trip)
                        showDeleteConfirm = false
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Видалити")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Скасувати")
                }
            }
        )
    }

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
                ,
                actions = {
                    if (trip != null) {
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Видалити подорож",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
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
                    onDismissRequest = {
                        showDialog = false
                        newPlaceName = "" },
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
                            if (newPlaceName.isNotBlank()) {
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
                        TextButton(onClick = {
                            showDialog = false
                            newPlaceName = ""
                        }) {
                            Text("Скасувати")
                        }
                    }
                )
            }

            LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
                item {
                    TripHeaderCard(
                        budget = trip.budget,
                        date = trip.startDate.formatToUk()
                    )

                    Button(
                        onClick = {
                            navController.navigate("packing_list/${trip.id}")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8E6CEF)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.List, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Список речей")
                    }

                    Text(
                        text = "Місця для відвідування",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 14.dp)
                    )
                }

                items(trip.places) { place ->
                    PlaceItem(
                        place = place,
                        onCheckedChange = { isChecked ->
                            val updatedPlaces = trip.places.map {
                                if (it.id == place.id) it.copy(isVisited = isChecked) else it
                            }
                            val allVisited = updatedPlaces.all { it.isVisited }
                            viewModel.addTrip(trip.copy(places = updatedPlaces, isCompleted = allVisited))
                        },
                        onDelete = {
                            val updatedPlaces = trip.places.filter { it.id != place.id }
                            val allVisited = updatedPlaces.isNotEmpty() && updatedPlaces.all { it.isVisited }

                            viewModel.addTrip(trip.copy(places = updatedPlaces, isCompleted = allVisited))
                        }
                    )
                }
            }
        } else {
            Text("Подорож не знайдена", modifier = Modifier.padding(padding))
        }
    }
}