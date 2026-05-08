package com.example.travelplanner.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.travelplanner.data.local.dao.TripDao
import com.example.travelplanner.data.local.entity.PlaceEntity
import com.example.travelplanner.data.local.entity.TripEntity
import android.content.Context
import androidx.room.Room
import com.example.travelplanner.data.local.dao.UserDao
import com.example.travelplanner.data.local.entity.UserEntity

/**
 * The main database class for the application
 * Stores local data about trips and locations
 */
@Database(
    entities = [TripEntity::class, PlaceEntity::class, UserEntity::class],
    version = 4,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tripDao(): TripDao
    abstract fun userDao(): UserDao

    companion object {
        private const val DATABASE_NAME = "travel_planner_db"
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // A method for obtaining a database instance. Uses the Singleton pattern to ensure that only one connection exists.
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}