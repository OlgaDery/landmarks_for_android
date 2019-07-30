package com.google.albertasights.models

import java.io.Serializable


class Place(var name: String, var description: String?, var photolink: String, var lng: Double, var lat: Double,
            var weblink: String, var rating: Int?) : Serializable {

    //   public static String selectedMarkerID;

    var id: String? = null
    var category: String? = null
    var catIndex: Int? = null
    var extraCategory: String? = null
    var extraCategoryIndex: Int? = null
    var pendStatus: String? = null
    var isLoved = false


    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val place = o as Place?

        if (java.lang.Double.compare(place!!.lng, lng) != 0) return false
        if (java.lang.Double.compare(place.lat, lat) != 0) return false
        if (name != place.name) return false
        return photolink == place.photolink

    }

    override fun hashCode(): Int {
        var result: Int
        var temp: Long
        result = name.hashCode()
        result = 31 * result + if (description != null) description!!.hashCode() else 0
        temp = java.lang.Double.doubleToLongBits(lng)
        result = 31 * result + (temp xor temp.ushr(32)).toInt()
        temp = java.lang.Double.doubleToLongBits(lat)
        result = 31 * result + (temp xor temp.ushr(32)).toInt()
        result = 31 * result + photolink.hashCode()
        return result
    }

    override fun toString(): String {
        return "Place{" +
                "name='" + name + '\''.toString() +
                ", descript='" + description + '\''.toString() +
                ", category='" + category + '\''.toString() +
                ", extraCategory='" + extraCategory + '\''.toString() +
                ", photoLink='" + photolink + '\''.toString() +
                '}'.toString()
    }

    companion object {
        var poi_main_cat = arrayOf("archeological", "wild area or preserve", "sport", "crafts", "museum/exibition", "science/education", "urban area", "building/construction", "monument/installation", "deposits", "pets and animals", "natural attraction", "historical site", "amusement", "cult/religion", "farm/sanctuary", "other")
        var poi_extra_cat = arrayOf("famous person", "famous object or place", "historical event", "local stories/legends", "cristian legends", "paranormal or unexplaned facts", "healing properties", "scientifically important", "good for kids", "memorial", "historical building", "n/a")
    }
}
//    String name = mJsonObjectProperty.getString("name");
//    // Log.i(TAG, "name: "+mJsonObjectProperty.getString("name"));
//    String lat = mJsonObjectProperty.getString("lat");
//    String lng = mJsonObjectProperty.getString("lng");
//    String main_point_id = mJsonObjectProperty.getString("main_point_id");
//    String descr = mJsonObjectProperty.getString("description");
//    String category = mJsonObjectProperty.getString("category");
//    String extraCategoryIndex = mJsonObjectProperty.getString("extra_category");
//    //    Log.i(TAG, "cat: "+mJsonObjectProperty.getString("category"));
//    String link = mJsonObjectProperty.getString("photolink");
//    String webLink = mJsonObjectProperty.getString("weblink");
//    Integer rating = Integer.valueOf(mJsonObjectProperty.getString("rating"));
