package com.google.albertasights;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import java.util.Map;

/**
 * Created by olga on 4/9/18.
 */

public class MapViewModel extends ViewModel {
    private static final String TAG = MapViewModel.class.getSimpleName();
    public final MutableLiveData<Map<String, Boolean>> filtersToApply;

    public MapViewModel() {
        Log.d(TAG, "enter MapViewModel()");
        filtersToApply = new MutableLiveData<Map<String, Boolean>>();
        Log.d(TAG, "exit MapViewModel()");
    }

    public void updateFilterMap (Map<String, Boolean> newFilter) {
        Log.d(TAG, "enter updateFilterMap (Map<String, Boolean> newFilter)");
        filtersToApply.setValue(newFilter);
        Log.d(TAG, "exit updateFilterMap (Map<String, Boolean> newFilter)");
    }

}
