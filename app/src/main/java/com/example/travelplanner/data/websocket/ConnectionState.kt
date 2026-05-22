package com.example.travelplanner.data.websocket

sealed class ConnectionState {
    data object Disconnected : ConnectionState()
    data object Connecting : ConnectionState()
    data object Connected : ConnectionState()
    data object Reconnecting : ConnectionState()

    override fun toString(): String = this::class.simpleName ?: "Unknown"
}