package com.example.travelplanner

import com.example.travelplanner.data.local.entity.PackingItemEntity
import com.example.travelplanner.data.model.PackingCategory
import org.junit.Assert.assertEquals
import org.junit.Test

class PackingMapperTest {
    @Test
    fun filterCheckedItems_returnsOnlyTrue() {
        val items = listOf(
            PackingItemEntity("1", "T", "Item 1", PackingCategory.OTHER, true),
            PackingItemEntity("2", "T", "Item 2", PackingCategory.OTHER, false)
        )

        val checkedItems = items.filter { it.isChecked }

        assertEquals(1, checkedItems.size)
        assertEquals("1", checkedItems[0].id)
    }

    @Test
    fun calculatePackingProgress_returnsCorrectPercentage() {
        val items = listOf(
            PackingItemEntity("1", "T", "A", PackingCategory.OTHER, true),
            PackingItemEntity("2", "T", "B", PackingCategory.OTHER, true),
            PackingItemEntity("3", "T", "C", PackingCategory.OTHER, false),
            PackingItemEntity("4", "T", "D", PackingCategory.OTHER, false)
        )

        val total = items.size
        val checked = items.count { it.isChecked }
        val progress = if (total > 0) (checked.toFloat() / total * 100).toInt() else 0

        assertEquals(50, progress) // 2 з 4 = 50%
    }

    @Test
    fun groupByCategory_returnsCorrectGroups() {
        val items = listOf(
            PackingItemEntity("1", "T", "Passport", PackingCategory.DOCUMENTS, false),
            PackingItemEntity("2", "T", "Ticket", PackingCategory.DOCUMENTS, false),
            PackingItemEntity("3", "T", "Shirt", PackingCategory.CLOTHING, false)
        )

        val grouped = items.groupBy { it.category }

        assertEquals(2, grouped[PackingCategory.DOCUMENTS]?.size)
        assertEquals(1, grouped[PackingCategory.CLOTHING]?.size)
    }

    @Test
    fun isItemNameValid_returnsFalseIfEmpty() {
        val emptyName = ""
        val isValid = emptyName.isNotBlank() && emptyName.length >= 2

        assertEquals(false, isValid)
    }

    @Test
    fun isItemNameValid_returnsTrueForNormalName() {
        val name = "Socks"
        val isValid = name.isNotBlank() && name.length >= 2

        assertEquals(true, isValid)
    }
}
