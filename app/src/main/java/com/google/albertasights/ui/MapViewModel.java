package com.google.albertasights.ui;


import android.arch.lifecycle.LiveData;
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
        Log.i(TAG, "size: "+filter.size());
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




    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
