package com.example.tripplanner.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {

    // --- Поездки ---
    @Query("SELECT * FROM trips ORDER BY startDay ASC, id ASC")
    fun trips(): Flow<List<Trip>>

    @Query("SELECT * FROM trips WHERE id = :id")
    fun trip(id: Long): Flow<Trip?>

    @Query("SELECT * FROM trips WHERE id = :id")
    suspend fun getTrip(id: Long): Trip?

    @Insert
    suspend fun insertTrip(trip: Trip): Long

    @Update
    suspend fun updateTrip(trip: Trip)

    @Delete
    suspend fun deleteTrip(trip: Trip)

    @Query("SELECT tripId, SUM(amount) AS total FROM expenses GROUP BY tripId")
    fun spentByTrip(): Flow<List<TripSpent>>

    // --- Маршрут ---
    @Query("SELECT * FROM places WHERE tripId = :tripId ORDER BY dayIndex ASC, time ASC, id ASC")
    fun places(tripId: Long): Flow<List<Place>>

    @Insert
    suspend fun insertPlace(place: Place)

    @Delete
    suspend fun deletePlace(place: Place)

    // --- Расходы ---
    @Query("SELECT * FROM expenses WHERE tripId = :tripId ORDER BY id DESC")
    fun expenses(tripId: Long): Flow<List<Expense>>

    @Insert
    suspend fun insertExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    // --- Чек-лист ---
    @Query("SELECT * FROM pack_items WHERE tripId = :tripId ORDER BY done ASC, id ASC")
    fun packItems(tripId: Long): Flow<List<PackItem>>

    @Insert
    suspend fun insertPackItem(item: PackItem)

    @Insert
    suspend fun insertPackItems(items: List<PackItem>)

    @Update
    suspend fun updatePackItem(item: PackItem)

    @Delete
    suspend fun deletePackItem(item: PackItem)
}
