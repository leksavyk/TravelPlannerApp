package com.example.travelplanner.data.local.dao

import androidx.room.*
import com.example.travelplanner.data.local.entity.PlaceEntity
import com.example.travelplanner.data.local.entity.TripEntity
import com.example.travelplanner.data.local.entity.TripWithPlaces
import com.example.travelplanner.data.model.SyncStatus
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for managing trips and their places in Room.
 *
 * Provides methods to:
 * - Retrieve all trips or a specific trip by id
 * - Insert, update, and delete trips
 * - Update synchronization status for a trip
 * - Retrieve places linked to a trip
 * - Insert and delete places
 */
@Dao
interface TripDao {
//    @Query("SELECT * FROM trips ORDER BY date DESC")
//    fun getAllTrips(): Flow<List<TripEntity>>

    @Transaction
    @Query("SELECT * FROM trips ORDER BY date DESC")
    fun getAllTrips(): Flow<List<TripWithPlaces>>

//    @Query("SELECT * FROM trips WHERE id = :id")
//    suspend fun getTripById(id: String): TripEntity?

    @Transaction
    @Query("SELECT * FROM trips WHERE id = :id")
    suspend fun getTripById(id: String): TripWithPlaces?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity)

    @Delete
    suspend fun deleteTrip(trip: TripEntity)

    @Query("UPDATE trips SET syncStatus = :newStatus WHERE id = :tripId")
    suspend fun updateSyncStatus(tripId: String, newStatus: SyncStatus)

    @Query("SELECT * FROM places WHERE tripId = :tripId")
    fun getPlacesForTrip(tripId: String): Flow<List<PlaceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: PlaceEntity)

    @Delete
    suspend fun deletePlace(place: PlaceEntity)

    @Query("DELETE FROM places WHERE tripId = :tripId")
    suspend fun deletePlacesForTrip(tripId: String)
}