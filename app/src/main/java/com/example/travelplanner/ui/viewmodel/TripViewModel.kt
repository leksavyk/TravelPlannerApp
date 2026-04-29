package com.example.travelplanner.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplanner.data.model.Trip
import com.example.travelplanner.data.repository.TripRepository
import com.example.travelplanner.data.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TripViewModel(private val repository: TripRepository, private val userRepository: UserRepository) : ViewModel() {
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

    init {
        // Імітація: при старті перевіряємо, чи є юзер. Якщо нема — створюємо.
        viewModelScope.launch {
            userRepository.currentUser.collect { user ->
                if (user == null) {
                    userRepository.registerUser(
                        id = "dev_user_123",
                        username = "Oleksandra",
                        email = "oleksandra@example.com",
                    )
                }
            }
        }
    }
//    fun addTrip(trip: Trip) {
//        viewModelScope.launch {
//            repository.saveTrip(trip)
//        }
//    }
    fun addTrip(trip: Trip) {
        viewModelScope.launch {
            val user = currentUser.value
            if (user != null) {
                repository.saveTrip(trip, user.id)
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