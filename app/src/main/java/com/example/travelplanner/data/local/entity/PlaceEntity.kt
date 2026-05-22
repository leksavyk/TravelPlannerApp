package com.example.travelplanner.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


/**
 * Room entity that persists one place within a trip
 * Each place is linked to a TripEntity via the tripId foreign key
 */
@Entity(
    tableName = "places",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PlaceEntity(
    @PrimaryKey val id: String,
    val tripId: String,
    val name: String,
    // val rating: Float,
    val isVisited: Boolean = false
)
