package com.google.albertasights;


import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;

/**
 * Created by olga on 4/16/18.
 */

public class DBConnection {

    private static final String TAG =
            DBConnection.class.getCanonicalName();

    private com.couchbase.lite.Manager manager = null;

    private Database database = null;


    public DBConnection (String databaseName, Context context) {
        if (database!= null) {
            if (database.getName().equals(databaseName)) {
                //the required DB has been instantiated, no extra actions required
                Log.d(TAG, databaseName+ " already exists");

            } else {
                //another DB has been instantiated, need to replace it
                try {

                    database = manager.getDatabase(databaseName);
                    Log.d(TAG, "database instantiated: " + database.getName());

                } catch (CouchbaseLiteException e) {
                    Log.e(TAG, e.getMessage());
                }
            }

        } else {
            try {
                manager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);
                Log.d(TAG, "DB manager created");
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }

            //TODO database has to have the same name as user email, to improve the later associations

            try {
                database = manager.getDatabase(databaseName);
                //   Log.d(TAG, "database name: " + database.getName());
                Log.d(TAG, "DB just instantiated: "+ database.getName());
            } catch (CouchbaseLiteException e) {
                Log.e(TAG, e.getMessage());
            }
        }

    }

    public Database getDatabase () {
        return database;
    }

}
