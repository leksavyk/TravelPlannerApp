package com.example.travelplanner.data.biometric

import android.content.Context
import android.content.pm.PackageManager
import androidx.biometric.BiometricManager as AndroidBiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.travelplanner.data.local.TravelSecurityStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BiometricManagerImpl(
    private val context: Context,
    private val securityStorage: TravelSecurityStorage
) : BiometricManager {

    private val _authState = MutableStateFlow<BiometricAuthState>(BiometricAuthState.Idle)
    override val authState: StateFlow<BiometricAuthState> = _authState.asStateFlow()

    override fun checkAvailability(): SensorType {
        val manager = AndroidBiometricManager.from(context)
        val canAuth = manager.canAuthenticate(AndroidBiometricManager.Authenticators.BIOMETRIC_STRONG)

        if (canAuth != AndroidBiometricManager.BIOMETRIC_SUCCESS) return SensorType.UNSUPPORTED
        return detectHardwareSensor()
    }

    override fun authenticate(activity: FragmentActivity, reason: String) {
        if (_authState.value == BiometricAuthState.Authenticating) return

        if (checkAvailability() == SensorType.UNSUPPORTED) {
            _authState.value = BiometricAuthState.Unavailable
            return
        }

        _authState.value = BiometricAuthState.Authenticating

        val executor = ContextCompat.getMainExecutor(context)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                _authState.value = BiometricAuthState.Success
            }

            override fun onAuthenticationFailed() {
                // Невдала спроба (наприклад, приклали не той палець).
                // Діалог залишається відкритим, тому стан залишається Authenticating
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                _authState.value = when (errorCode) {
                    BiometricPrompt.ERROR_USER_CANCELED,
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON -> BiometricAuthState.Dismissed

                    BiometricPrompt.ERROR_HW_NOT_PRESENT,
                    BiometricPrompt.ERROR_HW_UNAVAILABLE,
                    BiometricPrompt.ERROR_NO_BIOMETRICS -> BiometricAuthState.Unavailable

                    else -> BiometricAuthState.Failed(errString.toString())
                }
            }
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Підтвердження особи")
            .setSubtitle(reason)
            .setNegativeButtonText("Скасувати")
            .setAllowedAuthenticators(AndroidBiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()

        BiometricPrompt(activity, executor, callback).authenticate(promptInfo)
    }

    override fun isEnabledByUser(): Boolean = securityStorage.isBiometricProtectionEnabled()

    override fun resetState() {
        _authState.value = BiometricAuthState.Idle
    }

    private fun detectHardwareSensor(): SensorType {
        val pm = context.packageManager
        val hasFingerprint = pm.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
        val hasFace = pm.hasSystemFeature("android.hardware.biometrics.face")
        val hasIris = pm.hasSystemFeature("android.hardware.biometrics.iris")

        return when {
            hasFingerprint && (hasFace || hasIris) -> SensorType.COMBINED
            hasFingerprint -> SensorType.TOUCH_ID
            hasFace -> SensorType.FACE_ID
            else -> SensorType.TOUCH_ID
        }
    }
}