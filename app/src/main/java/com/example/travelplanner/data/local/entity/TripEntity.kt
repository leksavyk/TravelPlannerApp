package com.example.travelplanner.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.travelplanner.data.model.SyncStatus

/**
 * Room entity that persists one trip
 * Each trip can contain multiple PlaceEntity records linked via tripId
 */
@Entity(
    tableName = "trips",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TripEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val budget: Double,
    val date: Long,
    val isCompleted: Boolean = false,
    val syncStatus: SyncStatus = SyncStatus.PENDING
)