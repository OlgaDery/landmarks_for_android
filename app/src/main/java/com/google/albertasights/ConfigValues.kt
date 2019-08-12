package com.google.albertasights

object ConfigValues {

    //https://albertasights.herokuapp.com/api/v1/points_by_district?district=Calgary

    const val DISTRICT = "Calgary"
    const val BASE_URL = "https://albertasights.herokuapp.com/api/v1/"
    const val PATH = "points_by_district"
    val headers = mutableMapOf<String, String>()
    val apiCallParams = mutableMapOf<String, String>()
    //val placeObjectFieldsNames = arrayOf("id", "name", "lat", "lng", "description", "category", "photolink", "weblink")
    const val ID = "id"
    const val NAME = "name"
    const val LAT = "lat"
    const val LNG = "lng"
    const val DESCRIPT = "description"
    const val CATEGORY = "category"
    const val PHOTOLINK = "photolink"
    const val WEBLINK = "weblink"

    //please specify if you are passing JSON array or object
    var isJsonObject = false
    var parameterNameOfJsonObject = "businesses"

    //This map is to map the fields of the objects is being received from the server to the fields of the "Place" class of the app.
    //Keys are the names of the app fields, values are the names of the object coming from the server. So, replace hardcoded string values
    //with your own, leave it as empty string if there is no correspondent field
    val fields_to_remane = mutableMapOf<String, String>()
    get() {
        field[ID] = "id"
        field[NAME] = "name"
        field[LAT] = "lat"
        field[LNG] = "lng"
        field[DESCRIPT] = "description"
        field[CATEGORY] = "category"
        field[PHOTOLINK] = "photolink"
        field[WEBLINK] = "weblink"
        return field
    }

    fun setHeaders(): MutableMap<String, String> {
        headers["X-Api-Key"] = "3.14"
        return headers
    }

    fun setParams(): MutableMap<String, String> {
        apiCallParams["district"] = DISTRICT
        return apiCallParams
    }

}