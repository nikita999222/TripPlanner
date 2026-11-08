package com.example.tripplanner.data

import kotlinx.coroutines.flow.Flow

/** Тонкая прослойка между ViewModel и базой данных. */
class TripRepository(private val dao: TripDao) {

    val trips: Flow<List<Trip>> = dao.trips()
    val spentByTrip: Flow<List<TripSpent>> = dao.spentByTrip()

    fun trip(id: Long): Flow<Trip?> = dao.trip(id)
    suspend fun getTrip(id: Long): Trip? = dao.getTrip(id)
    suspend fun insertTrip(trip: Trip): Long = dao.insertTrip(trip)
    suspend fun updateTrip(trip: Trip) = dao.updateTrip(trip)
    suspend fun deleteTrip(trip: Trip) = dao.deleteTrip(trip)

    fun places(tripId: Long): Flow<List<Place>> = dao.places(tripId)
    suspend fun addPlace(place: Place) = dao.insertPlace(place)
    suspend fun deletePlace(place: Place) = dao.deletePlace(place)

    fun expenses(tripId: Long): Flow<List<Expense>> = dao.expenses(tripId)
    suspend fun addExpense(expense: Expense) = dao.insertExpense(expense)
    suspend fun deleteExpense(expense: Expense) = dao.deleteExpense(expense)

    fun packItems(tripId: Long): Flow<List<PackItem>> = dao.packItems(tripId)
    suspend fun addPackItem(item: PackItem) = dao.insertPackItem(item)
    suspend fun addPackItems(items: List<PackItem>) = dao.insertPackItems(items)
    suspend fun updatePackItem(item: PackItem) = dao.updatePackItem(item)
    suspend fun deletePackItem(item: PackItem) = dao.deletePackItem(item)
}
