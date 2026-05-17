package com.example.travelplanner.ui.components

import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.travelplanner.data.model.PackingCategory

@Composable
fun CategoryTabs(
    categories: List<PackingCategory>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val allTabs = listOf("Усі") + categories.map { it.displayName }
    val selectedIndex = allTabs.indexOf(selectedCategory).coerceAtLeast(0)

    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        containerColor = Color.Transparent,
        edgePadding = 16.dp,
        divider = {},
        indicator = { tabPositions ->
            if (selectedIndex < tabPositions.size) {
                SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                    color = Color(0xFF8E6CEF)
                )
            }
        }
    ) {
        allTabs.forEach { categoryTitle ->
            val isSelected = selectedCategory == categoryTitle
            Tab(
                selected = isSelected,
                onClick = { onCategorySelected(categoryTitle) },
                text = {
                    Text(
                        text = categoryTitle,
                        color = if (isSelected) Color(0xFF8E6CEF) else Color.Gray,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }
    }
}
