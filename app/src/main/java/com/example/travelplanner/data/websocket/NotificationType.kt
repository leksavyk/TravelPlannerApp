package com.example.travelplanner.data.websocket

enum class NotificationType {
    TRIP_START_REMINDER,   // The journey is about to begin
    TRIP_COMPLETED,        // All locations have been visited
    INACTIVITY_REMINDER,   // It's been a while since we updated the plan
    SYSTEM                 // General system messages
}