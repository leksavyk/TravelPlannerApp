package com.example.travelplanner.ui.viewmodel

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplanner.data.biometric.BiometricAuthState
import com.example.travelplanner.data.biometric.SensorType
import com.example.travelplanner.data.biometric.BiometricManager
import com.example.travelplanner.data.biometric.AppLockController
import com.example.travelplanner.data.local.SharedSecurityStorage
import com.example.travelplanner.data.local.TravelSecurityStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SecurityViewModel(
    private val biometricManager: BiometricManager,
    private val securityStorage: TravelSecurityStorage,
    private val appLockController: AppLockController
) : ViewModel() {
    private val _sensorType = MutableStateFlow<SensorType>(SensorType.UNSUPPORTED)
    val sensorType: StateFlow<SensorType> = _sensorType.asStateFlow()

    private val _isBiometricEnabled = MutableStateFlow(false)
    val isBiometricEnabled: StateFlow<Boolean> = _isBiometricEnabled.asStateFlow()
    val biometricAuthState: StateFlow<BiometricAuthState> = biometricManager.authState

    private val _lockTimeoutSeconds = MutableStateFlow(SharedSecurityStorage.DEFAULT_TIMEOUT_SECONDS)
    val lockTimeoutSeconds: StateFlow<Int> = _lockTimeoutSeconds.asStateFlow()

    init {
        _sensorType.value = biometricManager.checkAvailability()
        _isBiometricEnabled.value = securityStorage.isBiometricProtectionEnabled()
        _lockTimeoutSeconds.value = securityStorage.getLockoutTimeout()
    }

    /**
     * Called when the user toggles the switch on the settings screen
     */
    fun toggleBiometricProtection(isEnabled: Boolean, userId: String?) {
        securityStorage.setBiometricProtectionEnabled(isEnabled)
        _isBiometricEnabled.value = isEnabled

        if (isEnabled && userId != null) {
            securityStorage.saveLastUserId(userId)
        } else {
            appLockController.lockSessionManually()
            securityStorage.saveLastUserId(null)
        }
    }

    fun getSavedUserId(): String? {
        return securityStorage.getLastUserId()
    }

    fun updateLockTimeout(seconds: Int) {
        securityStorage.setLockoutTimeout(seconds)
        _lockTimeoutSeconds.value = seconds
    }

    fun triggerBiometricAuth(activity: FragmentActivity, reason: String) {
        biometricManager.authenticate(activity, reason)
    }

    fun checkIfAppShouldLock(): Boolean {
        return appLockController.shouldActivateLock()
    }

    fun handleAuthSuccess() {
        appLockController.markAsUnlocked()
        biometricManager.resetState()
    }

    fun resetAuthState() {
        biometricManager.resetState()
    }

    fun appWentToBackground() {
        appLockController.registerBackgroundTransition()
    }
}