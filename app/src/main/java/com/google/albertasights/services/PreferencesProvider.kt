package com.google.albertasights.services

import java.lang.reflect.Type

interface PreferencesProvider {

    fun getSelectedPlaces(key: String): MutableSet<String>?
    fun setSelectedPoints(key: String, places: MutableSet<String>): Boolean
   // fun deleteValue(key: String): Boolean
}