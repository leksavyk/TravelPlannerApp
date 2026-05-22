package com.example.travelplanner

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.travelplanner.data.local.entity.PackingItemEntity
import com.example.travelplanner.data.model.PackingCategory
import com.example.travelplanner.data.repository.TripRepository
import com.example.travelplanner.data.repository.UserRepository
import com.example.travelplanner.data.websocket.ConnectionState
import com.example.travelplanner.data.websocket.ISocketManager
import com.example.travelplanner.ui.viewmodel.TripViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import kotlinx.coroutines.test.*
import org.junit.*
import io.mockk.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class TripViewModelPackingTest {
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
    fun `loadPackingItems updates state correctly`() = runTest {
        val mockItems = listOf(PackingItemEntity("1", "T1", "Passport", PackingCategory.DOCUMENTS))
        every { repository.getPackingItems("T1") } returns flowOf(mockItems)

        viewModel.loadPackingItems("T1")
        advanceUntilIdle()
        assertEquals(mockItems, viewModel.packingItems.value)
    }

    @Test
    fun `addPackingItem calls repository insert`() = runTest {
        viewModel.addPackingItem("trip_1", "Money", PackingCategory.OTHER)
        advanceUntilIdle()

        coVerify { repository.addPackingItem(any()) }
    }

    @Test
    fun `togglePackingItem calls repository update`() = runTest {
        viewModel.togglePackingItem("item_1", true)
        advanceUntilIdle()
        coVerify { repository.updatePackingItemStatus("item_1", true) }
    }

    @Test
    fun `deletePackingItem calls repository delete by ID`() = runTest {
        viewModel.deletePackingItem("id_123")
        advanceUntilIdle()
        coVerify { repository.deletePackingItemById("id_123") }
    }

    @Test
    fun `updatePackingItemName calls repository`() = runTest {
        viewModel.updatePackingItemName("id1", "New Name")
        advanceUntilIdle()

        coVerify { repository.updatePackingItemName("id1", "New Name") }
    }
}
