package com.google.albertasights.ui;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v4.widget.Space;
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
import java.util.Collections;
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
    public static final String REMOVE_POINT = "REMOVE_POINT";
    public static final String CREATE_USER = "CREATE_USER";
    public static final String CHECK_CONFIG = "CHECK_CONFIG";
    public static final String POINT_ID = "POINT_ID";
    public static final String POINT_REMOVED = "POINT_REMOVED";
    public static final String USER_UPDATED = "USER_UPDATED";
    public static final String UPDATE_USER = "UPDATE_USER";
    public static final String EMAIL = "EMAIL";
    public static final String PASSWORD = "PASSWORD";
    public static final String ROLE = "ROLE";
    public static final String USER_ID = "USER_ID";
    public static final String FIRST_NAME = "FIRST_NAME";
    public static final String LAST_NAME = "LAST_NAME";

    public static final String BACK_TO_MAP = "BACK_TO_MAP";
    public static final String BACK_TO_POINT = "BACK_TO_POINT";


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
                                         View.OnClickListener checkBoxListener, String currentFilter,
                                         View.OnClickListener clearButtonList,
                                         View.OnClickListener seeMoreBList) {
        Log.d(TAG, "enter configureFilters");
        Log.d(TAG, "received filters: "+receivedFilters.size());
        Log.d(TAG, "current filter: "+currentFilter);

        filter.removeAllViews();
        ImageButton showFilterSection = new ImageButton(context);//(ImageButton) v.findViewById(R.id.imageB);
        showFilterSection.setImageResource(R.drawable.show_more_up);
        showFilterSection.getBackground().setAlpha(0);
        showFilterSection.setOnClickListener(seeMoreBList);
     //   showFilterSection.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN));

        ImageButton clearAll = new ImageButton(context);//(ImageButton) v.findViewById(R.id.clearMap);
        clearAll.setImageResource(R.drawable.close_trimmed);
        clearAll.getBackground().setAlpha(0);
        clearAll.setTag("CLEAR_MAP");
        clearAll.setOnClickListener(clearButtonList);

        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int screen_width = metrics.widthPixels;
        int elementWight = 0;

        LinearLayout bottomsLayout = new LinearLayout(context);
        bottomsLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        Space space = new Space(context);
        Space space1 = new Space(context);
        Space space2 = new Space(context);
        space.setLayoutParams(param);
        space1.setLayoutParams(param);
        space2.setLayoutParams(param);

        bottomsLayout.addView(space, 0);
        bottomsLayout.addView(showFilterSection, 1);
        bottomsLayout.addView(space1, 2);
        bottomsLayout.addView(clearAll, 3);
        bottomsLayout.addView(space2, 4);
        filter.addView(bottomsLayout, 0);

        TextView text = new TextView(context);
        text.setPadding(20,20,20,20);
        if (getOrientation(context).equals("Portrait")) {
            switch (currentFilter) {
                case MapFragment.FILTERS:
                    text.setText("Categories:");
                    filter.addView(text, 1);
                    elementWight = screen_width - 300;
                    break;
                case MapFragment.ALL:
                    text.setText("All points:");
                    filter.addView(text, 1);
                    elementWight = screen_width - 200;
                    break;
                case MapFragment.LOVED:
                    text.setText("Selected points:");
                    filter.addView(text, 1);
                    elementWight = screen_width - 200;
                    break;
            }
        } else {
            switch (currentFilter) {
                case MapFragment.FILTERS:
                    text.setText("Categories:");
                    filter.addView(text, 1);
                    elementWight = screen_width - 550;
                    break;
                case MapFragment.ALL:
                    text.setText("All points:");
                    filter.addView(text, 1);
                    elementWight = screen_width - 450;
                    break;
                case MapFragment.LOVED:
                    text.setText("Selected points:");
                    filter.addView(text, 1);
                    elementWight = screen_width - 450;
                    break;
            }
        }
        filter.setLayoutParams(new FrameLayout.LayoutParams(elementWight, FrameLayout.LayoutParams.WRAP_CONTENT));

        if (currentFilter.equals(MapFragment.LOVED)&& receivedFilters.size()==0) {
            TextView text1 = new TextView(context);
            text.setText("You have not selected any points yet");
            filter.addView(text1);
            return;
        }

        ArrayList<String> lst = new ArrayList<>(receivedFilters.size());
        lst.addAll(receivedFilters);
        Collections.sort(lst);
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
         //   Log.i(TAG, lst.get(i));
            if (deviceType.equals("tablet")) {
                checkBox.setTextSize(context.getResources().getDimension(R.dimen.avg_textsize));
            }

            if (selectedFilters.contains(lst.get(i))) {
                // set checked checkbox
                checkBox.setChecked(true);
                Log.i(TAG, "should be checked: " + lst.get(i));
            }
            checkBox.setPadding(20,20,20,20);
            checkBox.setTag(lst.get(i));

            //setting the color of text and the box of check box
            checkBox.setTextColor(Color.BLACK);
            CompoundButtonCompat.setButtonTintList(checkBox,colorStateList);
            checkBox.setOnClickListener(checkBoxListener);
            filter.addView(checkBox, i+2);
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

    public static void manageFragments (Fragment fragmemt, FragmentManager fManager, boolean addToBackStack,
                                        int containerID, String action) {
        Log.d(TAG, "enter manageFragments");
        Fragment newFr = fragmemt;
        for (Fragment f: fManager.getFragments()) {
            if (f.getClass().getName().equals(fragmemt.getClass().getName())) {
                newFr = f;
                Log.i(TAG, "the same fragm exists");
                break;
            }
        }
        FragmentTransaction transaction = fManager.beginTransaction();
        if (action.equals("ADD")) {
            transaction.add(containerID, newFr);
        } else if (action.equals("REPLACE")) {
            transaction.replace(containerID, newFr);
        }
        if (addToBackStack == true) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
        Log.d(TAG, "exit manageFragments");
    }

}
