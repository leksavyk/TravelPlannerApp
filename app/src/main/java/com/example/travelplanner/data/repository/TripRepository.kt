package com.example.travelplanner.data.repository

import com.example.travelplanner.data.local.dao.PackingDao
import com.example.travelplanner.data.local.dao.TripDao
import com.example.travelplanner.data.local.entity.PackingItemEntity
import com.example.travelplanner.data.local.entity.PlaceEntity
import com.example.travelplanner.data.mapper.toDomain
import com.example.travelplanner.data.mapper.toEntity
import com.example.travelplanner.data.model.SyncStatus
import com.example.travelplanner.data.model.Trip
import com.example.travelplanner.data.remote.MockTripApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class TripRepository(private val tripDao: TripDao, private val packingDao: PackingDao, private val apiService: MockTripApiService, private val userRepository: UserRepository) {
    @OptIn(ExperimentalCoroutinesApi::class)
    val allTrips: Flow<List<Trip>> = userRepository.currentUser.flatMapLatest { user ->
        if (user == null) {
            flowOf(emptyList())
        } else {
            tripDao.getTripsByUser(user.id).map { entities ->
                entities.map { it.toDomain() }
            }
        }
    }

    suspend fun getTripById(id: String): Trip? {
        return tripDao.getTripById(id)?.toDomain()
    }

    suspend fun saveTrip(trip: Trip, userId: String) {
        val tripEntity = trip.toEntity(status = SyncStatus.PENDING, ownerId = userId)

        tripDao.insertTrip(tripEntity)

        trip.places.forEach { place ->
            val placeEntity = PlaceEntity(
                id = place.id.toString(),
                tripId = tripEntity.id,
                name = place.name,
                isVisited = place.isVisited
            )
            tripDao.insertPlace(placeEntity)
        }

        try {
            val isSynced = apiService.syncTripWithServer(tripEntity)
            if (isSynced) {
                tripDao.updateSyncStatus(tripEntity.id, SyncStatus.SYNCED)
            }
        } catch (e: Exception) {
            tripDao.updateSyncStatus(tripEntity.id, SyncStatus.ERROR)
        }
    }

    suspend fun deleteTrip(trip: Trip, userId: String) {
        val entity = trip.toEntity(ownerId = userId)

        try {
            val isDeletedOnServer = apiService.deleteTripFromServer(entity.id)

            if (isDeletedOnServer) {
                tripDao.deleteTrip(entity)
                // println("DEBUG: Видалено всюди")
            } else {
                tripDao.updateSyncStatus(entity.id, SyncStatus.ERROR)
            }
        } catch (e: Exception) {
            // OFFLINE-FIRST: якщо мережі немає
            tripDao.updateSyncStatus(entity.id, SyncStatus.ERROR)
            tripDao.deleteTrip(entity)
            // println("DEBUG: Видалено локально, але сервер не відповів. Потрібна синхронізація пізніше")
        }
    }

    fun getPackingItems(tripId: String): Flow<List<PackingItemEntity>> {
        return packingDao.getItemsForTrip(tripId)
    }

    suspend fun addPackingItem(item: PackingItemEntity) {
        packingDao.insertItem(item)
    }

    suspend fun updatePackingItemStatus(itemId: String, isChecked: Boolean) {
        packingDao.updateCheckStatus(itemId, isChecked)
    }

    suspend fun deletePackingItem(item: PackingItemEntity) {
        packingDao.deleteItem(item)
    }
}