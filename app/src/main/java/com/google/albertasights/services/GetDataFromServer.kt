package com.google.albertasights.services

import com.google.albertasights.models.Place

interface GetDataFromServer {

    fun getAllTerritoryPoints(callback: (List<Place>?) -> Unit)
}