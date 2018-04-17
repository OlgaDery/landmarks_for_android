package com.google.albertasights.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.albertasights.DBIntentService;
import com.google.albertasights.R;
import com.google.albertasights.RestIntentServer;
import com.google.albertasights.UiUtils;
import com.google.albertasights.models.Place;
import com.google.albertasights.models.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapsActivity extends MenuActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener, GoogleMap.OnInfoWindowCloseListener{//AdapterView.OnItemSelectedListener,
    // LocationListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraMoveStartedListener,

    private static final String TAG = MapsActivity.class.getSimpleName();
    //TODO variables to store map position and zoom if the activity is restarted
    private float zoomIfRestarted = 0.0f;
    private double longIfRestarted = 0.0f;
    private double latIfRestarted = 0.0f;

    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // The entry point to Google Play services, used by the Places API and Fused Location Provider.
    private GoogleApiClient mGoogleApiClient;
    private final LatLng mDefaultCoord = new LatLng(51.0533674, -114.072997);
    private float default_zoom = 9.0f;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private float currentZoom = 0.0f;

    //This boolean is to indicate if we need to zoom in or zoom out the camera while using filters. Extra zoom is necessary because
    //it helps to make the cluster more stable
    private boolean zoomingOut = true;

  //  private TextView txt;
  //  private MySpinner mySpinner;//
    private LinearLayout filter;
    private RelativeLayout rr;
    private RelativeLayout mapWrapper;
    private LinearLayout topWrapper;

    private ImageButton showFilterSection;
    private ImageButton showFilters;
    private ImageButton showLoved;
    private ImageButton showAll;
    private ImageButton clearAll;
   // private TextView filterText;
    private ProgressBar simpleProgressBar;
    private Map<String, Boolean> filters = new HashMap<String, Boolean>(1);


    private String orientation;
    private String deviceType;
    private boolean isRestarted = false;
    private boolean saveInfoWindow = false;
    private boolean animationStarted = false;
    private boolean filterSidebarModified = false;
    private boolean filtersModified = false;
    private boolean showFiltersSidebar = false;
    private int posit=0;

    // Declare a variable for the cluster manager.
    private ClusterManager<MyClusterItem> mClusterManager;

    //has to be saved as the SavedInstance
    private HashSet<Place> places = new HashSet<>();
    //filters selected by user
    private ArrayList<String> selectedFilters = new ArrayList<>();
    //filters received from APIs
    private ArrayList<String> receivedFilters = new ArrayList<>();
    //to store markerIDs to track the photo loading, if the photo of the marker has once been loaded, it`s Id should be removed
   //TODO to store the name of current filter using for points
    private String current_filter;

    //TODO place them in ModelView???
    public static Set<String> markerIds = new HashSet<>();
    //all the markers is stored here and extracted to modify
    //   public Set<Marker> markers = new HashSet<>();
    //boolean to indicate if the markers must be shown on the map after the activity has been recreated
    private boolean selectPointsToShow = false;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
  //  private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String CURRENT_ZOOM = "zoom";
    private static final String KEY_MARKER_IDS = "marker_ids";
    private static final String KEY_RECEIVED_FILTERS = "received_fltrs";
    private static final String KEY_SELECTED_FILTERS = "selected_fltrs";
    private static final String KEY_SELECT_POINTS_TO_SHOW = "select_points_to_show";
    private static final String KEY_PLACES = "places";
    private static final String SPINNER_POSIT = "spinner_posit";
    private static final String SAVE_INFO_WINDOW = "save_i_w";
    private static final String STARTED_ANIMATION = "animation";

    //button tags
    public static final String FILTERS = "filters";
    public static final String LOVED = "loved";
    public static final String ALL = "all";
    private static final String CLEAR_MAP = "clear_map";

    private View.OnClickListener checkBoxListener;
    private View.OnClickListener filterButtonsListener;
    private Set <ImageButton> buttons = new HashSet<>();
    private Set <String> selectedByUser = new HashSet<>();

    private BroadcastReceiver receiver;
    private MapViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //there may be two options: the activity created the first time whan the app just started, or it is recreated. The behaviour
        //will be different.
        Log.d(TAG, "enter onCreate");
        super.onCreate(savedInstanceState);

        //TODO check the DB content
        Intent intent = new Intent(this, DBIntentService.class);
        intent.setAction(DBIntentService.CHECK_CONFIG);
        startService(intent);
     //   selectedByUser.addAll(UiUtils.selectedPointsIds);

        // Retrieve all the saved variable
        if (savedInstanceState != null) {
       //     mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            receivedFilters = savedInstanceState.getStringArrayList(KEY_RECEIVED_FILTERS);
            selectedFilters = savedInstanceState.getStringArrayList(KEY_SELECTED_FILTERS);
            selectPointsToShow = savedInstanceState.getBoolean(KEY_SELECT_POINTS_TO_SHOW);
            ArrayList<String> ids = savedInstanceState.getStringArrayList(KEY_MARKER_IDS);
            currentZoom = savedInstanceState.getFloat(CURRENT_ZOOM);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
            places = (HashSet)savedInstanceState.getSerializable(KEY_PLACES);
            posit = savedInstanceState.getInt(SPINNER_POSIT);
            saveInfoWindow = savedInstanceState.getBoolean(SAVE_INFO_WINDOW);
            animationStarted = savedInstanceState.getBoolean(STARTED_ANIMATION);
            markerIds.addAll(ids);

        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);
     //   Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
 //       setSupportActionBar(toolbar);

        //getting the type and the orientation of device
        orientation = UiUtils.getOrientation(getApplicationContext());
        deviceType = UiUtils.findScreenSize(getApplicationContext());
        rr = (RelativeLayout) findViewById(R.id.rr);
        mapWrapper = (RelativeLayout) findViewById(R.id.mapWrapper);
      //  topWrapper = (RelativeLayout) findViewById(R.id.wrapper_top);
        showFilterSection = (ImageButton) findViewById(R.id.imageB);
        showFilterSection.setImageResource(R.drawable.expand_more);
        showFilterSection.getBackground().setAlpha(0);

        showFilters = (ImageButton) findViewById(R.id.showFiltersOnly);
        showFilters.setImageResource(R.drawable.filters);
        showFilters.getBackground().setAlpha(0);
        showFilters.setTag(FILTERS);

        showLoved = (ImageButton) findViewById(R.id.showLoved);
        showLoved.setImageResource(R.drawable.like);
        showLoved.getBackground().setAlpha(0);
        showLoved.setTag(LOVED);

        showAll = (ImageButton) findViewById(R.id.showAll);
        showAll.setImageResource(R.drawable.show_sorted);
        showAll.getBackground().setAlpha(0);
        showAll.setTag(ALL);

        clearAll = (ImageButton) findViewById(R.id.clearMap);
        clearAll.setImageResource(R.drawable.clear);
        clearAll.getBackground().setAlpha(0);
        clearAll.setTag(CLEAR_MAP);
     //   if (deviceType.equals("tablet")) {
           // txt.setTextSize(getApplicationContext().getResources().getDimension(R.dimen.big_textsize));
        //    filterText.setTextSize(getApplicationContext().getResources().getDimension(R.dimen.big_textsize));
     //   }

        filter = (LinearLayout) findViewById(R.id.filters);

        // set the position of the spinner
        topWrapper = (LinearLayout) findViewById(R.id.wrapperTop);
    //    mySpinner = new MySpinner(this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);

        //declaring the progress bar and set the visibility
        simpleProgressBar = new ProgressBar(this);
        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);

        lp1.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        simpleProgressBar.setLayoutParams(lp1);
        topWrapper.addView(simpleProgressBar);
        if (animationStarted) {
            simpleProgressBar.setVisibility(View.VISIBLE);
        } else {
            simpleProgressBar.setVisibility(View.INVISIBLE);
        }

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

                showClusters();
                stabilizeVieWithZoom ();


                //      Log.d(TAG, "exit onClick checkBox(View view) ");
            }
        };

        // declare listeners for imagebuttons
        // set the listener for all the functional buttons. It has to change the filters map depending on the selected button.
        // If the "clear" button is selected, than the map should be empty. viewModel.updateFilterMap(filters) is called. Also the
        // clusters have to be reload and the lists of filters (for checkbox setting) as well
        filterButtonsListener = new View.OnClickListener() {

            public void onClick(View v) {
                Log.d(TAG, "enter onClick imageButtons(View view) ");
                String button =((ImageButton) v).getTag().toString();

                if (current_filter.equals(button)) {
                    return;
                }
                receivedFilters.clear();
                selectedFilters.clear();
              //  current_filter = button;
                if (button.equals(FILTERS)) {
                    filters.clear();
                    filters.put(FILTERS, showFiltersSidebar);

                } else if (button.equals(LOVED)) {
                    //TODO user has to be logged in, otherwise to show tost and return;
                    SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                    if (sharedPref.contains("USER")) {
                        filters.clear();
                        filters.put(LOVED, showFiltersSidebar);
                    } else {
                        UiUtils.showToast(getApplicationContext(), "To use this option, please log in or create an account");
                    }


                } else if (button.equals(ALL)) {
                    filters.clear();
                    filters.put(ALL, showFiltersSidebar);

                } else {
                    //clear all
                    filters.clear();
                    filters.put(CLEAR_MAP, showFiltersSidebar);
                }
                filtersModified = true;
                selectedFilters.clear();
                viewModel.updateFilterMap(filters);
                showClusters ();

                Log.d(TAG, "exit onClick imageButtons(View view) ");
            }
        };

        showLoved.setOnClickListener(filterButtonsListener);
        showAll.setOnClickListener(filterButtonsListener);
        showFilters.setOnClickListener(filterButtonsListener);
        clearAll.setOnClickListener(filterButtonsListener);
        buttons.add(showAll);
        buttons.add(showFilters);
        buttons.add(showLoved);

        View.OnClickListener showMoreButtonsListener = new View.OnClickListener() {
            public void onClick(View view) {
                //   Log.d(TAG, "enter showFilters(View view)");
              //  UiUtils.showFilters=!UiUtils.showFilters;

                //TODO visible
                showFilters.setAlpha(1.0f);
                showLoved.setAlpha(1.0f);
                showAll.setAlpha(1.0f);
                clearAll.setAlpha(1.0f);
                filterSidebarModified = true;

                if (filters.isEmpty()) {
                    filters.put(FILTERS, !showFiltersSidebar);
                    filtersModified = true;
                //    current_filter = FILTERS;
                } else {
                    filters.clear();
                    filters.put(current_filter, !showFiltersSidebar);
                }
                viewModel.updateFilterMap(filters);

                //    Log.d(TAG, "exit showFilters(View view)");
            }
        };

        showFilterSection.setOnClickListener(showMoreButtonsListener);

        // Build the Play services client for use by the Fused Location Provider and the Places API.
        // Use the addApi() method to request the Google Places API and the Fused Location Provider.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        receiver = new BroadcastReceiver () {
            @Override
            public void onReceive(Context context, Intent intent) {
                //On receive should be called very rarely, onle the current location is significantly changed. In our case, it is calling
                //onle once
                //      Log.d(TAG, "enter onReceive(Context context, Intent intent)");
                if (intent.getAction().equals("DATA_RECEIVED")) {
                    if (intent.getSerializableExtra("PLACES")!=null) {
                        ArrayList<Place> placesLst = (ArrayList)intent.getSerializableExtra("PLACES");
                        //         Log.i(TAG, "places: "+ placesLst.size());
                        places.addAll(placesLst);

                        if (mMap!=null) { //isDataRequestedFromDropDown == true &&
                      //      Log.d(TAG, "received!");
                            showClusters();
                            updateLocationUI(filters);
                            stabilizeVieWithZoom();
                            simpleProgressBar.setVisibility(View.INVISIBLE);
                            animationStarted = false;
                            // isDataRequestedFromDropDown=false;
                        }

                        // show toast if the service failed
                        UiUtils.showToast(getApplicationContext(),
                                "got the data");

                    } else {
                        UiUtils.showToast(getApplicationContext(),
                                "server with the data may be unavailable, try again later");
                    }
                } else if (intent.getAction().equals("POINT_ADDED")) {
                    if (intent.getStringExtra("LOVED")!=null) {
                        selectedByUser.add(intent.getStringExtra("LOVED"));
                        updateLocationUI(filters);
                        UiUtils.showToast(getApplicationContext(),
                                "added!");
                    }
                } else if (intent.getAction().equals("DB_CHECKED")) {

                    //TODO the broadcast may receive the User data and the IDs selected by user
                    if (intent.getSerializableExtra("USER")!=null) {
                        User user = (User) intent.getSerializableExtra("USER");
                        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("email", user.getEmail());
                        editor.putString("id", user.getId());
                        editor.putString("first name", user.getFirstName());
                        editor.putString("role", user.getRole());
                        editor.commit();
                    }

                    if (intent.getSerializableExtra("SELECTED_POINTS")!=null) {
                        selectedByUser.addAll((ArrayList<String>)intent.getStringArrayListExtra("SELECTED_POINTS"));
                    }
                }
                //       Log.d(TAG, "exit onReceive(Context context, Intent intent)");
            }
        };

        if (places.size()==0) {
            showFilterSection.setAlpha(0.0f);
            showLoved.setAlpha(0.0f);
            showAll.setAlpha(0.0f);
            showFilters.setAlpha(0.0f);
            clearAll.setAlpha(0.0f);

        }
        //TODO if not instantiated
        viewModel = ViewModelProviders.of(this).get(MapViewModel.class);
        final Observer<Map<String, Boolean>> filtersObserver = new Observer<Map<String, Boolean>>() {
            @Override
            public void onChanged(@Nullable final Map<String, Boolean> newFilter) {
                // Update the UI, in this case, a TextView.
                updateLocationUI(newFilter);
            }
        };
        viewModel.filtersToApply.observe(this, filtersObserver);
        Log.d(TAG, "tracking filters size:" + filters.size());

        Log.d(TAG, "exit onCreate");
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
          Log.d(TAG, "enter onSaveInstanceState");

        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            latIfRestarted = mMap.getCameraPosition().target.latitude;
            longIfRestarted = mMap.getCameraPosition().target.longitude;
     //       outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            ArrayList <String> ids = new  ArrayList <String>(markerIds.size());
            ids.addAll(markerIds);
            outState.putStringArrayList(KEY_MARKER_IDS, ids);
            outState.putSerializable(KEY_PLACES, places);
            outState.putStringArrayList(KEY_RECEIVED_FILTERS, receivedFilters);
            outState.putStringArrayList(KEY_SELECTED_FILTERS, selectedFilters);
            outState.putBoolean(KEY_SELECT_POINTS_TO_SHOW, selectPointsToShow);
            outState.putInt(SPINNER_POSIT, posit);
            outState.putFloat(CURRENT_ZOOM, mMap.getCameraPosition().zoom);
            zoomIfRestarted = mMap.getCameraPosition().zoom;
         //   outState.putBoolean(API_WAS_CALLED, apiNotCalled);
            outState.putBoolean(STARTED_ANIMATION, animationStarted);

            // save info window
            for (Marker m: mClusterManager.getMarkerCollection().getMarkers()) {
                if (m.isInfoWindowShown()==true) {
                    Place.selectedMarkerID = m.getTitle();
                    saveInfoWindow = true;
                    break;
                }
            }
            outState.putBoolean(SAVE_INFO_WINDOW, saveInfoWindow);

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
        Log.d(TAG, "enter onConnected");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Log.d(TAG, "exit onConnected");
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
        Log.d(TAG, "enter onResume()");
        super.onResume();
        registerReceiver();
        Log.d(TAG, "exit onResume()");

    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "enter onRestart()");
        super.onRestart();
        isRestarted = true;
        Log.d(TAG, "exit onRestart()");
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "enter onStart()");
        super.onStart();
        Log.d(TAG, "exit onStart()");
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        Log.d(TAG, "enter onMapReady");
        mMap = map;
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

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            //  Log.i(TAG, "non-clustered markers: "+mClusterManager.getMarkerCollection().getMarkers().size());

        } else {
            mMap.setMyLocationEnabled(false);
            //   Log.d(TAG, "smth wrong with permissions");
            //TODO show the toast that permiss not granted
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

        if (places.size()==0) {
            //to send the intent to request the data from API
            Intent intent = new Intent(this, RestIntentServer.class);
            intent.setAction("SUBMIT");
            intent.putExtra(RestIntentServer.URL, "https://albertasights.herokuapp.com/api/v1/points_by_district?district=Calgary");
            intent.putExtra(RestIntentServer.LNG, String.valueOf(mDefaultCoord.longitude));
            intent.putExtra(RestIntentServer.LAT, String.valueOf(mDefaultCoord.latitude));
            intent.putExtra(RestIntentServer.DISTANCE, String.valueOf(30));
            startService(intent);
            // start the animation for the period of data loading
            animationStarted = true;
            simpleProgressBar.setVisibility(View.VISIBLE);

        }

    //    mySpinner.setSelection(posit);//Arrays.asList(Place.distances).indexOf(Place.distance)
    //    mySpinner.setOnItemSelectedEvenIfUnchangedListener(
    //            this);

        if (isRestarted == false) {
            //that means that the activity is created the first time or recreated
        //    Log.d(TAG, "select points to show: "+selectPointsToShow);

            mClusterManager = new ClusterManager<MyClusterItem>(this, mMap);
            mClusterManager.setRenderer(new MyClassRenderer(this, mMap, mClusterManager));
            mMap.setOnCameraIdleListener(mClusterManager);

            MyInfoWindowAdaptor adaptor = new MyInfoWindowAdaptor(getApplicationContext(), places,
                    orientation, deviceType);
            mMap.setInfoWindowAdapter(adaptor);

            mMap.setOnInfoWindowClickListener(this);

            // this listener has to remove checkboxes from the filter layout
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                @Override
                public boolean onMarkerClick(Marker m) {
                        //  Log.d(TAG, "enter onMarkerClick(Marker m)");

                    if (m.getTitle()==null) {
                        //          Log.d(TAG, "cluster clicked");
                        return true;
                    } else {

                        if (showFiltersSidebar==true) {
                          //  UiUtils.showFilters=!UiUtils.showFilters;
                            //TODO update filters in ViewModel
                            filters.clear();
                            filters.put(current_filter, !showFiltersSidebar);
                            viewModel.updateFilterMap(filters);
                        }

                        m.showInfoWindow();

                            //   Log.d(TAG, "exit onMarkerClick(Marker m)");
                        return true;
                    }

                }
            });
        }


        if (places.size()>0)  {
            //if not, that means that the activity is being recreated and the points already received from the server
            if (isRestarted==false) {
                LatLng myLatLng;
                if (mCameraPosition!= null) {
                    myLatLng =  new LatLng(mCameraPosition.target.latitude,
                            mCameraPosition.target.longitude);
                } else {

                    myLatLng = mDefaultCoord;
                    currentZoom = default_zoom;
                }

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,
                        currentZoom));

                showClusters();
                if (simpleProgressBar.getVisibility()==View.VISIBLE) {
                    simpleProgressBar.setVisibility(View.INVISIBLE);
                }
            } else {
                mCameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(latIfRestarted,
                                longIfRestarted))
                        .build();//mMap.getCameraPosition();
                currentZoom = zoomIfRestarted;
             //   Log.i(TAG, "long after restart: " + mCameraPosition.target.longitude);
             //   Log.i(TAG, "zoom after restart: " + mCameraPosition.zoom);
            }
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultCoord,
                    default_zoom));
        }
        Log.d(TAG, "tracking filters size:" + filters.size());

        //TODO in the activity has been recreation or reset the ViewModel data has to be used
        if (viewModel.filtersToApply.getValue()!=null) {
            filters = viewModel.filtersToApply.getValue();
        }

        updateLocationUI(filters);
        stabilizeVieWithZoom ();

        isRestarted=false;
        Log.d(TAG, "exit onMapReady");
    }

    private void registerReceiver() {
//        Log.d(TAG, "enter registerReceiver() for DATA_RECEIVED");
        // Create an intent filter for DATA_RECEIVED.
        IntentFilter intentFilter =
                new IntentFilter();
        intentFilter.addAction("DATA_RECEIVED");
        intentFilter.addAction("POINT_ADDED");
        intentFilter.addAction("DB_CHECKED");

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
     //   updateLocationUI();
    }
    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */

    //TODO add observer to follow the changes in the filter map, depending on it`s state add or remove the functional buttons
    //TODO and filter bar. Modify the lists of received and selected filters (may be the list of loved points or list of all
    //TODO points names). Call the updateLocationUI()

    private void updateLocationUI(Map <String, Boolean> newFilter) {

        Log.d(TAG, "enter updateLocationUI");
//        if (mMap == null) {
//            UiUtils.showToast(getApplicationContext(),
//                    "Map is currently unavailable");
//            return;
//        }

//TODO set visibility for buttons
        if (places.size()>0) { //&& selectPointsToShow==true
            // set full opacity
            //       Log.i(TAG, "setting full opacity");
            showFilterSection.setAlpha(1.0f);
            //   filterText.setText("Select:");
        }

        //TODO logic to process filters (add/remove element with filters, change the list of filters, change the buttons color)
       if (!newFilter.isEmpty()) {
           ArrayList<String> temp = new ArrayList<String>(0);
           temp.addAll(newFilter.keySet());

           Log.d(TAG, "current filter: " + temp.get(0));
           Log.d(TAG, "sidebar modified: "+filterSidebarModified);
           Log.d(TAG, "active filters modified: "+filtersModified);
           Log.d(TAG, "show sidebar: " + newFilter.get(temp.get(0)));

           if (filtersModified==true) {

               //new filter set and the sidebar is open, new checkboxes have to get loaded
               if (newFilter.containsKey(FILTERS)) {
                   for (Place p : places) {
                       if (!receivedFilters.contains(p.getCategory()))
                       receivedFilters.add(p.getCategory());
                   }

               } else if (newFilter.containsKey(LOVED)) {
                   //  receivedFilters
                   //TODO remove!!!
                   selectedByUser.addAll(UiUtils.selectedPointsIds);
                   for (Place p : places) {
                       if (selectedByUser.contains(p.getId())) {
                           receivedFilters.add(p.getName());
                       }
                   }

               } else if (newFilter.containsKey(ALL)) {
                   for (Place p : places) {
                       receivedFilters.add(p.getName());
                   }

               } else if (newFilter.containsKey(CLEAR_MAP)) {
                   // set all buttons of the same color, remove filter view
                   newFilter.clear();
                   showAll.setAlpha(0.0f);
                   showFilters.setAlpha(0.0f);
                   showLoved.setAlpha(0.0f);
                   clearAll.setAlpha(0.0f);
                   filter.removeAllViews();
                   filter.getLayoutParams().height = 0;
                   filter.getLayoutParams().width = 0;
                   filterSidebarModified = false;
                   filtersModified = false;
                   return;
               }

               if (newFilter.get(temp.get(0))==true) {
                   UiUtils.configureFilters(getApplicationContext(), filter, deviceType,
                           receivedFilters, selectedFilters, checkBoxListener, temp.get(0));
                   Log.d(TAG, "1");
               } else {
                   //new filter set, but filter sidebar is hidden
                //   filter.removeAllViews();
                   Log.d(TAG, "2");
               }

           } else if (filtersModified==false && newFilter.get(temp.get(0))==true) {

               //using the same filter, opening the filter sidebar
               Log.d(TAG, "3");
               if (newFilter.containsKey(FILTERS)) {
                   for (Place p : places) {
                       if (!receivedFilters.contains(p.getCategory()))
                           receivedFilters.add(p.getCategory());
                   }

               } else if (newFilter.containsKey(LOVED)) {
                   //  receivedFilters

               } else if (newFilter.containsKey(ALL)) {
                   for (Place p : places) {
                       receivedFilters.add(p.getName());
                   }
               }
               UiUtils.configureFilters(getApplicationContext(), filter, deviceType,
                       receivedFilters, selectedFilters, checkBoxListener, temp.get(0));

           } else if (filtersModified==false && newFilter.get(temp.get(0))==false) {
               //using the same filter, closing the sidebar
               filter.removeAllViews();
               filter.getLayoutParams().height = 0;
               filter.getLayoutParams().width = 0;
               Log.d(TAG, "4");
           }

           //resetting the values of class variables for later usage
           current_filter = temp.get(0);
           showFiltersSidebar = newFilter.get(current_filter);
           if (current_filter!=null) {
               UiUtils.modifyButtons(buttons, current_filter);
           }
           filterSidebarModified = false;
           filtersModified = false;

       }

    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        //   Log.d(TAG, "enter onInfoWindowClick(Marker marker)");
        //open google map and show the route to the selected location
        //TODO
        Intent i = new Intent(getApplicationContext(), PointActivity.class);
        for (Place p : places) {
            if (p.getName().equals(marker.getTitle())) {
                if (selectedByUser.contains(p.getId())) {
                    p.setLoved(true);
                }
                i.putExtra("POINT", p);
                break;
            }
        }

        startActivity(i);
        //    Log.d(TAG, "exit onInfoWindowClick(Marker marker)");
    }

    @Override
    protected void onPause () {
        Log.d(TAG, "enter onPause ()");
        super.onPause();
        this.unregisterReceiver(this.receiver);
        Log.d(TAG, "exit onPause()");
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
        if (selectedFilters.size()==0) {
            for (Place p : places) {

                MyClusterItem offsetItem = new MyClusterItem(p.getLat(), p.getLng(), p.getName(), p.getCategory());
                mClusterManager.addItem(offsetItem);
                markerIds.add(p.getName());
            }

        } else {
            for (Place p : places) {
                if (current_filter.equals(FILTERS)) {
                    if (selectedFilters.contains(p.getCategory())) {

                        MyClusterItem offsetItem = new MyClusterItem(p.getLat(), p.getLng(), p.getName(), p.getCategory());
                        mClusterManager.addItem(offsetItem);
                        markerIds.add(p.getName());
                    }
                } else {
                    //to see "ALL" or "LOVED"
                    if (selectedFilters.contains(p.getName())) {

                        MyClusterItem offsetItem = new MyClusterItem(p.getLat(), p.getLng(), p.getName(), p.getCategory());
                        mClusterManager.addItem(offsetItem);
                        markerIds.add(p.getName());
                    }
                }

            }
        }

        //   Log.d(TAG, "exit showClusters()");

    }

    @Override
    protected void onStop() {
        Log.d(TAG, "enter onStop()");
        super.onStop();
        Log.d(TAG, "exit onStop()");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "enter onDestroy()");
        super.onDestroy();
        Log.d(TAG, "exit onDestroy()");
    }

    @Override
    public void onInfoWindowClose(Marker marker) {
      //  Log.d(TAG, "enter onInfoWindowClose(Marker marker)");

      //  Log.d(TAG, "exit onInfoWindowClose(Marker marker)");
    }

    private void stabilizeVieWithZoom () {
        if (zoomingOut==false) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMap.getCameraPosition().target,
                    mMap.getCameraPosition().zoom-0.005f));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMap.getCameraPosition().target,
                    mMap.getCameraPosition().zoom+0.005f));
        }
        zoomingOut=!zoomingOut;
    }

    public class MyClassRenderer extends DefaultClusterRenderer <MyClusterItem>{


        public MyClassRenderer(Context context, GoogleMap map, ClusterManager<MyClusterItem> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onClusterItemRendered(MyClusterItem item, Marker marker) {
            super.onClusterItemRendered(item, marker);
            if (saveInfoWindow==true) {
                if (Place.selectedMarkerID.equals(marker.getTitle())) {
                //    Log.i(TAG, "got it!");
                    marker.showInfoWindow();
                    saveInfoWindow=false;
                }
            }

        }

    }

}
