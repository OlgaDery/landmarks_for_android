package com.google.albertasights;

import com.google.android.gms.maps.GoogleMap;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
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

    //TODO TEST PHOTOS
    @Override
    public View getInfoContents(Marker marker) {
        //    Log.d(TAG, "enter getInfoContents(Marker marker)"+ marker.getId());
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
            ImageButton button = (ImageButton) v.findViewById(R.id.directions);
            button.setImageResource(R.drawable.directions);
            if (deviceType.equals("tablet")) {
                descr.setTextSize(context.getResources().getDimension(R.dimen.avg_textsize));
                name.setTextSize(context.getResources().getDimension(R.dimen.big_textsize));
            }

            //get screen size
            WindowManager wm = (WindowManager)    context.getSystemService(Context.WINDOW_SERVICE);
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
                    //big vertical
                    RelativeLayout ll = (RelativeLayout) v.findViewById(R.id.base);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams
                            ((screen_width/100)*70, ViewGroup.LayoutParams.WRAP_CONTENT);
                    ll.setLayoutParams(layoutParams);
                    //  ll.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    //  ll.getLayoutParams().width = 400;
                    photo.getLayoutParams().height = 300;//screen_height/2-20;
                    photo.getLayoutParams().width = (screen_width/100)*65-20;

                } else {
                    //big horizontal
                    RelativeLayout rl = (RelativeLayout)v.findViewById(R.id.base);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(screen_width/100*70,
                            screen_height-80);
                    rl.setLayoutParams(layoutParams);
                    photo.getLayoutParams().height = 300;//(screen_height-80)-50;
                    photo.getLayoutParams().width = 300;//rl.getWidth()/20-30;

                }

            }

            for (Place p: places) {
                if (p.getName().equals(marker.getTitle())) {
                    name.setText(p.getName());
                    //TODO!!
                    Place.selectedMarkerID = p.getName();
                    String newDescr;
                    if (p.getDescript().length() > 200) {
                        newDescr = p.getDescript().substring(0, 201);
                    } else {
                        newDescr = p.getDescript();
                    }
                    descr.setText(newDescr);


                    // to get a link
                    if (!MapsActivity.markerIds.contains(marker.getTitle())) {
                        //      Log.i(TAG, "not first time opening");
                        if (toLoad.equals("photo")) {
                            Picasso.with(context)
                                    .load(R.drawable.filter)
                                    .into(photo);

                        } else {
                            Picasso.with(context)
                                    .load(UiUtils.parseUrl(p.getPhotoLink()))
                                    .into(photo);

                        }
                        toLoad= null;
                        count = 0;
                    } else {
                        // not_first_time_showing_info_window = true;
                        //     Log.i(TAG, "first time opening");
                        count++;
                        if (toLoad==null) {
                            if (!UiUtils.parseUrl(p.getPhotoLink()).equals("no")) {

                                toLoad = "url";
                             //   Log.d(TAG, "setting toLoad eq url");

                            } else {
                              //  Log.d(TAG, "setting toLoad eq photo");
                                toLoad = "photo";

                            }
                        }
                        loadPicasso(UiUtils.parseUrl(p.getPhotoLink()), marker, photo);
//                        try {
//
//                            MapsActivity.markerIds.remove(marker.getTitle());
//                            Log.d(TAG, "picture loaded, id removed");
//                            //toLoad is staying the same
//                        } catch (Exception e1) {
//                            toLoad = "photo";
//                            Log.d(TAG, "link does not work");
//
//                        } finally {
//                            marker.showInfoWindow();
//                            Log.d(TAG, "another time opening window");
//                        }

                        break;
                    }

                }
            }
            //    Log.d(TAG, "exit getInfoContents(Marker marker)");

            return v;
        } catch (Exception e) {
     //       Log.e(TAG, e.getMessage());
            toLoad=null;
            return null;
        }

    }

    private void loadPicasso (String url, Marker marker, ImageView photo) {
        if (this.toLoad.equals("url")) {
            Picasso.with(context)
                    .load(url)
                    .into(photo, new InfoWindowRefresher(marker));
            //   Log.i(TAG, "id removed: "+ )) ;

        } else {
            Picasso.with(context)
                    .load(R.drawable.filter)
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
    //        Log.d(TAG, "enter onSuccess()");
            //    Log.i(TAG, "count: "+ count);
            MapsActivity.markerIds.remove(markerToRefresh.getTitle());
            if (count>2) {
                count=0;
                return;
            }
            markerToRefresh.showInfoWindow();

    //        Log.d(TAG, "exit onSuccess()");

        }

        @Override
        public void onError() {
     //       Log.d(TAG, "enter onError()");
            toLoad = "photo";
            if (count>2) {
                count=0;
                return;
            }
            markerToRefresh.showInfoWindow();

      // Log.d(TAG, "exit onError()");

        }
    }
}
