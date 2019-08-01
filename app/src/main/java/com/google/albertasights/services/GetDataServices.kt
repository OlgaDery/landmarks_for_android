package com.google.albertasights.services

import com.google.albertasights.models.Place

interface GetDataServices {

    fun getAllTerritoryPoints(district: String, callback: (List<Place>?) -> Unit)
}