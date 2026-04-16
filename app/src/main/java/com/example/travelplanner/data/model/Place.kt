package com.example.travelplanner.data.model

import java.util.UUID

// Represents a specific location on a trip
data class Place(
    val id: UUID,
    val name: String,
    val rating: Float,
    val isVisited: Boolean
)