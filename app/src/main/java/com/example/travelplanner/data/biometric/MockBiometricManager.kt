package com.example.travelplanner.data.biometric

import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockBiometricManager(
    private val customSensorType: SensorType = SensorType.TOUCH_ID,
    private val desiredResult: BiometricAuthState = BiometricAuthState.Success,
    private var isUserSettingEnabled: Boolean = false
) : BiometricManager {

    private val _authState = MutableStateFlow<BiometricAuthState>(BiometricAuthState.Idle)
    override val authState: StateFlow<BiometricAuthState> = _authState.asStateFlow()

    override fun checkAvailability(): SensorType = customSensorType

    override fun authenticate(activity: FragmentActivity, reason: String) {
        _authState.value = BiometricAuthState.Authenticating
        _authState.value = desiredResult
    }

    override fun isEnabledByUser(): Boolean = isUserSettingEnabled

    override fun resetState() {
        _authState.value = BiometricAuthState.Idle
    }

    fun setBiometricEnabled(enabled: Boolean) {
        isUserSettingEnabled = enabled
    }
}