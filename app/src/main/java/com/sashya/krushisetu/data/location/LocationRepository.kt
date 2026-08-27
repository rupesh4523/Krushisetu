package com.sashya.krushisetu.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

data class DeviceLocation(
    val latitude: Double,
    val longitude: Double,
    val locationName: String
)

class LocationRepository(
    context: Context
) {

    private val appContext = context.applicationContext

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(appContext)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Result<DeviceLocation> =
        suspendCancellableCoroutine { continuation ->

            fusedLocationClient
                .getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    null
                )
                .addOnSuccessListener { location: Location? ->

                    if (location == null) {

                        continuation.resume(
                            Result.failure(
                                Exception(
                                    "Unable to determine current location."
                                )
                            )
                        )

                        return@addOnSuccessListener
                    }

                    val locationName =
                        getLocationName(
                            location.latitude,
                            location.longitude
                        )

                    continuation.resume(
                        Result.success(
                            DeviceLocation(
                                latitude = location.latitude,
                                longitude = location.longitude,
                                locationName = locationName
                            )
                        )
                    )
                }
                .addOnFailureListener { exception ->

                    continuation.resume(
                        Result.failure(exception)
                    )
                }
        }

    private fun getLocationName(
        latitude: Double,
        longitude: Double
    ): String {

        return try {

            val geocoder = Geocoder(
                appContext,
                Locale.getDefault()
            )

            @Suppress("DEPRECATION")
            val addresses: List<Address> =
                geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1
                ) ?: emptyList()

            if (addresses.isNotEmpty()) {

                val address = addresses[0]

                val city =
                    address.locality
                        ?: address.subAdminArea
                        ?: address.adminArea

                val state =
                    address.adminArea

                when {

                    city != null &&
                            state != null &&
                            city != state ->
                        "$city, $state"

                    city != null ->
                        city

                    state != null ->
                        state

                    else ->
                        "Current location"
                }

            } else {

                "Current location"
            }

        } catch (exception: Exception) {

            "Current location"
        }
    }
}