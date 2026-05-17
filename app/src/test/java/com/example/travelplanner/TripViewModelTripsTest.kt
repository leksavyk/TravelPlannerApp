package com.example.travelplanner

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.travelplanner.data.repository.TripRepository
import com.example.travelplanner.data.repository.UserRepository
import com.example.travelplanner.data.websocket.ConnectionState
import com.example.travelplanner.data.websocket.ISocketManager
import com.example.travelplanner.ui.viewmodel.TripViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import kotlinx.coroutines.test.*
import org.junit.*

class TripViewModelTripsTest {
    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()
    private val repository = mockk<TripRepository>(relaxed = true)
    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val socketManager = mockk<ISocketManager>(relaxed = true)
    private lateinit var viewModel: TripViewModel

    private val fakeUserFlow = MutableStateFlow(null)
    private val fakeSocketMessages = MutableSharedFlow<String>(replay = 1)
    private val fakeSocketState = MutableStateFlow(ConnectionState.Disconnected)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { userRepository.currentUser } returns fakeUserFlow
        every { socketManager.messages } returns fakeSocketMessages
        every { socketManager.state } returns fakeSocketState
        every { repository.allTrips } returns flowOf(emptyList())

        viewModel = TripViewModel(repository, userRepository, socketManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `addTrip does not save if user is null`() = runTest {
        viewModel.addTrip(mockk())
        advanceUntilIdle()
        coVerify(exactly = 0) { repository.saveTrip(any(), any()) }
    }

    @Test
    fun `deleteTrip prevents action without active user`() = runTest {
        viewModel.deleteTrip(mockk())
        advanceUntilIdle()
        coVerify(exactly = 0) { repository.deleteTrip(any(), any()) }
    }

    @Test
    fun `sortedTrips is initially empty`() {
        assertEquals(0, viewModel.sortedTrips.value.size)
    }
}