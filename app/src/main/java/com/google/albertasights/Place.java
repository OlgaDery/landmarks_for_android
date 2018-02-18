package com.google.albertasights;

import java.io.Serializable;

/**
 * Created by olga on 2/13/18.
 */

public class Place implements Serializable {

    public static final int [] distances = {1, 3, 5, 7, 10};
    public static String [] poi_main_cat = {
            "archeological", "protected wild area", "sport", "crafts", "rest area",
            "museum/exibition", "scientific site", "vis.art/urban",
            "deposits", "pets and animals", "natural", "historical", "entertainment", "cult/religion", "farm"
    };

    public static int distance=1;
    public static String selectedMarkerID;

    private String name;
    private String descript;
    private double lng;

    private String category;
    private double lat;
    private String photoLink = "lnk";

    public Place(String name1, String descr1, String photoLink1, double lng1, double lat1) {
        this.descript= descr1;
        this.name= name1;
        this.photoLink= photoLink1;
        this.lat=lat1;
        this.lng = lng1;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public static String[] getPoi_main_cat() {
        return poi_main_cat;
    }

    public static void setPoi_main_cat(String[] poi_main_cat) {
        Place.poi_main_cat = poi_main_cat;
    }

    public static int getDistance() {
        return distance;
    }

    public static void setDistance(int distance) {
        Place.distance = distance;
    }

    public static String getSelectedMarkerID() {
        return selectedMarkerID;
    }

    public static void setSelectedMarkerID(String selectedMarkerID) {
        Place.selectedMarkerID = selectedMarkerID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescript() {
        return descript;
    }

    public static int[] getDistances() {
        return distances;
    }

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }

    public String getPhotoLink() {
        return photoLink;
    }

//    public static Set<Place> filterCategory (Set<String> categories) {
//        Set<Place> toReturn = new HashSet<>();
//        for (Place p : places) {
//            if (categories.contains(p.getCategory())) {
//                toReturn.add(p);
//            }
//
//        }
//        return toReturn;
//
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Place place = (Place) o;

        if (Double.compare(place.lng, lng) != 0) return false;
        if (Double.compare(place.lat, lat) != 0) return false;
        if (name != null ? !name.equals(place.name) : place.name != null) return false;
        if (descript != null ? !descript.equals(place.descript) : place.descript != null)
            return false;
        return photoLink != null ? photoLink.equals(place.photoLink) : place.photoLink == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = name != null ? name.hashCode() : 0;
        result = 31 * result + (descript != null ? descript.hashCode() : 0);
        temp = Double.doubleToLongBits(lng);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lat);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (photoLink != null ? photoLink.hashCode() : 0);
        return result;
    }
}
