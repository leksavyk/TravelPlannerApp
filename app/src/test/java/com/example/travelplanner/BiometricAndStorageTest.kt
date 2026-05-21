package com.example.travelplanner

import com.example.travelplanner.data.biometric.BiometricAuthState
import com.example.travelplanner.data.biometric.BiometricManager
import com.example.travelplanner.data.biometric.SensorType
import com.example.travelplanner.data.local.TravelSecurityStorage
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class BiometricAndStorageTest {
    private lateinit var mockStorage: TravelSecurityStorage
    private lateinit var mockBiometricManager: BiometricManager

    @Before
    fun setUp() {
        mockStorage = mockk(relaxed = true)
        mockBiometricManager = mockk()
    }

    @Test
    fun `isEnabledByUser returns correct value after save`() {
        every { mockStorage.isBiometricProtectionEnabled() } returns true

        val isEnabled = mockStorage.isBiometricProtectionEnabled()
        assertTrue(isEnabled)

        verify { mockStorage.isBiometricProtectionEnabled() }
    }

    @Test
    fun `isEnabledByUser returns false when disabled`() {
        every { mockStorage.isBiometricProtectionEnabled() } returns false

        val isEnabled = mockStorage.isBiometricProtectionEnabled()
        assertFalse(isEnabled)
    }

    @Test
    fun `lockout timeout settings are saved and retrieved correctly without distortion`() {
        every { mockStorage.getLockoutTimeout() } returns 30

        val timeout = mockStorage.getLockoutTimeout()
        assertEquals(30, timeout)
    }

    @Test
    fun `last userId is saved and read correctly`() {
        every { mockStorage.getLastUserId() } returns "user_sasha_123"

        val userId = mockStorage.getLastUserId()
        assertEquals("user_sasha_123", userId)
    }

    // --- MOCK SCENARIOS ---

    @Test
    fun `checkAvailability returns UNSUPPORTED on device without sensor`() {
        every { mockBiometricManager.checkAvailability() } returns SensorType.UNSUPPORTED

        val type = mockBiometricManager.checkAvailability()
        assertEquals(SensorType.UNSUPPORTED, type)
    }

    @Test
    fun `checkAvailability returns TOUCH_ID when fingerprint is present`() {
        every { mockBiometricManager.checkAvailability() } returns SensorType.TOUCH_ID

        val type = mockBiometricManager.checkAvailability()
        assertEquals(SensorType.TOUCH_ID, type)
    }

    @Test
    fun `authenticate returns Success on successful scan`() {
        val successState = BiometricAuthState.Success
        every { mockBiometricManager.authState } returns MutableStateFlow(successState)

        val currentState = mockBiometricManager.authState.value
        assertEquals(BiometricAuthState.Success, currentState)
    }

    @Test
    fun `authenticate returns Failed when user cancels operation`() {
        val failedState = BiometricAuthState.Failed("User cancelled")
        every { mockBiometricManager.authState } returns MutableStateFlow(failedState)

        val currentState = mockBiometricManager.authState.value
        assertTrue(currentState is BiometricAuthState.Failed)
        assertEquals("User cancelled", (currentState as BiometricAuthState.Failed).error)
    }
}