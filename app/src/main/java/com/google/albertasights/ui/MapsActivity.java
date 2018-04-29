package com.google.albertasights.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
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
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class MapsActivity extends MenuActivity implements MapFragment.OnPointDataExtendedListener {

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
    private String selectedMarkerID = "";

    //TODO place them in ModelView???
  //  public static Set<String> markerIds = new HashSet<>();
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
   // public static final String FILTERS = "filters";
//    public static final String LOVED = "loved";
 //   public static final String ALL = "all";
    private static final String CLEAR_MAP = "clear_map";

    private View.OnClickListener checkBoxListener;
    private View.OnClickListener filterButtonsListener;
    private Set <ImageButton> buttons = new HashSet<>();
    //TODO to create the infrastracture to update selectedByUser
    private Set <String> selectedByUser = new HashSet<>();

    private BroadcastReceiver receiver;
    private MapViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //there may be two options: the activity created the first time whan the app just started, or it is recreated. The behaviour
        //will be different.
        Log.d(TAG, "enter onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //TODO check the shared preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        viewModel = ViewModelProviders.of(this).get(MapViewModel.class);

        if (prefs.getStringSet(UiUtils.SELECTED_POINTS, new HashSet<String>())!=null) {
            LinkedList <String> lst = new LinkedList<>();
            lst.addAll(prefs.getStringSet(UiUtils.SELECTED_POINTS, new HashSet<String>()));
            viewModel.updateLoved(lst);
        }

        if (places.size()==0) {
            //to send the intent to request the data from API
            Intent intent = new Intent(this, RestIntentServer.class);
            intent.setAction(UiUtils.SUBMIT);
            intent.putExtra(UiUtils.URL, "https://albertasights.herokuapp.com/api/v1/points_by_district?district=Calgary");
            intent.putExtra(UiUtils.LNG, String.valueOf(mDefaultCoord.longitude));
            intent.putExtra(UiUtils.LAT, String.valueOf(mDefaultCoord.latitude));
            intent.putExtra(UiUtils.DISTANCE, String.valueOf(30));
            startService(intent);
            // start the animation for the period of data loading
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.map_container, new StatusBarFragment()).commit();

        }

        // Retrieve the content view that renders the map.



        receiver = new BroadcastReceiver () {
            @Override
            public void onReceive(Context context, Intent intent) {
                //On receive should be called very rarely, onle the current location is significantly changed. In our case, it is calling
                //onle once
                Log.d(TAG, "enter onReceive(Context context, Intent intent)");
                if (intent.getAction().equals(UiUtils.DATA_RECEIVED)) {
                    if (intent.getSerializableExtra(UiUtils.PLACES)!=null) {
                        ArrayList<Place> placesLst = (ArrayList)intent.getSerializableExtra(UiUtils.PLACES);
                        //         Log.i(TAG, "places: "+ placesLst.size());
                        LinkedList <Place> lst = new LinkedList<>();
                        lst.addAll(placesLst);
                        viewModel.updatePoint(lst);
                        LinkedList <String> lst1 = new LinkedList<>();
                        for (Place p : placesLst) {
                            lst1.add(p.getName());
                        }
                        viewModel.updatePointsToShow(lst1);
                        getSupportFragmentManager().beginTransaction().replace(R.id.map_container,
                                new MapFragment()).commit();
                        // show toast if the service failed
//                        UiUtils.showToast(getApplicationContext(),
//                                "got the data");

                    } else {
                        UiUtils.showToast(getApplicationContext(),
                                "server with the data may be unavailable, try again later");
                    }
                } else if (intent.getAction().equals(UiUtils.POINT_ADDED)) {
                    if (intent.getStringExtra(UiUtils.LOVED)!=null) {
                        LinkedList<String> lst = new LinkedList<>();
                        lst.addAll(viewModel.getLoved().getValue());
                        lst.add(intent.getStringExtra(UiUtils.LOVED));
                        viewModel.updateLoved(lst);
                      //  selectedByUser.add(intent.getStringExtra(UiUtils.LOVED));
                     //   updateLocationUI(filters);
                        UiUtils.showToast(getApplicationContext(),
                                "added!");
                    }
//                } else if (intent.getAction().equals(UiUtils.DB_CHECKED)) {
//                    Log.d(TAG, "DB checked, receiver started");
//
//                    //TODO the broadcast may receive the User data and the IDs selected by user
//                    if (intent.getSerializableExtra(UiUtils.USER)!=null) {
//                        User user = (User) intent.getSerializableExtra(UiUtils.USER);
//
//                        if (intent.getSerializableExtra(UiUtils.SELECTED_POINTS)!=null) {
//                            selectedByUser.addAll((ArrayList<String>)intent.getStringArrayListExtra(UiUtils.SELECTED_POINTS));
//                        }
//                    } else {
//                        UiUtils.showToast(getApplicationContext(), "user is null");
//                    }

                } else if (intent.getAction().equals(UiUtils.POINT_REMOVED)) {
                    if (intent.getStringExtra(UiUtils.LOVED)!=null) {
                      //  selectedByUser.remove(intent.getStringExtra(UiUtils.LOVED));
                      //  updateLocationUI(filters);
                        LinkedList<String> lst = new LinkedList<>();
                        lst.addAll(viewModel.getLoved().getValue());
                        lst.remove(intent.getStringExtra(UiUtils.LOVED));
                        viewModel.updateLoved(lst);
                        if (viewModel.getPointsNamesToShow().getValue().contains(intent.getStringExtra(UiUtils.LOVED))) {
                            LinkedList<String> lst1 = new LinkedList<>();
                            lst.addAll(viewModel.getPointsNamesToShow().getValue());
                            lst.remove(intent.getStringExtra(UiUtils.LOVED));
                            viewModel.updatePointsToShow(lst);

                        }

                        UiUtils.showToast(getApplicationContext(),
                                "removed!");
                    }
                }
                Log.d(TAG, "exit onReceive(Context context, Intent intent)");
            }
        };

    //    Log.d(TAG, "tracking filters size:" + filters.size());

        Log.d(TAG, "exit onCreate");
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
          Log.d(TAG, "enter onSaveInstanceState");
        super.onSaveInstanceState(outState);
       Log.d(TAG, "exit onSaveInstanceState");
    }

    /**
     * Builds the map when the Google Play services client is successfully connected.
     */

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

    private void registerReceiver() {
//        Log.d(TAG, "enter registerReceiver() for DATA_RECEIVED");
        // Create an intent filter for DATA_RECEIVED.
        IntentFilter intentFilter =
                new IntentFilter();
        intentFilter.addAction(UiUtils.DATA_RECEIVED);
        intentFilter.addAction(UiUtils.DB_CHECKED);
        intentFilter.addAction(UiUtils.POINT_ADDED);
        intentFilter.addAction(UiUtils.POINT_REMOVED);

        // Register the receiver and the intent filter.
        registerReceiver(receiver,
                intentFilter);
        //       Log.d(TAG, "exit registerReceiver() for DATA_RECEIVED");
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */

    //TODO add observer to follow the changes in the filter map, depending on it`s state add or remove the functional buttons
    //TODO and filter bar. Modify the lists of received and selected filters (may be the list of loved points or list of all
    //TODO points names). Call the updateLocationUI()


    @Override
    protected void onPause () {
        Log.d(TAG, "enter onPause ()");
        super.onPause();
        this.unregisterReceiver(this.receiver);
        Log.d(TAG, "exit onPause()");
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
    public void onPointDetailsSelected(String name) {

    }
}