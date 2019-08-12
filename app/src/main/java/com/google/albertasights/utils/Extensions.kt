@file:Suppress("DEPRECATION")

package com.google.albertasights.utils

import android.content.Context
import android.content.res.Configuration
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.albertasights.ConfigValues
import com.google.albertasights.models.Place
import org.json.JSONArray


fun View.resetLayoutParameters(height: Int, width: Int) {
    this.layoutParams.height = height
    this.layoutParams.width = width
}


fun Context.getWidthInches(): Int {
    val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    val metrics = DisplayMetrics()
    display.getMetrics(metrics)
    return metrics.widthPixels
}

fun Context.getHightInches(): Int {
    val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    val metrics = DisplayMetrics()
    display.getMetrics(metrics)
    return metrics.heightPixels
}

fun Context.findScreenSize(): String {
    val metrics = DisplayMetrics()
    val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    wm.defaultDisplay.getMetrics(metrics)
    val yInches = metrics.heightPixels / metrics.ydpi
    val xInches = metrics.widthPixels / metrics.xdpi
    val diagonalInches = Math.sqrt((xInches * xInches + yInches * yInches).toDouble())
    return if (diagonalInches >= 6.5) {
        // 6.5inch device or bigger
        UiUtils.TABLET
    } else {
        // smaller device
        UiUtils.PHONE
    }
}

fun Context.getOrientation(): String {
    val orientation: String
    val orientationValue = this.resources.configuration.orientation
    orientation = if (orientationValue == Configuration.ORIENTATION_PORTRAIT) {
        UiUtils.PORTRAIT
    } else {
        UiUtils.LANDSCAPE
    }
    return orientation
}

fun Context.showToast(message: String) {
    Toast.makeText(this,
            message,
            Toast.LENGTH_LONG).show()
}

fun String?.parseUrl(): String {
    return if (this != null && this.length > 5) {
        this.replace("\\", "")
    } else {
        "no"
    }
}

fun JSONArray.parseJsonArray (): MutableList<Place> {
    val listToReturn = mutableListOf<Place>()
    for (i in 0 until this.length()) {
        val mJsonObject = this.getJSONObject(i)
        val name: String
        val lat: Double
        val lng: Double
        val id: String
        var descr: String? = null
        val category: String
        val link: String
        val webLink: String

        //If the fields above are empty or do not exist, we are skipping to instantiate the Place object, as it can
        //not be properly visualize on the map
        if (!ConfigValues.fields_to_remane.containsKey(ConfigValues.NAME) || ConfigValues.fields_to_remane[ConfigValues.NAME].isNullOrBlank()) {
            continue
        } else {
            name = mJsonObject.getString(ConfigValues.fields_to_remane[ConfigValues.NAME])
        }

        if (!ConfigValues.fields_to_remane.containsKey(ConfigValues.LAT) || ConfigValues.fields_to_remane[ConfigValues.LAT].isNullOrBlank()) {
            continue
        } else {
            lat = mJsonObject.getString(ConfigValues.fields_to_remane[ConfigValues.LAT]).toDouble()
        }

        if (!ConfigValues.fields_to_remane.containsKey(ConfigValues.LNG) || ConfigValues.fields_to_remane[ConfigValues.LNG].isNullOrBlank()) {
            continue
        } else {
            lng = mJsonObject.getString(ConfigValues.fields_to_remane[ConfigValues.LNG]).toDouble()
        }

        if (!ConfigValues.fields_to_remane.containsKey(ConfigValues.ID) || ConfigValues.fields_to_remane[ConfigValues.ID].isNullOrBlank()) {
            id = lat.toString().plus(lng.toString())
        } else {
            id = mJsonObject.getString(ConfigValues.fields_to_remane[ConfigValues.LNG])
        }

        if (ConfigValues.fields_to_remane.containsKey(ConfigValues.DESCRIPT) && !ConfigValues.fields_to_remane[ConfigValues.DESCRIPT].isNullOrBlank()) {
            descr = mJsonObject.getString(ConfigValues.fields_to_remane[ConfigValues.DESCRIPT])
        }

        category = if (!ConfigValues.fields_to_remane.containsKey(ConfigValues.CATEGORY) || ConfigValues.fields_to_remane[ConfigValues.CATEGORY].isNullOrBlank()) {
            ""
        } else {
            mJsonObject.getString(ConfigValues.fields_to_remane[ConfigValues.CATEGORY])
        }

        link = if (!ConfigValues.fields_to_remane.containsKey(ConfigValues.PHOTOLINK) || ConfigValues.fields_to_remane[ConfigValues.PHOTOLINK].isNullOrBlank()) {
            ""
        } else {
            mJsonObject.getString(ConfigValues.fields_to_remane[ConfigValues.PHOTOLINK])
        }

        webLink = if (!ConfigValues.fields_to_remane.containsKey(ConfigValues.WEBLINK) || ConfigValues.fields_to_remane[ConfigValues.WEBLINK].isNullOrBlank()) {
            ""
        } else {
            mJsonObject.getString(ConfigValues.fields_to_remane[ConfigValues.WEBLINK])
        }

        listToReturn.add(Place(id, name, descr, link, lng, lat, webLink, category))
    }
    return listToReturn
}


