package com.example.travelplanner.data.local

import androidx.room.TypeConverter
import com.example.travelplanner.data.model.PackingCategory
import com.example.travelplanner.data.model.SyncStatus

/**
 * Room type converters for custom data types
 */
class Converters {
    @TypeConverter
    fun fromSyncStatus(status: SyncStatus): String = status.name

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus = SyncStatus.valueOf(value)

    @TypeConverter
    fun fromPackingCategory(value: PackingCategory): String {
        return value.name
    }

    @TypeConverter
    fun toPackingCategory(value: String): PackingCategory {
        return PackingCategory.valueOf(value)
    }
}