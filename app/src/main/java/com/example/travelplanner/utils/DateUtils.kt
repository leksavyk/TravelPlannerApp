package com.example.travelplanner.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Date.formatToUk(): String {
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale("uk", "UA"))
    return formatter.format(this)
}

fun getDaysInMonth(monthOffset: Int): List<Date?> {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.MONTH, monthOffset)
    calendar.set(Calendar.DAY_OF_MONTH, 1)

    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 2 // Корекція під понеділок

    val days = mutableListOf<Date?>()
    repeat(if (firstDayOfWeek < 0) 6 else firstDayOfWeek) { days.add(null) }

    repeat(daysInMonth) {
        days.add(calendar.time)
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }
    return days
}

/**
 * Допоміжна функція для гарного форматування діапазону дат
 */
fun formatDateRange(start: Date, end: Date): String {
    val dayFormat = SimpleDateFormat("d", Locale("uk"))
    val monthFormat = SimpleDateFormat("MMMM", Locale("uk"))

    val calendarStart = Calendar.getInstance().apply { time = start }
    val calendarEnd = Calendar.getInstance().apply { time = end }

    return if (calendarStart.get(Calendar.MONTH) == calendarEnd.get(Calendar.MONTH)) {
        // Якщо один місяць: "30 – 31 травня"
        "${dayFormat.format(start)} – ${dayFormat.format(end)} ${monthFormat.format(end)}"
    } else {
        // Якщо різні місяці: "30 травня – 1 червня"
        "${dayFormat.format(start)} ${monthFormat.format(start)} – ${dayFormat.format(end)} ${monthFormat.format(end)}"
    }
}