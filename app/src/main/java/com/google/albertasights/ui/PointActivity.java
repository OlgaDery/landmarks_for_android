package com.google.albertasights.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.albertasights.DBIntentService;
import com.google.albertasights.R;
import com.google.albertasights.models.Place;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class PointActivity extends MenuActivity {
    private Place point;
    private ListView listview;
    private static final String TAG = PointActivity.class.getSimpleName();
    private ImageButton closeButton;
    private String orientation;
    private String deviceType;

 //   ArrayList <String> listItemsValue = new ArrayList<>(4);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "enter onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putSerializable("POINT", point);
    }

}
