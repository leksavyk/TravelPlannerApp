package com.example.travelplanner.data.local.dao

import androidx.room.*
import com.example.travelplanner.data.local.entity.PackingItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PackingDao {
    @Query("SELECT * FROM packing_items WHERE tripId = :tripId")
    fun getItemsForTrip(tripId: String): Flow<List<PackingItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: PackingItemEntity)

    @Update
    suspend fun updateItem(item: PackingItemEntity)

    @Delete
    suspend fun deleteItem(item: PackingItemEntity)
}