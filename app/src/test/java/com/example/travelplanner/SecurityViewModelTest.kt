package com.example.travelplanner

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.travelplanner.data.biometric.AppLockController
import com.example.travelplanner.data.biometric.BiometricAuthState
import com.example.travelplanner.data.biometric.BiometricManager
import com.example.travelplanner.data.biometric.SensorType
import com.example.travelplanner.data.local.TravelSecurityStorage
import com.example.travelplanner.ui.viewmodel.SecurityViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SecurityViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockBiometricManager: BiometricManager
    private lateinit var mockStorage: TravelSecurityStorage
    private lateinit var mockLockController: AppLockController
    private lateinit var viewModel: SecurityViewModel

    private val fakeAuthStateFlow = MutableStateFlow<BiometricAuthState>(BiometricAuthState.Idle)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockBiometricManager = mockk()
        mockStorage =
            mockk(relaxed = true)
        mockLockController = mockk(relaxed = true)

        every { mockBiometricManager.checkAvailability() } returns SensorType.TOUCH_ID
        every { mockBiometricManager.authState } returns fakeAuthStateFlow
        every { mockStorage.isBiometricProtectionEnabled() } returns true
        every { mockStorage.getLockoutTimeout() } returns 15

        viewModel = SecurityViewModel(mockBiometricManager, mockStorage, mockLockController)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `UI state initializes correctly from storage parameters`() {
        assertEquals(SensorType.TOUCH_ID, viewModel.sensorType.value)
        assertEquals(true, viewModel.isBiometricEnabled.value)
        assertEquals(15, viewModel.lockTimeoutSeconds.value)
    }

    @Test
    fun `UI state transitions to Authenticating after authenticate call triggered`() {
        fakeAuthStateFlow.value = BiometricAuthState.Authenticating

        val currentState = viewModel.biometricAuthState.value
        assertEquals(BiometricAuthState.Authenticating, currentState)
    }

    @Test
    fun `UI state transitions to Success after biometric auth completes successfully`() {
        fakeAuthStateFlow.value = BiometricAuthState.Success

        val currentState = viewModel.biometricAuthState.value
        assertEquals(BiometricAuthState.Success, currentState)
    }

    @Test
    fun `toggleBiometricProtection sets disabled and clears userId on false status`() {
        viewModel.toggleBiometricProtection(false, null)

        verify { mockStorage.setBiometricProtectionEnabled(false) }
        verify { mockStorage.saveLastUserId(null) }
        verify { mockLockController.lockSessionManually() }
    }

    @Test
    fun `toggleBiometricProtection saves userId into storage when enabled`() {
        viewModel.toggleBiometricProtection(true, "user_sasha")

        verify { mockStorage.setBiometricProtectionEnabled(true) }
        verify { mockStorage.saveLastUserId("user_sasha") }
    }

    @Test
    fun `updateLockTimeout propagates changes directly to secure storage`() {
        viewModel.updateLockTimeout(300)

        verify { mockStorage.setLockoutTimeout(300) }
        assertEquals(300, viewModel.lockTimeoutSeconds.value)
    }
}