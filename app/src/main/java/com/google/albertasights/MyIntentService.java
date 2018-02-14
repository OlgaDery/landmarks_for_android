package com.google.albertasights;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyIntentService extends IntentService {
    public static final String URL = "URL";
    public static final String LNG = "LNG";
    public static final String LAT = "LAT";
    public static final String DISTANCE = "DISTANCE";
    private final String TAG =
            getClass().getSimpleName();
    private ArrayList<Place> places;

    public MyIntentService() {

        super("MyIntentService");
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
            if ("SUBMIT".equals(action)) {
                final String url = intent.getStringExtra(URL);
                final String lat = intent.getStringExtra(LAT);
                final String lng = intent.getStringExtra(LNG);
                final String dist = intent.getStringExtra(DISTANCE);
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
        // TODO: Handle action Foo
        //    Log.d(TAG, "enter postPointData(String url) ");
        URL url1 = null;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("lng", lng);
        params.put("lat", lat);
        params.put("distance", distance);
        //   Log.d(TAG, "distance: "+ distance);
        Intent intent = new Intent();
        intent.setAction("DATA_RECEIVED");

        try {
            url1 = new URL(url);
        } catch (Exception e) {
            Log.e(TAG, "something wron with url");
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection)url1.openConnection();
            connection.setRequestMethod("POST");//("POST")
            connection.setRequestProperty("authorization", "3.14");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(params));

            writer.flush();
            writer.close();
            os.close();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                //   Log.i(TAG, response.toString());
                //TODO broadcast this message to map activity

                ArrayList<Place> places1 = parsePlaceData(response.toString());
                if (places1!=null) {
                    intent.putExtra("RESULT", "Data received");
                    intent.putExtra("PLACES", (Serializable)places1);
                } else {
                    intent.putExtra("RESULT", "Error, server may be unavailable");
                }


            } else {
                // Log.d(TAG, String.valueOf(connection.getResponseCode()));
                Log.e(TAG, connection.getResponseMessage());
                intent.putExtra("RESULT", "Error, server may be unavailable");
            }

        } catch (IOException e) {
            Log.e(TAG, "io exception");
            intent.putExtra("RESULT", "Error, server may be unavailable");
        } finally {
            if (connection!=null)
                connection.disconnect();
            getApplicationContext().sendBroadcast(intent);
            //   Log.d(TAG, "exit postPointData(String url) ");
        }

    }

    private String getPostDataString(HashMap<String, String> params)  {

        JSONObject obj = new JSONObject();
        try {
            obj.put("lat",  params.get("lat"));
            obj.put("lng",  params.get("lng"));
            obj.put("distance", params.get("distance"));
        } catch (JSONException e) {

            //   e.printStackTrace();
        }

        return obj.toString();
    }

    private ArrayList<Place> parsePlaceData (String data) {
        //    Log.d(TAG, "enter parsePlaceData (String data)");
        places = new ArrayList<>();

        try{
            JSONArray mJsonArray = new JSONArray(data);
            for (int i = 0; i < mJsonArray.length(); i++) {
                JSONObject mJsonObjectProperty = mJsonArray.getJSONObject(i);

                String name = mJsonObjectProperty.getString("poi_name");
                String lat = mJsonObjectProperty.getString("poi_lat");
                String lng = mJsonObjectProperty.getString("poi_lng");
                String descr = mJsonObjectProperty.getString("descript");
                String category = mJsonObjectProperty.getString("poi_main_cat_index");
                String link = null;
                JSONArray photos = mJsonObjectProperty.getJSONArray("photos_with_point");
                for (int ii = 0; ii < photos.length(); ii++) {
                    JSONObject photoProp = photos.getJSONObject(ii);
                    //   Log.i(TAG, photoProp.toString());
                    try {
                        link = photoProp.getString("photo_id");
                    }catch (Exception e) {
                        //      Log.i(TAG, "no photos");
                    }
                    break;
                }

                //  String result = mJsonObject.getString("photos_with_point");
                Place p = new Place(name, descr, link, Double.valueOf(lng), Double.valueOf(lat));
                p.setCategory(Place.poi_main_cat[Integer.valueOf(category)]);
                //   Place.places.add(p);
                places.add(p);
                // Log.i(TAG, p.getName());
            }
            //    Log.i(TAG, "places size: "+Place.places.size());

            return places;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        } finally {
            //   Log.d(TAG, "exit parsePlaceData (String data)");
        }


    }
}
