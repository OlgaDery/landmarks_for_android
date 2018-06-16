package com.google.albertasights.ui;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.util.Log;

import com.google.albertasights.models.Place;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by olga on 4/9/18.
 */

public class MapViewModel extends ViewModel {
    private static final String TAG = MapViewModel.class.getSimpleName();

    public final MutableLiveData<Map<String, Boolean>> filtersToApply = new MutableLiveData<Map<String, Boolean>>();
    public final MutableLiveData<LinkedList<String>> pointsToShow = new MutableLiveData<LinkedList<String>>();
    public final MutableLiveData<LinkedList<String>> loved = new MutableLiveData<LinkedList<String>>();
    public final MutableLiveData<LinkedList<String>> namesSortedByRating = new MutableLiveData<LinkedList<String>>();
    public final MutableLiveData<LinkedList<Place>> receivedPoints = new MutableLiveData<LinkedList<Place>>();
    public final MutableLiveData<Place> pointToSee = new MutableLiveData<>();
    public final MutableLiveData<String> currentFragment = new MutableLiveData<>();
    public final MutableLiveData<String> currentFilter = new MutableLiveData<>();
    public final MutableLiveData<Boolean> showSidebar = new MutableLiveData<>();
    public final MutableLiveData<LinkedList<String>> dataToFilter = new MutableLiveData<>();
    public final MutableLiveData<LinkedList<String>> ratings = new MutableLiveData<>();

    public final MutableLiveData<LinkedList<String>> selectedFiltersForCategories = new MutableLiveData<>();
    public final MutableLiveData<LinkedList<String>> selectedFiltersForAllPoints = new MutableLiveData<>();
    public final MutableLiveData<LinkedList<String>> selectedFiltersForLoved = new MutableLiveData<>();

    public final MutableLiveData<String> deviceType = new MutableLiveData<>();
    public final MutableLiveData<String> orientation = new MutableLiveData<>();
    public final MutableLiveData<Integer> wight = new MutableLiveData<>();
    public final MutableLiveData<Integer> hight = new MutableLiveData<>();

    public final MutableLiveData<LinkedList<String>> namesToShowInScroll = new MutableLiveData<>();
    public final MutableLiveData<Integer> scrollY = new MutableLiveData<>();
    public final MutableLiveData<Boolean> dataReceived = new MutableLiveData<>();
    public final MutableLiveData<Boolean> locationAccessPermitted = new MutableLiveData<>();

    public MapViewModel() {
        Log.d(TAG, "enter MapViewModel()");
        Log.d(TAG, "exit MapViewModel()");
    }

    public void updateFilterMap (Map<String, Boolean> newFilter) {
        Log.d(TAG, "enter updateFilterMap (Map<String, Boolean> newFilter)");
        filtersToApply.setValue(newFilter);
        Log.d(TAG, "exit updateFilterMap (Map<String, Boolean> newFilter)");
    }

    public LiveData<Map<String, Boolean>> getFilters() {
        Log.d(TAG, "enter getFilters");
        return filtersToApply;
    }

    public LiveData<LinkedList<String>> getPointsNamesToShow () {
      //  Log.d(TAG, "enter getPointsNamesToShow");
        return pointsToShow;
    }

    public LiveData<LinkedList<String>> getNamesSortedByRating () {
        //  Log.d(TAG, "enter getPointsNamesToShow");
        return namesSortedByRating;
    }

    public void updateNamesSortedByRating (LinkedList <String> pointNames) {
        Log.d(TAG, "enter updateNamesSortedByRating");
        namesSortedByRating.setValue(pointNames);
        Log.d(TAG, "exit updateNamesSortedByRating");
    }

    public LiveData<LinkedList<String>> getDataToFilter () {
        //  Log.d(TAG, "enter getPointsNamesToShow");
        return dataToFilter;
    }

    public void updateDataToFilter (LinkedList <String> filter) {
        Log.d(TAG, "enter updateDataToFilter (LinkedList <String> filter)");
        dataToFilter.setValue(filter);
        if (filter!=null) {
            Log.i(TAG, "size: "+filter.size());
        }
        Log.d(TAG, "exit updateDataToFilter (LinkedList <String> filter)");
    }

    public LiveData<String> getCurrentFragment() {
        return currentFragment;
    }

    public LiveData<String> getCurrentFilter() {
        return currentFilter;
    }

    public void updateCurrentFilter(String filter) {
        Log.d(TAG, "enter updateCurrentFilter");
        currentFilter.setValue(filter);
        Log.d(TAG, "exit updateCurrentFilter");
    }

    public LiveData<Boolean> isSidebarReguested() {
        return showSidebar;
    }

    public void upateShowSidebar(Boolean toShow) {
        Log.d(TAG, "enter upateShowSidebar");
        Log.i(TAG, "show sidebar: "+toShow);
        showSidebar.setValue(toShow);
        Log.d(TAG, "exit upateShowSidebar");
    }

    public void updateCurrentFragment(String newFragment) {
        Log.d(TAG, "enter updateCurrentFragment");
        currentFragment.setValue(newFragment);
        Log.d(TAG, "exit updateCurrentFragment");
    }

    public void updatePointsToShow (LinkedList <String> pointNames) {
        Log.d(TAG, "enter updatePointsToShow");
        pointsToShow.setValue(pointNames);
        Log.d(TAG, "exit updatePointsToShow");
    }

    public LiveData<LinkedList<Place>> getRecievedPoints () {
      //  Log.d(TAG, "enter getRecievedPoints");
        return receivedPoints;
    }

    public void updatePoints(LinkedList <Place> points) {
        Log.d(TAG, "enter updatePoint");
        receivedPoints.setValue(points);
        Log.d(TAG, "exit updatePoint");
    }

    public LiveData<LinkedList<String>>  getLoved () {
     //   Log.d(TAG, "enter getLoved");
        return loved;
    }

    public void updateLoved (LinkedList <String> pointNames) {
        Log.d(TAG, "enter updateLoved");
        loved.setValue(pointNames);
        Log.d(TAG, "exit updateLoved");
    }

    public void updatePoint(Place newPoint) {
        Log.d(TAG, "enter updatePoint");
        pointToSee.setValue(newPoint);
        Log.d(TAG, "exit updatePoint");
    }

    public LiveData<Place> getPointToSee() {

        return pointToSee;
    }

    public LiveData<LinkedList<String>> getSelectedFiltersForCategories (){
        return selectedFiltersForCategories;
    }

    public LiveData<LinkedList<String>> getSelectedFiltersForAll (){
        return selectedFiltersForAllPoints;
    }

    public LiveData<LinkedList<String>> getSelectedFiltersForLoved (){
        return selectedFiltersForLoved;
    }

    public void updateSelectedFiltersForCategories(LinkedList <String> cats) {
        Log.d(TAG, "enter updateSelectedFiltersForCategories");
        selectedFiltersForCategories.setValue(cats);
        Log.d(TAG, "exit updateSelectedFiltersForCategories");
    }

    public void updateSelectedFiltersForAllPoints(LinkedList <String> names) {
        Log.d(TAG, "enter updateSelectedFiltersForAllPoints");
        selectedFiltersForAllPoints.setValue(names);
        Log.d(TAG, "exit updateSelectedFiltersForAllPoints");
    }

    public void updateSelectedFiltersForLoved(LinkedList <String> names) {
        Log.d(TAG, "enter updateSelectedFiltersForLoved");
        selectedFiltersForLoved.setValue(names);
        Log.d(TAG, "exit updateSelectedFiltersForLoved");
    }

    public void updateWight (Integer wight1) {
        wight.setValue(wight1);
    }

    public LiveData<Integer> getWight() {
        return wight;
    }

    public LiveData<Integer> getHight() {

        return hight;
    }

    public LiveData<String> getOrienr() {
        return orientation;
    }

    public LiveData<String> getDevice() {
        return deviceType;
    }

    public void updateHights(Integer hight1) {

        hight.setValue(hight1);
    }

    public void updateOrientation (String orient) {

        orientation.setValue(orient);
    }

    public void updateDeviceType (String device) {

        deviceType.setValue(device);
    }

    public void updateY(Integer y) {

        Log.i(TAG, "top index:" + y);
        scrollY.setValue(y);
    }

    public LiveData<Integer> getScrollY() {

        return scrollY;
    }

    public void updateRatings (LinkedList <String> r) {
        Log.d(TAG, "enter updateRatings");
        ratings.setValue(r);
        Log.d(TAG, "exit updateRatings");
    }

    public LiveData<LinkedList<String>> getRatings () {
        return ratings;
    }



    public void updateNamesToShowInScroll(LinkedList <String> names) {
        Log.d(TAG, "enter updateNamesToShowInScroll");
        namesToShowInScroll.setValue(names);
        Log.d(TAG, "exit updateNamesToShowInScroll");
    }

    public LiveData<LinkedList<String>> getNamesToShowInScroll(){
        return namesToShowInScroll;
    }

    public LiveData <Boolean> getDataReceived () {
        return dataReceived;
    }

    public void updateDataReceived(Boolean info) {
        dataReceived.setValue(info);
    }

    public LiveData<Boolean> getLocationAccessPermitted() {
        return locationAccessPermitted;
    }

    public void setLocationAccessPermitted (Boolean isPermitted) {
        locationAccessPermitted.setValue(isPermitted);
    }



    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
