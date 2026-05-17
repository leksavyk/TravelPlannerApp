package com.example.travelplanner.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import com.example.travelplanner.data.model.PackingCategory
import com.example.travelplanner.ui.components.CategoryTabs
import com.example.travelplanner.ui.components.PackingHeader
import com.example.travelplanner.ui.viewmodel.TripViewModel
import com.example.travelplanner.utils.formatToUk

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PackingScreen(tripId: String, viewModel: TripViewModel, navController: NavController) {
    val trips by viewModel.trips.collectAsState()
    val trip = trips.find { it.id.toString() == tripId }

    LaunchedEffect(tripId) {
        viewModel.loadPackingItems(tripId)
    }
    val items by viewModel.packingItems.collectAsState()

//    val items by viewModel.getPackingItems(tripId).collectAsState()
    var selectedCategoryName by remember { mutableStateOf("Усі") }

    val categories = PackingCategory.values().toList()

    var showAddDialog by remember { mutableStateOf(false) }
    var newItemName by remember { mutableStateOf("") }
    var categoryForNewItem by remember { mutableStateOf<PackingCategory?>(null) }

    val filteredItems = remember(items, selectedCategoryName) {
        if (selectedCategoryName == "Усі") items
        else items.filter { it.category.displayName == selectedCategoryName }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                newItemName = ""
            },
            title = { Text("Додати річ") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newItemName,
                        onValueChange = { newItemName = it },
                        label = { Text("Що беремо?") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF8E6CEF),
                            focusedLabelColor = Color(0xFF8E6CEF)
                        )
                    )

                    Text("Категорія:", style = MaterialTheme.typography.labelLarge)

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        categories.forEach { category ->
                            FilterChip(
                                selected = categoryForNewItem == category,
                                onClick = { categoryForNewItem = category },
                                label = { Text(category.displayName) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF8E6CEF).copy(alpha = 0.2f),
                                    selectedLabelColor = Color(0xFF8E6CEF)
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newItemName.isNotBlank() && categoryForNewItem != null) {
                            viewModel.addPackingItem(tripId, newItemName, categoryForNewItem!!)
                            newItemName = ""
                            showAddDialog = false
                        }
                    },
                    enabled = newItemName.isNotBlank() && categoryForNewItem != null,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E6CEF))
                ) {
                    Text("Додати")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    newItemName = ""
                }) {
                    Text("Скасувати")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Речі") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    categoryForNewItem = categories.find { it.displayName == selectedCategoryName }
                        ?: PackingCategory.OTHER
                    showAddDialog = true
                },
                containerColor = Color(0xFF8E6CEF)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            CategoryTabs(
                categories = categories,
                selectedCategory = selectedCategoryName,
                onCategorySelected = { selectedCategoryName = it }
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    PackingHeader(
                        tripTitle = trip?.title ?: "Подорож",
                        dateRange = if (trip != null) {
                            "${trip.startDate.formatToUk()} — ${trip.endDate.formatToUk()}"
                        } else "",
                        packedCount = items.count { it.isChecked },
                        totalCount = items.size
                    )
                }

                if (filteredItems.isEmpty()) {
                    item {
                        Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                            Text("У цій категорії поки порожньо", color = Color.Gray)
                        }
                    }
                } else {
                    val grouped = filteredItems.groupBy { it.category }
                    grouped.forEach { (category, categoryItems) ->
                        item {
                            Text(
                                text = category.displayName,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        items(categoryItems) { item ->
                            PackingItemRow(
                                item = item,
                                onToggle = { isChecked -> viewModel.togglePackingItem(item.id, isChecked) }
                            )
                        }

                        item {
                            TextButton(
                                onClick = {
                                    categoryForNewItem = category
                                    showAddDialog = true
                                },
                                modifier = Modifier.padding(start = 12.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF8E6CEF))
                                Spacer(Modifier.width(8.dp))
                                Text("Додати річ", color = Color(0xFF8E6CEF))
                            }
                        }
                    }
                }
            }
        }
    }
}
