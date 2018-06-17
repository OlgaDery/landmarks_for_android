package com.google.albertasights.ui;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.support.v4.widget.Space;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.albertasights.R;
import com.google.albertasights.ui.MapsActivity;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
    public static final String SORTED_BY = "SORTED_BY";
    public static final String PORTRAIT = "PORTRAIT";
    public static final String LANDSCAPE = "LANDSCAPE";
    public static final String TABLET = "TABLET";
    public static final String PHONE = "PHONE";
    public static final String RESET_PASSWORD = "RESET_PASSWORD";
    public static final String UPDATE_PASSWORD = "UPDATE_PASSWORD";
    public static final String SEE_USER_DATA = "SEE_USER_DATA";
    public static final String RECREATE_USER = "RECREATE_USER";
    public static final String BY_RATING = "BY_RATING";
    public static final String BY_NAME = "BY_NAME";
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;


    private static final String TAG = "UiUtils";
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

    public static Integer getWidthInches (Context context) {
        WindowManager wm = (WindowManager)    context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        return metrics.widthPixels;
    }

    public static Integer getHightInches (Context context) {
        WindowManager wm = (WindowManager)    context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        return metrics.heightPixels;
    }

    public static String findScreenSize (Context context) {
        //TODO figure out if the device is a phone or a tablet
        //  Log.d(TAG, "enter setTextSizeGettingScreenSize (TextView textview, Context context)");
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);

        float yInches= metrics.heightPixels/metrics.ydpi;
        float xInches= metrics.widthPixels/metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches*xInches + yInches*yInches);
        if (diagonalInches>=6.5){
            // 6.5inch device or bigger
            return TABLET;
        }else{
            // smaller device
            return PHONE;
        }
    }

    public static String getOrientation (Context context) {
        String orientation;
        int orientationValue = context.getResources().getConfiguration().orientation;

        if (orientationValue == Configuration.ORIENTATION_PORTRAIT) {
            orientation = PORTRAIT;
        } else {
            orientation = LANDSCAPE;
        }

        return orientation;
    }


    public static void showToast(Context context, String message) {
        Toast.makeText(context,
                message,
                Toast.LENGTH_LONG).show();
    }

    public static void modifyButtons (Set<ImageButton> buttons, String activeButtonTag, boolean anyFiltersSelected) {
        Log.d(TAG, "enter modifyButtons (Set<ImageButton> buttons, String activeButtonTag)");
        for (ImageButton b : buttons) {
            if (b.getTag().equals(activeButtonTag) && anyFiltersSelected==true) {
                b.setColorFilter(new PorterDuffColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN));
            } else {
                b.setColorFilter(new PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN));
            }
        }
    }

    public static void setTextSize (Integer screenHight, TextView textView, String orientation,
                                    Boolean isHeader) {
      //  textView.setTextSize(screenHight/120);
        if (orientation.equals(UiUtils.PORTRAIT)) {
            textView.setTextSize(screenHight/110);
        } else {
            textView.setTextSize(screenHight/70);
        }
        if (isHeader==true){
            textView.setTypeface(null, Typeface.BOLD);
        }
    }

    public static SharedPreferences.Editor getEditor (Context context) {
        Log.d(TAG, "enter getEditor (Context context)");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        return editor;
    }

    public static void manageFragments (FragmentManager fManager, boolean addToBackStack,
                                        int containerID, Map<String, String> tagsAndActions) {
        Log.d(TAG, "enter manageFragments");
        FragmentTransaction transaction = fManager.beginTransaction();
        Map <String, Fragment> tagsAndFragments = new HashMap<>();
        tagsAndFragments.put("login", new NoUserFragment());
        tagsAndFragments.put("loader", new LoadingFragment());
        tagsAndFragments.put("sidebar", new SideBarFragment1());
        tagsAndFragments.put("progr1", new StatusBarFragment());
        tagsAndFragments.put("map", new MapFragment());
        tagsAndFragments.put("socialBt", new SocialButtonsFragment());
        tagsAndFragments.put("u1", new UserFragment());
        tagsAndFragments.put("banner", new AdsFragment());
        tagsAndFragments.put("point", new PointFragment());
        tagsAndFragments.put("connect", new StayInTouchFragment());

        for (String tag : tagsAndActions.keySet()) {
            Log.i(TAG, "searching : "+tag);
            String action = tagsAndActions.get(tag);
            Log.i(TAG, "what to do : "+ action);
            Fragment fragmemt = tagsAndFragments.get(tag);

            if (action.equals("ADD")) {
                Log.i(TAG, "fragment found to add");
                transaction.add(containerID, fragmemt, tag);

            } else if (action.equals("REPLACE")) {
                Log.i(TAG, "fragment found to replace");
                transaction.replace(containerID, fragmemt, tag);

            } else if (action.equals("REMOVE")) {
                fragmemt = fManager.findFragmentByTag(tag);
                if (fragmemt!=null) {
                    Log.i(TAG, "fragment found to remove");
                    transaction.remove(fragmemt);
                }
            } else if (action.equals("HIDE")) {
                fragmemt = fManager.findFragmentByTag(tag);
                if (fragmemt!=null) {
                    Log.i(TAG, "fragment found to hide");
                    transaction.hide(fragmemt);
                }
            } else if (action.equals("SHOW")) {
                fragmemt = fManager.findFragmentByTag(tag);
                if (fragmemt!=null) {
                    Log.i(TAG, "fragment found to show");
                    transaction.show(fragmemt);
                }
            }
        }
        if (addToBackStack == true) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
        for (Fragment f: fManager.getFragments()) {
            Log.i(TAG, "fragment exists: "+ f.getClass().getName());
        }
        Log.d(TAG, "exit manageFragments");
    }

    public static boolean checkIfFragmentAdded (String tag, FragmentManager fManager) {
        Log.d(TAG, "enter checkIfFragmentAdded (String tag, FragmentManager fManager)");

        Fragment fragment = fManager.findFragmentByTag(tag);
        if (fragment!=null) {
            Log.d(TAG, "fragment found, exit checkIfFragmentAdded (String tag, FragmentManager fManager)");
            return true;

        } else {
            Log.d(TAG, "fragment is null, exit checkIfFragmentAdded (String tag, FragmentManager fManager)");
            return false;
        }
    }

    public static void modifyIconSize (ImageButton fb, ImageButton insta,
                                       Integer hight, Integer wight, String orient) {
        if (orient.equals(UiUtils.PORTRAIT)) {

        }

    }

    public static void displayLocationSettingsRequest(final Activity activity, final MapViewModel viewModel) {
        Log.d(TAG, "enter displayLocationSettingsRequest(Context context)");

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        SettingsClient client = LocationServices.getSettingsClient(activity);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                viewModel.setGpsEnabled(true);
                Log.i(TAG, "All location settings are satisfied.");
            }
        });

        task.addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    Log.i(TAG, "Location settings are not satisfied. " +
                            "Show the user a dialog to upgrade location settings ");
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(activity,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                        viewModel.setGpsEnabled(false);
                    }
                } else {
                    Log.i(TAG, "Location settings are inadequate," +
                            " and cannot be fixed here. Dialog not created.");
                    viewModel.setGpsEnabled(false);
                }
            }
        });

    }


}
