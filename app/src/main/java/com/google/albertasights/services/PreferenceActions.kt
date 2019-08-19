package com.google.albertasights.services

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceActions @Inject constructor (val context: Context): PreferencesProvider {

    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    @Suppress("UNCHECKED_CAST")
    override fun getSelectedPlaces(key: String): MutableSet<String>? {
        return preferences.getStringSet(key, mutableSetOf())
    }

    override fun setSelectedPoints(key: String, places: MutableSet<String>): Boolean {
        val editor = preferences.edit()
        editor.putStringSet(key, places)
        return editor.commit()
    }
}