package com.google.albertasights;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DBIntentService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ADD_POINT_TO_LOVED = "add_to_loved";
    public static final String CREATE_USER = "create_user";
    public static final String CHECK_CONFIG = "check_config";
    public static final String POINT_ID = "point_id";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String ROLE = "role";
    private static final String TAG = DBIntentService.class.getSimpleName();

    public DBIntentService() {

        super("DBIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "enter onHandleIntent(Intent intent)");
        if (intent != null) {
            final String action = intent.getAction();
            if (ADD_POINT_TO_LOVED.equals(action)) {
                final String param1 = intent.getStringExtra(POINT_ID);
                saveSelectedPoint(param1);
            } else if (CREATE_USER.equals(action)) {
                final String email = intent.getStringExtra(EMAIL);
                final String password = intent.getStringExtra(PASSWORD);
                String role = "user";
                try {
                    role = intent.getStringExtra(ROLE);
                } catch (Exception e) {

                }
                try {
                    createUser(email, password, role);
                    Intent i = new Intent();
                    i.setAction("USER_CREATED");
                    //  i.putExtra("USER_CREATED", param1);
                    getApplicationContext().sendBroadcast(i);
                } catch (Exception e) {

                }
            } else if (CHECK_CONFIG.equals(action)) {


            }
        }
        Log.d(TAG, "exit onHandleIntent(Intent intent)");
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void saveSelectedPoint(String param1) {
        // TODO: Handle action Foo
        Log.d(TAG, "exit saveSelectedPoint(String param1)");
        //TODO temporary
        UiUtils.selectedPointsIds.add(param1);
        Intent i = new Intent();
        i.setAction("POINT_ADDED");
        i.putExtra("LOVED", param1);
        getApplicationContext().sendBroadcast(i);

    }

    private void createUser(String email, String uPassword, String role) {
        Log.d(TAG, "enter createUser");
        // TODO: Replace with injection
        DBConnection connection = new DBConnection("user", getApplicationContext());
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("user_name", "Olga");
        properties.put("email", "androgeny80@gmail.com");
        //TODO encript
        properties.put("password", "androgeny80@gmail.com");
        properties.put("role", "admin");
    //    properties.put("ID", document.getId());
//        try {
//            document.putProperties(properties);
//        } catch (CouchbaseLiteException e) {
//            e.printStackTrace();
//        }

    }

    private void checkDB() {
        Log.d(TAG, "enter createUser");
        // TODO: Replace with injection
        DBConnection connection = new DBConnection("user", getApplicationContext());
        if (connection.getDatabase().getDocumentCount()>1) {


        } else {
            //TODO create documents: USER, SELECTED_POINTS

        }

    }



}
