package com.example.tripplanner.ui.trips

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tripplanner.ui.AppCard
import com.example.tripplanner.ui.EmptyState
import com.example.tripplanner.ui.IconBadge
import com.example.tripplanner.ui.InfoChip
import com.example.tripplanner.ui.TripPhase
import com.example.tripplanner.ui.daysCount
import com.example.tripplanner.ui.daysWord
import com.example.tripplanner.ui.fmtMoney
import com.example.tripplanner.ui.fmtRange
import com.example.tripplanner.ui.tripStatus

@Composable
fun TripListScreen(
    vm: TripsViewModel,
    onOpen: (Long) -> Unit,
    onAdd: () -> Unit
) {
    val items by vm.items.collectAsStateWithLifecycle()
    val scheme = MaterialTheme.colorScheme

    Scaffold(
        containerColor = scheme.background,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAdd,
                containerColor = scheme.primary,
                contentColor = scheme.onPrimary,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Поездка") }
            )
        }
    ) { padding ->
        if (items.isEmpty()) {
            EmptyState(
                icon = Icons.Filled.Flight,
                title = "Пока нет поездок",
                subtitle = "Нажмите «Поездка», чтобы спланировать маршрут, бюджет и список вещей.",
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 96.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(key = "header") {
                    Column(Modifier.padding(bottom = 4.dp)) {
                        Text("Мои поездки", style = MaterialTheme.typography.headlineMedium)
                        Text(
                            "Планируйте маршрут, бюджет и вещи в одном месте",
                            style = MaterialTheme.typography.bodyMedium,
                            color = scheme.onSurfaceVariant
                        )
                    }
                }

                items(items, key = { it.trip.id }) { item ->
                    val trip = item.trip
                    val status = tripStatus(trip)
                    val days = daysCount(trip.startDay, trip.endDay)

                    AppCard(
                        onClick = { onOpen(trip.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                IconBadge(icon = Icons.Filled.Flight)
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        trip.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 1
                                    )
                                    if (trip.destination.isNotBlank()) {
                                        Text(
                                            trip.destination,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = scheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }
                                }
                                when (status.phase) {
                                    TripPhase.UPCOMING -> InfoChip(
                                        text = status.text,
                                        container = scheme.primaryContainer,
                                        content = scheme.onPrimaryContainer
                                    )
                                    TripPhase.ACTIVE -> InfoChip(
                                        text = status.text,
                                        container = scheme.tertiaryContainer,
                                        content = scheme.onTertiaryContainer
                                    )
                                    TripPhase.DONE -> InfoChip(text = status.text)
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                InfoChip(fmtRange(trip.startDay, trip.endDay))
                                InfoChip(daysWord(days))
                            }

                            if (trip.budget > 0.0) {
                                val over = item.spent > trip.budget
                                val progress = (item.spent / trip.budget).coerceIn(0.0, 1.0).toFloat()
                                Column {
                                    LinearProgressIndicator(
                                        progress = { progress },
                                        modifier = Modifier.fillMaxWidth(),
                                        color = if (over) scheme.error else scheme.primary,
                                        trackColor = scheme.surfaceVariant
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        "Потрачено ${fmtMoney(item.spent)} из ${fmtMoney(trip.budget)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (over) scheme.error else scheme.onSurfaceVariant
                                    )
                                }
                            } else if (item.spent > 0.0) {
                                Text(
                                    "Потрачено ${fmtMoney(item.spent)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = scheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
