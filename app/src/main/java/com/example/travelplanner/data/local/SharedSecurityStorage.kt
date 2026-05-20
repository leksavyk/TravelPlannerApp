package com.example.travelplanner.data.local

import android.content.Context

class SharedSecurityStorage(context: Context) : TravelSecurityStorage {
    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun setBiometricProtectionEnabled(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_BIOMETRIC_ACTIVE, enabled).apply()
    }

    override fun isBiometricProtectionEnabled(): Boolean {
        return preferences.getBoolean(KEY_BIOMETRIC_ACTIVE, false)
    }

    override fun setLockoutTimeout(seconds: Int) {
        preferences.edit().putInt(KEY_LOCKOUT_TIMEOUT, seconds).apply()
    }

    override fun getLockoutTimeout(): Int {
        return preferences.getInt(KEY_LOCKOUT_TIMEOUT, DEFAULT_TIMEOUT_SECONDS)
    }

    override fun saveLastUserId(userId: String?) {
        preferences.edit().putString("last_secure_user_id", userId).apply()
    }

    override fun getLastUserId(): String? {
        return preferences.getString("last_secure_user_id", null)
    }

    companion object {
        private const val PREFS_NAME = "travel_planner_security_prefs"
        private const val KEY_BIOMETRIC_ACTIVE = "secure_biometric_active"
        private const val KEY_LOCKOUT_TIMEOUT = "secure_lockout_timeout"
        const val DEFAULT_TIMEOUT_SECONDS = 30
    }
}
