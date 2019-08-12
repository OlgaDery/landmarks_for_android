package com.google.albertasights.utils

import android.app.Activity
import android.content.IntentSender
import com.google.albertasights.MapViewModel
import com.google.albertasights.R
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest

object UiUtils {
    const val POINT = "point"
    const val PORTRAIT = "portrait"
    const val LANDSCAPE = "landscape"
    const val PHONE = "phone"
    const val TABLET = "tablet"
    const val REQUEST_CHECK_SETTINGS = 0x1

    fun displayLocationSettingsRequest(activity: Activity, viewModel: MapViewModel) {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = (10000 / 2).toLong()

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val client = LocationServices.getSettingsClient(activity)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener(activity) {
            viewModel.setGpsEnabled(true)
        }
        task.addOnFailureListener(activity) { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(activity,
                            REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                    activity.showToast(activity.getString(R.string.error_turning_gps_on))
                    viewModel.setGpsEnabled(false)
                }

            } else {
                activity.showToast(activity.getString(R.string.error_turning_gps_on))
                viewModel.setGpsEnabled(false)
            }
        }
    }

}
