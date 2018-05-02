package com.google.albertasights.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import com.google.albertasights.R;
import com.google.albertasights.RestIntentServer;
import com.google.albertasights.models.Place;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

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
            Log.i(TAG, "selected points added: "+ viewModel.getLoved().getValue().size());
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (viewModel.getRecievedPoints().getValue()==null) {
            Intent intent = new Intent(this, RestIntentServer.class);
            intent.setAction(UiUtils.SUBMIT);
            intent.putExtra(UiUtils.URL, "https://albertasights.herokuapp.com/api/v1/points_by_district?district=Calgary");
            intent.putExtra(UiUtils.LNG, String.valueOf(mDefaultCoord.longitude));
            intent.putExtra(UiUtils.LAT, String.valueOf(mDefaultCoord.latitude));
            intent.putExtra(UiUtils.DISTANCE, String.valueOf(30));
            startService(intent);
            // start the animation for the period of data loading
            transaction.add(R.id.map_container, new StatusBarFragment()).commit();
        } else if (viewModel.getPointToSee().getValue()!=null){
            transaction.add(R.id.map_container, new PointFragment()).commit();
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
                        viewModel.updatePoints(lst);
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
        viewModel.updatePoint(null);
        super.onDestroy();
        Log.d(TAG, "exit onDestroy()");
    }


    @Override
    public void onPointDetailsSelected(String action) {
        Log.d(TAG, "enter onPointDetailsSelected(String name)");
        Log.i(TAG, "act: "+action);
        if (action.equals(UiUtils.LOG_IN)) {
            getSupportFragmentManager().beginTransaction().replace(R.id.map_container,
                    new NoUserFragment()).addToBackStack(null).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.map_container,
                    new PointFragment()).addToBackStack(null).commit();
        }

        Log.d(TAG, "exit onPointDetailsSelected(String name)");

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onLogInOrRegisterButtonClickedListener(String action) {
        if (action.equals(UiUtils.LOG_IN)) {
            //show the fragment with user data

        } else if (action.equals(UiUtils.CREATE_USER)) {
            //TODO show the 3d fragment with forms
            //   modifYUserDataFragment = new EnterUserFragment();
            Intent i = new Intent(this, UserActivity.class);
            i.setAction(UiUtils.CREATE_USER);
            i.putExtra(UiUtils.BACK_TO_MAP, true);
            startActivity(i);
        }

    }
}