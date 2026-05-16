package com.example.travelplanner.data.mapper

import com.example.travelplanner.data.local.entity.TripEntity
import com.example.travelplanner.data.local.entity.PlaceEntity
import com.example.travelplanner.data.local.entity.TripWithPlaces
import com.example.travelplanner.data.model.Trip
import com.example.travelplanner.data.model.Place
import com.example.travelplanner.data.model.SyncStatus
import java.util.Date
import java.util.UUID

/**
 * Provides mapping between persistence layer TripEntity and domain model Trip
 */
fun TripEntity.toDomain(placesList: List<Place> = emptyList()): Trip {
    return Trip(
        id = UUID.fromString(id),
        title = title,
        budget = budget,
        startDate = Date(startDate),
        endDate = Date(endDate),
        isCompleted = isCompleted,
        places = placesList
    )
}

fun Trip.toEntity(status: SyncStatus = SyncStatus.PENDING, ownerId: String): TripEntity {
    return TripEntity(
        id = id.toString(),
        userId = ownerId,
        title = title,
        budget = budget,
        startDate = startDate.time,
        endDate = endDate.time,
        isCompleted = isCompleted,
        syncStatus = status
    )
}

fun TripWithPlaces.toDomain(): Trip {
    return Trip(
        id = UUID.fromString(trip.id),
        title = trip.title,
        budget = trip.budget,
        startDate = Date(trip.startDate),
        endDate = Date(trip.endDate),
        isCompleted = trip.isCompleted,
        // Мапимо список PlaceEntity у список Place
        places = places.map { it.toDomain() }
    )
}

fun PlaceEntity.toDomain(): Place {
    return Place(
        id = UUID.fromString(id),
        name = name,
        isVisited = isVisited
    )
}