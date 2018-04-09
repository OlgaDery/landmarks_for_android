package com.google.albertasights;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by olga on 4/1/18.
 */

public class PointListviewAdapter extends BaseAdapter {
  //  private ArrayList<String> content;
  private static final String TAG = PointListviewAdapter.class.getSimpleName();
    private String[] titles = {"Point name: ", "Description: ", "More info: ", "Rating: "};
    private Context context;
    private Place point;
    private String orientation;
    private String deviceType;

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

        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int screen_height = metrics.heightPixels;
        int screen_width = metrics.widthPixels;
        orientation = UiUtils.getOrientation(context);
        deviceType = UiUtils.findScreenSize(context);
        //      listItemsValue.clear();

        //initializing the photo
        ImageView photo = (ImageView) convertView.findViewById(R.id.img);
        RelativeLayout listView = (RelativeLayout) convertView.findViewById(R.id.listV);

        TextView name = (TextView) convertView.findViewById(R.id.name);
        name.setText(point.getName());
        TextView descript = (TextView) convertView.findViewById(R.id.descript);
        descript.setText(point.getDescript());
        TextView link = (TextView) convertView.findViewById(R.id.link);
        link.setText(titles[2] + point.getWebLink());

     //   Linkify.addLinks(link, Linkify.ALL);
        //TODO

        View.OnClickListener listener = new View.OnClickListener() {

            public void onClick(View v) {
                Log.d(TAG, "enter onClick (View view) ");
                try{
                    Intent myWebLink = new Intent(android.content.Intent.ACTION_VIEW);
                    myWebLink.setData(Uri.parse(point.getWebLink()));
                    context.startActivity(myWebLink);
                } catch (Exception e) {
                    UiUtils.showToast(context, "Error, maybe no browsers have been installed");
                }

                Log.d(TAG, "exit onClick (View view) ");
            }
        };
        link.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        link.setOnClickListener(listener);

        TextView rating = (TextView) convertView.findViewById(R.id.rating);
        rating.setText(titles[3] + point.getRating());

        if (deviceType.equals("tablet")) {
            descript.setTextSize(context.getResources().getDimension(R.dimen.big_textsize));
            name.setTextSize(context.getResources().getDimension(R.dimen.big_textsize));
            link.setTextSize(context.getResources().getDimension(R.dimen.big_textsize));
            rating.setTextSize(context.getResources().getDimension(R.dimen.big_textsize));


        } else {

        }

        //setting the photo

        if (point.getPhotoLink()!=null && point.getPhotoLink().length()>5)
        {
            if (orientation.equals("Portrait")) {
                //big portrait screen
                photo.getLayoutParams().height = screen_height/3;
                //TODO set the width of the parental element
                photo.getLayoutParams().width = screen_width-40;

            } else {
                //big landscape screen
                photo.getLayoutParams().height = screen_height/3+50;
                //TODO set the width of the parental element
                photo.getLayoutParams().width = screen_width - 150-40;
            }
            Picasso.with(context)
                    .load(UiUtils.parseUrl(point.getPhotoLink()))
                    .resize(photo.getLayoutParams().width, photo.getLayoutParams().height)
                    //    .onlyScaleDown()
                    .centerCrop()
                    .into(photo);

        } else
        {
            photo.getLayoutParams().height = screen_height/3-60;
            photo.getLayoutParams().width = screen_width/2-60;
            Picasso.with(context)
                    .load(R.drawable.no_ph)
                    .into(photo);
        }




        return convertView;
    }
}
