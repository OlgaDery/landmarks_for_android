package com.google.albertasights;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Spinner;

/**
 * Created by olga on 2/25/18.
 */

public class MySpinner extends android.support.v7.widget.AppCompatSpinner {

    private OnItemSelectedListener listener;
    private static final String TAG = MySpinner.class.getSimpleName();
    public static boolean firstTimeSelected = false;

    public MySpinner(Context context) {
        super(context);
    }

    public static MySpinner spinnerWithPosition(Context context, View below, View above) {
        MySpinner spinner = new MySpinner(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW, below.getId());
        layoutParams.addRule(RelativeLayout.ABOVE, above.getId());
        spinner.setLayoutParams(layoutParams);



        return spinner;

    }

    @Override
    public void setSelection(int position) {
   //     Log.i(TAG, "enter setSelection(int position)");
        super.setSelection(position);
   //     Log.i(TAG, "posit: " + position);
        if (listener != null) {
            listener.onItemSelected(null, null, position, 0);
        }
   //     Log.i(TAG, "exit setSelection(int position)");
    }

    public void setOnItemSelectedEvenIfUnchangedListener(
            OnItemSelectedListener listener) {
        this.listener = listener;
    }
}
