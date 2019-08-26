package com.google.albertasights.services


interface PreferencesProvider {

    fun getCollection(key: String): MutableSet<String>?
    fun saveCollection(key: String, places: MutableSet<String>): Boolean
}