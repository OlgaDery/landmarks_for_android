package com.google.albertasights;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Contacts;
import android.util.Log;


import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.MutableDocument;
import com.google.albertasights.models.User;
import com.google.albertasights.ui.UiUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DBIntentService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String TAG = DBIntentService.class.getSimpleName();

    public DBIntentService() {

        super("DBIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "enter onHandleIntent(Intent intent)");
        if (intent != null) {
            final String action = intent.getAction();
            Log.i(TAG, "action: "+ action);
            if (UiUtils.ADD_POINT_TO_LOVED.equals(action)) {
                final String param1 = intent.getStringExtra(UiUtils.POINT_ID);
                saveSelectedPoint(param1);
            } else if (UiUtils.CREATE_USER.equals(action)) {
                final String email = intent.getStringExtra(UiUtils.EMAIL);
                final String password = intent.getStringExtra(UiUtils.PASSWORD);
                final String firstName = intent.getStringExtra(UiUtils.FIRST_NAME);
                final String lastName = intent.getStringExtra(UiUtils.LAST_NAME);
                String role = "user";
                try {
                    role = intent.getStringExtra(UiUtils.ROLE);
                } catch (Exception e) {
                    role = "user";
                }
                try {
                    createUser(email, password, role, firstName, lastName);

                } catch (Exception e) {

                }
            } else if (UiUtils.CHECK_CONFIG.equals(action)) {
                checkDB();
            } else if (UiUtils.REMOVE_POINT.equals(action)) {
                final String param1 = intent.getStringExtra(UiUtils.POINT_ID);
                removeFromSelectedPoint(param1);
            }
        }
        Log.d(TAG, "exit onHandleIntent(Intent intent)");
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void saveSelectedPoint(String param1) {

        Log.d(TAG, "exit saveSelectedPoint(String param1)");
        //TODO add point ID to the document "selected_points", if does not exist, create the new one
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> selectedPoints = new HashSet<>();
        if (prefs.contains(UiUtils.SELECTED_POINTS)) {
            selectedPoints.addAll(prefs.getStringSet(UiUtils.SELECTED_POINTS, new HashSet<String>()));
            selectedPoints.add(param1);
        } else {
            selectedPoints.add(param1);
        }

        SharedPreferences.Editor editor = UiUtils.getEditor(this).putStringSet
                (UiUtils.SELECTED_POINTS,  selectedPoints);
        editor.commit();
     //   int size = prefs.getStringSet(UiUtils.SELECTED_POINTS, new HashSet<String>()).size();
     //   Log.i(TAG, "selected size: "+size);
        Intent i = new Intent();
        i.setAction(UiUtils.POINT_ADDED);
        i.putExtra(UiUtils.LOVED, param1);
        getApplicationContext().sendBroadcast(i);

    }

    private void removeFromSelectedPoint(String id) {

        Log.d(TAG, "enter removeFromSelectedPoint(String param1)");
        //TODO add point ID to the document "selected_points", if does not exist, create the new one

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> selectedPoints = new HashSet<>();
        selectedPoints.addAll(prefs.getStringSet(UiUtils.SELECTED_POINTS, new HashSet<String>()));
        selectedPoints.remove(id);
        SharedPreferences.Editor editor = UiUtils.getEditor(this).putStringSet
                (UiUtils.SELECTED_POINTS,  selectedPoints);
        editor.commit();
        Intent i = new Intent();
        i.setAction(UiUtils.POINT_REMOVED);
        i.putExtra(UiUtils.LOVED, id);
        getApplicationContext().sendBroadcast(i);

    }

    private void createUser(String email, String uPassword, String role, String firstName, String lastName) {
        Log.d(TAG, "enter createUser");
        Intent i = new Intent();
        try {
       //     db.save(userDoc);
            SharedPreferences.Editor editor = UiUtils.getEditor(this);
          //  Log.i(TAG, editor.toString());
            editor.putString(UiUtils.EMAIL, email);
            //TODO userID should be generated by the back end web service
            //  editor.putString(UiUtils.USER_ID, user.getId());
            editor.putString(UiUtils.FIRST_NAME, firstName);
            editor.putString(UiUtils.LAST_NAME, lastName);
            editor.putString(UiUtils.ROLE, role);
            editor.putBoolean(UiUtils.LOGGED_IN, true);
            editor.putString(UiUtils.PASSWORD, uPassword);
            editor.commit();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            Log.d(TAG, "pref size: " +prefs.getAll().size());

            User user = new User(email, uPassword);
            user.setLastName(lastName);
            user.setFirstName(firstName);
            user.setRole(role);
            i.putExtra(UiUtils.USER, user);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        i.setAction(UiUtils.USER_CREATED);
        //  i.putExtra("USER_CREATED", param1);
        getApplicationContext().sendBroadcast(i);

    }

    private void checkDB() {
        Log.d(TAG, "enter checkDB()");
        // TODO: Replace with injection
        // TODO chech if docks exist: USER, SELECTED_POINTS
        Database db = DBConnection.getDatabase("mydb", getApplicationContext());
        Intent i = new Intent();
        i.setAction(UiUtils.DB_CHECKED);
        if (db.getDocument(UiUtils.USER)!=null) {
            Log.d(TAG, "user is not null");
            User user = new User(db.getDocument(UiUtils.USER).getString(UiUtils.EMAIL),
                    db.getDocument(UiUtils.USER).getString(UiUtils.PASSWORD));
            user.setRole( db.getDocument(UiUtils.USER).getString(UiUtils.ROLE));
            user.setFirstName( db.getDocument(UiUtils.USER).getString(UiUtils.FIRST_NAME));
            user.setLastName( db.getDocument(UiUtils.USER).getString(UiUtils.LAST_NAME));
            if (db.getDocument(UiUtils.USER).contains(UiUtils.USER_ID)) {
                //TODO add user
                user.setId(db.getDocument(UiUtils.USER).getString(UiUtils.USER_ID));
            }
            //any points have been saved as selected
            if (db.getDocument(UiUtils.SELECTED_POINTS)!=null) {
                LinkedList<String> pointsIDs = new LinkedList<String>();
                for (Object o : db.getDocument(UiUtils.SELECTED_POINTS).getArray(UiUtils.SELECTED_POINTS)) {
                    pointsIDs.add(o.toString());
                }
                i.putExtra(UiUtils.SELECTED_POINTS, pointsIDs);
            }
            i.putExtra(UiUtils.USER, user);

        } else {
            Log.d(TAG, "user is null");

        }

        getApplicationContext().sendBroadcast(i);
        Log.d(TAG, "exit checkDB()");
    }

    private void updateUser() {
        Log.d(TAG, "enter updateUser()");
        // TODO: Replace with injection

    //    Database db = DBConnection.getDatabase("mydb", getApplicationContext());
        Intent i = new Intent();
        i.setAction(UiUtils.USER_UPDATED);
//        if (db.getDocument("user")!=null) {
//            if (db.getDocument("user").contains("id")) {
//                //TODO add user and list of selected points
//            }
//
//        }
        getApplicationContext().sendBroadcast(i);
        Log.d(TAG, "enter updateUser()");
    }

    private void logIn(String email, String password) {
        Log.d(TAG, "enter logIn(String email, String password)");
        Intent i = new Intent();
        i.setAction(UiUtils.LOG_IN);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getString(UiUtils.EMAIL, "email").equals(email)
                && prefs.getString(UiUtils.PASSWORD, "email").equals(password)) {

            i.putExtra(UiUtils.LOGGED_IN, true);
            SharedPreferences.Editor editor = UiUtils.getEditor(this);
            editor.putBoolean(UiUtils.LOGGED_IN, true);
            User user = new User (email, password);
            user.setRole(prefs.getString(UiUtils.ROLE, "user"));
            user.setFirstName(prefs.getString(UiUtils.FIRST_NAME, "Dear friend"));
            user.setLastName(prefs.getString(UiUtils.LAST_NAME, "No"));
            i.putExtra(UiUtils.USER, user);
        } else {
            i.putExtra(UiUtils.LOGGED_IN, false);
        }

        getApplicationContext().sendBroadcast(i);
        Log.d(TAG, "exit logIn(String email, String password)");
    }

}
