package com.google.albertasights.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.albertasights.R;
import com.google.albertasights.models.Place;
import com.squareup.picasso.Picasso;

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
    int screenH;
    int screenW;

    public PointListviewAdapter (Place p, Context context1, String orient, String device, int screenHight, int screenWight) {
        this.context = context1;
        this.point = p;
        this.screenH = screenHight;
        this.screenW = screenWight;
        this.orientation = orient;
        this.deviceType = device;
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

        if (point.getWebLink().length()>2) {
            link.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            link.setOnClickListener(listener);
        }

        TextView rating = (TextView) convertView.findViewById(R.id.rating);
        if (point.getRating().equals("5")) {
            rating.setText(titles[3] + "*****");
        } else if (point.getRating().equals("4")) {
            rating.setText(titles[3] + "****");
        } else if (point.getRating().equals("3")) {
            rating.setText(titles[3] + "***");
        } else if (point.getRating().equals("2")) {
            rating.setText(titles[3] + "**");
        } else {
            rating.setText(titles[3] + "*");
        }

        if (deviceType.equals(UiUtils.TABLET)) {
            descript.setTextSize(context.getResources().getDimension(R.dimen.big_textsize));
            name.setTextSize(context.getResources().getDimension(R.dimen.big_textsize));
            link.setTextSize(context.getResources().getDimension(R.dimen.big_textsize));
            rating.setTextSize(context.getResources().getDimension(R.dimen.big_textsize));
        }
        //setting the photo

        if (point.getPhotoLink()!=null && point.getPhotoLink().length()>5)
        {
            if (orientation.equals(UiUtils.PORTRAIT)) {
                // portrait screen, set the width of the parental element
              photo.getLayoutParams().height = screenH/3-50;
              photo.getLayoutParams().width = screenW-40;

            } else {
                // landscape screen
                photo.getLayoutParams().height = screenH/2-50;
                //TODO set the width of the parental element
                photo.getLayoutParams().width = screenW/100*70;
            }
            Picasso.with(context)
                    .load(UiUtils.parseUrl(point.getPhotoLink()))
                    .resize(photo.getLayoutParams().width, photo.getLayoutParams().height)
                    .centerCrop()
                    .into(photo);

        } else
        {
            photo.getLayoutParams().height = screenH/3-60;
            photo.getLayoutParams().width = screenW/2-60;
            Picasso.with(context)
                    .load(R.drawable.no_ph)
                    .into(photo);
        }

        return convertView;
    }
}
