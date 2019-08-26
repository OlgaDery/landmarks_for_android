package com.google.albertasights.services

import android.content.Context
import com.google.albertasights.models.Place
import com.google.albertasights.utils.deserializeJsonToList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import javax.inject.Inject

class DatabaseActions  @Inject constructor (val context: Context): DatabaseProvider {

    private val daoActions: DaoActions
    val gson: Gson by lazy(LazyThreadSafetyMode.NONE) { Gson() }

    init {
        val database = DefaultDatabase.getDatabase(context) ?: throw Exception("Failed to initialize cache database.")
        daoActions = database.cacheDataDao()
    }

    override suspend fun <T> getDataFromDatabase(key: String, type: Type): List<T>? {
        val data = daoActions.get(key)
        if (data != null) {
            val json = data.value
            if (!json.isBlank()) {
                return json.deserializeJsonToList(type, gson)
            }
        }
        return null
    }

    override suspend fun <T> submitDataToDatabase(type: Type, value: T?, collectionType: Type?, key: String): Boolean {
        val newToken = TypeToken.getParameterized(collectionType, type).type
        val json = gson.toJson(value, newToken)
        if (json != null) {
            daoActions.insert(DataToSave(key, json))
            return true
        }
        return false
    }

}