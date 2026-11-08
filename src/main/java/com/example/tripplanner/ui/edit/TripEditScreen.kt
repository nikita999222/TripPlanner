package com.example.tripplanner.ui.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.tripplanner.data.Trip
import com.example.tripplanner.data.TripRepository
import com.example.tripplanner.ui.CURRENCY
import com.example.tripplanner.ui.MILLIS_PER_DAY
import com.example.tripplanner.ui.daysCount
import com.example.tripplanner.ui.daysWord
import com.example.tripplanner.ui.fmtDay
import com.example.tripplanner.ui.todayEpoch
import com.example.tripplanner.ui.toDoubleOrNullRu
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateButton(label: String, epochDay: Long, onChange: (Long) -> Unit) {
    var show by remember { mutableStateOf(false) }

    OutlinedButton(
        onClick = { show = true },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("$label: ${fmtDay(epochDay)}")
    }

    if (show) {
        val state = rememberDatePickerState(initialSelectedDateMillis = epochDay * MILLIS_PER_DAY)
        DatePickerDialog(
            onDismissRequest = { show = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { onChange(Math.floorDiv(it, MILLIS_PER_DAY)) }
                    show = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { show = false }) { Text("Отмена") }
            }
        ) {
            DatePicker(state = state)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripEditScreen(
    repo: TripRepository,
    tripId: Long,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val scheme = MaterialTheme.colorScheme
    val today = remember { todayEpoch() }

    var title by rememberSaveable { mutableStateOf("") }
    var destination by rememberSaveable { mutableStateOf("") }
    var startDay by rememberSaveable { mutableLongStateOf(today) }
    var endDay by rememberSaveable { mutableLongStateOf(today + 6) }
    var budget by rememberSaveable { mutableStateOf("") }
    var loaded by rememberSaveable { mutableStateOf(false) }

    // Редактирование: подгружаем существующую поездку один раз
    LaunchedEffect(tripId) {
        if (tripId >= 0 && !loaded) {
            repo.getTrip(tripId)?.let { t ->
                title = t.title
                destination = t.destination
                startDay = t.startDay
                endDay = t.endDay
                budget = if (t.budget > 0.0) t.budget.toLong().toString() else ""
            }
            loaded = true
        }
    }

    val valid = title.isNotBlank() && endDay >= startDay

    Scaffold(
        containerColor = scheme.background,
        topBar = {
            TopAppBar(
                title = { Text(if (tripId >= 0) "Редактирование" else "Новая поездка") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = scheme.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Название поездки") },
                placeholder = { Text("Например, Отпуск в Сочи") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = destination,
                onValueChange = { destination = it },
                label = { Text("Куда едем") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            DateButton("Начало", startDay) { picked ->
                startDay = picked
                if (endDay < picked) endDay = picked
            }
            DateButton("Конец", endDay) { picked -> endDay = picked }

            if (endDay < startDay) {
                Text(
                    "Дата окончания не может быть раньше начала",
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.error
                )
            } else {
                Text(
                    "Длительность: ${daysWord(daysCount(startDay, endDay))}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurfaceVariant
                )
            }

            OutlinedTextField(
                value = budget,
                onValueChange = { budget = it },
                label = { Text("Бюджет, $CURRENCY (необязательно)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                enabled = valid,
                onClick = {
                    scope.launch {
                        val budgetValue = budget.toDoubleOrNullRu() ?: 0.0
                        if (tripId >= 0) {
                            val base = repo.getTrip(tripId)
                            if (base != null) {
                                repo.updateTrip(
                                    base.copy(
                                        title = title.trim(),
                                        destination = destination.trim(),
                                        startDay = startDay,
                                        endDay = endDay,
                                        budget = budgetValue
                                    )
                                )
                            }
                        } else {
                            repo.insertTrip(
                                Trip(
                                    title = title.trim(),
                                    destination = destination.trim(),
                                    startDay = startDay,
                                    endDay = endDay,
                                    budget = budgetValue
                                )
                            )
                        }
                        onBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(if (tripId >= 0) "Сохранить" else "Создать поездку")
            }
        }
    }
}
