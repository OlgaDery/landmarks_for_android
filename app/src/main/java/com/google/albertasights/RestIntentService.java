package com.google.albertasights;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.albertasights.models.Place;
import com.google.albertasights.ui.UiUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class RestIntentService extends IntentService {

    private final String TAG =
            "RestIntentService";
   // private ArrayList<Place> places;

    public RestIntentService() {

        super("RestIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */

    @Override
    protected void onHandleIntent(Intent intent) {
        //   Log.d(TAG, "enter onHandleIntent(Intent intent) ");

        if (intent != null) {
            final String action = intent.getAction();
            if (UiUtils.SUBMIT.equals(action)) {
                final String url = intent.getStringExtra(UiUtils.URL);
                final String lat = intent.getStringExtra(UiUtils.LAT);
                final String lng = intent.getStringExtra(UiUtils.LNG);
                final String dist = intent.getStringExtra(UiUtils.DISTANCE);
                //   final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                postPointData(url, lng, lat, dist);
            }
        }
        //   Log.d(TAG, "exit onHandleIntent(Intent intent) ");
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void postPointData(String url, String lng, String lat, String distance) {
        // TODO: temporary replace
     //   Log.d(TAG, "enter postPointData(String url) ");
        URL url1 = null;
        Intent intent = new Intent();
        intent.setAction(UiUtils.DATA_RECEIVED);

        try {
            url1 = new URL(url);
        } catch (Exception e) {
          //  Log.e(TAG, "something wron with url");
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection)url1.openConnection();
           // connection.setRequestMethod("GET");//("POST")
            connection.setRequestProperty("X-Api-Key", "3.14");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(120000);
            connection.connect();
          //  InputStream in = new BufferedInputStream(connection.getInputStream());

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = reader.readLine()) != null) {
                    response.append(inputLine);
                }
                //   Log.i(TAG, response.toString());
                //TODO broadcast this message to map activity

                parsePlaceData(response.toString());

            } else {

                Log.e(TAG, connection.getResponseMessage());
                intent.putExtra(UiUtils.RESULT, "Error, server may be unavailable");
                getApplicationContext().sendBroadcast(intent);
            }

        } catch (Exception e) {
            Log.e(TAG, "io exception");
            intent.putExtra(UiUtils.RESULT, "Error, server may be unavailable");
            getApplicationContext().sendBroadcast(intent);
        } finally {
            if (connection!=null)
                connection.disconnect();
            //   Log.d(TAG, "exit postPointData(String url) ");
        }

    }


    private ArrayList<Place> parsePlaceData (String data) {
        Log.d(TAG, "enter parsePlaceData (String data)");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ArrayList<String> selectedNames = new ArrayList<>();
        Set<String> selectedIds = new HashSet<>();
        boolean notConfig = false;
        //TODO temporary we need to replace names with ids
        if (prefs.contains(UiUtils.SELECTED_POINTS)&& (!prefs.contains(UiUtils.RECONFIGURED))) {
            selectedNames.addAll(prefs.getStringSet(UiUtils.SELECTED_POINTS, new HashSet<String>()));
            notConfig =true;
            Log.i(TAG, "points selected and not replaced with IDs");
        } else {
            if (!prefs.contains(UiUtils.RECONFIGURED)) {
                SharedPreferences.Editor editor = UiUtils.getEditor(this);
                editor.putBoolean(UiUtils.RECONFIGURED, true);
                editor.commit();
            }
            selectedIds.addAll(prefs.getStringSet(UiUtils.SELECTED_POINTS, new HashSet<String>()));
            Log.i(TAG, "selected ids size: "+selectedIds.size());
            Log.i(TAG, "points not selected or already replaced with IDs");
        }

        ArrayList<Place> places = new ArrayList<>();
        ArrayList<String> allNames = new ArrayList<>();
        Intent intent = new Intent();
        intent.setAction(UiUtils.DATA_RECEIVED);
        try{
            JSONArray mJsonArray = new JSONArray(data);
            for (int i = 0; i < mJsonArray.length(); i++) {

                JSONObject mJsonObjectProperty = mJsonArray.getJSONObject(i);
                String pendingStatus = mJsonObjectProperty.getString("pend_status");
                if (pendingStatus.contains("PBL")) {
                    String id = mJsonObjectProperty.getString("point_id");
                    String name = mJsonObjectProperty.getString("name");
                    // Log.i(TAG, "name: "+mJsonObjectProperty.getString("name"));
                    String lat = mJsonObjectProperty.getString("lat");
                    String lng = mJsonObjectProperty.getString("lng");
                    String main_point_id = mJsonObjectProperty.getString("main_point_id");
                    String descr = mJsonObjectProperty.getString("description");
                    String category = mJsonObjectProperty.getString("category");
                    String extraCategoryIndex = mJsonObjectProperty.getString("extra_category");
                //    Log.i(TAG, "cat: "+mJsonObjectProperty.getString("category"));
                    String link = mJsonObjectProperty.getString("photolink");
                    String webLink = mJsonObjectProperty.getString("weblink");
                    Integer rating = Integer.valueOf(mJsonObjectProperty.getString("rating"));
                    int newRating = 3;
                    switch (rating) {
                        case 5: {
                            newRating =3;
                            break;
                        }
                        case 4: {
                            newRating =3;
                            break;
                        }
                        case 3: {
                            newRating =2;
                            break;
                        }
                        case 2: {
                            newRating =1;
                            break;
                        }case 1: {
                            newRating =1;
                            break;
                        }
                    }
                    Place p = new Place(name, descr, link, Double.valueOf(lng),
                            Double.valueOf(lat), webLink, newRating);
                    p.setCategory(category);
                    p.setId(id);
                    //   Place.places.add(p);
                    allNames.add(name);
                    places.add(p);
                    if (notConfig) {
                        if (selectedNames.contains(name)) {
                            selectedIds.add(id);
                        } else {
                            selectedNames.remove(name);
                        }
                    } else {
                        if (selectedIds.size()>0) {
                            if (selectedIds.contains(id)) {
                                selectedNames.add(name);
                            }
                        }
                    }

                }
               // Log.i(TAG, "size: " + places.size());
            }
            if (notConfig) {
                //TODO here we are replacing names with IDs in shared preferences
                SharedPreferences.Editor editor = UiUtils.getEditor(this).putStringSet
                        (UiUtils.SELECTED_POINTS,  selectedIds);
                editor.putBoolean(UiUtils.RECONFIGURED, true);
                editor.commit();
            }
            intent.putExtra(UiUtils.RESULT, "Data received");
            intent.putExtra(UiUtils.PLACES, places);
            intent.putStringArrayListExtra(UiUtils.NAMES, allNames);
            intent.putStringArrayListExtra(UiUtils.LOVED, selectedNames);

            return places;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            intent.putExtra(UiUtils.RESULT, "Error with data processing");
            return null;
        } finally {
            Log.d(TAG, "exit parsePlaceData (String data)");
            getApplicationContext().sendBroadcast(intent);
        }

    }
}
