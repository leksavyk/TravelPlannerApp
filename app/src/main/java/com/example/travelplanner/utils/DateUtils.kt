package com.example.travelplanner.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.formatToUk(): String {
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale("uk", "UA"))
    return formatter.format(this)
}