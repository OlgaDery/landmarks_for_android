package com.google.albertasights.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.albertasights.DBIntentService;
import com.google.albertasights.R;
import com.google.albertasights.RestIntentServer;
import com.google.albertasights.models.Place;
import com.google.albertasights.models.User;

import java.util.ArrayList;

public class UserActivity extends AppCompatActivity {

    private BroadcastReceiver receiver;
    private static final String TAG = UserActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //TODO

        Fragment firstFragment = new Fragment();
        Fragment second = new Fragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back
        transaction.replace(firstFragment.getId(), second);
        transaction.addToBackStack(null);
// Commit the transaction
        transaction.commit();


        //TODO
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DBIntentService.class);
                intent.setAction(UiUtils.CREATE_USER);
                intent.putExtra(UiUtils.EMAIL, "androgeny80@gmail.com");
                intent.putExtra(UiUtils.PASSWORD, "bla");
                startService(intent);
            }
        });

        receiver = new BroadcastReceiver () {
            @Override
            public void onReceive(Context context, Intent intent) {

                Log.d(TAG, "enter onReceive(Context context, Intent intent)");
                if (intent.getAction().equals(UiUtils.USER_CREATED)) {
                    //TODO the broadcast may receive the User data
                    if (intent.getSerializableExtra(UiUtils.USER)!=null) {
                        User user = (User) intent.getSerializableExtra(UiUtils.USER);
                        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(UiUtils.EMAIL, user.getEmail());
                      //  editor.putString(U, user.getId());
                        editor.putString(UiUtils.FIRST_NAME, user.getFirstName());
                        editor.putString(UiUtils.LAST_NAME, user.getLastName());
                        editor.putString(UiUtils.ROLE, user.getRole());
                        editor.commit();
                        Log.d(TAG, "user added");
                        //TODO update UI (replace one fragment with another)

                    } else {
                        UiUtils.showToast(getApplicationContext(), "user is null");
                    }
                    Log.d(TAG, "exit onReceive(Context context, Intent intent)");
                }

                }

        };

    }

    //TODO register receiver
    @Override
    protected void onResume() {
        Log.d(TAG, "enter onResume()");
        super.onResume();
        registerReceiver();
        Log.d(TAG, "exit onResume()");

    }

    private void registerReceiver() {
//        Log.d(TAG, "enter registerReceiver() for DATA_RECEIVED");
        // Create an intent filter for DATA_RECEIVED.
        IntentFilter intentFilter =
                new IntentFilter();
        intentFilter.addAction(UiUtils.USER_CREATED);

        // Register the receiver and the intent filter.
        registerReceiver(receiver,
                intentFilter);
        //       Log.d(TAG, "exit registerReceiver() for DATA_RECEIVED");
    }

    //TODO unregister receiver
    @Override
    protected void onPause () {
        Log.d(TAG, "enter onPause ()");
        super.onPause();
        this.unregisterReceiver(this.receiver);
        Log.d(TAG, "exit onPause()");
    }

}
