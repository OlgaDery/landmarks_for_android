package com.google.albertasights;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.albertasights.ui.UiUtils;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!prefs.contains(UiUtils.EMERGENT_TERMINATION)) {
            SharedPreferences.Editor editor = UiUtils.getEditor(this);
            editor.putBoolean(UiUtils.EMERGENT_TERMINATION, false);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.i("App", "memory has trimed");
        SharedPreferences.Editor editor = UiUtils.getEditor(this);
        editor.putBoolean(UiUtils.EMERGENT_TERMINATION, true);
        editor.commit();
    }
}
