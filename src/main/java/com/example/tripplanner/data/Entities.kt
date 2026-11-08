package com.example.tripplanner.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Поездка. Даты хранятся как номер дня (LocalDate.toEpochDay()). */
@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val destination: String,
    val startDay: Long,
    val endDay: Long,
    val budget: Double
)

/** Пункт маршрута: место или событие в конкретный день поездки. */
@Entity(
    tableName = "places",
    foreignKeys = [
        ForeignKey(
            entity = Trip::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tripId")]
)
data class Place(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: Long,
    val dayIndex: Int,          // 0 — первый день поездки
    val name: String,
    val time: String = "",      // например «10:30», необязательно
    val note: String = ""
)

/** Расход в рамках поездки. */
@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = Trip::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tripId")]
)
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: Long,
    val category: String,
    val amount: Double,
    val note: String = ""
)

/** Пункт чек-листа «что взять с собой». */
@Entity(
    tableName = "pack_items",
    foreignKeys = [
        ForeignKey(
            entity = Trip::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tripId")]
)
data class PackItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: Long,
    val title: String,
    val done: Boolean = false
)

/** Результат запроса: сколько потрачено по каждой поездке. */
data class TripSpent(
    val tripId: Long,
    val total: Double
)

object ExpenseCategories {
    val all = listOf(
        "Билеты",
        "Жильё",
        "Еда",
        "Транспорт",
        "Развлечения",
        "Покупки",
        "Прочее"
    )
}

object DefaultPack {
    val items = listOf(
        "Паспорт и документы",
        "Билеты",
        "Банковская карта и наличные",
        "Телефон и зарядка",
        "Аптечка",
        "Одежда по погоде",
        "Средства гигиены"
    )
}
