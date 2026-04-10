package com.example.momentary.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import com.example.momentary.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.format.DateTimeParseException
import java.util.Locale

private const val SECOND = 1_000L
private const val MINUTE = 60 * SECOND
private const val HOUR = 60 * MINUTE
private const val DAY = 24 * HOUR
private const val WEEK = 7 * DAY
private const val MONTH = 30 * DAY
private const val YEAR = 365 * DAY

fun Long.toRelativeTime(now: Long = System.currentTimeMillis(), context: Context): String {
    val diff = now - this

    return when {
        diff < MINUTE -> context.getString(R.string.time_format_now)

        diff < HOUR -> {
            val m = diff / MINUTE
            if (m == 1L) context.getString(R.string.time_format_minutes_ago, 1)
            else context.getString(R.string.time_format_minutes_ago, m)
        }

        diff < 2 * HOUR -> context.getString(R.string.time_format_an_hour_ago)

        diff < DAY -> {
            val h = diff / HOUR
            context.getString(R.string.time_format_hours_ago, h)
        }

        diff < WEEK -> {
            val d = diff / DAY
            if (d == 1L) context.getString(R.string.time_format_days_ago, 1)
            else context.getString(R.string.time_format_days_ago, d)
        }

        diff < MONTH -> {
            val w = diff / WEEK
            if (w == 1L) context.getString(R.string.time_format_weeks_ago, 1)
            else context.getString(R.string.time_format_weeks_ago, w)
        }

        diff < YEAR -> {
            val mo = diff / MONTH
            if (mo == 1L) context.getString(R.string.time_format_months_ago, 1)
            else context.getString(R.string.time_format_months_ago, mo)
        }

        else -> {
            val y = diff / YEAR
            if (y == 1L) context.getString(R.string.time_format_years_ago, 1)
            else context.getString(R.string.time_format_years_ago, y)
        }
    }
}

fun String.toEpochMillis(): Long {
    return try {
        Instant.parse(this).toEpochMilli()
    } catch (_: DateTimeParseException) {
        0L
    }
}

fun Context.getCityCountrySafe(
    lat: Double,
    lon: Double,
    onResult: (String) -> Unit
) {
    val geocoder = Geocoder(this, Locale.getDefault())

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        geocoder.getFromLocation(lat, lon, 1, object : Geocoder.GeocodeListener {
            override fun onGeocode(addresses: MutableList<Address>) {
                val address = addresses.firstOrNull()
                onResult(formatAddress(address))
            }

            override fun onError(errorMessage: String?) {
                onResult("")
            }
        })
    } else {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val address = geocoder
                    .getFromLocation(lat, lon, 1)
                    ?.firstOrNull()

                withContext(Dispatchers.Main) {
                    onResult(formatAddress(address))
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    onResult("")
                }
            }
        }
    }
}

private fun formatAddress(address: Address?): String {
    val city = address?.locality
        ?: address?.subAdminArea
        ?: address?.adminArea

    val country = address?.countryName

    return listOfNotNull(city, country).joinToString(", ")
}
