package com.example.travelplanner.utils

import androidx.compose.ui.graphics.Color

val tripColors = listOf(
    Color(0xFF8E6CEF), // Фіолетовий
    Color(0xFF5CC8FF), // Блакитний
    Color(0xFFFFB156), // Помаранчевий
    Color(0xFF4CAF50), // Зелений
    Color(0xFFFF5E5E), // Червоний
    Color(0xFFF06292)  // Рожевий
)

fun getTripColor(tripId: String): Color {
    val index = Math.abs(tripId.hashCode()) % tripColors.size
    return tripColors[index]
}