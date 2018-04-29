package com.google.albertasights.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.albertasights.R;
import com.google.albertasights.models.Place;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnPointDataExtendedListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnInfoWindowClickListener {
    // TODO: Add values to pointsToShow
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private OnPointDataExtendedListener mListener;

    private static final String TAG = MapFragment.class.getSimpleName();
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
    private LinearLayout filter;
    private RelativeLayout rr;
    private RelativeLayout mapWrapper;
    private LinearLayout topWrapper;

    private ImageButton showFilterSection;
    private ImageButton showFilters;
    private ImageButton showLoved;
    private ImageButton showAll;
    private ImageButton clearAll;
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
    private String selectedMarkerID = "";

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
    //  private static final String KEY_PLACES = "places";
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
    //TODO to create the infrastracture to update selectedByUser
    private Set <String> selectedByUser = new HashSet<>();

    private BroadcastReceiver receiver;
    private MapViewModel viewModel;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "enter onCreate(Bundle savedInstanceState)");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            receivedFilters = savedInstanceState.getStringArrayList(KEY_RECEIVED_FILTERS);
            selectedFilters = savedInstanceState.getStringArrayList(KEY_SELECTED_FILTERS);
            selectPointsToShow = savedInstanceState.getBoolean(KEY_SELECT_POINTS_TO_SHOW);
            ArrayList<String> ids = savedInstanceState.getStringArrayList(KEY_MARKER_IDS);
            currentZoom = savedInstanceState.getFloat(CURRENT_ZOOM);
            selectedMarkerID = savedInstanceState.getString("SELECTED_MARKER_ID");
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
            places = (HashSet)savedInstanceState.getSerializable(UiUtils.PLACES);
            posit = savedInstanceState.getInt(SPINNER_POSIT);
            saveInfoWindow = savedInstanceState.getBoolean(SAVE_INFO_WINDOW);
            animationStarted = savedInstanceState.getBoolean(STARTED_ANIMATION);
            markerIds.addAll(ids);
        }

        //getting the type and the orientation of device


        // Build the Play services client for use by the Fused Location Provider and the Places API.
        // Use the addApi() method to request the Google Places API and the Fused Location Provider.
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        //TODO if not instantiated
        viewModel = ViewModelProviders.of(getActivity()).get(MapViewModel.class);
        final Observer<Map<String, Boolean>> filtersObserver = new Observer<Map<String, Boolean>>() {
            @Override
            public void onChanged(@Nullable final Map<String, Boolean> newFilter) {
                // Update the UI, in this case, a TextView.
                updateLocationUI(newFilter);
            }
        };
        viewModel.filtersToApply.observe(this, filtersObserver);
        places.addAll(viewModel.getRecievedPoints().getValue());
        Log.d(TAG, "places: "+places.size());
        Log.d(TAG, "exit onCreate(Bundle savedInstanceState)");

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
        latIfRestarted = mMap.getCameraPosition().target.latitude;
        longIfRestarted = mMap.getCameraPosition().target.longitude;
        //       outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
        ArrayList <String> ids = new  ArrayList <String>(markerIds.size());
        ids.addAll(markerIds);
        outState.putStringArrayList(KEY_MARKER_IDS, ids);
        outState.putSerializable(UiUtils.PLACES, places);
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
                selectedMarkerID = m.getTitle();
                saveInfoWindow = true;
                break;
            }
        }
        outState.putString("SELECTED_MARKER_ID", selectedMarkerID);
        outState.putBoolean(SAVE_INFO_WINDOW, saveInfoWindow);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "enter onCreateView");
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
        orientation = UiUtils.getOrientation(getActivity());
        deviceType = UiUtils.findScreenSize(getActivity());
        rr = (RelativeLayout) v.findViewById(R.id.rr);
        mapWrapper = (RelativeLayout) v.findViewById(R.id.mapWrapper);
        topWrapper = (LinearLayout) v.findViewById(R.id.wrapperTop);
        showFilterSection = (ImageButton) v.findViewById(R.id.imageB);
        showFilterSection.setImageResource(R.drawable.expand_more);
        showFilterSection.getBackground().setAlpha(0);

        showFilters = (ImageButton) v.findViewById(R.id.showFiltersOnly);
        showFilters.setImageResource(R.drawable.filters);
        showFilters.getBackground().setAlpha(0);
        showFilters.setTag(FILTERS);

        showLoved = (ImageButton) v.findViewById(R.id.showLoved);
        showLoved.setImageResource(R.drawable.like);
        showLoved.getBackground().setAlpha(0);
        showLoved.setTag(LOVED);

        showAll = (ImageButton) v.findViewById(R.id.showAll);
        showAll.setImageResource(R.drawable.show_sorted);
        showAll.getBackground().setAlpha(0);
        showAll.setTag(ALL);

        clearAll = (ImageButton) v.findViewById(R.id.clearMap);
        clearAll.setImageResource(R.drawable.clear);
        clearAll.getBackground().setAlpha(0);
        clearAll.setTag(CLEAR_MAP);

        //temporary remove buttons from the top
        showFilterSection.setAlpha(0.0f);
        topWrapper.removeView(showAll);
        topWrapper.removeView(clearAll);
        topWrapper.removeView(showLoved);
        topWrapper.removeView(showFilters);

        filter = (LinearLayout) v.findViewById(R.id.filters);

//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams
//                (RelativeLayout.LayoutParams.WRAP_CONTENT,
//                        RelativeLayout.LayoutParams.WRAP_CONTENT);

        //declaring the progress bar and set the visibility
        //TODO revove progress bar
//        simpleProgressBar = new ProgressBar(this);
//        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams
//                (RelativeLayout.LayoutParams.WRAP_CONTENT,
//                        RelativeLayout.LayoutParams.WRAP_CONTENT);
//
//        lp1.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//        simpleProgressBar.setLayoutParams(lp1);
//        topWrapper.addView(simpleProgressBar);
//        if (animationStarted) {
//            simpleProgressBar.setVisibility(View.VISIBLE);
//        } else {
//            simpleProgressBar.setVisibility(View.INVISIBLE);
//        }

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
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    if (prefs.contains(UiUtils.LOGGED_IN)) {
                        Log.i(TAG, "user logged in: "+ prefs.getBoolean(UiUtils.LOGGED_IN, true));
                        if (prefs.getBoolean(UiUtils.LOGGED_IN, true)==true) {
                            filters.clear();
                            filters.put(LOVED, showFiltersSidebar);
                        }
                    } else {
                        UiUtils.showToast(getActivity(), "To use this option, please log in or create an account");
                        return;
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
                stabilizeVieWithZoom();

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
//                showFilters.setAlpha(1.0f);
//                showLoved.setAlpha(1.0f);
//                showAll.setAlpha(1.0f);
//                clearAll.setAlpha(1.0f);
                if (filters.isEmpty()) {
                    topWrapper.addView(showFilters);
                    topWrapper.addView(showLoved);
                    topWrapper.addView(showAll);
                    topWrapper.addView(clearAll);
                }

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
        Log.d(TAG, "exit onCreateView");
        return v;
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
                    updateLocationUI(filters);

                    if (places.size()>0) {
                        showClusters();
                        stabilizeVieWithZoom ();
                    }
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {

        Log.d(TAG, "enter onMapReady");
        mMap = map;
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */

        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
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

        //    mySpinner.setSelection(posit);//Arrays.asList(Place.distances).indexOf(Place.distance)
        //    mySpinner.setOnItemSelectedEvenIfUnchangedListener(
        //            this);

        if (isRestarted == false) {
            //that means that the activity is created the first time or recreated
            //    Log.d(TAG, "select points to show: "+selectPointsToShow);

            mClusterManager = new ClusterManager<MyClusterItem>(getContext(), mMap);
            mClusterManager.setRenderer(new MyClassRenderer(getContext(), mMap, mClusterManager));
            mMap.setOnCameraIdleListener(mClusterManager);

            MyInfoWindowAdaptor adaptor = new MyInfoWindowAdaptor(getContext(), places,
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

            } else {
                mCameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(latIfRestarted,
                                longIfRestarted))
                        .build();//mMap.getCameraPosition();
                currentZoom = zoomIfRestarted;
                //TODO in the activity has been recreation or reset the ViewModel data has to be used
                if (viewModel.filtersToApply.getValue()!=null) {
                    filters = viewModel.filtersToApply.getValue();
                }

                isRestarted=false;
            }
            updateLocationUI(filters);
            showClusters();
            stabilizeVieWithZoom ();

        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultCoord,
                    default_zoom));
        }

        Log.d(TAG, "exit onMapReady");

    }


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
            // topWrapper.addView(showFilterSection);
            showFilterSection.setAlpha(1.0f);
        }

        //TODO logic to process filters (add/remove element with filters, change the list of filters, change the buttons color)
        if (!newFilter.isEmpty()) {
            ArrayList<String> temp = new ArrayList<String>(0);
            temp.addAll(newFilter.keySet());
            receivedFilters.clear();

//           Log.d(TAG, "current filter: " + temp.get(0));
//           Log.d(TAG, "sidebar modified: "+filterSidebarModified);
//           Log.d(TAG, "active filters modified: "+filtersModified);
//           Log.d(TAG, "show sidebar: " + newFilter.get(temp.get(0)));
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            Log.i(TAG, "selected points: "+prefs.getStringSet
                    (UiUtils.SELECTED_POINTS, new HashSet<String>()).size());

            if (filtersModified==true) {

                //new filter set and the sidebar is open, new checkboxes have to get loaded
                if (newFilter.containsKey(FILTERS)) {
                    for (Place p : places) {
                        if (!receivedFilters.contains(p.getCategory()))
                            receivedFilters.add(p.getCategory());
                    }

                } else if (newFilter.containsKey(LOVED)) {
                    //  receivedFilters
                    if (selectedByUser.size()>0) {
                        for (Place p : places) {
                            if (selectedByUser.contains(p.getName())) {
                                receivedFilters.add(p.getName());
                            }
                        }
                    }

                } else if (newFilter.containsKey(ALL)) {
                    for (Place p : places) {
                        receivedFilters.add(p.getName());
                    }

                } else if (newFilter.containsKey(CLEAR_MAP)) {
                    // set all buttons of the same color, remove filter view
                    newFilter.clear();
//                   showAll.setAlpha(0.0f);
//                   showFilters.setAlpha(0.0f);
//                   showLoved.setAlpha(0.0f);
//                   clearAll.setAlpha(0.0f);
                    topWrapper.removeView(showAll);
                    topWrapper.removeView(showFilters);
                    topWrapper.removeView(showLoved);
                    topWrapper.removeView(clearAll);

                    filter.removeAllViews();
                    filter.getLayoutParams().height = 0;
                    filter.getLayoutParams().width = 0;
                    filterSidebarModified = false;
                    filtersModified = false;
                    return;
                }

                if (newFilter.get(temp.get(0))==true) {
                    UiUtils.configureFilters(getActivity(), filter, deviceType,
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
                    for (Place p : places) {
                        if (selectedByUser.contains(p.getName()))
                            receivedFilters.add(p.getName());
                    }

                } else if (newFilter.containsKey(ALL)) {
                    for (Place p : places) {
                        receivedFilters.add(p.getName());
                    }
                }
                UiUtils.configureFilters(getActivity(), filter, deviceType,
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
        Log.d(TAG, "exit updateUI");

    }

    private void showClusters () {
        Log.d(TAG, "enter showClusters()");
        //    Log.i(TAG, "marker to show up: "+Place.selectedMarkerID);
        mClusterManager.clearItems();
        mClusterManager.getClusterMarkerCollection().clear();
        mClusterManager.getMarkerCollection().clear();
        markerIds.clear();
        mClusterManager.cluster();

        //  int i = 0;
        for (Place p : places) {
            if (viewModel.getPointsNamesToShow().getValue().contains(p.getName())) {
                MyClusterItem offsetItem = new MyClusterItem(p.getLat(), p.getLng(), p.getName(), p.getCategory());
                mClusterManager.addItem(offsetItem);
                markerIds.add(p.getName());
            }
//                if (current_filter.equals(FILTERS)) {
//                    if (viewModel.getFilters().getValue().keySet().contains(p.getCategory())) {
//
//                        MyClusterItem offsetItem = new MyClusterItem(p.getLat(), p.getLng(), p.getName(), p.getCategory());
//                        mClusterManager.addItem(offsetItem);
//                        markerIds.add(p.getName());
//                    }
//                } else {
//                    //to see "ALL" or "LOVED"
//                    if (viewModel.getFilters().getValue().keySet().contains(p.getName())) {
//
//                        MyClusterItem offsetItem = new MyClusterItem(p.getLat(), p.getLng(), p.getName(), p.getCategory());
//                        Log.d(TAG, "name " + p.getName());
//                        mClusterManager.addItem(offsetItem);
//                        markerIds.add(p.getName());
//                    }
//                }

        }
        saveInfoWindow=false;
        selectedMarkerID = "";
        Log.d(TAG, "exit showClusters()");

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPointDataExtendedListener) {
            mListener = (OnPointDataExtendedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPointDataExtendedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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


    public void onInfoViewExpanded(String pointName) {
        if (mListener != null) {
            mListener.onPointDetailsSelected(pointName);
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //TODO hook the click on the marker to the listener, what should trigger the replacement of the activity
        onInfoViewExpanded(marker.getTitle());

    }

    public class MyClassRenderer extends DefaultClusterRenderer<MyClusterItem> {


        public MyClassRenderer(Context context, GoogleMap map, ClusterManager<MyClusterItem> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onClusterItemRendered(MyClusterItem item, Marker marker) {
            super.onClusterItemRendered(item, marker);
            if (saveInfoWindow==true) {
                if (selectedMarkerID.equals(marker.getTitle())) {
                    //    Log.i(TAG, "got it!");
                    marker.showInfoWindow();

                }
            }

        }

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
    public interface OnPointDataExtendedListener {
        // TODO: Update argument type and name
        void onPointDetailsSelected(String name);
    }
}
