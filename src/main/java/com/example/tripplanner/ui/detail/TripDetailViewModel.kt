package com.example.tripplanner.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tripplanner.data.DefaultPack
import com.example.tripplanner.data.Expense
import com.example.tripplanner.data.PackItem
import com.example.tripplanner.data.Place
import com.example.tripplanner.data.Trip
import com.example.tripplanner.data.TripRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TripDetailViewModel(
    private val repo: TripRepository,
    private val tripId: Long
) : ViewModel() {

    private val sharing = SharingStarted.WhileSubscribed(5_000)

    val trip: StateFlow<Trip?> =
        repo.trip(tripId).stateIn(viewModelScope, sharing, null)

    val places: StateFlow<List<Place>> =
        repo.places(tripId).stateIn(viewModelScope, sharing, emptyList())

    val expenses: StateFlow<List<Expense>> =
        repo.expenses(tripId).stateIn(viewModelScope, sharing, emptyList())

    val packItems: StateFlow<List<PackItem>> =
        repo.packItems(tripId).stateIn(viewModelScope, sharing, emptyList())

    fun addPlace(dayIndex: Int, name: String, time: String, note: String) {
        viewModelScope.launch {
            repo.addPlace(
                Place(
                    tripId = tripId,
                    dayIndex = dayIndex,
                    name = name.trim(),
                    time = time.trim(),
                    note = note.trim()
                )
            )
        }
    }

    fun deletePlace(place: Place) {
        viewModelScope.launch { repo.deletePlace(place) }
    }

    fun addExpense(category: String, amount: Double, note: String) {
        viewModelScope.launch {
            repo.addExpense(
                Expense(
                    tripId = tripId,
                    category = category,
                    amount = amount,
                    note = note.trim()
                )
            )
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch { repo.deleteExpense(expense) }
    }

    fun addPackItem(title: String) {
        viewModelScope.launch {
            repo.addPackItem(PackItem(tripId = tripId, title = title.trim()))
        }
    }

    fun addDefaultPack() {
        viewModelScope.launch {
            repo.addPackItems(DefaultPack.items.map { PackItem(tripId = tripId, title = it) })
        }
    }

    fun togglePackItem(item: PackItem) {
        viewModelScope.launch { repo.updatePackItem(item.copy(done = !item.done)) }
    }

    fun deletePackItem(item: PackItem) {
        viewModelScope.launch { repo.deletePackItem(item) }
    }

    fun deleteTrip(trip: Trip, onDone: () -> Unit) {
        viewModelScope.launch {
            repo.deleteTrip(trip)
            onDone()
        }
    }
}

class TripDetailViewModelFactory(
    private val repo: TripRepository,
    private val tripId: Long
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        TripDetailViewModel(repo, tripId) as T
}
