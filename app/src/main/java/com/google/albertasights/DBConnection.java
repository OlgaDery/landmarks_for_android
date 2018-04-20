package com.google.albertasights;


import android.content.Context;
import android.util.Log;


import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;

import java.io.IOException;

/**
 * Created by olga on 4/16/18.
 */

public class DBConnection {

    private static final String TAG =
            DBConnection.class.getCanonicalName();

    private static DatabaseConfiguration config;
    private static Database database;


    private DBConnection () {

    }

    public static Database getDatabase (String databaseName, Context context) {

        if (database==null) {
            try {
                config = new DatabaseConfiguration(context);
                database = new Database(databaseName, config);
            } catch (CouchbaseLiteException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        return database;
    }

}
