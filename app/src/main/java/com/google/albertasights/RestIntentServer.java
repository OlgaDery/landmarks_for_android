package com.google.albertasights;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
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
public class RestIntentServer extends IntentService {
    public static final String URL = "URL";
    public static final String LNG = "LNG";
    public static final String LAT = "LAT";
    public static final String DISTANCE = "DISTANCE";
    private final String TAG =
            getClass().getSimpleName();
    private ArrayList<Place> places;

    public RestIntentServer() {

        super("RestIntentServer");
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
        // TODO: temporary replace
     //   Log.d(TAG, "enter postPointData(String url) ");
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
          //  Log.e(TAG, "something wron with url");
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection)url1.openConnection();
           // connection.setRequestMethod("GET");//("POST")
            connection.setRequestProperty("X-Api-Key", "3.14");
            connection.setRequestProperty("Content-Type", "application/json");
          //  connection.setDoInput(true);
          //  connection.setDoOutput(true);
            connection.connect();
            InputStream in = new BufferedInputStream(connection.getInputStream());

         //   OutputStream os = connection.getOutputStream();
//            BufferedWriter writer = new BufferedWriter(
//                    new OutputStreamWriter(in, "UTF-8"));
//            writer.write(getPostDataString(params));
//
//            writer.flush();
//            writer.close();
//            os.close();

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

                ArrayList<Place> places1 = parsePlaceData(response.toString());
                if (places1!=null) {
                    intent.putExtra("RESULT", "Data received");
                    intent.putExtra("PLACES", (Serializable)places1);
                } else {
                    intent.putExtra("RESULT", "Error, server may be unavailable");
                }


            } else {

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


    private ArrayList<Place> parsePlaceData (String data) {
        //    Log.d(TAG, "enter parsePlaceData (String data)");
        places = new ArrayList<>();

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
                    Place p = new Place(name, descr, link, Double.valueOf(lng),
                            Double.valueOf(lat), webLink, rating);
                    //TODO!!!
                    p.setCategory(category);
                    p.setId(id);
                    //   Place.places.add(p);
                    places.add(p);

                }
               // Log.i(TAG, "size: " + places.size());
            }

            return places;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        } finally {
            //   Log.d(TAG, "exit parsePlaceData (String data)");
        }


    }
}
