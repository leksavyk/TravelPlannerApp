package com.example.travelplanner.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.navigation.NavController

import com.example.travelplanner.data.MockData
import com.example.travelplanner.data.model.Place
import com.example.travelplanner.ui.components.DatePickerModal
import com.example.travelplanner.ui.navigation.Screen
import com.example.travelplanner.ui.viewmodel.TripViewModel
import com.example.travelplanner.utils.formatToUk
import java.util.Date
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTripScreen(navController: NavController, viewModel: TripViewModel) {
    var title by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    val tempPlaces = remember { mutableStateListOf<Place>() }
    var newPlaceName by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Нова подорож") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Назва подорожі") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                OutlinedTextField(
                    value = budget,
                    onValueChange = { budget = it },
                    label = { Text("Бюджет (грн)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(text = selectedDate?.let { Date(it).formatToUk() } ?: "Оберіть дату")
                }
            }

            item {
                Text("План місць:", style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newPlaceName,
                        onValueChange = { newPlaceName = it },
                        label = { Text("Назва місця") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                    IconButton(onClick = {
                        if (newPlaceName.isNotBlank()) {
                            tempPlaces.add(Place(UUID.randomUUID(), newPlaceName, false))
                            newPlaceName = ""
                        }
                    }) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Додати", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            items(tempPlaces) { place ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(place.name, modifier = Modifier.padding(8.dp))
                }
            }

            item {
                Button(
                    onClick = {
                        if (title.isNotBlank() && budget.isNotBlank()) {
                            val newTrip = com.example.travelplanner.data.model.Trip(
                                id = UUID.randomUUID(),
                                title = title,
                                budget = budget.toDoubleOrNull() ?: 0.0,
                                startDate = java.util.Date(selectedDate ?: System.currentTimeMillis()),
                                isCompleted = false,
                                places = tempPlaces.toList()
                            )
                            viewModel.addTrip(newTrip)

                            navController.navigate(Screen.Trips.route) {
                                popUpTo(Screen.Trips.route) { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Зберегти подорож")
                }
            }
        }

        if (showDatePicker) {
            DatePickerModal(
                onDateSelected = { date -> selectedDate = date },
                onDismiss = { showDatePicker = false }
            )
        }
    }
}