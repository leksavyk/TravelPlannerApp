package com.example.travelplanner.data.model

import java.util.UUID

// Represents the app user
data class User(
    val id: UUID,
    val username: String,
    val email: String,
    val myTrips: List<Trip>
)