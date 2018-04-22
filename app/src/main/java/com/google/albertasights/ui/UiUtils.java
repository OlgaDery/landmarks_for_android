package com.google.albertasights.ui;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.preference.PreferenceManager;
import android.support.v4.widget.CompoundButtonCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.albertasights.R;
import com.google.albertasights.ui.MapsActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by olga on 2/13/18.
 */

public class UiUtils {
    public static final String URL = "URL";
    public static final String LNG = "LNG";
    public static final String LAT = "LAT";
    public static final String DISTANCE = "DISTANCE";
    public static final String SUBMIT = "SUBMIT";
    public static final String POINT = "POINT";
    public static final String DATA_RECEIVED = "DATA_RECEIVED";
    public static final String DB_CHECKED = "DB_CHECKED";
    public static final String POINT_ADDED = "POINT_ADDED";
    public static final String LOVED = "LOVED";
    public static final String RESULT = "RESULT";
    public static final String PLACES = "PLACES";
    public static final String USER = "USER";
    public static final String LOGGED_IN = "LOGGED_IN";
    public static final String LOG_IN = "LOG_IN";
    public static final String LOG_OUT = "LOG_OUT";
    public static final String USER_CREATED = "USER_CREATED";
    public static final String SELECTED_POINTS = "SELECTED_POINTS";
    public static final String ADD_POINT_TO_LOVED = "ADD_POINT_TO_LOVED";
    public static final String CREATE_USER = "CREATE_USER";
    public static final String CHECK_CONFIG = "CHECK_CONFIG";
    public static final String POINT_ID = "POINT_ID";
    public static final String POINT_REMOVED = "POINT_REMOVED";
    public static final String USER_UPDATED = "USER_UPDATED";
    public static final String EMAIL = "EMAIL";
    public static final String PASSWORD = "PASSWORD";
    public static final String ROLE = "ROLE";
    public static final String USER_ID = "USER_ID";
    public static final String FIRST_NAME = "FIRST_NAME";
    public static final String LAST_NAME = "LAST_NAME";


    private static final String TAG = MapsActivity.class.getSimpleName();
  //  public static Set <String> selectedPointsIds = new HashSet<>();

  //  public static boolean showFilters = false;

    public static String parseUrl (String url) {
        String modifiedUrl ="";

        if (url!=null && url.length()>5) {
            modifiedUrl = url.replace("\\", "");
            //    Log.i(TAG, "modified: "+modifiedUrl);

        } else {
            modifiedUrl = "no";
            //    Log.d(TAG, "no image link");
        }
        return modifiedUrl;

    }

    public static String findScreenSize (Context context) {
        //TODO figure out if the device is a phone or a tablet
        //  Log.d(TAG, "enter setTextSizeGettingScreenSize (TextView textview, Context context)");

        WindowManager wm = (WindowManager)    context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        float widthInches = metrics.widthPixels / metrics.xdpi;
        float heightInches = metrics.heightPixels / metrics.ydpi;
        double diagonalInches = Math.sqrt(Math.pow(widthInches, 2) + Math.pow(heightInches, 2));
        if (diagonalInches >= 7.0) {
            return "tablet";
        } else {
            return "phone";
        }

    }

    public static String getOrientation (Context context) {
        String orientation;
        int orientationValue = context.getResources().getConfiguration().orientation;

        if (orientationValue == Configuration.ORIENTATION_PORTRAIT) {
            orientation = "Portrait";
        } else {
            orientation = "Landscape";
        }

        return orientation;
    }

    public static void configureFilters (Context context, LinearLayout filter, String deviceType,
                                         ArrayList<String> receivedFilters,
                                         ArrayList<String> selectedFilters,
                                         View.OnClickListener checkBoxListener, String currentFilter) {
        Log.d(TAG, "enter configureFilters");
     //   Log.d(TAG, "received filters: "+receivedFilters.size());

        filter.removeAllViews();
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int screen_width = metrics.widthPixels;
        int elementWight = 0;

        TextView text = new TextView(context);
        text.setPadding(20,20,20,20);
        if (getOrientation(context).equals("Portrait")) {
            switch (currentFilter) {
                case MapsActivity.FILTERS:
                    text.setText("Categories:");
                    filter.addView(text, 0);
                    elementWight = screen_width - 300;
                    break;
                case MapsActivity.ALL:
                    text.setText("All points:");
                    filter.addView(text, 0);
                    elementWight = screen_width - 200;
                    break;
                case MapsActivity.LOVED:
                    text.setText("Selected points:");
                    filter.addView(text, 0);
                    elementWight = screen_width - 200;
                    break;
            }
        } else {
            switch (currentFilter) {
                case MapsActivity.FILTERS:
                    text.setText("Categories:");
                    filter.addView(text, 0);
                    elementWight = screen_width - 450;
                    break;
                case MapsActivity.ALL:
                    text.setText("All points:");
                    filter.addView(text, 0);
                    elementWight = screen_width - 350;
                    break;
                case MapsActivity.LOVED:
                    text.setText("Selected points:");
                    filter.addView(text, 0);
                    elementWight = screen_width - 350;
                    break;
            }
        }

        filter.setLayoutParams(new FrameLayout.LayoutParams(elementWight, FrameLayout.LayoutParams.WRAP_CONTENT));

        if (currentFilter.equals(MapsActivity.LOVED)&& receivedFilters.size()==0) {
            TextView text1 = new TextView(context);
            text.setText("You have not selected any points yet");
            filter.addView(text1);
            return;
        }

        ArrayList<String> lst = new ArrayList<>(receivedFilters.size());
        lst.addAll(receivedFilters);
        // adding checkboxes dynamically
        //    filter.removeAllViews();
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked}, // unchecked
                        new int[]{android.R.attr.state_checked} , // checked
                },
                new int[]{
                        Color.parseColor("#000000"),
                        Color.parseColor("#40b6ff"),
                }
        );

//set the space between checkboxes
        for (int i = 0; i < receivedFilters.size(); i++) {
         //   Log.d(TAG, "!!!");
            CheckBox checkBox = new CheckBox(context);

            // set the text size depending on the device type
            checkBox.setText(lst.get(i));
            if (deviceType.equals("tablet")) {
                checkBox.setTextSize(context.getResources().getDimension(R.dimen.avg_textsize));
            }

            if (selectedFilters.contains(lst.get(i))) {
                // set checked checkbox
                checkBox.setChecked(true);
            }
            checkBox.setPadding(20,20,20,20);
            checkBox.setTag(lst.get(i));

            //setting the color of text and the box of check box
            checkBox.setTextColor(Color.BLACK);
            CompoundButtonCompat.setButtonTintList(checkBox,colorStateList);
            checkBox.setOnClickListener(checkBoxListener);
            filter.addView(checkBox, i+1);
        }

    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context,
                message,
                Toast.LENGTH_LONG).show();
    }

    public static void modifyButtons (Set<ImageButton> buttons, String activeButtonTag) {
        Log.d(TAG, "enter modifyButtons (Set<ImageButton> buttons, String activeButtonTag)");
        for (ImageButton b : buttons) {
            if (b.getTag().equals(activeButtonTag)) {
                b.setColorFilter(new PorterDuffColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN));
            } else {
                b.setColorFilter(new PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN));
            }
        }
    }

    public static SharedPreferences.Editor getEditor (Context context) {
        Log.d(TAG, "enter getEditor (Context context)");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        return editor;
    }

}
