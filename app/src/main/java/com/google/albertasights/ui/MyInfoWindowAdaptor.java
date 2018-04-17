package com.google.albertasights.ui;

import com.google.albertasights.R;
import com.google.albertasights.UiUtils;
import com.google.albertasights.models.Place;
import com.google.android.gms.maps.GoogleMap;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Set;

/**
 * Created by olga on 2/13/18.
 */

public class MyInfoWindowAdaptor implements GoogleMap.InfoWindowAdapter {
    private TextView name;
    private TextView descr;
    private ImageView photo;
    private Context context;
    private Set<Place> places;
    private String orientation;
    private String deviceType;
    private String toLoad;
    int count = 0;
    private static final String TAG = MyInfoWindowAdaptor.class.getSimpleName();

    public MyInfoWindowAdaptor (Context context1, Set<Place> places1, String orient, String device) {
        //  Log.d(TAG, "enter MyInfoWindowAdaptor (Context context1, Set<Place> places1)");

        this.context = context1;
        this.places= places1;
        this.orientation=orient;
        this.deviceType=device;
        //   Log.d(TAG, "exit MyInfoWindowAdaptor (Context context1, Set<Place> places1)");
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
     //   Log.d(TAG, "enter getInfoContents(Marker marker)"+ marker.getId());
        //   String type = UiUtils.findScreenSize(context);
        View v;

        try{
            if (orientation.equals("Portrait")) {
                v = LayoutInflater.from(context).inflate(R.layout.custom_info_contents, null);
            } else {
                v = LayoutInflater.from(context).inflate(R.layout.custom_info_contents1, null);
            }

            descr = (TextView) v.findViewById(R.id.descript);
            name = (TextView) v.findViewById(R.id.name);
            photo = (ImageView) v.findViewById(R.id.img);
            ImageButton button = (ImageButton) v.findViewById(R.id.more);
            button.setImageResource(R.drawable.expand_more);
            if (deviceType.equals("tablet")) {
                descr.setTextSize(context.getResources().getDimension(R.dimen.avg_textsize));
                name.setTextSize(context.getResources().getDimension(R.dimen.big_textsize));
            }

            //get screen size
            WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            int screen_height = metrics.heightPixels;
            int screen_width = metrics.widthPixels;
            if (deviceType.equals("tablet")) {
                if (orientation.equals("Portrait")) {
                    //big vertical
                    RelativeLayout ll = (RelativeLayout) v.findViewById(R.id.base);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams
                            (screen_width/2, ViewGroup.LayoutParams.WRAP_CONTENT);
                    ll.setLayoutParams(layoutParams);
                    //  ll.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    //  ll.getLayoutParams().width = 400;
                    photo.getLayoutParams().height = screen_height/3-20;
                    photo.getLayoutParams().width = screen_width/2-20;

                } else {
                    //big horizontal
                    RelativeLayout rl = (RelativeLayout)v.findViewById(R.id.base);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(screen_width/2, screen_height/2);
                    rl.setLayoutParams(layoutParams);
                    photo.getLayoutParams().height = screen_height/3;
                    photo.getLayoutParams().width = screen_width/4;

                }

            } else {

                if (orientation.equals("Portrait")) {
                    //small vertical
                    RelativeLayout ll = (RelativeLayout) v.findViewById(R.id.base);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams
                            ((screen_width/100)*70, ViewGroup.LayoutParams.WRAP_CONTENT);
                    ll.setLayoutParams(layoutParams);
                    //  ll.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    //  ll.getLayoutParams().width = 400;
                    photo.getLayoutParams().height = 300;//screen_height/2-20;
                    photo.getLayoutParams().width = (screen_width/100)*70-20;

                } else {
                    //small horizontal
                    RelativeLayout rl = (RelativeLayout)v.findViewById(R.id.base);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(screen_width/100*70,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    rl.setLayoutParams(layoutParams);
                    photo.getLayoutParams().height = 250;//(screen_height-80)-50;
                    photo.getLayoutParams().width = 330;//rl.getWidth()/20-30;

                }

            }

            for (Place p: places) {
                if (p.getName().equals(marker.getTitle())) {
                    name.setText(p.getName());
                    //TODO!!
                    Place.selectedMarkerID = p.getName();
                    String newDescr;
                    if (p.getDescript().length() > 150) {
                        newDescr = p.getDescript().substring(0, 150);
                    } else {
                        newDescr = p.getDescript();
                    }
                    descr.setText(newDescr + "...");

                    // to get a link
                    if (!MapsActivity.markerIds.contains(marker.getTitle())) {
                             Log.i(TAG, "link: "+ p.getPhotoLink());
                        if (p.getPhotoLink()!=null && p.getPhotoLink().length()>5)
                        {
                            Picasso.with(context)
                                    .load(UiUtils.parseUrl(p.getPhotoLink()))
                                    .resize(photo.getLayoutParams().width, photo.getLayoutParams().height)
                                    .centerCrop()
                                    .into(photo);

                        } else
                        {
                            Picasso.with(context)
                                    .load(R.drawable.no_ph)
                                    .into(photo);
                        }

                        toLoad= null;
                        count = 0;
                    } else {
                        // not_first_time_showing_info_window = true;
                            Log.i(TAG, "first time opening");
                        count++;
                        if (toLoad==null) {
                            if (!UiUtils.parseUrl(p.getPhotoLink()).equals("no")) {

                                toLoad = "url";
                                Log.d(TAG, "setting toLoad eq url");

                            } else {
                              Log.d(TAG, "setting toLoad eq photo");
                                toLoad = "photo";

                            }
                        }
                        loadPicasso(UiUtils.parseUrl(p.getPhotoLink()), marker, photo);

                        break;
                    }

                }
            }
    //   Log.d(TAG, "exit getInfoContents(Marker marker)");

            return v;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            toLoad=null;
            return null;
        }

    }

    private void loadPicasso (String url, Marker marker, ImageView photo) {
        if (this.toLoad.equals("url")) {
            Picasso.with(context)
                    .load(url)
                    .resize(photo.getLayoutParams().width, photo.getLayoutParams().height)
                    .centerCrop()
                    .into(photo, new InfoWindowRefresher(marker));
            //   Log.i(TAG, "id removed: "+ )) ;

        } else {
            Picasso.with(context)
                    .load(R.drawable.no_ph)
                    .into(photo, new InfoWindowRefresher(marker));
            //    Log.i(TAG, "id removed: "+ MapsActivity.markerIds.remove(marker.getTitle())) ;

        }
    }

    private class InfoWindowRefresher implements Callback {

        private Marker markerToRefresh;

        private InfoWindowRefresher(Marker markerToRefresh) {
            this.markerToRefresh = markerToRefresh;
        }

        @Override
        public void onSuccess() {
            Log.d(TAG, "enter onSuccess()");
            //    Log.i(TAG, "count: "+ count);
            MapsActivity.markerIds.remove(markerToRefresh.getTitle());
            if (count>2) {
                count=0;
                return;
            }
            markerToRefresh.showInfoWindow();

            Log.d(TAG, "exit onSuccess()");

        }

        @Override
        public void onError() {
            Log.d(TAG, "enter onError()");
            toLoad = "photo";
//            //TODO replace with iterator
//            for (Place p: places) {
//                if (p.getName().equals(markerToRefresh.getTitle())) {
//                    p.setPhotoLink("no");
//                }
//            }

            if (count>2) {
                count=0;
                return;
            }
            markerToRefresh.showInfoWindow();

         Log.d(TAG, "exit onError()");

        }
    }
}
