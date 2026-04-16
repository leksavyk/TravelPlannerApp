package com.example.travelplanner.data.model

import java.util.Date
import java.util.UUID

// Represents a specific user journey
data class Trip(
    val id: UUID,
    val title: String,
    val budget: Double,
    val startDate: Date,
    val isCompleted: Boolean,
    val places: List<Place>
)