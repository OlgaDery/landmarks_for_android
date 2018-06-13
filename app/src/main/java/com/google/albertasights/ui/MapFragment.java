package com.google.albertasights.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.Space;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, View.OnTouchListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnInfoWindowClickListener {
    // TODO: Add values to pointsToShow
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private OnPointDataExtendedListener mListener;

    private static final String TAG = MapFragment.class.getSimpleName();
    //TODO variables to store map position and zoom if the activity is restarted
    private float zoomIfRestarted = 0.0f;
    private double longIfRestarted = 0.0f;
    private double latIfRestarted = 0.0f;
    private boolean recreated = false;

    private double westCoord= 0.0f;
    private double eastCoord = 0.0f;
    private double southCoord = 0.0f;
    private double northCoord = 0.0f;

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
    private LinearLayout topWrapper;

    private LinearLayout sideBarListener;
    private LinearLayout pseudiMargin;
    private ImageButton clearAll;
    private ImageButton showFilters;
    private ImageButton showLoved;
    private ImageButton showAll;

    private String orientation;
    private String deviceType;
    private boolean isRestarted = false;
    private boolean saveInfoWindow = false;
    private boolean animationStarted = false;

    int count;
    // Declare a variable for the cluster manager.
    private ClusterManager<MyClusterItem> mClusterManager;

    //has to be saved as the SavedInstance
    private HashSet<Place> places = new HashSet<>();
    //to store markerIDs to track the photo loading, if the photo of the marker has once been loaded, it`s Id should be removed
    //TODO to store the name of current filter using for points
  //  private String current_filter = "";
    private String selectedMarkerID = "";
    private String sortedBy = "";

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
    public static final String KEY_CAMERA_POSITION = "camera_position";
    public static final String CURRENT_ZOOM = "zoom";
    public static final String KEY_MARKER_IDS = "marker_ids";
    public static final String KEY_RECEIVED_FILTERS = "received_fltrs";
    public static final String KEY_SELECTED_FILTERS = "selected_fltrs";
    public static final String KEY_SELECT_POINTS_TO_SHOW = "select_points_to_show";
    //  private static final String KEY_PLACES = "places";
    private static final String SAVE_INFO_WINDOW = "save_i_w";
    private static final String STARTED_ANIMATION = "animation";

    //button tags
    public static final String FILTERS = "filters";
    public static final String LOVED = "loved";
    public static final String ALL = "all";
    public static final String CLEAR_MAP = "clear_map";

    private View.OnClickListener checkBoxListener;
    private View.OnClickListener filterButtonsListener;
    private View.OnClickListener showMoreButtonsListener;
    private View.OnClickListener applyRatingListener;
    private Set <ImageButton> buttons = new HashSet<>();
    // private Set <String> selectedByUser = new HashSet<>();

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
        if (savedInstanceState != null) {
            Log.d(TAG, "savedInstanceState not null");
            try {

                selectPointsToShow = savedInstanceState.getBoolean(KEY_SELECT_POINTS_TO_SHOW);
                ArrayList<String> ids = savedInstanceState.getStringArrayList(KEY_MARKER_IDS);
                currentZoom = savedInstanceState.getFloat(CURRENT_ZOOM);
                selectedMarkerID = savedInstanceState.getString("SELECTED_MARKER_ID");
                mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
                Log.i(TAG, mCameraPosition.target.toString());
                saveInfoWindow = savedInstanceState.getBoolean(SAVE_INFO_WINDOW);
                animationStarted = savedInstanceState.getBoolean(STARTED_ANIMATION);
                sortedBy = savedInstanceState.getString(UiUtils.SORTED_BY);
                markerIds.addAll(ids);
                if (savedInstanceState.getParcelable(KEY_CAMERA_POSITION)==null) {

                }
            }catch (Exception e) {

            }

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

        viewModel = ViewModelProviders.of(getActivity()).get(MapViewModel.class);

        //observes the list with points names to show on the map
        final Observer<LinkedList<String>> pointsToShowObserver = new Observer<LinkedList<String>>() {
            @Override
            public void onChanged(@Nullable LinkedList<String> strings) {
                Log.d(TAG, "enter onChanged(@Nullable LinkedList<String> strings)");
                if (mMap!=null) {
                    Log.d(TAG, "map is not null");
                    showClusters();
                    boolean anyFiltersSelected = true;
                    if (viewModel.getNamesToShowInScroll().getValue()==null&&viewModel.getRatings().getValue()==null) {
                        anyFiltersSelected=false;
                    } else {
                        if (viewModel.getNamesToShowInScroll().getValue().size()==0&&viewModel.getRatings().getValue().size()==0) {
                            anyFiltersSelected=false;
                        }
                    }

                    if (viewModel.getCurrentFilter().getValue()!=null) {
                        UiUtils.modifyButtons(buttons, viewModel.getCurrentFilter().getValue(),
                                anyFiltersSelected);
                    } else {
                        UiUtils.modifyButtons(buttons, "", anyFiltersSelected);
                    }
                } else {
                    Log.d(TAG, "map is null");
                }

            }
        };
        viewModel.getPointsNamesToShow().observe(this, pointsToShowObserver);

        places.addAll(viewModel.getRecievedPoints().getValue());
        viewModel.updateCurrentFragment(this.getClass().getSimpleName());
        Log.d(TAG, "exit onCreate(Bundle savedInstanceState)");

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(TAG, "enter onSaveInstanceState(@NonNull Bundle outState)");
        super.onSaveInstanceState(outState);
        try {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            latIfRestarted = mMap.getCameraPosition().target.latitude;
            longIfRestarted = mMap.getCameraPosition().target.longitude;
            outState.putFloat(CURRENT_ZOOM, mMap.getCameraPosition().zoom);
            zoomIfRestarted = mMap.getCameraPosition().zoom;
        } catch (Exception e) {

        }
        ArrayList <String> ids = new  ArrayList <String>(markerIds.size());
        ids.addAll(markerIds);
        outState.putStringArrayList(KEY_MARKER_IDS, ids);
        outState.putBoolean(KEY_SELECT_POINTS_TO_SHOW, selectPointsToShow);
        outState.putBoolean(STARTED_ANIMATION, animationStarted);
        outState.putString(UiUtils.SORTED_BY, sortedBy);

        // save info window
        try{
            for (Marker m: mClusterManager.getMarkerCollection().getMarkers()) {
                if (m.isInfoWindowShown()==true) {
                    selectedMarkerID = m.getTitle();
                    saveInfoWindow = true;
                    break;
                }
            }
        }catch (Exception e) {

        }

        outState.putString("SELECTED_MARKER_ID", selectedMarkerID);
        outState.putBoolean(SAVE_INFO_WINDOW, saveInfoWindow);
        Log.d(TAG, "enter onSaveInstanceState(@NonNull Bundle outState)");

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        UiUtils.showToast(getActivity(), "Google Map is about to stop");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        UiUtils.showToast(getActivity(), "Error trying to connect to Google Map");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "enter onCreateView");
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        Log.i(TAG, "restarted: " + isRestarted);
        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
        orientation = UiUtils.getOrientation(getActivity());
        deviceType = UiUtils.findScreenSize(getActivity());
        topWrapper = (LinearLayout) v.findViewById(R.id.wrapperTop);

        showFilters = (ImageButton) v.findViewById(R.id.showFiltersOnly);
        showFilters.setImageResource(R.drawable.filter_new);
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

        clearAll = new ImageButton(getActivity());//(ImageButton) v.findViewById(R.id.clearMap);
        clearAll.setImageResource(R.drawable.close_trimmed);
        clearAll.getBackground().setAlpha(0);
        clearAll.setTag("CLEAR_MAP");
//        showFilterSection.setOnClickListener(showMoreButtonsListener);
        clearAll.setOnClickListener(filterButtonsListener);
        sideBarListener = (LinearLayout) v.findViewById(R.id.sideBarListener);
        sideBarListener.setOnTouchListener(this);
        pseudiMargin = (LinearLayout) v.findViewById(R.id.pseudoMargin);

        if (viewModel.getDevice().getValue().equals(UiUtils.TABLET)) {
            if (viewModel.getOrienr().getValue().equals(UiUtils.LANDSCAPE)) {
                sideBarListener.getLayoutParams().width=viewModel.getWight().getValue()/30;
                pseudiMargin.getLayoutParams().width=viewModel.getWight().getValue()/30;
            } else {
                sideBarListener.getLayoutParams().width=viewModel.getHight().getValue()/30;
                pseudiMargin.getLayoutParams().width=viewModel.getHight().getValue()/30;
            }

        }
        // declare listeners for imagebuttons
        // set the listener for all the functional buttons. It has to change the filters map depending on the selected button.
        // If the "clear" button is selected, than the map should be empty. viewModel.updateFilterMap(filters) is called. Also the
        // clusters have to be reload and the lists of filters (for checkbox setting) as well
        filterButtonsListener = new View.OnClickListener() {

            public void onClick(View v) {
                Log.d(TAG, "enter onClick imageButtons(View view) ");
                String button =((ImageButton) v).getTag().toString();
                //TODO filter has already been set
                if (viewModel.getCurrentFilter().getValue()!=null) {

                    //TODO user has selected the same filter, upgating isSidebarReguested only
                    if (viewModel.getCurrentFilter().getValue().equals(button)) {
                        Log.i(TAG, "the same filter selected");
                        viewModel.upateShowSidebar(true);
                        return;
                    }
                } else {
                    Log.i(TAG, "the filter was null");
                    //TODO user has selected a filter first time, need to show all the points
                   // viewModel.updatePointsToShow(viewModel.getNamesSortedByRating().getValue());
                }
                LinkedList<String> list = new LinkedList<>();
                if (viewModel.getCurrentFilter().getValue()!=null) {
                    if (viewModel.getCurrentFilter().getValue().equals(MapFragment.FILTERS)){
                        viewModel.updateRatings(new LinkedList<String>());
                    }
                    viewModel.updateNamesToShowInScroll(null);
                }

                //  current_filter = button;
                if (button.equals(FILTERS)) {
                   // filters.clear();
                   // filters.put(FILTERS, true);
                    viewModel.updateCurrentFilter(button);
                    viewModel.upateShowSidebar(true);
                    viewModel.updateY(0);

                    if (viewModel.getSelectedFiltersForCategories().getValue()!=null &&
                            viewModel.getSelectedFiltersForCategories().getValue().size()>0) {
                        Log.d(TAG, "1");
                        for (Place p : viewModel.getRecievedPoints().getValue()) {
                            if (viewModel.getSelectedFiltersForCategories().getValue().contains(p.getCategory())) {
                                list.add(p.getName());
                            }
                        }
                    } else {
                        Log.d(TAG, "2");
                        list.addAll(viewModel.getNamesSortedByRating().getValue());
                    }

                } else if (button.equals(LOVED)) {
                    viewModel.updateCurrentFilter(button);
                    viewModel.upateShowSidebar(true);
                    viewModel.updateY(0);
                    if (viewModel.getSelectedFiltersForLoved().getValue()!=null &&
                            viewModel.getSelectedFiltersForLoved().getValue().size()>0) {
                        list.addAll(viewModel.getSelectedFiltersForLoved().getValue());
                    } else {
                        list.addAll(viewModel.getNamesSortedByRating().getValue());
                    }
                    //TODO user has to be logged in, otherwise to show tost and return;
//                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//                    if (prefs.contains(UiUtils.LOGGED_IN)) {
//                        Log.i(TAG, "user logged in: "+ prefs.getBoolean(UiUtils.LOGGED_IN, true));
//                        Log.i(TAG, "email: "+ prefs.getString(UiUtils.EMAIL, "email"));
//                        Log.i(TAG, "password: "+ prefs.getString(UiUtils.PASSWORD, "psw"));
//                        if (prefs.getBoolean(UiUtils.LOGGED_IN, true)==true) {
//                            //TODO to remove redundant pieces
//
//                        } else {
//                            onInfoViewExpanded(UiUtils.LOG_IN, "");
//                            return;
//                        }
//                    } else {
//                        //UiUtils.showToast(getActivity(), "To use this option, please log in or create an account");
//                        onInfoViewExpanded(UiUtils.CREATE_USER, "");
//                        return;
//                    }

                } else if (button.equals(ALL)) {
                    if (viewModel.getSelectedFiltersForAll().getValue()!=null &&
                            viewModel.getSelectedFiltersForAll().getValue().size()>0) {
                        list.addAll(viewModel.getSelectedFiltersForAll().getValue());
                        Log.d(TAG, "3");
                    } else {
                        list.addAll(viewModel.getNamesSortedByRating().getValue());
                        Log.d(TAG, "4");
                    }
                    viewModel.updateY(0);
                    viewModel.updateCurrentFilter(button);
                    viewModel.upateShowSidebar(true);

                }
                viewModel.updatePointsToShow(list);

                Log.d(TAG, "exit onClick imageButtons(View view) ");
            }
        };

        showLoved.setOnClickListener(filterButtonsListener);
        showAll.setOnClickListener(filterButtonsListener);
        showFilters.setOnClickListener(filterButtonsListener);
        //  clearAll.setOnClickListener(filterButtonsListener);
        buttons.add(showAll);
        buttons.add(showFilters);
        buttons.add(showLoved);

        if (viewModel.getNamesToShowInScroll().getValue()!=null || viewModel.getRatings().getValue()!=null) {
            if (viewModel.getNamesToShowInScroll().getValue().size()>0 || viewModel.getRatings().getValue().size()>0)
            UiUtils.modifyButtons(buttons, viewModel.getCurrentFilter().getValue(), true);
        }

        //   showFilterSection.setOnClickListener(showMoreButtonsListener);
        Log.d(TAG, "exit onCreateView");
        return v;
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

        if (isRestarted == false) {
            //that means that the activity is created the first time or recreated
            //    Log.d(TAG, "select points to show: "+selectPointsToShow);

            mClusterManager = new ClusterManager<MyClusterItem>(getContext(), mMap);
            mClusterManager.setRenderer(new MyClassRenderer(getContext(), mMap, mClusterManager));
            mMap.setOnCameraIdleListener(mClusterManager);

            MyInfoWindowAdaptor adaptor = new MyInfoWindowAdaptor(getContext(), places,
                    orientation, deviceType, viewModel.getHight().getValue(), viewModel.getWight().getValue());
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
//                            //TODO hide sidebar
                        if (viewModel.isSidebarReguested().getValue()!=null) {
                            if (viewModel.isSidebarReguested().getValue().equals(Boolean.TRUE)) {
                                onInfoViewExpanded("HIDE_SIDEBAR", "");
                            }
                        }

                        m.showInfoWindow();

                        //   Log.d(TAG, "exit onMarkerClick(Marker m)");
                        return true;
                    }

                }
            });
            LatLng myLatLng;
            if (mCameraPosition!= null) {
                myLatLng =  new LatLng(mCameraPosition.target.latitude,
                        mCameraPosition.target.longitude);
                recreated=true;
            } else {

                myLatLng = mDefaultCoord;
                currentZoom = default_zoom;
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,
                    currentZoom));
            //   updateLocationUI(filters);
            showClusters();
            if (viewModel.getNamesToShowInScroll().getValue()!=null||viewModel.getRatings().getValue()!=null) {
                if (viewModel.getNamesToShowInScroll().getValue().size()>0||viewModel.getRatings().getValue().size()>0){
                    UiUtils.modifyButtons(buttons, viewModel.getCurrentFilter().getValue(), true);
                }
            }

        } else {
            Log.i(TAG, "restarted");
            try {
                mCameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(latIfRestarted,
                                longIfRestarted))
                        .build();//mMap.getCameraPosition();
                currentZoom = zoomIfRestarted;
            } catch (Exception e) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultCoord,
                        currentZoom));
            }

            isRestarted=false;

        }

        //TODO in the activity has been recreation or reset the ViewModel data has to be used

        Log.d(TAG, "exit onMapReady");

    }

    private void showClusters () {
        Log.d(TAG, "enter showClusters()");
//        //    Log.i(TAG, "marker to show up: "+Place.selectedMarkerID);

        mClusterManager.clearItems();
        mClusterManager.getClusterMarkerCollection().clear();
        mClusterManager.getMarkerCollection().clear();
        markerIds.clear();
        mClusterManager.cluster();
        southCoord=0.0f;
        northCoord=0.0f;
        eastCoord=0.0f;
        westCoord=0.0f;

        Log.i(TAG, "to show: "+viewModel.getPointsNamesToShow().getValue().size());

        count = 0;
        for (Place p : places) {
            if (viewModel.getPointsNamesToShow().getValue().contains(p.getName())) {
                if (southCoord==0.0f) {
                    southCoord=p.getLat();
                } else {
                    if (p.getLat()>southCoord) {
                        southCoord =p.getLat();
                    }
                }
                if (northCoord==0.0f) {
                    northCoord=p.getLat();
                } else {
                    if (p.getLat()<northCoord) {
                        northCoord =p.getLat();
                    }
                }
                if (southCoord==0.0f) {
                    southCoord=p.getLat();
                } else {
                    if (p.getLat()>southCoord) {
                        southCoord =p.getLat();
                    }
                }
                if (westCoord==0.0f) {
                    westCoord=p.getLng();
                } else {
                    if (p.getLng()<westCoord) {
                        westCoord =p.getLng();
                    }
                }
                if (eastCoord==0.0f) {
                    eastCoord=p.getLng();
                } else {
                    if (p.getLng()>eastCoord) {
                        eastCoord =p.getLng();
                    }
                }
                MyClusterItem offsetItem = new MyClusterItem(p.getLat(), p.getLng(), p.getName(), p.getCategory());
                mClusterManager.addItem(offsetItem);
                markerIds.add(p.getName());
            }

        }
        stabilizeViewWithZoom();
        Log.d(TAG, "exit showClusters()");
    }


    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "enter onAttach(Context context)");
        super.onAttach(context);
        if (context instanceof OnPointDataExtendedListener) {
            mListener = (OnPointDataExtendedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPointDataExtendedListener");
        }
        Log.d(TAG, "exit onAttach(Context context)");
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "enter onDetach()");
        super.onDetach();
        mListener = null;
        Log.d(TAG, "exit onDetach()");
    }

    private void stabilizeViewWithZoom() {
        Log.d(TAG, "enter stabilizeViewWithZoom ()");
        if (recreated==false) {
            if (westCoord!=0.0f) {
                LatLngBounds currentBoundaries = new LatLngBounds(
                        new LatLng(northCoord, westCoord), new LatLng(southCoord, eastCoord));

                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(currentBoundaries, 50));
            }
        }

        if (zoomingOut==false) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMap.getCameraPosition().target,
                    mMap.getCameraPosition().zoom-0.005f));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMap.getCameraPosition().target,
                    mMap.getCameraPosition().zoom+0.005f));
        }
        zoomingOut=!zoomingOut;
        recreated=false;

        Log.d(TAG, "exit stabilizeViewWithZoom ()");
    }


    public void onInfoViewExpanded(String action, String pointName) {
        //TODO
        Log.i(TAG, "action: "+action);
        if (!action.equals("HIDE_SIDEBAR")) {
            if (mListener != null) {
                isRestarted=true;
                latIfRestarted = mMap.getCameraPosition().target.latitude;
                longIfRestarted = mMap.getCameraPosition().target.longitude;
                zoomIfRestarted = mMap.getCameraPosition().zoom;
            }
        }

        mListener.onPointDetailsSelected(action);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //TODO hook the click on the marker to the listener, what should trigger the replacement of the activity
        for (Place p : places) {
            if (p.getName().equals(marker.getTitle())) {
                Log.i(TAG, "point to expand: "+p.getName());
                viewModel.updatePoint(p);
                break;
            }
        }

        onInfoViewExpanded("SEE_MORE", marker.getTitle());

    }

    @Override
    public boolean onTouch(View v, MotionEvent motionEvent) {
        if (viewModel.getCurrentFilter().getValue()!=null) {
            viewModel.upateShowSidebar(true);
        }

        return true;
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
                    Log.i(TAG, "got it!");
                    marker.showInfoWindow();
                    selectedMarkerID = "";
                    saveInfoWindow=false;
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
        void onPointDetailsSelected(String action);

    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "enter onResume()");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "enter onPause()");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "enter onStop()");
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }
    }
}
