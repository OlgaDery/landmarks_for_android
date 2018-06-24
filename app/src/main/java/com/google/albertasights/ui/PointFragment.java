package com.google.albertasights.ui;

import android.annotation.TargetApi;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.albertasights.DBIntentService;
import com.google.albertasights.R;
import com.google.albertasights.models.Place;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnPointFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PointFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PointFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private OnPointFragmentInteractionListener mListener;
    private Place point;
    private ListView listview;
    private static final String TAG = PointFragment.class.getSimpleName();
    private ImageButton closeButton;
    private String orientation;
    private String deviceType;
    private MapViewModel viewModel;

    public PointFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PointFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PointFragment newInstance(String param1, String param2) {
        return new PointFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "enter onCreate(Bundle savedInstanceState)");
        viewModel = ViewModelProviders.of(getActivity()).get(MapViewModel.class);
        viewModel.updateCurrentFragment(this.getClass().getSimpleName());
        Log.d(TAG, "exit onCreate(Bundle savedInstanceState)");
    }

    @Override
    @TargetApi(22)
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //getting the type and the orientation of device

        Log.d(TAG, "enter  onCreateView");
        final Map<String, Boolean> directionsRequested = new HashMap<>();
        directionsRequested.put("DIRECT", false);
        point = viewModel.getPointToSee().getValue();
        Log.i(TAG, "point in point activity: "+point.getName());
        View v = inflater.inflate(R.layout.activity_point, container, false);
        v.setTranslationZ(10.0f);
        v.setElevation(5.0f);

        MobileAds.initialize(getActivity(), "ca-app-pub-9273347200561604~7518194920");
        AdView mAdView = v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
//
        ImageButton fab = v.findViewById(R.id.fab);
        //   fab.getBackground().setAlpha(0);
        fab.setImageResource(R.drawable.directions);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onclick");
                directionsRequested.put("DIRECT", true);
                LocationManager locationManager =
                        (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
                if (ContextCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    //TODO check if GPS enable
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)==false){
                        Log.i(TAG, "gps disabled");
                        UiUtils.displayLocationSettingsRequest(getActivity(), viewModel);
                    } else {
                        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)",
                                point.getLat(), point.getLng(), "Going there");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }

                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            1);
                }
            }
        });

        final ImageButton likeButton = v.findViewById(R.id.like);
        // likeButton.getBackground().setAlpha(0);
        if (viewModel.getLoved().getValue()!=null) {
            Log.i(TAG, "loved not null");
        }
        if (viewModel.getLoved().getValue().contains(point.getName())) {
            likeButton.setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_IN));
        }
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO hook this method to the listener to make it visible for the activity
                Log.d(TAG, "onclick");
                String id =(view).getTag().toString();
                if (!viewModel.getLoved().getValue().contains(point.getName())) {
                    onButtonPressed(UiUtils.ADD_POINT_TO_LOVED);
                    likeButton.setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_IN));
                } else {
                    //call the method to remove from selected
                    onButtonPressed(UiUtils.REMOVE_POINT);
                    likeButton.setColorFilter(new PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN));
                }
//                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//                if (prefs.contains(UiUtils.LOGGED_IN)) {
//                    if (prefs.getBoolean(UiUtils.LOGGED_IN, true)==true) {
//
//
//                    } else {
//                        //TODO!!!!!!
//                        onButtonPressed(UiUtils.LOG_IN);
//                        //  UiUtils.showToast(getActivity(), "To use this option, please log in or create an account");
//                    }
//                } else {
//                    onButtonPressed(UiUtils.LOG_IN);
//                }

            }
        });

        likeButton.setImageResource(R.drawable.like);
        likeButton.setTag(point.getName());
        listview = v.findViewById(R.id.listView1);

        //TODO provide dimensions, position and device type via constructor
        PointListviewAdapter adapter = new PointListviewAdapter(point, getActivity(), viewModel.getOrienr().getValue(),
                viewModel.getDevice().getValue(), viewModel.getHight().getValue(), viewModel.getWight().getValue());
        listview.setAdapter(adapter);

        if (viewModel.getOrienr().getValue().equals(UiUtils.LANDSCAPE)) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(viewModel.getWight().getValue()/100*80,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            lp.addRule(Gravity.RIGHT);
            v.setLayoutParams(lp);

        }

        if (viewModel.getDevice().getValue().equals(UiUtils.TABLET)) {
            if (viewModel.getOrienr().getValue().equals(UiUtils.LANDSCAPE)) {
                v.setPadding(viewModel.getHight().getValue()/30, viewModel.getHight().getValue()/30
                        , viewModel.getHight().getValue()/30, viewModel.getHight().getValue()/30);
            } else {
                v.setPadding(viewModel.getWight().getValue()/30, viewModel.getWight().getValue()/30,
                        viewModel.getWight().getValue()/30, viewModel.getWight().getValue()/30);
            }

        }

        //TODO observer to change the permittions to access the geo data
        final Observer<Boolean> locationPermissionsObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isPermittionsGranted) {
                Log.d(TAG, "enter onChanged(@Nullable Boolean isPermittionsGranted)");
                if (isPermittionsGranted==true) {

                    //TODO check if GPS is enabled
                    LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)==false){
                        Log.i(TAG, "gps unabled");
                        UiUtils.displayLocationSettingsRequest(getActivity(), viewModel);
                    } else {
                        if (directionsRequested.get("DIRECT")==true) {
                            String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)",
                                    point.getLat(), point.getLng(), point.getName());
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            mapIntent.setPackage("com.google.android.apps.maps");
                            startActivity(mapIntent);
                            directionsRequested.put("DIRECT", false);
                        }
                    }

                } else {
                    UiUtils.showToast(getActivity(), "Sorry, you can not get directions without this permission.");

                }
            }
        };
        viewModel.getLocationAccessPermitted().observe(this,
                locationPermissionsObserver);

        //TODO observer to change the permittions to access the geo data
        final Observer<Boolean> gpsAccessObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isGpsEnabled) {
                Log.d(TAG, "enter onChanged(@Nullable Boolean isPermittionsGranted)");
                if (isGpsEnabled==false) {
                   //
                } else {
                    if (directionsRequested.get("DIRECT")==true) {
                        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)",
                                point.getLat(), point.getLng(), point.getName());
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                        directionsRequested.put("DIRECT", false);
                    }
                }
            }
        };
        viewModel.getGpsEnabled().observe(this,
                gpsAccessObserver);
        Log.d(TAG, "exit  onCreateView");
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String tag) {
        if (mListener != null) {
            mListener.onPointFragmentInteraction(tag);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "enter  onAttach(Context context)");
        if (context instanceof OnPointFragmentInteractionListener) {
            mListener = (OnPointFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPointFragmentInteractionListener");
        }
        Log.d(TAG, "exit  onAttach(Context context)");
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "enter ononDetach()");
        super.onDetach();
        mListener = null;
        Log.d(TAG, "exit onDetach()");
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnPointFragmentInteractionListener {
        // TODO: Update argument type and name
        void onPointFragmentInteraction(String action);
    }
}