package com.google.albertasights.models;

import java.io.Serializable;

/**
 * Created by olga on 2/13/18.
 */
//  t.string "name"
//          t.decimal "lat"
//          t.decimal "lng"
//          t.string "main_point_id"
//          t.integer "district_id"
//          t.integer "category_id"
//          t.integer "extra_category_id"
//          t.string "user_id"
//          t.integer "rating"
//          t.string "description"
//          t.string "weblink"
//          t.string "photolink"
//          t.string "pend_status"
//          t.datetime "created_at", null: false
//          t.datetime "updated_at", null: false

public class Place implements Serializable {

    public static final int [] distances = {1, 3, 5, 7, 10};
    public static String [] poi_main_cat = {
            "archeological", "wild area or preserve", "sport", "crafts", "museum/exibition",
            "science/education", "urban area", "building/construction", "monument/installation",
            "deposits", "pets and animals", "natural attraction", "historical site", "amusement",
            "cult/religion", "farm/sanctuary", "other"
    };

    public static String [] poi_extra_cat = {
            "famous person", "famous object or place", "historical event", "local stories/legends", "cristian legends", "paranormal or unexplaned facts", "healing properties",
            "scientifically important", "good for kids", "memorial", "historical building", "n/a"
    };

 //   public static String selectedMarkerID;

    private String id;

    private String name;
    private String descript;

    private double lat;
    private double lng;

    private String category;
    private Integer catIndex;

    private String extraCategory;
    private Integer extraCategoryIndex;
    private Integer rating;
    private String pendStatus;
    private String photoLink = "lnk";
    private String webLink = "lnk";
    private boolean isLoved = false;

    public Place(String name1, String descr1, String photoLink1, double lng1, double lat1,
                 String webLink, Integer rating) {
        this.descript= descr1;
        this.name= name1;
        this.photoLink= photoLink1;
        this.lat=lat1;
        this.lng = lng1;
        this.webLink = webLink;
        this.rating = rating;
    }

    public boolean isLoved() {
        return isLoved;
    }

    public void setLoved(boolean loved) {
        isLoved = loved;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCatIndex() {
        return catIndex;
    }

    public void setCatIndex(Integer catIndex) {
        this.catIndex = catIndex;
    }

    public String getExtraCategory() {
        return extraCategory;
    }

    public void setExtraCategory(String extraCategory) {
        this.extraCategory = extraCategory;
    }

    public Integer getExtraCategoryIndex() {
        return extraCategoryIndex;
    }

    public void setExtraCategoryIndex(Integer extraCategoryIndex) {
        this.extraCategoryIndex = extraCategoryIndex;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getPendStatus() {
        return pendStatus;
    }

    public void setPendStatus(String pendStatus) {
        this.pendStatus = pendStatus;
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

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
