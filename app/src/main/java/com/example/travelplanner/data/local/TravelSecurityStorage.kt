package com.example.travelplanner.data.local

/**
 * To save travel security settings and app access settings.
 */
interface TravelSecurityStorage {
    fun setBiometricProtectionEnabled(enabled: Boolean)
    fun isBiometricProtectionEnabled(): Boolean

    fun setLockoutTimeout(seconds: Int)
    fun getLockoutTimeout(): Int

    fun saveLastUserId(userId: String?)
    fun getLastUserId(): String?
}