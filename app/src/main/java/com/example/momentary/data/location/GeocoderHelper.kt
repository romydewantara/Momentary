package com.example.momentary.data.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import com.example.momentary.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume

class GeocoderHelper @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    suspend fun getAddress(
        latitude: Double,
        longitude: Double
    ): String = suspendCancellableCoroutine { cont ->
        val geocoder = Geocoder(
            context,
            Locale.getDefault()
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(
                latitude,
                longitude,
                1,
                object : Geocoder.GeocodeListener {
                    override fun onGeocode(
                        addresses: MutableList<Address>
                    ) {
                        val address = addresses
                            .firstOrNull()
                            ?.getAddressLine(0)
                            ?: context.getString(
                                R.string.add_story_unknown_location
                            )

                        cont.resume(address)
                    }

                    override fun onError(errorMessage: String?) {
                        cont.resume(
                            context.getString(R.string.add_story_unknown_location)
                        )
                    }
                }
            )

        } else {
            @Suppress("DEPRECATION")
            val address = geocoder.getFromLocation(
                latitude,
                longitude,
                1
            )
                ?.firstOrNull()
                ?.getAddressLine(0)
                ?: context.getString(R.string.add_story_unknown_location)

            cont.resume(address)
        }
    }
}
