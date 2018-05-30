package com.google.albertasights.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import com.google.albertasights.R;
import com.google.albertasights.RestIntentServer;
import com.google.albertasights.models.Place;
import com.google.albertasights.models.User;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MapsActivity extends MenuActivity implements MapFragment.OnPointDataExtendedListener,
        PointFragment.OnPointFragmentInteractionListener, NoUserFragment.OnButtonClickedListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    //TODO variables to store map position and zoom if the activity is restarted
    private final LatLng mDefaultCoord = new LatLng(51.0533674, -114.072997);

    //has to be saved as the SavedInstance
 //   private HashSet<Place> places = new HashSet<>();
    //filters selected by user

    private BroadcastReceiver receiver;
    private MapViewModel viewModel;
    private MapFragment mapFragment = new MapFragment();
    private PointFragment pointFr = new PointFragment();
    private NoUserFragment loginFragment = new NoUserFragment();
    private StatusBarFragment progressFr = new StatusBarFragment();
    private SideBarFragment1 sideBar= new SideBarFragment1();
    private Boolean restarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //there may be two options: the activity created the first time whan the app just started, or it is recreated. The behaviour
        //will be different.
        Log.d(TAG, "enter onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if (savedInstanceState!=null) {
            restarted=true;
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        viewModel = ViewModelProviders.of(this).get(MapViewModel.class);

        //TODO saving the dimentions, orientation and device type
        viewModel.updateHights(UiUtils.getHightInches(getApplicationContext()));
        viewModel.updateWight(UiUtils.getWidthInches(getApplicationContext()));
        viewModel.updateDeviceType(UiUtils.findScreenSize(getApplicationContext()));
        viewModel.updateOrientation(UiUtils.getOrientation(getApplicationContext()));
        if (viewModel.getNamesToShowInScroll().getValue()==null) {
            viewModel.updateNamesToShowInScroll(new LinkedList<String>());
        }
        if (viewModel.getRatings().getValue()==null) {
            viewModel.updateRatings(new LinkedList<String>());
        }


        //observes the map with selected points filters to apply
        final Observer<String> filtersObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newFilter) {
                Log.i(TAG, "enter onChanged(String newFilter)");
                Log.i(TAG, "filter: "+newFilter);

                if (newFilter!=null) {
                    LinkedList<String> sortedLost = new LinkedList<>();
                    //if string = FILTER, putting categories as arguments
                    if (newFilter.equals(MapFragment.ALL)) {
                        sortedLost.addAll(viewModel.getNamesSortedByRating().getValue());
                    } else if (newFilter.equals(MapFragment.FILTERS)) {
                        for (Place p : viewModel.getRecievedPoints().getValue()) {
                            if (!sortedLost.contains(p.getCategory())) {
                                sortedLost.add(p.getCategory());
                            }
                        }
                      //  viewModel.updateDataToFilter(tmp);
                    } else if (newFilter.equals(MapFragment.LOVED)) {
                       // viewModel.updateDataToFilter(viewModel.getLoved().getValue());
                        sortedLost.addAll(viewModel.getLoved().getValue());
                    }
                    Collections.sort(sortedLost);

                    viewModel.updateDataToFilter(sortedLost);
                }

            }
        };
        viewModel.getCurrentFilter().observe(this, filtersObserver);

        final Observer<Boolean> sideBarObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable final Boolean newStatus) {
                Log.i(TAG, "enter onChanged(@Nullable final Boolean newStatus)");
                //
                //if string = FILTER, putting categories as arguments
                if (newStatus==false) {
                    UiUtils.manageFragments(sideBar, getSupportFragmentManager(), false,
                            R.id.map_container, "HIDE", "sidebar");
                } else {
                    if (UiUtils.checkIfFragmentAdded("sidebar", getSupportFragmentManager())==false)  {
                        UiUtils.manageFragments(sideBar, getSupportFragmentManager(), false,
                                R.id.map_container, "ADD", "sidebar");
                    } else {
                        UiUtils.manageFragments(sideBar, getSupportFragmentManager(), false,
                                R.id.map_container, "SHOW", "sidebar");
                    }
                }
                //TODO declare the listener in sidebar to change the content
            }
        };
        viewModel.isSidebarReguested().observe(this, sideBarObserver);

        if (viewModel.getLoved().getValue()==null) {
            if (prefs.getStringSet(UiUtils.SELECTED_POINTS, new HashSet<String>())!=null) {
                LinkedList <String> lst = new LinkedList<>();
                lst.addAll(prefs.getStringSet(UiUtils.SELECTED_POINTS, new HashSet<String>()));
                viewModel.updateLoved(lst);
                //  Log.i(TAG, "selected points added: "+ viewModel.getLoved().getValue().size());
            } else {
                viewModel.updateLoved(new LinkedList<String>());
            }
        }

        if (viewModel.getRecievedPoints().getValue()==null) {
            Intent intent = new Intent(this, RestIntentServer.class);
            intent.setAction(UiUtils.SUBMIT);
            intent.putExtra(UiUtils.URL, "https://albertasights.herokuapp.com/api/v1/points_by_district?district=Calgary");
            intent.putExtra(UiUtils.LNG, String.valueOf(mDefaultCoord.longitude));
            intent.putExtra(UiUtils.LAT, String.valueOf(mDefaultCoord.latitude));
            intent.putExtra(UiUtils.DISTANCE, String.valueOf(30));
            startService(intent);
            // start the animation for the period of data loading
            //TODO fragment test
            UiUtils.manageFragments(progressFr, getSupportFragmentManager(), false,
                    R.id.map_container, "ADD", "progr1");

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
                        LinkedList <Place> temp = new LinkedList<>();

                        LinkedList <String> rating1 = new LinkedList<>();
                        LinkedList <String> rating2 = new LinkedList<>();
                        LinkedList <String> rating3 = new LinkedList<>();
                        LinkedList <String> all = new LinkedList<>();
                        temp.addAll(placesLst);
                        viewModel.updatePoints(temp);
                        LinkedList <String> lst1 = new LinkedList<>();
                        for (Place p : placesLst) {
                            lst1.add(p.getName());
                            if (p.getRating()==1||p.getRating()==2) {
                                rating1.add(p.getName());
                            } else if (p.getRating()==3) {
                                rating2.add(p.getName());
                            } else {
                                rating3.add(p.getName());
                            }
                        }
                        all.addAll(rating3);
                        all.addAll(rating2);
                        all.addAll(rating1);
                        viewModel.updatePointsToShow(lst1);
                        viewModel.updateNamesSortedByRating(all);
                        //TODO fragments
                        UiUtils.manageFragments(mapFragment, getSupportFragmentManager(), false,
                                R.id.map_container, "REPLACE", "map");


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
                        if (viewModel.getCurrentFilter().getValue().equals(MapFragment.LOVED)) {
                            viewModel.updateDataToFilter(lst);
                        }

                        UiUtils.showToast(getApplicationContext(),
                                "added!");
                    }


                } else if (intent.getAction().equals(UiUtils.POINT_REMOVED)) {
                    if (intent.getStringExtra(UiUtils.LOVED)!=null) {
                      //  selectedByUser.remove(intent.getStringExtra(UiUtils.LOVED));
                      //  updateLocationUI(filters);
                        LinkedList<String> lst = new LinkedList<>();
                        lst.addAll(viewModel.getLoved().getValue());
                        lst.remove(intent.getStringExtra(UiUtils.LOVED));
                        viewModel.updateLoved(lst);
                        Log.i(TAG, "currrent filter: " + viewModel.getCurrentFilter().getValue());
                        if (viewModel.getCurrentFilter().getValue().equals(MapFragment.LOVED)) {
                            viewModel.updateDataToFilter(lst);
                        }
                        if (viewModel.getPointsNamesToShow().getValue().contains(intent.getStringExtra(UiUtils.LOVED))) {
                            LinkedList<String> lst1 = new LinkedList<>();
                            lst1.addAll(viewModel.getPointsNamesToShow().getValue());
                            lst1.remove(intent.getStringExtra(UiUtils.LOVED));
                            viewModel.updatePointsToShow(lst1);

                        }

                        UiUtils.showToast(getApplicationContext(),
                                "removed!");
                    }
                } else if (intent.getAction().equals(UiUtils.LOG_IN)||intent.getAction().equals(UiUtils.USER_CREATED)) {

                    // the broadcast may receive the User data
                    if (intent.getSerializableExtra(UiUtils.USER)!=null) {
                        Log.d(TAG, "user added");
                        UiUtils.showToast(getApplicationContext(), "Success!");
                    //    getSupportFragmentManager().beginTransaction().replace(R.id.map_container, mapFragment).commit();

                       //TODO remove status bar fragment
                        UiUtils.manageFragments(progressFr, getSupportFragmentManager(), false,
                                R.id.map_container, "REMOVE", "progr1");


                                //replace(R.id.map_container, mapFragment).commit();

                    } else {
                        //no User data received from the service, the reason may be either an error or the lack of user data
                        if (intent.getBooleanExtra((UiUtils.LOGGED_IN), true)==false) {
                            UiUtils.showToast(getApplicationContext(), "We have not find these credentials");

                        } else {
                            UiUtils.showToast(getApplicationContext(), "Error with data submittion");
                        }
                        //TODO remove status bar fragment and return the login one
                        UiUtils.manageFragments(loginFragment, getSupportFragmentManager(), false,
                                R.id.map_container, "ADD", "login");

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
        outState.putBoolean("RESTARTED", true);
       Log.d(TAG, "exit onSaveInstanceState");
    }

    /**
     * Builds the map when the Google Play services client is successfully connected.
     */

    @Override
    protected void onResume() {
        Log.d(TAG, "enter onResume()");
        super.onResume();
       // restarted=true;
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
        intentFilter.addAction(UiUtils.LOG_IN);
        intentFilter.addAction(UiUtils.USER_CREATED);

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
        Log.i(TAG, "fragments: " + getSupportFragmentManager().getFragments().size());
        for (Fragment f : getSupportFragmentManager().getFragments()) {
            Log.i(TAG, "fragm"+ f.getClass().getName());
        }
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
      //  viewModel.updatePoint(null);
        super.onDestroy();
        Log.d(TAG, "exit onDestroy()");
    }


    @Override
    public void onPointDetailsSelected(String action) {
        Log.d(TAG, "enter onPointDetailsSelected(String name)");
        Log.i(TAG, "act: "+action);

        if (action.equals(UiUtils.LOG_IN) || action.equals(UiUtils.CREATE_USER)) {
//TODO add instead of replace
            UiUtils.manageFragments(loginFragment, getSupportFragmentManager(), true,
                    R.id.map_container, "ADD", "login");
        } else if (action.equals("HIDE_SIDEBAR")) {
            UiUtils.manageFragments(new SideBarFragment1(), getSupportFragmentManager(), false,
                    R.id.map_container, "HIDE", "sidebar");
            viewModel.upateShowSidebar(false);

        } else{
            Log.i(TAG, viewModel.getPointToSee().getValue().getName());

            UiUtils.manageFragments(pointFr, getSupportFragmentManager(), true,
                    R.id.map_container, "REPLACE", "point");
            if (viewModel.getOrienr().equals(UiUtils.LANDSCAPE)) {
                UiUtils.manageFragments(new AdsFragment(), getSupportFragmentManager(), true,
                        R.id.map_container, "ADD", "banner");
            }
        }

        Log.d(TAG, "exit onPointDetailsSelected(String name)");

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onLogInOrRegisterButtonClickedListener(String action) {
        Log.i(TAG, "enter onLogInOrRegisterButtonClickedListener(String action)");
        //TODO add instead of replace
        UiUtils.manageFragments(loginFragment, getSupportFragmentManager(), false,
                R.id.map_container, "REMOVE", "login");
        UiUtils.manageFragments(progressFr, getSupportFragmentManager(), false,
                R.id.map_container, "ADD", "progr1");

        Log.i(TAG, "exit onLogInOrRegisterButtonClickedListener(String action)");
    }
}