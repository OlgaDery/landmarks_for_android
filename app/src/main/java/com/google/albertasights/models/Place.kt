package com.google.albertasights.models

import java.io.Serializable


data class Place(var id: String?, var name: String, var description: String?, var photolink: String,
            var lng: Double, var lat: Double, var weblink: String, var category: String?): Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val place = other as Place?
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
                "id='" + id + '\''.toString() +
                ", name='" + name + '\''.toString() +
                ", descript='" + description + '\''.toString() +
                ", category='" + category + '\''.toString() +
                ", photoLink='" + photolink + '\''.toString() +
                '}'.toString()
    }

}
