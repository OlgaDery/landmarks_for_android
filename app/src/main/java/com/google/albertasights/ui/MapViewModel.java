package com.google.albertasights.ui;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.util.Log;

import java.util.Map;
import java.util.Set;

/**
 * Created by olga on 4/9/18.
 */

public class MapViewModel extends ViewModel {
    private static final String TAG = MapViewModel.class.getSimpleName();
    public final MutableLiveData<Map<String, Boolean>> filtersToApply = new MutableLiveData<Map<String, Boolean>>();
    BroadcastReceiver receiver;

    public MapViewModel() {
        Log.d(TAG, "enter MapViewModel()");
        Log.d(TAG, "exit MapViewModel()");
    }

    public void updateFilterMap (Map<String, Boolean> newFilter) {
        Log.d(TAG, "enter updateFilterMap (Map<String, Boolean> newFilter)");
        filtersToApply.setValue(newFilter);
        Log.d(TAG, "exit updateFilterMap (Map<String, Boolean> newFilter)");
    }


    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
