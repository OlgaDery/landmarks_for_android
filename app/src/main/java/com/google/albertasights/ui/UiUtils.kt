package com.google.albertasights.ui

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.res.Configuration

import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.Toast

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
    internal val REQUEST_CHECK_SETTINGS = 0x1

    fun parseUrl(url: String?): String {
        return if (url != null && url.length > 5) {
            url.replace("\\", "")
        } else {
            "no"
        }
    }

    fun getWidthInches(context: Context): Int? {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        return metrics.widthPixels
    }

    fun getHightInches(context: Context): Int? {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        return metrics.heightPixels
    }

    fun findScreenSize(context: Context): String {
        val metrics = DisplayMetrics()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metrics)
        val yInches = metrics.heightPixels / metrics.ydpi
        val xInches = metrics.widthPixels / metrics.xdpi
        val diagonalInches = Math.sqrt((xInches * xInches + yInches * yInches).toDouble())
        return if (diagonalInches >= 6.5) {
            // 6.5inch device or bigger
            TABLET
        } else {
            // smaller device
            PHONE
        }
    }

    fun getOrientation(context: Context): String {
        val orientation: String
        val orientationValue = context.resources.configuration.orientation
        orientation = if (orientationValue == Configuration.ORIENTATION_PORTRAIT) {
            PORTRAIT
        } else {
            LANDSCAPE
        }
        return orientation
    }


    fun showToast(context: Context, message: String) {
        Toast.makeText(context,
                message,
                Toast.LENGTH_LONG).show()
    }

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
                    showToast(activity, activity.getString(R.string.error_turning_gps_on))
                    viewModel.setGpsEnabled(false)
                }

            } else {
                showToast(activity, activity.getString(R.string.error_turning_gps_on))
                viewModel.setGpsEnabled(false)
            }
        }

    }
}
