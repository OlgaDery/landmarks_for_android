package com.google.albertasights;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static android.R.attr.galleryItemBackground;
import static android.R.attr.value;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, AdapterView.OnItemSelectedListener,
        GoogleMap.OnInfoWindowClickListener, GoogleMap.OnInfoWindowCloseListener{
    // LocationListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraMoveStartedListener,

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // The entry point to Google Play services, used by the Places API and Fused Location Provider.
    private GoogleApiClient mGoogleApiClient;
    private final LatLng mDefaultLocation = new LatLng(51.052645, -114.029200);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private float currentZoom = 0.0f;

    private TextView txt;
    private Spinner mySpinner;
    private LinearLayout filter;
    private RelativeLayout rr;
    private ImageButton imgbutton;
    private TextView filterText;
    private String orientation;
    private int orientationValue;
    private String deviceType;
    private boolean isRestarted = false;

    // Declare a variable for the cluster manager.
    private ClusterManager<MyClusterItem> mClusterManager;

    //has to be saved as the SavedInstance
    private HashSet<Place> places = new HashSet<>();
    //filters selected by user
    private ArrayList<String> selectedFilters = new ArrayList<>();
    //filters received from APIs
    private ArrayList<String> receivedFilters = new ArrayList<>();
    //to store markerIDs to track the photo loading, if the photo of the marker has once been loaded, it`s Id should be removed
    public static Set<String> markerIds = new HashSet<>();
    //all the markers is stored here and extracted to modify
    //   public Set<Marker> markers = new HashSet<>();
    //boolean to indicate if the markers must be shown on the map after the activity has been recreated
    private boolean selectPointsToShow = false;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private boolean isDataRequestedFromDropDown = false;
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final String CURRENT_ZOOM = "zoom";
    private static final String KEY_MARKER_IDS = "marker_ids";
    private static final String KEY_RECEIVED_FILTERS = "received_fltrs";
    private static final String KEY_SELECTED_FILTERS = "selected_fltrs";
    private static final String KEY_SELECT_POINTS_TO_SHOW = "select_points_to_show";
    private static final String KEY_PLACES = "places";
    private View.OnClickListener checkBoxListener;

    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //there may be two options: the activity created the first time whan the app just started, or it is recreated. The behaviour
        //will be different.
        //      Log.d(TAG, "enter onCreate");
        super.onCreate(savedInstanceState);

        // Retrieve all the saved variable
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
            receivedFilters = savedInstanceState.getStringArrayList(KEY_RECEIVED_FILTERS);
            selectedFilters = savedInstanceState.getStringArrayList(KEY_SELECTED_FILTERS);
            selectPointsToShow = savedInstanceState.getBoolean(KEY_SELECT_POINTS_TO_SHOW);
            ArrayList<String> ids = savedInstanceState.getStringArrayList(KEY_MARKER_IDS);
            currentZoom = savedInstanceState.getFloat(CURRENT_ZOOM);
            places = (HashSet)savedInstanceState.getSerializable(KEY_PLACES);
            markerIds.addAll(ids);

        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);

        if (places.size()==0) {
            //      Log.i(TAG, "no data yet");
            Intent intent = new Intent(this, MyIntentService.class);
            intent.setAction("SUBMIT");
            intent.putExtra(MyIntentService.URL, "http://albertasights.com/rest/v1/findClosePoints");
            intent.putExtra(MyIntentService.LNG, String.valueOf(mDefaultLocation.longitude));
            intent.putExtra(MyIntentService.LAT, String.valueOf(mDefaultLocation.latitude));
            intent.putExtra(MyIntentService.DISTANCE, String.valueOf(20));
            startService(intent);
        }

        //getting the type and the orientation of device
        orientation = UiUtils.getOrientation(getApplicationContext());
        deviceType = UiUtils.findScreenSize(getApplicationContext());

        //initializing the elements
        txt =(TextView) findViewById(R.id.savePlace);
        filterText = (TextView) findViewById(R.id.txt);

        mySpinner = (Spinner) findViewById(R.id.spinner);
        rr = (RelativeLayout) findViewById(R.id.rr);
        imgbutton = (ImageButton) findViewById(R.id.imageB);
        imgbutton.setImageResource(R.drawable.expand_more);
        imgbutton.getBackground().setAlpha(0);
        if (deviceType.equals("tablet")) {
            txt.setTextSize(getApplicationContext().getResources().getDimension(R.dimen.big_textsize));
            filterText.setTextSize(getApplicationContext().getResources().getDimension(R.dimen.big_textsize));
        }

        filter = (LinearLayout) findViewById(R.id.filters);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.distance, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        mySpinner.setAdapter(adapter);
        mySpinner.setOnItemSelectedListener(this);
        //this listener starts when the user check the checkbox in
        checkBoxListener = new View.OnClickListener() {

            public void onClick(View v) {
                //    Log.d(TAG, "enter onClick checkBox(View view) ");
                String checked =((CheckBox) v).getTag().toString();
                if (selectedFilters.contains(checked)) {
                    selectedFilters.remove(checked);
                } else {
                    selectedFilters.add(checked);
                }
                //      Log.d(TAG, "size of selected: "+selectedFilters.size());
                // showMarkers();
                showClusters();

                //      Log.d(TAG, "exit onClick checkBox(View view) ");
            }
        };

        // Build the Play services client for use by the Fused Location Provider and the Places API.
        // Use the addApi() method to request the Google Places API and the Fused Location Provider.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();

        receiver = new BroadcastReceiver () {
            @Override
            public void onReceive(Context context, Intent intent) {
                //On receive should be called very rarely, onle the current location is significantly changed. In our case, it is calling
                //onle once
                //      Log.d(TAG, "enter onReceive(Context context, Intent intent)");

                //TODO put the data in the service to the intent and send to the MapActivity

                if (intent.getSerializableExtra("PLACES")!=null) {
                    ArrayList<Place> placesLst = (ArrayList)intent.getSerializableExtra("PLACES");
                    //         Log.i(TAG, "places: "+ placesLst.size());
                    places.addAll(placesLst);
                    HashSet<String> filters = new HashSet<String> ();
                    for (Place p: places) {
                        filters.add(p.getCategory());
                    }
                    receivedFilters.addAll(filters);
                    //TODO if sent from the drop down, update u and show clusters
                    if (isDataRequestedFromDropDown == true && mMap!=null) {
                        showClusters();
                        updateLocationUI();
                        isDataRequestedFromDropDown=false;
                    }

                    //TODO show toast if the service failed
                    Toast.makeText(getApplicationContext(),
                            "got the data",
                            Toast.LENGTH_LONG).show();

                }
                //       Log.d(TAG, "exit onReceive(Context context, Intent intent)");
            }
        };

        if (places.size()>0) {

            if (UiUtils.showFilters==true) {
                //the distance was selected before activity was destroyed, the UI contained the filters and the filter button.
                //We are recreating the filters with check boxes

                UiUtils.configureFilters(getApplicationContext(), filter, deviceType,
                        receivedFilters, selectedFilters, checkBoxListener);

                mySpinner.setSelection(Place.distance);

                //this means that the activity is recreated and the points have been received from the server
                if (selectPointsToShow==false) {
                    //       Log.d(TAG, "removing filter button");

                    imgbutton.setAlpha(0.0f);
                    filterText.setText("");

                }
            }

        } else {
            //      Log.d(TAG, "removing filter button - 2");
            imgbutton.setAlpha(0.0f);
            filterText.setText("");

        }

        //    Log.d(TAG, "exit onCreate");
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //   Log.d(TAG, "enter onSaveInstanceState");
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            ArrayList <String> ids = new  ArrayList <String>(markerIds.size());
            ids.addAll(markerIds);
            outState.putStringArrayList(KEY_MARKER_IDS, ids);
            outState.putSerializable(KEY_PLACES, places);
            outState.putStringArrayList(KEY_RECEIVED_FILTERS, receivedFilters);
            outState.putStringArrayList(KEY_SELECTED_FILTERS, selectedFilters);
            outState.putBoolean(KEY_SELECT_POINTS_TO_SHOW, selectPointsToShow);
            outState.putFloat(CURRENT_ZOOM, mMap.getCameraPosition().zoom);

            //TODO save info window
            for (Marker m: mClusterManager.getMarkerCollection().getMarkers()) {
                if (m.isInfoWindowShown()==true) {
                    Place.selectedMarkerID = m.getTitle();
                    //           Log.i(TAG, "saving open info window");
                }
            }
            super.onSaveInstanceState(outState);
        }
        Log.d(TAG, "exit onSaveInstanceState");
    }

    /**
     * Builds the map when the Google Play services client is successfully connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Build the map.
        //    Log.d(TAG, "enter onConnected");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //   Log.d(TAG, "exit onConnected");
    }

    /**
     * Handles failure to connect to the Google Play services client.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Refer to the reference doc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        //   Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
        //          + result.getErrorCode());
    }

    /**
     * Handles suspension of the connection to the Google Play services client.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        //  Log.d(TAG, "Play services connection suspended");
    }

    @Override
    protected void onResume() {
        //    Log.d(TAG, "enter onResume()");
        registerReceiver();
        super.onResume();
        //    Log.d(TAG, "exit onResume()");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isRestarted = true;
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        //   Log.d(TAG, "enter onMapReady");
        mMap = map;
        //setting the custom window adapter
        if (isRestarted == false) {
            mClusterManager = new ClusterManager<MyClusterItem>(this, mMap);
            mMap.setOnCameraIdleListener(mClusterManager);

            MyInfoWindowAdaptor adaptor = new MyInfoWindowAdaptor(getApplicationContext(), places,
                    orientation, deviceType);
            mMap.setInfoWindowAdapter(adaptor);

            mMap.setOnInfoWindowClickListener(this);

            // this listener has to remove checkboxes from the filter layout
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                @Override
                public boolean onMarkerClick(Marker m) {
                    //      Log.d(TAG, "enter onMarkerClick(Marker m)");

                    if (m.getTitle()==null) {
                        //          Log.d(TAG, "cluster clicked");
                        return true;
                    } else {
                        if (UiUtils.showFilters==true) {
                            UiUtils.showFilters=!UiUtils.showFilters;
                            mCameraPosition = mMap.getCameraPosition();
                            currentZoom = mMap.getCameraPosition().zoom;
                            updateLocationUI();
                            mCameraPosition=null;
                        }

                        m.showInfoWindow();

                        //        Log.d(TAG, "exit onMarkerClick(Marker m)");
                        return true;
                    }

                }
            });
        }


        if (places.size()==0) {

        } else {
            //if not, that means that the activity is being recreated and the points already received from the server
            if (selectPointsToShow == true && isRestarted==false) {
                showClusters();
            }
        }
        //TODO on restart the device loozing camera position, need to be solved!
        updateLocationUI();
//        if (Place.selectedMarkerID != null) {
//            showMarkers();
//        }
        //TODO to show marker content maybe on Google map and then remove marker


        isRestarted=false;
//        Log.d(TAG, "exit onMapReady");
    }

    private void registerReceiver() {
//        Log.d(TAG, "enter registerReceiver() for DATA_RECEIVED");
        // Create an intent filter for DATA_RECEIVED.
        IntentFilter intentFilter =
                new IntentFilter();
        intentFilter.addAction("DATA_RECEIVED");

        // Register the receiver and the intent filter.
        registerReceiver(receiver,
                intentFilter);
        //       Log.d(TAG, "exit registerReceiver() for DATA_RECEIVED");
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }


    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {

        //      Log.d(TAG, "enter updateLocationUI");
        if (mMap == null) {
            Toast.makeText(getApplicationContext(),
                    "Map is currently unavailable",
                    Toast.LENGTH_LONG).show();
            return;
        }

        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {

            //TODO request the data here
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            try {

                mLastKnownLocation = LocationServices.FusedLocationApi
                        .getLastLocation(mGoogleApiClient);
                //          Log.i(TAG, "lat " + Double.toString(mLastKnownLocation.getLatitude()));
                //          Log.i(TAG, "lng " + Double.toString(mLastKnownLocation.getLongitude()));
                LatLng myLatLng;

                //TODO reset the zoom level for tablets

                if(mLastKnownLocation !=null) {
                    //that means that the saved instance is recreated and the camera may be centred not on the device location
                    if (mCameraPosition!= null) {
                        myLatLng =  new LatLng(mCameraPosition.target.latitude,
                                mCameraPosition.target.longitude);
                    } else {
                        myLatLng = new LatLng(mLastKnownLocation.getLatitude(),
                                mLastKnownLocation.getLongitude());
                    }

                    //move the camera according to selected or default distance
                    if (currentZoom==0.0f) {
                        //it is not set
                        if (Place.distance==1 && deviceType.equals("phone")) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,
                                    18.0f));
                            //           Log.i(TAG, "camera is 18");

                        } else if (Place.distance==3 && deviceType.equals("phone")) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,
                                    13.0f));
                            //           Log.i(TAG, "camera is 13");

                        } else if (Place.distance==5 && deviceType.equals("phone")) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,
                                    12.0f));
                            //          Log.i(TAG, "camera is 12");

                        } else if (Place.distance==7 && deviceType.equals("phone")) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,
                                    11.5f));
                            //            Log.i(TAG, "camera is 11");

                        } else if (Place.distance>=10 && deviceType.equals("phone")){
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,
                                    11.0f));
                            //            Log.i(TAG, "camera is 10");
                        }

                        else if (Place.distance==1 && deviceType.equals("tablet")) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,
                                    16.0f));
                        }

                        else if (Place.distance==3 && deviceType.equals("tablet")) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,
                                    14.5f));

                        } else if (Place.distance==5 && deviceType.equals("tablet")) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,
                                    13.0f));

                        } else if (Place.distance==7 && deviceType.equals("tablet")) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,
                                    12.5f));

                        } else if (Place.distance==10 && deviceType.equals("tablet")) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,
                                    12.0f));
                        }
                    } else {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,
                                currentZoom));
                    }
                    currentZoom = 0.0f;
                    mCameraPosition = null;

                }


                //    Log.i(TAG, "show filter in UI_update: "+UiUtils.showFilters);
                if (!UiUtils.showFilters) {
                    //        Log.d(TAG, "show filters false");
                    filter.removeAllViews();


                } else {
                    //       Log.d(TAG, "need to show filters");
                    filter.removeAllViews();
                    try {
                        UiUtils.configureFilters(getApplicationContext(), filter, deviceType,
                                receivedFilters, selectedFilters, checkBoxListener);

                    } catch (Exception e) {
                        //           Log.i(TAG, "filter layer already added");
                    }
                }

                if (places.size()>0 && selectPointsToShow==true) {
                    // set full opacity
                    //       Log.i(TAG, "setting full opacity");
                    imgbutton.setAlpha(1.0f);
                    filterText.setText("Hide categories:");

                    //  resetDistance = true;
                } else if (selectPointsToShow==false || places.size()==0) {
                    //       Log.i(TAG, "trying to remove filter elements again");
                    // set full transparancy
                    imgbutton.setAlpha(0.0f);
                    filterText.setText("");

                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

            //  Log.i(TAG, "non-clustered markers: "+mClusterManager.getMarkerCollection().getMarkers().size());

        } else {
            mMap.setMyLocationEnabled(false);
            //   Log.d(TAG, "smth wrong with permissions");
            //TODO show the toast that permiss not granted
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mLastKnownLocation = null;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //    Log.d(TAG, "enter onItemSelected(AdapterView<?> parent, View view, int position, long id)");
        if (mMap == null) {
            return;
        }

            /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //       Log.d(TAG, "permissions granted");
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            //       Log.d(TAG, "no permissions granted");
            return;
        }
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        if (mLocationPermissionGranted) {

            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);

            if (mLastKnownLocation==null) {
                Toast.makeText(getApplicationContext(),
                        "You have to set the location, check the device settings",
                        Toast.LENGTH_LONG).show();
                //TODO make sure that the location is not null
                return;
            }
        }

//        if (Place.distance == Place.distances[position] && selectPointsToShow==false) {
//            return;
//        }
        Place.distance = Place.distances[position];
        //   Log.i(TAG, "distance set "+Place.distance);
        selectPointsToShow = true;

        if (places.size()==0) {
            //TODO if data is still null, send the intent and after getting the response show clusters
            isDataRequestedFromDropDown = true;
            Intent intent = new Intent(this, MyIntentService.class);
            intent.setAction("SUBMIT");
            intent.putExtra(MyIntentService.URL, "http://albertasights.com/rest/v1/findClosePoints");
            intent.putExtra(MyIntentService.LNG, String.valueOf(mDefaultLocation.longitude));
            intent.putExtra(MyIntentService.LAT, String.valueOf(mDefaultLocation.latitude));
            intent.putExtra(MyIntentService.DISTANCE, String.valueOf(20));
            startService(intent);

        } else {
            if (mClusterManager.getMarkerCollection().getMarkers().size()==0
                    && mClusterManager.getClusterMarkerCollection().getMarkers().size()==0) {
                Log.i(TAG, "showing clusters");
                showClusters();
            }
            updateLocationUI();

        }

        //    Log.d(TAG, "exit onItemSelected(AdapterView<?> parent, View view, int position, long id)");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //   Log.d(TAG, "enter onInfoWindowClick(Marker marker)");
        //open google map and show the route to the selected location
        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)",
                marker.getPosition().latitude, marker.getPosition().longitude, "Going there");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
        //    Log.d(TAG, "exit onInfoWindowClick(Marker marker)");

    }

    @Override
    protected void onPause () {
        //    Log.d(TAG, "enter onPause ()");
        super.onPause();
        this.unregisterReceiver(this.receiver);
        //    Log.d(TAG, "exit onPause()");
    }

    public void showFilters(View view) {
        //   Log.d(TAG, "enter showFilters(View view)");
        UiUtils.showFilters=!UiUtils.showFilters;
        //   Log.d(TAG, "show filt: "+ UiUtils.showFilters);

        //save the camera position and the zoom level
        mCameraPosition = mMap.getCameraPosition();
        currentZoom = mMap.getCameraPosition().zoom;
        updateLocationUI();
        mCameraPosition=null;

        //    Log.d(TAG, "exit showFilters(View view)");
    }

    private void showMarkers() {
        //TODO add marker to cluster, handle showInfoWindow on activity recreation
        //   Log.d(TAG, "enter showMarkers()");
        Marker m = null;
        for (Place p : places) {
            if (p.getName().equals(Place.selectedMarkerID)) {
                LatLng lnglat = new LatLng (p.getLng(), p.getLat());
                m = mMap.addMarker(new MarkerOptions()
                        .title(p.getName())
                        .position(lnglat)
                        .snippet(p.getCategory()));
                break;

            }
        }
        m.showInfoWindow();
        Place.selectedMarkerID = null;
        //           m.hideInfoWindow();
//                    mClusterManager.getMarkerCollection().getMarkers().remove(m);

        //   Log.d(TAG, "exit showMarkers()");

    }

    private void showClusters () {
        //    Log.d(TAG, "enter showClusters()");
        //    Log.i(TAG, "marker to show up: "+Place.selectedMarkerID);
        mClusterManager.clearItems();
        mClusterManager.getClusterMarkerCollection().clear();
        mClusterManager.getMarkerCollection().clear();
        markerIds.clear();
        mClusterManager.cluster();

        //  int i = 0;
        for (Place p: places) {
            if (!selectedFilters.contains(p.getCategory())) {

                MyClusterItem offsetItem = new MyClusterItem(p.getLat(), p.getLng(), p.getName(), p.getCategory());
                mClusterManager.addItem(offsetItem);
                markerIds.add(p.getName());
            }
        }

        //   Log.d(TAG, "exit showClusters()");

    }

    @Override
    public void onInfoWindowClose(Marker marker) {
        Log.d(TAG, "enter onInfoWindowClose(Marker marker)");

        Log.d(TAG, "exit onInfoWindowClose(Marker marker)");
    }

    //TODO if onRestart - do not show new clusters

}
