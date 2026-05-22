package com.example.travelplanner

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.travelplanner.data.repository.TripRepository
import com.example.travelplanner.data.repository.UserRepository
import com.example.travelplanner.data.websocket.ConnectionState
import com.example.travelplanner.data.websocket.ISocketManager
import com.example.travelplanner.ui.viewmodel.TripViewModel
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import kotlinx.coroutines.test.*
import org.junit.*
import io.mockk.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertTrue

class TripViewModelNotificationsTest {
    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()
    private val socketManager = mockk<ISocketManager>(relaxed = true)
    private val tripRepository = mockk<TripRepository>(relaxed = true)
    private val userRepository = mockk<UserRepository>(relaxed = true)
    private lateinit var viewModel: TripViewModel

    private val fakeUserFlow = MutableStateFlow(null)
    private val fakeMessagesFlow = MutableSharedFlow<String>(replay = 1)
    private val fakeSocketState = MutableStateFlow(ConnectionState.Disconnected)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { userRepository.currentUser } returns fakeUserFlow

        every { socketManager.messages } returns fakeMessagesFlow
        every { socketManager.state } returns fakeSocketState

        every { tripRepository.allTrips } returns flowOf(emptyList())

        viewModel = TripViewModel(
            tripRepository,
            userRepository,
            socketManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `clearNotifications empties the list`() = runTest {
        viewModel.clearNotifications()
        assertEquals(0, viewModel.notifications.value.size)
    }

    @Test
    fun `markAllAsRead updates all items`() = runTest {
        viewModel.markAllAsRead()
        assertTrue(viewModel.notifications.value.all { it.isRead })
    }

    @Test
    fun `startWebSocket connects only if disconnected`() = runTest {
        every { socketManager.state } returns MutableStateFlow(ConnectionState.Disconnected)
        viewModel.startWebSocket()
        advanceUntilIdle()
        coVerify { socketManager.connect(any()) }
    }
}
