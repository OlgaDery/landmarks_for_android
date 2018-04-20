package com.google.albertasights.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Bundle;
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

public class PointActivity extends AppCompatActivity {
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

        //getting the type and the orientation of device
        orientation = UiUtils.getOrientation(getApplicationContext());
        deviceType = UiUtils.findScreenSize(getApplicationContext());

        if (getIntent().getExtras()!= null) {
            Bundle extras = getIntent().getExtras();
            point = (Place) extras.getSerializable("POINT");
        } else {
            if (savedInstanceState != null) {
                point = (Place)savedInstanceState.getSerializable("POINT");

            }
        }

        //        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        RelativeLayout allTheView = (RelativeLayout) findViewById(R.id.pointView);
        ImageView banner = (ImageView) findViewById(R.id.banner);
        WindowManager wm = (WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        if (orientation.equals("Portrait")) {
            allTheView.removeView(banner);
        } else {
            if (deviceType.equals("tablet")) {
                Picasso.with(getApplicationContext())
                        .load(R.raw.banner)
                        .resize(300, metrics.heightPixels)
                        //    .onlyScaleDown()
                        .centerCrop()
                        .into(banner);
            } else {
                Picasso.with(getApplicationContext())
                        .load(R.raw.banner)
                        .resize(300, metrics.heightPixels)
                        //    .onlyScaleDown()
                        .centerCrop()
                        .into(banner);
            }

        }

        //initializing the buttons

        ImageButton fab = (ImageButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.directions);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //    Log.d(TAG, "onclick");
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)",
                        point.getLat(), point.getLng(), "Going there");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        final ImageButton likeButton = (ImageButton) findViewById(R.id.like);
        if (point.isLoved()==true) {
            likeButton.setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_IN));
        }
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onclick");
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                if (sharedPref.contains("USER")) {
                    String id =((ImageButton) view).getTag().toString();
                    if (point.isLoved()==false) {
                        Intent writeToFile = new Intent(getApplicationContext(), DBIntentService.class);
                        writeToFile.setAction(UiUtils.ADD_POINT_TO_LOVED);
                        writeToFile.putExtra(UiUtils.POINT_ID, point.getId());
                        startService(writeToFile);
                        likeButton.setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_IN));
                    } else {
                        //TODO call the method to remove from selected
                        likeButton.setColorFilter(new PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN));
                    }

                } else {
                    UiUtils.showToast(getApplicationContext(), "To use this option, please log in or create an account");
                }
            }
        });

        //TODO set onClick method for the button
        likeButton.setImageResource(R.drawable.like);
        likeButton.setTag(point.getId());
        listview = (ListView) findViewById(R.id.listView1);

        //TODO provide dimensions, position and device type via constructor
        PointListviewAdapter adapter = new PointListviewAdapter(point, getApplicationContext());
        listview.setAdapter(adapter);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "enter onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putSerializable("POINT", point);
    }

}
