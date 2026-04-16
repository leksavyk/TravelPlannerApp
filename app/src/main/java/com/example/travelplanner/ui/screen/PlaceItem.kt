package com.example.travelplanner.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import com.example.travelplanner.data.model.Place

@Composable
fun PlaceItem(place: Place, onCheckedChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        onClick = { onCheckedChange(!place.isVisited) }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = place.name, style = MaterialTheme.typography.titleMedium)
//                Text(text = "Рейтинг: ${place.rating} ⭐", style = MaterialTheme.typography.bodySmall)
            }
            Checkbox(
                checked = place.isVisited,
                onCheckedChange = { onCheckedChange(it) }
            )
        }
    }
}