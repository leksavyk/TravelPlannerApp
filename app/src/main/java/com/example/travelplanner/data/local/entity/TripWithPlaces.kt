package com.example.travelplanner.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class TripWithPlaces(
    @Embedded val trip: TripEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "tripId"
    )
    val places: List<PlaceEntity>
)