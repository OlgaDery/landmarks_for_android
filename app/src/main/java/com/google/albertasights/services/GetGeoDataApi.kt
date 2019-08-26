package com.google.albertasights.services

import com.google.albertasights.models.Place

interface GetGeoDataApi {

    fun getAllTerritoryPoints(callback: (List<Place>?) -> Unit)
}