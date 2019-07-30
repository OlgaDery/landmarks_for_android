package com.google.albertasights.services

import com.google.albertasights.models.Place

interface GetDataServices {

    //https://albertasights.herokuapp.com/api/v1/points_by_district?district=Calgary
    //"X-Api-Key", "3.14"
    fun getAllTerritoryPoints(district: String, callback: (List<Place>) -> Unit)
}