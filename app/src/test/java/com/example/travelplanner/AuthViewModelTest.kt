package com.example.travelplanner

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.travelplanner.ui.viewmodel.AuthViewModel
import com.example.travelplanner.ui.viewmodel.AuthState
import com.example.travelplanner.data.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val userRepository = mockk<UserRepository>(relaxed = true)
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel(userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- LOGIN TESTS ---

    @Test
    fun `login sets Success state when credentials are correct`() = runTest {
        coEvery { userRepository.login("test@mail.com", "1234") } returns true

        viewModel.login("test@mail.com", "1234")

        advanceUntilIdle()

        assertEquals(AuthState.Success, viewModel.authState.value)
    }

    @Test
    fun `login sets Error state when credentials are wrong`() = runTest {
        coEvery { userRepository.login(any(), any()) } returns false

        viewModel.login("wrong@mail.com", "wrong")
        advanceUntilIdle()

        assertTrue(viewModel.authState.value is AuthState.Error)
        assertEquals("Невірний email або пароль", (viewModel.authState.value as AuthState.Error).message)
    }

    // --- REGISTRATION TESTS ---

    @Test
    fun `register sets Success state on successful repository call`() = runTest {
        viewModel.register("Sasha", "sasha@mail.com", "password")
        advanceUntilIdle()

        coVerify { userRepository.registerUser("Sasha", "sasha@mail.com", "password") }
        assertEquals(AuthState.Success, viewModel.authState.value)
    }

    @Test
    fun `register sets Error state when repository throws exception`() = runTest {
        coEvery { userRepository.registerUser(any(), any(), any()) } throws Exception("Email already exists")

        viewModel.register("Sasha", "sasha@mail.com", "password")
        advanceUntilIdle()

        assertTrue(viewModel.authState.value is AuthState.Error)
        val state = viewModel.authState.value as AuthState.Error
        assertTrue(state.message.contains("Email already exists"))
    }

    // --- LOGOUT & STATE TESTS ---

    @Test
    fun `logout calls repository and resets state to Idle`() = runTest {
        viewModel.logout("user_123")
        advanceUntilIdle()

        coVerify { userRepository.logout("user_123") }
        assertEquals(AuthState.Idle, viewModel.authState.value)
    }

    @Test
    fun `resetState changes state to Idle`() {
        viewModel.resetState()

        assertEquals(AuthState.Idle, viewModel.authState.value)
    }
}