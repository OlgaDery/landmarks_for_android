package com.google.albertasights.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.albertasights.DBIntentService;
import com.google.albertasights.R;
import com.google.albertasights.models.Place;
import com.google.albertasights.models.User;

public class UserActivity extends MenuActivity implements NoUserFragment.OnButtonClickedListener,
        UserFragment.OnUserUpdateListener{


    private Fragment firstFragment;
    private Fragment second;
    private User user;
    private static final String TAG = UserActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "enter onCreate(Bundle savedInstanceState)");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_container);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        if (savedInstanceState != null) {
            return;
        }
        //TODO
        if (getIntent().getExtras()!= null) {
            user = (User) getIntent().getExtras().getSerializable(UiUtils.USER);
            if (user.getLoggedIn()==true) {
                Log.d(TAG, "user logged in, adding the User Fragment");
                second = new UserFragment();
                second.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.user_container, second).commit();
            } else {
                Log.d(TAG, "user exists but not logged in");
                firstFragment = new NoUserFragment();
                firstFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.user_container, firstFragment).commit();
            }
        } else {
            firstFragment = new NoUserFragment();
            Log.i("TAG", "user does not exists");
            //  firstFragment.setArguments(getIntent().getExtras());
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.user_container, firstFragment).commit();
        }

        Log.d(TAG, "exit onCreate(Bundle savedInstanceState)");

    }

    //TODO register receiver
    @Override
    protected void onResume() {
        Log.d(TAG, "enter onResume()");
        super.onResume();
        Log.d(TAG, "exit onResume()");

    }


    @Override
    public void onButtonClickedListener() {
        Log.i(TAG, "enter onButtonClickedListener()");
        second = new UserFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.user_container, second);
        transaction.addToBackStack(null);
// Commit the transaction
        transaction.commit();
        Log.i(TAG, "exit onButtonClickedListener()");

    }

    @Override
    public void onUserUpdateListener() {

    }
}
