package com.google.albertasights.ui

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class MyClusterItem (lat: Double, lng: Double, val mTitle: String, val mSnippet: String) : ClusterItem {

    private var mPosition: LatLng? = null

    init {
        mPosition = LatLng(lat, lng)
    }

    override fun getPosition(): LatLng? {
        return mPosition
    }

    override fun getTitle(): String {
        return mTitle
    }

    override fun getSnippet(): String {
        return mSnippet
    }
}
