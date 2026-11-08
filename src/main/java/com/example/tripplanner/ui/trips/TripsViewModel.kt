package com.example.tripplanner.ui.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tripplanner.data.Trip
import com.example.tripplanner.data.TripRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/** Поездка вместе с суммой уже потраченного. */
data class TripItem(val trip: Trip, val spent: Double)

class TripsViewModel(private val repo: TripRepository) : ViewModel() {

    val items: StateFlow<List<TripItem>> =
        combine(repo.trips, repo.spentByTrip) { trips, spent ->
            val byTrip = spent.associate { it.tripId to it.total }
            trips.map { TripItem(it, byTrip[it.id] ?: 0.0) }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}

class TripsViewModelFactory(private val repo: TripRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        TripsViewModel(repo) as T
}
