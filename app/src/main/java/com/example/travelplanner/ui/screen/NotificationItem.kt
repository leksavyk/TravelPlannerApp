package com.example.travelplanner.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.travelplanner.data.websocket.NotificationType
import com.example.travelplanner.data.websocket.TravelNotification
import java.text.SimpleDateFormat
import java.util.Date
import androidx.compose.ui.platform.LocalLocale

@Composable
fun NotificationItem(notification: TravelNotification, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = if (notification.isRead) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        } else {
            CardDefaults.cardColors()
        },
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val (icon, color) = when (notification.type) {
                NotificationType.TRIP_START_REMINDER -> Icons.Default.Notifications to Color(0xFF4CAF50)
                NotificationType.TRIP_COMPLETED -> Icons.Default.CheckCircle to Color(0xFF2196F3)
                NotificationType.INACTIVITY_REMINDER -> Icons.Default.Info to Color(0xFFFF9800)
                NotificationType.SYSTEM -> Icons.Default.Settings to Color(0xFF9C27B0)
            }

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = SimpleDateFormat("HH:mm:ss", LocalLocale.current.platformLocale)
                        .format(Date(notification.timestamp)),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}