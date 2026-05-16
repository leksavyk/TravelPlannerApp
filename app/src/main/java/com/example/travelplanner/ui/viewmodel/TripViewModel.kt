package com.example.travelplanner.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplanner.data.model.Trip
import com.example.travelplanner.data.repository.TripRepository
import com.example.travelplanner.data.repository.UserRepository
import com.example.travelplanner.data.websocket.ConnectionState
import com.example.travelplanner.data.websocket.ISocketManager
import com.example.travelplanner.data.websocket.TravelNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class TripViewModel(private val repository: TripRepository, val userRepository: UserRepository, private val socketManager: ISocketManager) : ViewModel() {
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

    val connectionState: StateFlow<ConnectionState> = socketManager.state
    private val _notifications = MutableStateFlow<List<TravelNotification>>(emptyList())
    val notifications: StateFlow<List<TravelNotification>> = _notifications.asStateFlow()
    private val json = Json { ignoreUnknownKeys = true }

    init {
        observeSocketMessages()
        socketManager.connect("wss://travel.planner.mock")
    }

    private fun observeSocketMessages() {
        viewModelScope.launch {
            socketManager.messages.collect { rawJson ->
                try {
                    val newNotification = json.decodeFromString<TravelNotification>(rawJson)
                    _notifications.update { currentList ->
                        listOf(newNotification) + currentList
                    }
                } catch (e: Exception) {
                    println("DEBUG: Socket parse error: ${e.message}")
                }
            }
        }
    }

    fun clearNotifications() {
        _notifications.value = emptyList()
    }

    override fun onCleared() {
        super.onCleared()
        socketManager.disconnect()
    }

    fun markAsRead(timestamp: Long) {
        _notifications.value = _notifications.value.map { notification ->
            if (notification.timestamp == timestamp) {
                notification.copy(isRead = true)
            } else {
                notification
            }
        }
    }

    fun markAllAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }
}