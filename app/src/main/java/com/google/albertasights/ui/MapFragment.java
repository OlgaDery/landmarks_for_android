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
    //  private RelativeLayout mapWrapper;
    private LinearLayout topWrapper;
    private LinearLayout bottomWr;

    private ImageButton showFilterSection;
    private ImageButton clearAll;
    private ImageButton showFilters;
    private ImageButton showLoved;
    private ImageButton showAll;

    private Map<String, Boolean> filters = new HashMap<String, Boolean>(1);


    private String orientation;
    private String deviceType;
    private boolean isRestarted = false;
    private boolean saveInfoWindow = false;
    private boolean animationStarted = false;
    private boolean filterSidebarModified = false;
    private boolean filtersModified = false;
    private boolean showFiltersSidebar = false;

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

            selectPointsToShow = savedInstanceState.getBoolean(KEY_SELECT_POINTS_TO_SHOW);
            ArrayList<String> ids = savedInstanceState.getStringArrayList(KEY_MARKER_IDS);
            currentZoom = savedInstanceState.getFloat(CURRENT_ZOOM);
            Log.i(TAG, "zoom: " + currentZoom);
            selectedMarkerID = savedInstanceState.getString("SELECTED_MARKER_ID");
       //     current_filter = savedInstanceState.getString("CURRENT_FILTER");
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
            Log.i(TAG, mCameraPosition.target.toString());
            //    places = (HashSet)savedInstanceState.getSerializable(UiUtils.PLACES);
            saveInfoWindow = savedInstanceState.getBoolean(SAVE_INFO_WINDOW);
            animationStarted = savedInstanceState.getBoolean(STARTED_ANIMATION);
            sortedBy = savedInstanceState.getString(UiUtils.SORTED_BY);
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

        viewModel = ViewModelProviders.of(getActivity()).get(MapViewModel.class);

        //observes the list with points names to show on the map
        final Observer<LinkedList<String>> pointsToShowObserver = new Observer<LinkedList<String>>() {
            @Override
            public void onChanged(@Nullable LinkedList<String> strings) {
                Log.d(TAG, "enter onChanged(@Nullable LinkedList<String> strings)");
                if (mMap!=null) {
                    Log.d(TAG, "map is not null");
                    showClusters();
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
        outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
        latIfRestarted = mMap.getCameraPosition().target.latitude;
        longIfRestarted = mMap.getCameraPosition().target.longitude;
        //       outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
        ArrayList <String> ids = new  ArrayList <String>(markerIds.size());
        ids.addAll(markerIds);
        outState.putStringArrayList(KEY_MARKER_IDS, ids);
        //   outState.putSerializable(UiUtils.PLACES, places);
//        outState.putStringArrayList(KEY_RECEIVED_FILTERS, receivedFilters);
//        outState.putStringArrayList("RATINGS", ratings);
//        outState.putStringArrayList(KEY_SELECTED_FILTERS, selectedFilters);
        outState.putBoolean(KEY_SELECT_POINTS_TO_SHOW, selectPointsToShow);
        outState.putFloat(CURRENT_ZOOM, mMap.getCameraPosition().zoom);
        zoomIfRestarted = mMap.getCameraPosition().zoom;
     //   outState.putString("CURRENT_FILTER", current_filter);
        //   outState.putBoolean(API_WAS_CALLED, apiNotCalled);
        outState.putBoolean(STARTED_ANIMATION, animationStarted);
        outState.putString(UiUtils.SORTED_BY, sortedBy);

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
        Log.d(TAG, "enter onSaveInstanceState(@NonNull Bundle outState)");

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
        Log.i(TAG, "restarted: " + isRestarted);
        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
        orientation = UiUtils.getOrientation(getActivity());
        deviceType = UiUtils.findScreenSize(getActivity());
        //      rr = (RelativeLayout) v.findViewById(R.id.rr);
        //    mapWrapper = (RelativeLayout) v.findViewById(R.id.mapWrapper);
        //    bottomWr = (RelativeLayout) v.findViewById(R.id.wrapperBottom);
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

        //TODO
  //      filter = (LinearLayout) v.findViewById(R.id.filters);

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
                        viewModel.upateShowSidebar(true);
//                        if (viewModel.isSidebarReguested().getValue()!=null) {
//                            viewModel.upateShowSidebar(!viewModel.isSidebarReguested().getValue());
//                        } else {
//
//                        }
                        return;
//                    } else {
//                        //TODO user has selected a new filter, need to show all the points
                    }
                } else {
                    //TODO user has selected a filter first time, need to show all the points
                   // viewModel.updatePointsToShow(viewModel.getNamesSortedByRating().getValue());
                }
                LinkedList<String> list = new LinkedList<>();

                //  current_filter = button;
                if (button.equals(FILTERS)) {
                   // filters.clear();
                   // filters.put(FILTERS, true);
                    viewModel.updateCurrentFilter(button);
                    viewModel.upateShowSidebar(true);

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
                    //TODO user has to be logged in, otherwise to show tost and return;
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    if (prefs.contains(UiUtils.LOGGED_IN)) {
                        Log.i(TAG, "user logged in: "+ prefs.getBoolean(UiUtils.LOGGED_IN, true));
                        Log.i(TAG, "email: "+ prefs.getString(UiUtils.EMAIL, "email"));
                        Log.i(TAG, "password: "+ prefs.getString(UiUtils.PASSWORD, "psw"));
                        if (prefs.getBoolean(UiUtils.LOGGED_IN, true)==true) {

                            viewModel.updateCurrentFilter(button);
                            viewModel.upateShowSidebar(true);
                            if (viewModel.getSelectedFiltersForLoved().getValue()!=null &&
                                    viewModel.getSelectedFiltersForLoved().getValue().size()>0) {
                                list.addAll(viewModel.getSelectedFiltersForLoved().getValue());
                            } else {
                                list.addAll(viewModel.getNamesSortedByRating().getValue());
                            }

                        } else {
                            onInfoViewExpanded(UiUtils.LOG_IN, "");
                            return;
                        }
                    } else {
                        //UiUtils.showToast(getActivity(), "To use this option, please log in or create an account");
                        onInfoViewExpanded(UiUtils.CREATE_USER, "");
                        return;
                    }

                } else if (button.equals(ALL)) {
                    if (viewModel.getSelectedFiltersForAll().getValue()!=null &&
                            viewModel.getSelectedFiltersForAll().getValue().size()>0) {
                        list.addAll(viewModel.getSelectedFiltersForAll().getValue());
                        Log.d(TAG, "3");
                    } else {
                        list.addAll(viewModel.getNamesSortedByRating().getValue());
                        Log.d(TAG, "4");
                    }
                    viewModel.updateCurrentFilter(button);
                    viewModel.upateShowSidebar(true);

                }
                viewModel.updatePointsToShow(list);
             //   stabilizeViewWithZoom();

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

        //   showFilterSection.setOnClickListener(showMoreButtonsListener);
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
        Log.d(TAG, "enter onRequestPermissionsResult");
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                  //  updateLocationUI(filters);

                    if (places.size()>0) {
                        showClusters();
                    }
                }
            }
        }
        Log.d(TAG, "exit onRequestPermissionsResult");
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
//                            //TODO hide sidebar
                        onInfoViewExpanded("HIDE_SIDEBAR", "");
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
            } else {

                myLatLng = mDefaultCoord;
                currentZoom = default_zoom;
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,
                    currentZoom));
            //   updateLocationUI(filters);
            showClusters();

        } else {
            Log.i(TAG, "restarted");
            mCameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latIfRestarted,
                            longIfRestarted))
                    .build();//mMap.getCameraPosition();
            currentZoom = zoomIfRestarted;
          //  UiUtils.modifyButtons(buttons, current_filter);

            isRestarted=false;
        }

        //TODO in the activity has been recreation or reset the ViewModel data has to be used

        Log.d(TAG, "exit onMapReady");

    }

    private void showClusters () {
        Log.d(TAG, "enter showClusters()");
//        //    Log.i(TAG, "marker to show up: "+Place.selectedMarkerID);
//        if (selectedFilters.size()==0&& ratings.size()==0) {
//            UiUtils.modifyButtons(buttons, "");
//        } else {
//            UiUtils.modifyButtons(buttons, current_filter);
//        }

        mClusterManager.clearItems();
        mClusterManager.getClusterMarkerCollection().clear();
        mClusterManager.getMarkerCollection().clear();
        markerIds.clear();
        mClusterManager.cluster();
        Log.i(TAG, "to show: "+viewModel.getPointsNamesToShow().getValue().size());

        count = 0;
        for (Place p : places) {
            if (viewModel.getPointsNamesToShow().getValue().contains(p.getName())) {
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
        if (zoomingOut==false) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMap.getCameraPosition().target,
                    mMap.getCameraPosition().zoom-0.005f));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMap.getCameraPosition().target,
                    mMap.getCameraPosition().zoom+0.005f));
        }
        zoomingOut=!zoomingOut;
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
