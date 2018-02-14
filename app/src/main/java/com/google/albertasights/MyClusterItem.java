package com.google.albertasights;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by olga on 2/13/18.
 */

public class MyClusterItem implements ClusterItem {

    private LatLng mPosition;
    private String mTitle;
    private String mSnippet;

    public MyClusterItem(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    public MyClusterItem(double lat, double lng, String title, String snippet) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
    }


    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }
}
