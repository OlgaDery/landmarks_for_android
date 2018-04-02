package com.google.albertasights;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by olga on 4/1/18.
 */

public class PointListviewAdapter extends BaseAdapter {
  //  private ArrayList<String> content;
    private String[] titles = {"Point name: ", "Description: ", "Web link: ", "Rating: "};
    private Context context;
    private Place point;

    public PointListviewAdapter (Place p, Context context1) {
        this.context = context1;
        this.point = p;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return this.point;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.point_row, null);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        name.setText(point.getName());
        TextView descript = (TextView) convertView.findViewById(R.id.descript);
        descript.setText(point.getDescript());
        TextView link = (TextView) convertView.findViewById(R.id.link);
        link.setText(titles[2] + point.getWebLink());

        Linkify.addLinks(link, Linkify.ALL);
        TextView rating = (TextView) convertView.findViewById(R.id.rating);
        rating.setText(titles[3] + point.getRating());

        return convertView;
    }
}
