package com.example.travelplanner.data.websocket

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface ISocketManager {
    val state: StateFlow<ConnectionState>
    val messages: SharedFlow<String>

    fun connect(url: String)
    fun disconnect()
    fun send(message: String)
}