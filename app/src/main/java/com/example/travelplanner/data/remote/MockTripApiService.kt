package com.example.travelplanner.data.remote

import com.example.travelplanner.data.local.entity.TripEntity
import kotlinx.coroutines.delay

/**
 * Simulates a server API.
 * Uses a delay to simulate a network request
 */
class MockTripApiService {

    // Ендпоінт: POST /trips
    // Повертає true, якщо "сервер" прийняв дані
    suspend fun syncTripWithServer(trip: TripEntity): Boolean {
        delay(2000) // Імітуємо затримку мережі
        return true  // Симулюємо успішну відповідь
    }

    // Ендпоінт: DELETE /trips/{id}
    suspend fun deleteTripFromServer(id: String): Boolean {
        delay(1000)
        return true
    }
}