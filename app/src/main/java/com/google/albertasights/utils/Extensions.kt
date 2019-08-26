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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import java.lang.reflect.Type


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
        if (!ConfigValues.fieldsToRename.containsKey(ConfigValues.NAME) || ConfigValues.fieldsToRename[ConfigValues.NAME].isNullOrBlank()) {
            continue
        } else {
            name = mJsonObject.getString(ConfigValues.fieldsToRename[ConfigValues.NAME])
        }

        if (!ConfigValues.fieldsToRename.containsKey(ConfigValues.LAT) || ConfigValues.fieldsToRename[ConfigValues.LAT].isNullOrBlank()) {
            continue
        } else {
            lat = mJsonObject.getString(ConfigValues.fieldsToRename[ConfigValues.LAT]).toDouble()
        }

        if (!ConfigValues.fieldsToRename.containsKey(ConfigValues.LNG) || ConfigValues.fieldsToRename[ConfigValues.LNG].isNullOrBlank()) {
            continue
        } else {
            lng = mJsonObject.getString(ConfigValues.fieldsToRename[ConfigValues.LNG]).toDouble()
        }

        if (!ConfigValues.fieldsToRename.containsKey(ConfigValues.ID) || ConfigValues.fieldsToRename[ConfigValues.ID].isNullOrBlank()) {
            id = lat.toString().plus(lng.toString())
        } else {
            id = mJsonObject.getString(ConfigValues.fieldsToRename[ConfigValues.LNG])
        }

        if (ConfigValues.fieldsToRename.containsKey(ConfigValues.DESCRIPT) && !ConfigValues.fieldsToRename[ConfigValues.DESCRIPT].isNullOrBlank()) {
            descr = mJsonObject.getString(ConfigValues.fieldsToRename[ConfigValues.DESCRIPT])
        }

        category = if (!ConfigValues.fieldsToRename.containsKey(ConfigValues.CATEGORY) || ConfigValues.fieldsToRename[ConfigValues.CATEGORY].isNullOrBlank()) {
            ""
        } else {
            mJsonObject.getString(ConfigValues.fieldsToRename[ConfigValues.CATEGORY])
        }

        link = if (!ConfigValues.fieldsToRename.containsKey(ConfigValues.PHOTOLINK) || ConfigValues.fieldsToRename[ConfigValues.PHOTOLINK].isNullOrBlank()) {
            ""
        } else {
            mJsonObject.getString(ConfigValues.fieldsToRename[ConfigValues.PHOTOLINK])
        }

        webLink = if (!ConfigValues.fieldsToRename.containsKey(ConfigValues.WEBLINK) || ConfigValues.fieldsToRename[ConfigValues.WEBLINK].isNullOrBlank()) {
            ""
        } else {
            mJsonObject.getString(ConfigValues.fieldsToRename[ConfigValues.WEBLINK])
        }

        listToReturn.add(Place(id, name, descr, link, lng, lat, webLink, category))
    }
    return listToReturn
}

fun <T> String.deserializeJsonToList(type: Type, gson: Gson): List<T>? {
    val newToken = TypeToken.getParameterized(List::class.java, type).type
    return gson.fromJson<ArrayList<T>>(this, newToken)
}


