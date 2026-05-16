package com.example.travelplanner.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travelplanner.data.websocket.ConnectionState
import com.example.travelplanner.ui.viewmodel.TripViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(navController: NavController, viewModel: TripViewModel) {
    val notifications by viewModel.notifications.collectAsState()
    val hasUnread = notifications.any { !it.isRead }

//    val connectionState by viewModel.connectionState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Центр сповіщень",
                            style = MaterialTheme.typography.titleLarge
                        )
//                        Spacer(modifier = Modifier.height(4.dp))
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            val statusColor = when (connectionState) {
//                                is ConnectionState.Connected -> Color.Green
//                                is ConnectionState.Connecting -> Color.Yellow
//                                is ConnectionState.Reconnecting -> Color.Cyan
//                                else -> Color.Red
//                            }
//                            Box(modifier = Modifier.size(8.dp).background(statusColor, CircleShape))
//                            Spacer(Modifier.width(8.dp))
//                            Text(
//                                text = "Статус: ${connectionState}",
//                                style = MaterialTheme.typography.bodySmall
//                            )
//                        }
                    }
                },
                actions = {
                    if (hasUnread) {
                        TextButton(onClick = { viewModel.markAllAsRead() }) {
                            Text("Прочитати все")
                        }
                    }
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Закрити сповіщення"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text("Поки що немає нових сповіщень") }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)
            ) {
                items(notifications) {
                    notification -> NotificationItem(notification,  onClick = { viewModel.markAsRead(notification.timestamp)})
                }
            }
        }
    }
}
