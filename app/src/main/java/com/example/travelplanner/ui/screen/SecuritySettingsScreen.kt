package com.example.travelplanner.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelplanner.data.biometric.SensorType
import com.example.travelplanner.ui.viewmodel.SecurityViewModel
import com.example.travelplanner.ui.viewmodel.TripViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecuritySettingsScreen(
    viewModel: SecurityViewModel,
    tripViewModel: TripViewModel,
    onBackClick: () -> Unit
) {
    val sensorType by viewModel.sensorType.collectAsState()
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsState()
    val user by tripViewModel.currentUser.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Безпека та захист даних") } ,

                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад у профіль"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Метод біометрії на цьому пристрої",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = when (sensorType) {
                                SensorType.TOUCH_ID -> "👆"
                                SensorType.FACE_ID -> "👤"
                                SensorType.COMBINED -> "🔐"
                                SensorType.UNSUPPORTED -> "🚫"
                            },
                            fontSize = 24.sp
                        )

                        Text(
                            text = sensorType.getReadableName(),
                            fontSize = 16.sp,
                            style = MaterialTheme.colorScheme.onSurfaceVariant.let { MaterialTheme.typography.bodyLarge }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1.0f)) {
                    Text(
                        text = "Біометричний захист",
                        fontSize = 18.sp,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Запитувати відбиток або Face ID при кожному вході в TravelPlanner",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Switch(
                    checked = isBiometricEnabled,
                    onCheckedChange = { isChecked ->
                        viewModel.toggleBiometricProtection(isChecked, user?.id)
                    },
                    enabled = sensorType != SensorType.UNSUPPORTED
                )
            }

            if (sensorType == SensorType.UNSUPPORTED) {
                Text(
                    text = "На цьому пристрої відсутні налаштовані біометричні датчики. Будь ласка, додайте відбиток пальця або Face ID у системних налаштуваннях Android",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (isBiometricEnabled) {
                Text(
                    text = "Автоматичне блокування у фоні через:",
                    fontSize = 15.sp,
                    style = MaterialTheme.typography.titleSmall
                )

                val currentTimeout by viewModel.lockTimeoutSeconds.collectAsState()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val timeouts = listOf(15 to "15 сек", 30 to "30 сек", 60 to "1 хв", 300 to "5 хв")

                    timeouts.forEach { (seconds, label) ->
                        FilterChip(
                            selected = currentTimeout == seconds,
                            onClick = { viewModel.updateLockTimeout(seconds) },
                            label = { Text(label) }
                        )
                    }
                }
            }
        }
    }
}