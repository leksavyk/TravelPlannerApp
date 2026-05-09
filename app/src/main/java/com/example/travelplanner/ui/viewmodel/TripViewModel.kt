package com.example.travelplanner.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplanner.data.model.Trip
import com.example.travelplanner.data.repository.TripRepository
import com.example.travelplanner.data.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TripViewModel(private val repository: TripRepository, val userRepository: UserRepository) : ViewModel() {
    val trips: StateFlow<List<Trip>> = repository.allTrips
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    val currentUser = userRepository.currentUser.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun addTrip(trip: Trip) {
        viewModelScope.launch {
            try {
                val user = userRepository.currentUser.filterNotNull().first()

                repository.saveTrip(trip, user.id)
            } catch (e: Exception) {
                println("DEBUG: Save error: ${e.message}")
            }
        }
    }

    fun deleteTrip(trip: Trip) {
        viewModelScope.launch {
            val user = currentUser.value
            if (user != null) {
                repository.deleteTrip(trip, user.id)
            } else {
                println("Error: You cannot delete a trip without an active user")
            }
        }
    }
}