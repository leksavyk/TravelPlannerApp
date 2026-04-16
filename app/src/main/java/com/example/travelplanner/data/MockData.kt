package com.example.travelplanner.data

import androidx.compose.runtime.mutableStateListOf
import com.example.travelplanner.data.model.Place
import com.example.travelplanner.data.model.Trip
import com.example.travelplanner.data.model.User
import java.util.Date
import java.util.UUID

object MockData {
    val kyivPlaces = mutableStateListOf(
        Place(
            id = UUID.randomUUID(),
            name = "Золоті Ворота",
            rating = 4.8f,
            isVisited = true
        ),
        Place(
            id = UUID.randomUUID(),
            name = "Андріївський узвіз",
            rating = 4.9f,
            isVisited = true
        )
    )

    val lvivPlaces = mutableStateListOf(
        Place(
            id = UUID.randomUUID(),
            name = "Площа Ринок",
            rating = 5.0f,
            isVisited = false
        ),
        Place(
            id = UUID.randomUUID(),
            name = "Високий Замок",
            rating = 4.5f,
            isVisited = false
        )
    )

    val tripsList = mutableStateListOf(
        Trip(
            id = UUID.randomUUID(),
            title = "Вікенд у Києві",
            budget = 3500.0,
            startDate = Date(),
            isCompleted = true,
            places = kyivPlaces
        ),
        Trip(
            id = UUID.randomUUID(),
            title = "Прогулянка Львовом",
            budget = 5000.0,
            startDate = Date(),
            isCompleted = false,
            places = lvivPlaces
        )
    )

    val currentUser = User(
        id = UUID.randomUUID(),
        username = "Oleksandra",
        email = "oleksandra@example.com",
        myTrips = tripsList
    )

    // function for creating a new location and adding it to a specific trip
    fun addNewPlaceToTrip(tripId: UUID, placeName: String) {
        val tripIndex = tripsList.indexOfFirst { it.id == tripId }

        if (tripIndex != -1) {
            val currentTrip = tripsList[tripIndex]

            val newPlace = Place(
                id = UUID.randomUUID(),
                name = placeName,
                rating = 0.0f,
                isVisited = false
            )

            val updatedPlaces = currentTrip.places + newPlace
            tripsList[tripIndex] = currentTrip.copy(
                places = updatedPlaces,
                isCompleted = false
            )
        }
    }

    // function for creating a new trip
    fun addNewTrip(title: String, budget: Double, date: Long?, places: List<Place>) {
        val newTrip = Trip(
            id = UUID.randomUUID(),
            title = title,
            budget = budget,
            startDate = if (date != null) Date(date) else Date(),
            isCompleted = false,
            places = places.toMutableList()
        )
        tripsList.add(newTrip)
    }
}