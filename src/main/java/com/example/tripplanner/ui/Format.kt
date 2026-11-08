package com.example.tripplanner.ui

import com.example.tripplanner.data.Trip
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Валюта приложения — при необходимости поменяйте здесь. */
const val CURRENCY = "₽"

/** DatePicker отдаёт дату как UTC-полночь в миллисекундах. */
const val MILLIS_PER_DAY = 86_400_000L

private val ru: Locale = Locale.forLanguageTag("ru")
private val fullFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", ru)
private val shortFormatter = DateTimeFormatter.ofPattern("d MMM", ru)
private val weekdayFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM", ru)

fun todayEpoch(): Long = LocalDate.now().toEpochDay()

fun fmtDay(epochDay: Long): String = LocalDate.ofEpochDay(epochDay).format(fullFormatter)

fun fmtShort(epochDay: Long): String = LocalDate.ofEpochDay(epochDay).format(shortFormatter)

fun fmtWeekday(epochDay: Long): String =
    LocalDate.ofEpochDay(epochDay).format(weekdayFormatter)

fun fmtRange(start: Long, end: Long): String =
    if (start == end) fmtShort(start) else "${fmtShort(start)} – ${fmtShort(end)}"

fun daysCount(start: Long, end: Long): Int = (end - start + 1).toInt().coerceAtLeast(1)

fun fmtMoney(v: Double): String = "%,.0f $CURRENCY".format(v)

fun String.toDoubleOrNullRu(): Double? = trim().replace(',', '.').toDoubleOrNull()

/** Склонение: 1 день, 2 дня, 5 дней. */
fun plural(n: Int, one: String, few: String, many: String): String {
    val m100 = n % 100
    val m10 = n % 10
    return when {
        m100 in 11..14 -> many
        m10 == 1 -> one
        m10 in 2..4 -> few
        else -> many
    }
}

fun daysWord(n: Int): String = "$n ${plural(n, "день", "дня", "дней")}"

enum class TripPhase { UPCOMING, ACTIVE, DONE }

data class TripStatus(val phase: TripPhase, val text: String)

fun tripStatus(trip: Trip, today: Long = todayEpoch()): TripStatus = when {
    today < trip.startDay ->
        TripStatus(TripPhase.UPCOMING, "Через ${daysWord((trip.startDay - today).toInt())}")
    today <= trip.endDay ->
        TripStatus(TripPhase.ACTIVE, "Идёт сейчас")
    else ->
        TripStatus(TripPhase.DONE, "Завершена")
}
