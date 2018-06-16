package com.google.albertasights.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.albertasights.R;
import com.google.albertasights.models.Place;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by olga on 4/1/18.
 */

public class FilterListviewAdapter extends BaseAdapter {
  private LinkedList<String> content;
  private static List<String> selected;
  private static List <String> rating;
  private static final String TAG = FilterListviewAdapter.class.getSimpleName();
  private Context context;
  private String deviceType;
  private static String currentFilter;
  int screenH;
  int screenW;

    public FilterListviewAdapter(LinkedList<String> names, Context context1, String device, int screenHight, int screenWight) {
        this.context = context1;
        this.content = names;
        this.screenH = screenHight;
        this.screenW = screenWight;
        this.deviceType = device;
       // this.selected = selectedPoints;
      //  Log.d(FilterListviewAdapter.class.getCanonicalName(), "names: "+names.size());

    }

    @Override
    public int getCount() {

        return content.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "enter getView(int position, View convertView, ViewGroup parent)");

        //LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.layout);
        if (parent.getId()==R.id.rating_listView_id) {
            convertView = LayoutInflater.from(context).inflate(R.layout.content_list_view1, null);
            ImageView img = (ImageView) convertView.findViewById(R.id.img);
            // layout.addView(img);
            if (content.get(position).equals("3")) {
                img.setImageDrawable(context.getResources().getDrawable(R.drawable.great));
            } else if (content.get(position).equals("2")) {
                img.setImageDrawable(context.getResources().getDrawable(R.drawable.good));
            } else {
                img.setImageDrawable(context.getResources().getDrawable(R.drawable.not_bad));
            }

        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.content_list_view, null);
            TextView txt = (TextView) convertView.findViewById(R.id.filterTxt);
            txt.setPadding(0, screenH/55, 0, 0);
            txt.setText(content.get(position));
            UiUtils.setTextSize(screenH, txt, UiUtils.getOrientation(context), false);
        }

        if (selected!=null) {
            if (selected.contains(content.get(position)))
                convertView.setBackgroundColor(Color.LTGRAY);
        }
        if (rating!=null) {
            if (rating.contains(content.get(position)))
                convertView.setBackgroundColor(Color.LTGRAY);
        }

        convertView.setTag(content.get(position));
        return convertView;
    }

    public static void updateListOfSelectedFilters (LinkedList<String> newList){
        selected=newList;
    }

    public static void setCurrenrFilter(String filter) {
        currentFilter=filter;
    }

    public static void setRating (LinkedList<String> r) {
        rating = r;
    }

}
