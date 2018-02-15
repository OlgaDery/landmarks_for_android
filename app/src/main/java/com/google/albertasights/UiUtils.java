package com.google.albertasights;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.CompoundButtonCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Created by olga on 2/13/18.
 */

public class UiUtils {
    private static final String TAG = MapsActivity.class.getSimpleName();

    public static boolean showFilters = false;

    public static void loadImage(String url, Context context, ImageView img) {
        //   Log.d(TAG, "enter loadImage(String url, Context context)");

        try {
            String modifiedUrl ="";

            if (url!=null && url.length()>5) {
                modifiedUrl = url.replace("\\", "");
                //   Log.i(TAG, "modified: "+modifiedUrl);

            } else {
                modifiedUrl = "https://www.ivestraining.com/Assume-Nothing.png";
                //    Log.d(TAG, "no image link");
            }
            Picasso.with(context).load(modifiedUrl).into(img);
            //  Glide.with(context).load(modifiedUrl).into(img);


        } catch (Exception e) {
            Log.e(TAG, e.getMessage());

        } finally {

            //     Log.d(TAG, "exit loadImage(String url, Context context)");
        }


    }

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
        String orientation = null;
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
                                         View.OnClickListener checkBoxListener) {


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
            filter.addView(checkBox, i);

        }
    }

}
