package com.example.travelplanner.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import com.example.travelplanner.ui.viewmodel.SecurityViewModel
import com.example.travelplanner.ui.viewmodel.AuthViewModel
import com.example.travelplanner.ui.viewmodel.TripViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: TripViewModel,
    authViewModel: AuthViewModel,
    securityViewModel: SecurityViewModel
) {
    val user by viewModel.currentUser.collectAsState()
    val trips by viewModel.trips.collectAsState()

    val totalTrips = trips.size
    val completedTrips = trips.count { it.isCompleted }
    val totalBudget = trips.sumOf { it.budget }

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(text = "Видалення акаунта")
            },
            text = {
                Text(text = "Ви впевнені, що хочете видалити свій профіль? Цю дію неможливо скасувати, і всі ваші подорожі буде безповоротно втрачено")
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    onClick = {
                        showDeleteDialog = false
                        user?.let { currentUser ->
                            authViewModel.deleteAccount(currentUser.id)
                        }
                    }
                ) {
                    Text("Видалити")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Скасувати")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мій профіль") },

                actions = {
                    IconButton(onClick = { navController.navigate("security_settings") }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Налаштування безпеки"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = user?.username ?: "Завантаження...",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = user?.email ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Статистика подорожей", style = MaterialTheme.typography.titleMedium)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))

                    ProfileStatRow("Всього поїздок", totalTrips.toString())
                    ProfileStatRow("Завершено", completedTrips.toString())
                    ProfileStatRow("Загальний бюджет", "${totalBudget} грн")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            user?.let { currentUser ->
                Button(
                    onClick = {
                        authViewModel.logout(currentUser.id)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Вийти з профілю")
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = {
                        val isBiometricActive = securityViewModel.isBiometricEnabled.value
                        if (isBiometricActive) {
                            user?.let { currentUser ->
                                navController.navigate("critical_action_confirm/${currentUser.id}")
                            }
                        } else {
                            showDeleteDialog = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Видалити акаунт", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun ProfileStatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}