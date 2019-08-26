package com.google.albertasights.services

import com.google.albertasights.models.Place
import java.lang.reflect.Type

interface DatabaseProvider {

    suspend fun <T> getDataFromDatabase(key: String, type: Type): List<T?>?

    suspend fun <T> submitDataToDatabase(type: Type, value: T?, collectionType: Type?, key: String): Boolean

}