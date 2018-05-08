package com.google.albertasights.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.google.albertasights.R;
import com.google.albertasights.models.User;

public class UserActivity extends MenuActivity implements NoUserFragment.OnButtonClickedListener, UserFragment.OnUserUpdateOrLogoutListener, EnterUserFragment.OnSubmitUserListener{


    private Fragment logInFragment;
    private Fragment userDataFragment;
    private Fragment modifYUserDataFragment;
    private Fragment progressFragment;
    private User user;
    private BroadcastReceiver receiver;
    private UserViewModel viewModel;
    private static final String TAG = UserActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "enter onCreate(Bundle savedInstanceState)");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_container);
     //   FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        logInFragment = new NoUserFragment();
        userDataFragment = new UserFragment();
        modifYUserDataFragment = new EnterUserFragment();
        progressFragment = new StatusBarFragment();
        viewModel = ViewModelProviders.of(this).get(UserViewModel.class);

            //TODO
            if (getIntent().getExtras()!= null) {
                //If user exists in SharedPreferences
                if ((User) getIntent().getExtras().getSerializable(UiUtils.USER)!=null)  {
                    user = (User) getIntent().getExtras().getSerializable(UiUtils.USER);
                    viewModel.updateUser(user);
                    if (user.getLoggedIn()==true) {
                        //User logged in, adding UserFragment
//                    Log.d(TAG, user.getEmail());
//                    Log.d(TAG, user.getFirstName());
//                    Log.d(TAG, user.getLastName());
//                    Log.d(TAG, user.getRole());
                        Log.d(TAG, "user logged in, adding the User Fragment");
 //                       transaction.add(R.id.user_container, userDataFragment).commit();
                        UiUtils.manageFragments(userDataFragment, getSupportFragmentManager(), false,
                                R.id.user_container, "ADD");
                    } else {
                        Log.d(TAG, "user exists but not logged in");

                     //   transaction.add(R.id.user_container, logInFragment).commit();
                        UiUtils.manageFragments(logInFragment, getSupportFragmentManager(), false,
                                R.id.user_container, "ADD");
                    }
                }

            } else {
                    //User does not exist and has to be set
                    Log.i("TAG", "user does not exists");
                 //   transaction.add(R.id.user_container, logInFragment).commit();
                UiUtils.manageFragments(logInFragment, getSupportFragmentManager(), false,
                        R.id.user_container, "ADD");
                }
//        }

//        if (savedInstanceState != null) {
//            return;
//        }

        receiver = new BroadcastReceiver () {
            @Override
            public void onReceive(Context context, Intent intent) {

                Log.d(TAG, "enter onReceive(Context context, Intent intent)");
                if (intent.getAction().equals(UiUtils.USER_CREATED)||intent.getAction().equals(UiUtils.LOG_IN)) {
                     //   FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        // the broadcast may receive the User data
                        if (intent.getSerializableExtra(UiUtils.USER)!=null) {
                            User user = (User) intent.getSerializableExtra(UiUtils.USER);
                            viewModel.updateUser(user);
                       //     transaction.replace(R.id.user_container, userDataFragment);
                            UiUtils.manageFragments(userDataFragment, getSupportFragmentManager(), false,
                                    R.id.user_container, "REPLACE");

                            Log.d(TAG, "user added");
                            //TODO update UI (replace one fragment with another)

                        } else {
                            //no User data received from the service, the reason may be either an error or the lack of user data
                            if (intent.getBooleanExtra((UiUtils.LOGGED_IN), true)==false) {
                                UiUtils.showToast(getApplicationContext(), "We have not find these credentials");

                            } else {
                                UiUtils.showToast(getApplicationContext(), "Error with data submittion");
                            }
                        //    transaction.replace(R.id.user_container, logInFragment);
                            UiUtils.manageFragments(logInFragment, getSupportFragmentManager(), false,
                                    R.id.user_container, "REPLACE");
                        }
                       // transaction.commit();
                    }

                    Log.d(TAG, "exit onReceive(Context context, Intent intent)");
                }
          //  }

        };

        Log.d(TAG, "exit onCreate(Bundle savedInstanceState)");

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    //TODO register receiver
    @Override
    protected void onResume() {
        Log.d(TAG, "enter onResume()");
        super.onResume();
        registerReceiver();
        Log.d(TAG, "exit onResume()");

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onLogInOrRegisterButtonClickedListener(String action) {
        //Listener in NoUserFragment
        Log.i(TAG, "enter onLogInOrRegisterButtonClickedListener()");
        viewModel.updateAction(action);
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.user_container, progressFragment);
//        transaction.commit();
        UiUtils.manageFragments(progressFragment, getSupportFragmentManager(), false,
                R.id.user_container, "REPLACE");
        Log.i(TAG, "exit onLogInOrRegisterButtonClickedListener()");

    }

    @Override
    public void onUserUpdateListener(String action) {
        //here is the listener from UserFragment
        Log.i(TAG, "enter onUserUpdateListener(String action)");
        if (action.equals(UiUtils.LOG_OUT)) {
            SharedPreferences.Editor editor = UiUtils.getEditor(this);
            editor.putBoolean(UiUtils.LOGGED_IN, false);
            editor.commit();
            viewModel.updateAction(null);
            viewModel.updateUser(null);
            Intent i = new Intent(this, MapsActivity.class);
            startActivity(i);

        } else {
            viewModel.updateAction(UiUtils.UPDATE_USER);
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            transaction.replace(R.id.user_container, modifYUserDataFragment);
//            transaction.addToBackStack(null);
//            transaction.commit();
            UiUtils.manageFragments(modifYUserDataFragment, getSupportFragmentManager(), true,
                    R.id.user_container, "REPLACE");
        }

        Log.i(TAG, "exit onUserUpdateListener()");

    }

    @Override
    public void onSubmitUser() {
        //Listener in EnterUserFragment, adding UserFragment
        Log.i(TAG, "enter onSubmitUser()");
        viewModel.updateAction(UiUtils.USER_CREATED);
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.user_container, progressFragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
        UiUtils.manageFragments(progressFragment, getSupportFragmentManager(), true,
                R.id.user_container, "REPLACE");
        Log.i(TAG, "exit onSubmitUser()");

    }

    private void registerReceiver() {
        Log.d(TAG, "enter registerReceiver()");
        // Create an intent filter for DATA_RECEIVED.
        IntentFilter intentFilter =
                new IntentFilter();
        intentFilter.addAction(UiUtils.USER_CREATED);
      //  intentFilter.addAction(UiUtils.USER_UPDATED);
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
