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

public class UserActivity extends MenuActivity implements NoUserFragment.OnButtonClickedListener,
        UserFragment.OnUserUpdateListener, EnterUserFragment.OnSubmitUserListener{


    private Fragment logInFragment;
    private Fragment userDataFragment;
    private Fragment modifYUserDataFragment;
    private Fragment statusFragment;
    private User user;
    private BroadcastReceiver receiver;
    private UserViewModel viewModel;
    private static final String TAG = UserActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "enter onCreate(Bundle savedInstanceState)");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_container);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        logInFragment = new NoUserFragment();
        userDataFragment = new UserFragment();
        modifYUserDataFragment = new EnterUserFragment();
        statusFragment = new StatusBarFragment();
        viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(UiUtils.LOGGED_IN, true)==false) {
            User u = viewModel.getUser().getValue();
            u.setLoggedIn(false);
            viewModel.updateUser(u);
            transaction.add(R.id.user_container, logInFragment).commit();
            return;
        }

        if (viewModel.getUser().getValue()!=null) {

            if (viewModel.getCurrentAction().getValue()!=null) {
                //current action has already ben set, selecting the fragment
                switch (viewModel.getCurrentAction().getValue()) {
                    case UiUtils.LOG_IN:
                        transaction.add(R.id.user_container, userDataFragment).commit();
                        break;

                    case UiUtils.UPDATE_USER:
                        transaction.add(R.id.user_container, modifYUserDataFragment).commit();
                       break;

                    case UiUtils.LOG_OUT:
                        //TODO redirect to map
                        break;

                    default:
                        transaction.add(R.id.user_container, userDataFragment).commit();
                        break;
                }
            } else {
                //TODO action has not been set but user exists, so we likely have to show user`s data
                transaction.add(R.id.user_container, userDataFragment).commit();
            }
        } else {
            //activity created the first time, no data exist in the ModelView
            //TODO
            if (getIntent().getExtras().getSerializable(UiUtils.USER)!= null) {
                //If user exists in SharedPreferences
                user = (User) getIntent().getExtras().getSerializable(UiUtils.USER);
                viewModel.updateUser(user);
                if (user.getLoggedIn()==true) {
                    //User logged in, adding UserFragment
//                    Log.d(TAG, user.getEmail());
//                    Log.d(TAG, user.getFirstName());
//                    Log.d(TAG, user.getLastName());
//                    Log.d(TAG, user.getRole());
                    Log.d(TAG, "user logged in, adding the User Fragment");
                    //   userDataFragment = new UserFragment();
                    //   userDataFragment.setArguments(getIntent().getExtras());

                    transaction.add(R.id.user_container, userDataFragment).commit();
                } else {
                    Log.d(TAG, "user exists but not logged in");
                 //   logInFragment = new NoUserFragment();
                    //    logInFragment.setArguments(getIntent().getExtras());
                    transaction.add(R.id.user_container, logInFragment).commit();
                }
            } else if (getIntent().getAction().equals(UiUtils.CREATE_USER)) {
                if (getIntent().getExtras().getBoolean(UiUtils.BACK_TO_MAP)==true) {
                    viewModel.updateDestination(UiUtils.BACK_TO_MAP);

                } else if (getIntent().getExtras().getBoolean(UiUtils.BACK_TO_POINT)==true) {
                    viewModel.updateDestination(UiUtils.BACK_TO_POINT);
                }
                transaction.add(R.id.user_container, modifYUserDataFragment).commit();

            } else {
                    //User does not exist and has to be set
                    //  logInFragment = new NoUserFragment();
                    Log.i("TAG", "user does not exists");
                    //  logInFragment.setArguments(getIntent().getExtras());
                    // Add the fragment to the 'fragment_container' FrameLayout
                    transaction.add(R.id.user_container, logInFragment).commit();
                }
        }

//        if (savedInstanceState != null) {
//            return;
//        }

        receiver = new BroadcastReceiver () {
            @Override
            public void onReceive(Context context, Intent intent) {

                Log.d(TAG, "enter onReceive(Context context, Intent intent)");
                if (intent.getAction().equals(UiUtils.USER_CREATED)||intent.getAction().equals(UiUtils.LOG_IN)) {
                    Log.i(TAG, "destination: "+ viewModel.getDestination().getValue());
                    if (viewModel.getDestination().getValue()!=null && viewModel.getDestination().getValue().length()>2) {

                        if (viewModel.getDestination().getValue().equals(UiUtils.BACK_TO_MAP)) {
                            Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                            startActivity(i);

                        } else if (viewModel.getDestination().getValue().equals(UiUtils.BACK_TO_POINT)) {

                        }
                        viewModel.updateDestination(new String());
                    } else {
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        //TODO the broadcast may receive the User data
                        if (intent.getSerializableExtra(UiUtils.USER)!=null) {
                            User user = (User) intent.getSerializableExtra(UiUtils.USER);
                            viewModel.updateUser(user);
                            transaction.replace(R.id.user_container, userDataFragment);

                            Log.d(TAG, "user added");
                            //TODO update UI (replace one fragment with another)

                        } else {
                            //no User data received from the service, the reason may be either an error or the lack of user data
                            if (intent.getBooleanExtra((UiUtils.LOGGED_IN), true)==false) {
                                UiUtils.showToast(getApplicationContext(), "We have not find these credentials");

                            } else {
                                UiUtils.showToast(getApplicationContext(), "Error with data submittion");
                            }
                            transaction.replace(R.id.user_container, logInFragment);
                        }
                        transaction.commit();
                    }

                    Log.d(TAG, "exit onReceive(Context context, Intent intent)");
                }
            }

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
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (action.equals(UiUtils.LOG_IN)) {
            //show the fragment with user data
            transaction.replace(R.id.user_container, statusFragment);
        } else if (action.equals(UiUtils.CREATE_USER)) {
            //TODO show the 3d fragment with forms
         //   modifYUserDataFragment = new EnterUserFragment();
            transaction.replace(R.id.user_container, modifYUserDataFragment);
        }
        transaction.addToBackStack(null);
// Commit the transaction
        transaction.commit();
        Log.i(TAG, "exit onLogInOrRegisterButtonClickedListener()");

    }

    @Override
    public void onUserUpdateListener() {
        //here is the listener from UserFragment
        Log.i(TAG, "enter onUserUpdateListener()");
        viewModel.updateAction(UiUtils.UPDATE_USER);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.user_container, modifYUserDataFragment);
        transaction.addToBackStack(null);
// Commit the transaction
        transaction.commit();
        Log.i(TAG, "exit onUserUpdateListener()");

    }

    @Override
    public void onSubmitUser() {
        //Listener in EnterUserFragment, adding UserFragment
        Log.i(TAG, "enter onSubmitUser()");
        viewModel.updateAction(UiUtils.USER_CREATED);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.user_container, statusFragment);
        transaction.addToBackStack(null);
// Commit the transaction
        transaction.commit();
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
