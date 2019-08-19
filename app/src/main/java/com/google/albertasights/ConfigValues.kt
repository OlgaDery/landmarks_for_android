package com.google.albertasights

object ConfigValues {

    //parameters for the API call
    const val BASE_URL = "https://albertasights.herokuapp.com/api/v1/"
    const val RELATIVE_URL = "points_by_district"
    const val IMAGE_FOR_LOADING_FRAGMENT = "https://dl.dropboxusercontent.com/s/rjci9l5r6vajv49/20170806_173947.jpg"
    val headers = mutableMapOf<String, String>()
    val apiCallParams = mutableMapOf<String, String>()

    //parameters to parse JSON
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
    var parameterNameOfJsonObject = ""

    //This map is to map the fields of the objects is being received from the server to the fields of the "Place" class of the app.
    //Keys are the names of the app fields, values are the names of the object coming from the server. So, replace hardcoded string values
    //with your own, leave it as empty string if there is no correspondent field
    val fieldsToRename = mutableMapOf<String, String>()
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
        apiCallParams["district"] = "Calgary"
        return apiCallParams
    }

}