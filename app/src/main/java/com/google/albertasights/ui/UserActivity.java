package com.google.albertasights.ui;

import android.arch.lifecycle.ViewModelProviders;
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
    private BroadcastReceiver receiver;
    private UserViewModel viewModel;
    private static final String TAG = UserActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "enter onCreate(Bundle savedInstanceState)");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_container);
        viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        if (savedInstanceState != null) {
            return;
        }

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
                        viewModel.updateUser((User)intent.getSerializableExtra(UiUtils.USER));
                        Log.d(TAG, "user added");
                        //TODO update UI (replace one fragment with another)

                    } else {
                        UiUtils.showToast(getApplicationContext(), "error");
                    }
                    Log.d(TAG, "exit onReceive(Context context, Intent intent)");
                }
            }

        };

        registerReceiver();

        //TODO
        if (getIntent().getExtras()!= null) {
            user = (User) getIntent().getExtras().getSerializable(UiUtils.USER);
            viewModel.updateUser(user);

            if (user.getLoggedIn()==true) {
                Log.d(TAG, user.getEmail());
                Log.d(TAG, user.getFirstName());
                Log.d(TAG, user.getLastName());
                Log.d(TAG, user.getRole());
                Log.d(TAG, "user logged in, adding the User Fragment");
                second = new UserFragment();
             //   second.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.user_container, second).commit();
            } else {
                Log.d(TAG, "user exists but not logged in");
                firstFragment = new NoUserFragment();
            //    firstFragment.setArguments(getIntent().getExtras());
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
    public void onButtonClickedListener(String action) {
        Log.i(TAG, "enter onButtonClickedListener()");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (action.equals(UiUtils.LOG_IN)) {
            //show the fragment with user data
            second = new UserFragment();
            transaction.replace(R.id.user_container, second);
        } else if (action.equals(UiUtils.CREATE_USER)) {
            //TODO show the 3d fragment with forms
        }
        transaction.addToBackStack(null);
// Commit the transaction
        transaction.commit();
        Log.i(TAG, "exit onButtonClickedListener()");

    }

    @Override
    public void onUserUpdateListener() {

    }

    private void registerReceiver() {
        Log.d(TAG, "enter registerReceiver()");
        // Create an intent filter for DATA_RECEIVED.
        IntentFilter intentFilter =
                new IntentFilter();
        intentFilter.addAction(UiUtils.USER_CREATED);
        intentFilter.addAction(UiUtils.LOG_IN);
        intentFilter.addAction(UiUtils.LOG_OUT);

        // Register the receiver and the intent filter.
        registerReceiver(receiver,
                intentFilter);
        Log.d(TAG, "exit registerReceiver()");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "enter onDestroy() ");
        super.onDestroy();
        Log.d(TAG, "exit onDestroy() ");
    }
}
