package com.example.travelplanner.data.biometric

import com.example.travelplanner.data.local.TravelSecurityStorage

/**
 * Controls automatic locking
 * * Determines whether to display the lock screen in two cases:
 * 1. **Cold start** — security is enabled, but the user has not yet been authenticated in the current session
 * 2. **Return from the background** — the app has been in the background longer than the security timeout allows
 */
class AppLockController(private val securityStorage: TravelSecurityStorage) {
    private var backgroundTimestamp: Long = 0L

    /** Becomes true only after the first successful biometric scan in the current process session. */
    var isSessionUnlocked: Boolean = false
        private set

    fun registerBackgroundTransition() {
        if (securityStorage.isBiometricProtectionEnabled()) {
            backgroundTimestamp = System.currentTimeMillis()
        }
    }

    fun shouldActivateLock(): Boolean {
        if (!securityStorage.isBiometricProtectionEnabled()) return false

        // Cold start
        if (!isSessionUnlocked) return true

        // Return from the background
        if (backgroundTimestamp == 0L) return false

        val elapsedMillis = System.currentTimeMillis() - backgroundTimestamp
        val allowedTimeoutMillis = securityStorage.getLockoutTimeout() * 1_000L

        return elapsedMillis >= allowedTimeoutMillis
    }

    fun markAsUnlocked() {
        isSessionUnlocked = true
        backgroundTimestamp = 0L
    }

    fun lockSessionManually() {
        isSessionUnlocked = false
        backgroundTimestamp = 0L
    }
}