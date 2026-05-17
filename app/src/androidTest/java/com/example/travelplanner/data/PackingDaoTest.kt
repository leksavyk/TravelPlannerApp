package com.example.travelplanner.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.travelplanner.data.local.AppDatabase
import com.example.travelplanner.data.local.dao.PackingDao
import com.example.travelplanner.data.local.entity.PackingItemEntity
import com.example.travelplanner.data.model.PackingCategory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PackingDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: PackingDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .allowMainThreadQueries()
            .build()

        db.openHelper.writableDatabase.execSQL("PRAGMA foreign_keys = OFF;")

        dao = db.packingDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndReadItem() = runBlocking {
        val item = PackingItemEntity("1", "trip_A", "Passport", PackingCategory.DOCUMENTS, false)
        dao.insertItem(item)
        val items = dao.getItemsForTrip("trip_A").first()
        assertEquals(1, items.size)
        assertEquals("Passport", items[0].name)
    }

    @Test
    fun updateCheckStatus() = runBlocking {
        val item = PackingItemEntity("1", "trip_A", "Passport", PackingCategory.DOCUMENTS, false)
        dao.insertItem(item)
        dao.updateCheckStatus("1", true)
        val items = dao.getItemsForTrip("trip_A").first()
        assertEquals(true, items[0].isChecked)
    }

    @Test
    fun deleteItem() = runBlocking {
        val item = PackingItemEntity("1", "trip_A", "Passport", PackingCategory.DOCUMENTS, false)
        dao.insertItem(item)
        dao.deleteById("1")
        val items = dao.getItemsForTrip("trip_A").first()
        assertEquals(0, items.size)
    }

    @Test
    fun updateItemName() = runBlocking {
        val item = PackingItemEntity("1", "trip_A", "Old Name", PackingCategory.OTHER, false)
        dao.insertItem(item)
        dao.updateItemName("1", "New Name")
        val items = dao.getItemsForTrip("trip_A").first()
        assertEquals("New Name", items[0].name)
    }
}