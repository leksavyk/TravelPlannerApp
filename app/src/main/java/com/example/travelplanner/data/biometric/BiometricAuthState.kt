package com.example.travelplanner.data.biometric

/**
 * Current status of the biometric security system.
 * Serves as the single source of truth for security and login UI screens.
 */

sealed class BiometricAuthState {
    object Idle : BiometricAuthState()
    object Authenticating : BiometricAuthState()
    object Success : BiometricAuthState()
    object Dismissed : BiometricAuthState()
    object Unavailable : BiometricAuthState()
    data class Failed(val error: String) : BiometricAuthState()
}
