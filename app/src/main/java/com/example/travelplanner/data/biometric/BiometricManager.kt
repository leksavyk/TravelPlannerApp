package com.example.travelplanner.data.biometric

import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.StateFlow

/**
 * For biometric authentication
 * Allows the UI to rely on a single source of truth without unnecessary flags.
 */
interface BiometricManager {
    /** The current user authentication state. */
    val authState: StateFlow<BiometricAuthState>

    /** Checks for sensor availability and hardware readiness. */
    fun checkAvailability(): SensorType

    /** Launches the system scan dialog. [reason] is a message for the user. */
    fun authenticate(activity: FragmentActivity, reason: String)

    /** Checks whether the user has enabled this security feature in the app settings. */
    fun isEnabledByUser(): Boolean

    /** Resets the state back to Idle after processing the result. */
    fun resetState()
}