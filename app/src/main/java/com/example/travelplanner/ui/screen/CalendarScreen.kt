package com.example.travelplanner.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import com.example.travelplanner.data.model.Trip
import com.example.travelplanner.ui.viewmodel.TripViewModel
import com.example.travelplanner.utils.getDaysInMonth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.compose.ui.platform.LocalLocale
import com.example.travelplanner.utils.getTripColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(viewModel: TripViewModel, navController: NavController) {
    val trips by viewModel.sortedTrips.collectAsState()
    var monthOffset by remember { mutableIntStateOf(0) }

    val displayMonthCalendar = remember(monthOffset) {
        Calendar.getInstance().apply {
            add(Calendar.MONTH, monthOffset)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
        }
    }

    val startOfMonth = displayMonthCalendar.timeInMillis

    val endOfMonth = remember(monthOffset) {
        val cal = displayMonthCalendar.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.timeInMillis
    }

    val tripsInThisMonth = remember(trips, monthOffset) {
        trips.filter { trip ->
            trip.startDate.time <= endOfMonth && trip.endDate.time >= startOfMonth
        }
    }

    val days = remember(monthOffset) { getDaysInMonth(monthOffset) }

    val calendar = Calendar.getInstance().apply { add(Calendar.MONTH, monthOffset) }
    val monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("uk"))

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Календар") })
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {
            // Перемикач місяців
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { monthOffset-- }) { Icon(Icons.Default.ArrowBack, "Минулий") }
                Text(
                    text = "$monthName ${calendar.get(Calendar.YEAR)}",
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = { monthOffset++ }) {
                    Icon(
                        Icons.Default.ArrowForward,
                        "Наступний"
                    )
                }
            }

            Row(Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)) {
                listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Нд").forEach { day ->
                    Text(
                        day,
                        Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .padding(16.dp)
                    .height(300.dp)
            ) {
                items(days) { date ->
                    if (date != null) {
                        DayCell(date = date, trips = trips)
                    } else {
                        Box(modifier = Modifier.aspectRatio(1f))
                    }
                }
            }

            Text("Подорожі", Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
            LazyColumn(Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)) {
                if (tripsInThisMonth.isEmpty()) {
                    item {
                        Text(
                            "На цей місяць планів немає",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp),
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    }
                } else {
                    items(tripsInThisMonth) { trip ->
                        CalendarTripItem(
                            trip = trip,
                            onClick = {
                                navController.navigate("trip_detail/${trip.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DayCell(date: Date, trips: List<Trip>) {
    val activeTrip = trips.find { trip ->
        val dayTime = date.time
        dayTime >= trip.startDate.time && dayTime <= trip.endDate.time
    }

    val backgroundColor = activeTrip?.let { getTripColor(it.id.toString()) } ?: Color.Transparent
    val textColor = if (activeTrip != null) Color.White else Color.Black

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = SimpleDateFormat("d", LocalLocale.current.platformLocale).format(date),
            color = textColor,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
