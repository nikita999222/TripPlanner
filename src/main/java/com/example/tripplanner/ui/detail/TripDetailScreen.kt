package com.example.tripplanner.ui.detail

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tripplanner.data.Expense
import com.example.tripplanner.data.PackItem
import com.example.tripplanner.data.Place
import com.example.tripplanner.data.Trip
import com.example.tripplanner.ui.AppCard
import com.example.tripplanner.ui.EmptyState
import com.example.tripplanner.ui.HeroCard
import com.example.tripplanner.ui.HeroStat
import com.example.tripplanner.ui.IconBadge
import com.example.tripplanner.ui.InfoChip
import com.example.tripplanner.ui.daysCount
import com.example.tripplanner.ui.daysWord
import com.example.tripplanner.ui.fmtMoney
import com.example.tripplanner.ui.fmtRange
import com.example.tripplanner.ui.fmtWeekday
import com.example.tripplanner.ui.tripStatus

private val TABS = listOf("Маршрут", "Бюджет", "Чек-лист")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    vm: TripDetailViewModel,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit
) {
    val trip by vm.trip.collectAsStateWithLifecycle()
    val places by vm.places.collectAsStateWithLifecycle()
    val expenses by vm.expenses.collectAsStateWithLifecycle()
    val packItems by vm.packItems.collectAsStateWithLifecycle()
    val scheme = MaterialTheme.colorScheme

    var tab by rememberSaveable { mutableIntStateOf(0) }
    var showPlace by remember { mutableStateOf(false) }
    var showExpense by remember { mutableStateOf(false) }
    var showPack by remember { mutableStateOf(false) }
    var showDelete by remember { mutableStateOf(false) }

    val current = trip

    Scaffold(
        containerColor = scheme.background,
        topBar = {
            TopAppBar(
                title = { Text(current?.title ?: "", maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    if (current != null) {
                        IconButton(onClick = { onEdit(current.id) }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Изменить")
                        }
                        IconButton(onClick = { showDelete = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Удалить")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = scheme.background)
            )
        },
        floatingActionButton = {
            if (current != null) {
                FloatingActionButton(
                    onClick = {
                        when (tab) {
                            0 -> showPlace = true
                            1 -> showExpense = true
                            else -> showPack = true
                        }
                    },
                    containerColor = scheme.primary,
                    contentColor = scheme.onPrimary
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Добавить")
                }
            }
        }
    ) { padding ->
        if (current == null) {
            // поездка удалена или ещё загружается
            Spacer(Modifier.fillMaxSize())
        } else {
            val spent = expenses.sumOf { it.amount }
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                HeroSection(current, spent, places.size, packItems)

                TabRow(
                    selectedTabIndex = tab,
                    containerColor = scheme.background,
                    contentColor = scheme.primary
                ) {
                    TABS.forEachIndexed { index, label ->
                        Tab(
                            selected = tab == index,
                            onClick = { tab = index },
                            text = { Text(label) }
                        )
                    }
                }

                when (tab) {
                    0 -> RouteTab(
                        trip = current,
                        places = places,
                        onDelete = vm::deletePlace,
                        onAdd = { showPlace = true }
                    )
                    1 -> BudgetTab(
                        trip = current,
                        expenses = expenses,
                        spent = spent,
                        onDelete = vm::deleteExpense,
                        onAdd = { showExpense = true }
                    )
                    else -> PackTab(
                        items = packItems,
                        onToggle = vm::togglePackItem,
                        onDelete = vm::deletePackItem,
                        onAddDefault = vm::addDefaultPack,
                        onAdd = { showPack = true }
                    )
                }
            }
        }
    }

    if (showPlace && current != null) {
        AddPlaceDialog(
            startDay = current.startDay,
            daysCount = daysCount(current.startDay, current.endDay),
            onDismiss = { showPlace = false },
            onSave = { day, name, time, note ->
                vm.addPlace(day, name, time, note)
                showPlace = false
            }
        )
    }
    if (showExpense) {
        AddExpenseDialog(
            onDismiss = { showExpense = false },
            onSave = { category, amount, note ->
                vm.addExpense(category, amount, note)
                showExpense = false
            }
        )
    }
    if (showPack) {
        AddPackItemDialog(
            onDismiss = { showPack = false },
            onSave = { title ->
                vm.addPackItem(title)
                showPack = false
            }
        )
    }
    if (showDelete && current != null) {
        ConfirmDialog(
            title = "Удалить поездку?",
            text = "Маршрут, расходы и чек-лист этой поездки будут удалены без возможности восстановления.",
            confirmLabel = "Удалить",
            onConfirm = {
                showDelete = false
                vm.deleteTrip(current) { onBack() }
            },
            onDismiss = { showDelete = false }
        )
    }
}

@Composable
private fun HeroSection(trip: Trip, spent: Double, placesCount: Int, pack: List<PackItem>) {
    val status = tripStatus(trip)
    val packDone = pack.count { it.done }
    val subtitle = buildString {
        if (trip.destination.isNotBlank()) append(trip.destination).append(" · ")
        append(fmtRange(trip.startDay, trip.endDay))
    }

    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        HeroCard(title = subtitle, value = status.text) {
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                HeroStat("Длительность", daysWord(daysCount(trip.startDay, trip.endDay)))
                HeroStat("Мест", placesCount.toString())
                HeroStat(
                    "Потрачено",
                    fmtMoney(spent)
                )
                if (pack.isNotEmpty()) HeroStat("Вещи", "$packDone/${pack.size}")
            }
        }
    }
}

@Composable
private fun RouteTab(
    trip: Trip,
    places: List<Place>,
    onDelete: (Place) -> Unit,
    onAdd: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    if (places.isEmpty()) {
        EmptyState(
            icon = Icons.Filled.Map,
            title = "Маршрут пуст",
            subtitle = "Добавьте места и занятия по дням — получится план поездки."
        ) {
            Button(onClick = onAdd) { Text("Добавить место") }
        }
        return
    }

    val byDay = places.groupBy { it.dayIndex }
    val days = daysCount(trip.startDay, trip.endDay)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (d in 0 until days) {
            val list = (byDay[d] ?: emptyList()).sortedWith(compareBy({ it.time.isBlank() }, { it.time }, { it.id }))
            if (list.isEmpty()) continue
            item(key = "day_$d") {
                Column(Modifier.padding(top = 8.dp)) {
                    Text("День ${d + 1}", style = MaterialTheme.typography.titleMedium)
                    Text(
                        fmtWeekday(trip.startDay + d),
                        style = MaterialTheme.typography.bodySmall,
                        color = scheme.onSurfaceVariant
                    )
                }
            }
            items(list, key = { "place_${it.id}" }) { place ->
                AppCard(Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (place.time.isNotBlank()) InfoChip(place.time)
                        Column(Modifier.weight(1f)) {
                            Text(place.name, style = MaterialTheme.typography.titleSmall)
                            if (place.note.isNotBlank()) {
                                Text(
                                    place.note,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = scheme.onSurfaceVariant
                                )
                            }
                        }
                        IconButton(onClick = { onDelete(place) }) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Удалить",
                                tint = scheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BudgetTab(
    trip: Trip,
    expenses: List<Expense>,
    spent: Double,
    onDelete: (Expense) -> Unit,
    onAdd: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(key = "summary") {
            AppCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (trip.budget > 0.0) {
                        val over = spent > trip.budget
                        val left = trip.budget - spent
                        Text(
                            if (over) "Превышение: ${fmtMoney(-left)}" else "Осталось ${fmtMoney(left)}",
                            style = MaterialTheme.typography.titleLarge,
                            color = if (over) scheme.error else scheme.onSurface
                        )
                        LinearProgressIndicator(
                            progress = { (spent / trip.budget).coerceIn(0.0, 1.0).toFloat() },
                            modifier = Modifier.fillMaxWidth(),
                            color = if (over) scheme.error else scheme.primary,
                            trackColor = scheme.surfaceVariant
                        )
                        Text(
                            "Потрачено ${fmtMoney(spent)} из ${fmtMoney(trip.budget)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = scheme.onSurfaceVariant
                        )
                    } else {
                        Text("Потрачено ${fmtMoney(spent)}", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Бюджет не задан — его можно указать при редактировании поездки.",
                            style = MaterialTheme.typography.bodySmall,
                            color = scheme.onSurfaceVariant
                        )
                    }

                    val byCategory = expenses.groupBy { it.category }
                        .mapValues { entry -> entry.value.sumOf { it.amount } }
                        .entries.sortedByDescending { it.value }
                    if (byCategory.isNotEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        byCategory.forEach { (category, sum) ->
                            Row(Modifier.fillMaxWidth()) {
                                Text(
                                    category,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(fmtMoney(sum), style = MaterialTheme.typography.bodyMedium)
                            }
                            LinearProgressIndicator(
                                progress = { if (spent > 0) (sum / spent).toFloat() else 0f },
                                modifier = Modifier.fillMaxWidth(),
                                color = scheme.secondary,
                                trackColor = scheme.surfaceVariant
                            )
                        }
                    }
                }
            }
        }

        if (expenses.isEmpty()) {
            item(key = "empty") {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconBadge(icon = Icons.Filled.Payments, size = 56.dp)
                    Text(
                        "Расходов пока нет",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Button(onClick = onAdd) { Text("Добавить расход") }
                }
            }
        } else {
            items(expenses.sortedByDescending { it.id }, key = { "exp_${it.id}" }) { e ->
                AppCard(Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(e.category, style = MaterialTheme.typography.titleSmall)
                            if (e.note.isNotBlank()) {
                                Text(
                                    e.note,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = scheme.onSurfaceVariant
                                )
                            }
                        }
                        Text(fmtMoney(e.amount), style = MaterialTheme.typography.titleSmall)
                        IconButton(onClick = { onDelete(e) }) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Удалить",
                                tint = scheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PackTab(
    items: List<PackItem>,
    onToggle: (PackItem) -> Unit,
    onDelete: (PackItem) -> Unit,
    onAddDefault: () -> Unit,
    onAdd: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    if (items.isEmpty()) {
        EmptyState(
            icon = Icons.Filled.Checklist,
            title = "Чек-лист пуст",
            subtitle = "Составьте список вещей, чтобы ничего не забыть."
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = onAddDefault) { Text("Стандартный набор") }
                Spacer(Modifier.height(8.dp))
                androidx.compose.material3.TextButton(onClick = onAdd) { Text("Добавить свою вещь") }
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(key = "progress") {
            val done = items.count { it.done }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "Собрано $done из ${items.size}",
                    style = MaterialTheme.typography.titleSmall
                )
                LinearProgressIndicator(
                    progress = { done.toFloat() / items.size },
                    modifier = Modifier.fillMaxWidth(),
                    color = scheme.primary,
                    trackColor = scheme.surfaceVariant
                )
            }
        }
        items(items.sortedBy { it.done }, key = { "pack_${it.id}" }) { item ->
            AppCard(Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .clickable { onToggle(item) }
                        .padding(start = 8.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = item.done, onCheckedChange = { onToggle(item) })
                    Text(
                        item.title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (item.done) scheme.onSurfaceVariant else scheme.onSurface,
                        textDecoration = if (item.done) {
                            androidx.compose.ui.text.style.TextDecoration.LineThrough
                        } else null,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { onDelete(item) }) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Удалить",
                            tint = scheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
