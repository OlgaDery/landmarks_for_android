package com.google.albertasights;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.util.Log;

public class LifeCycleOwner implements LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)

    public void onResume() {
        Log.d("LifeCycleOwner", "resumed observing lifecycle.");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        Log.d("LifeCycleOwner", "paused observing lifecycle.");
    }


}
