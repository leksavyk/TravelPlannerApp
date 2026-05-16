package com.example.travelplanner.data

import androidx.compose.runtime.mutableStateListOf
import com.example.travelplanner.data.model.Place
import com.example.travelplanner.data.model.Trip
import com.example.travelplanner.data.model.User
import java.util.Date
import java.util.UUID
import java.util.Calendar

object MockData {
    private fun getFutureDate(daysFromNow: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, daysFromNow)
        return calendar.time
    }
    val kyivPlaces = mutableStateListOf(
        Place(
            id = UUID.randomUUID(),
            name = "Золоті Ворота",
            isVisited = true
        ),
        Place(
            id = UUID.randomUUID(),
            name = "Андріївський узвіз",
            isVisited = true
        )
    )

    val lvivPlaces = mutableStateListOf(
        Place(
            id = UUID.randomUUID(),
            name = "Площа Ринок",
            isVisited = false
        ),
        Place(
            id = UUID.randomUUID(),
            name = "Високий Замок",
            isVisited = false
        )
    )

    val tripsList = mutableStateListOf(
        Trip(
            id = UUID.randomUUID(),
            title = "Вікенд у Києві",
            budget = 3500.0,
            startDate = Date(),
            endDate = getFutureDate(2),
            isCompleted = true,
            places = kyivPlaces
        ),
        Trip(
            id = UUID.randomUUID(),
            title = "Прогулянка Львовом",
            budget = 5000.0,
            startDate = getFutureDate(4),
            endDate = getFutureDate(8),
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
                //rating = 0.0f,
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
    fun addNewTrip(title: String, budget: Double, startDateLong: Long?, endDateLong: Long?, places: List<Place>) {
        val start = if (startDateLong != null) Date(startDateLong) else Date()
        val end = if (endDateLong != null) Date(endDateLong) else Date(start.time + 86400000)

        val newTrip = Trip(
            id = UUID.randomUUID(),
            title = title,
            budget = budget,
            startDate = start,
            endDate = end,
            isCompleted = false,
            places = places.toMutableList()
        )
        tripsList.add(newTrip)
    }
}