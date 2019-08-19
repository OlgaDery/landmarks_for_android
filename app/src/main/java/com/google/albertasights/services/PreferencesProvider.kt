package com.google.albertasights.services


interface PreferencesProvider {

    fun getSelectedPlaces(key: String): MutableSet<String>?
    fun setSelectedPoints(key: String, places: MutableSet<String>): Boolean
}