package com.example.travelplanner.data.websocket

import kotlinx.serialization.Serializable

@Serializable
data class TravelNotification(
    val type: NotificationType,
    val title: String,
    val message: String,
    val timestamp: Long,
    val tripId: String? = null,
    val isRead: Boolean = false
)