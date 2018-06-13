package com.google.albertasights.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.google.albertasights.R;
import com.google.albertasights.models.User;

import java.util.HashMap;
import java.util.Map;

public class UserActivity extends MenuActivity implements NoUserFragment.OnButtonClickedListener, UserFragment.OnUserUpdateOrLogoutListener, EnterUserFragment.OnSubmitUserListener{

    private Fragment logInFragment;
    private Fragment userDataFragment;
    private Fragment modifYUserDataFragment;
    private Fragment progressFragment;
    private Fragment socialBtns;
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
        viewModel.updateHights(UiUtils.getHightInches(getApplicationContext()));
        viewModel.updateWight(UiUtils.getWidthInches(getApplicationContext()));
        viewModel.updateDeviceType(UiUtils.findScreenSize(getApplicationContext()));
        viewModel.updateOrientation(UiUtils.getOrientation(getApplicationContext()));
        Map<String, String> temp = new HashMap<>();

        //TODO
        if (viewModel.getOrienr().getValue().equals(UiUtils.LANDSCAPE)) {
           temp.put("banner", "ADD");

        } else {

            if (UiUtils.checkIfFragmentAdded("banner", getSupportFragmentManager())==true) {
                temp.put("banner", "REMOVE");
            }
        }

        if (viewModel.getCurrentAction().getValue()==null) {
            Log.i(TAG, "action is null");
        //    temp.put("u1", "ADD");
            temp.put("connect", "ADD");
            temp.put("socialBt", "ADD");
            viewModel.updateAction(UiUtils.SEE_USER_DATA);
//            if (viewModel.getUser().getValue()==null) {
//                if (getIntent().getExtras()!= null) {
//                    //If user exists in SharedPreferences
//                    if ((String) getIntent().getExtras().getSerializable(UiUtils.EMAIL)!=null)  {//(User) getIntent().getExtras().getSerializable(UiUtils.USER)!=null
//                        user = (User) getIntent().getExtras().getSerializable(UiUtils.USER);
//                        viewModel.updateUser(new User((String) getIntent().getExtras().getSerializable(UiUtils.EMAIL), ""));
//                        if (user.getLoggedIn()==false) {
//                            viewModel.updateAction(UiUtils.LOG_IN);
//                            temp.put("login", "ADD");
//                        } else {
//                            viewModel.updateAction(UiUtils.SEE_USER_DATA);
//                        }
//                    }
//
//                } else {
//                    //User does not exist and has to be set
//                    Log.i("TAG", "user does not exists");
//                    viewModel.updateAction(UiUtils.CREATE_USER);
//                    temp.put("login", "ADD");
//                }
//            } else {
//                //TODO user is not null
//                if (viewModel.getUser().getValue().getLoggedIn()==false) {
//                    viewModel.updateAction(UiUtils.LOG_IN);
//                    temp.put("login", "ADD");
//                } else {
//                    viewModel.updateAction(UiUtils.SEE_USER_DATA);
//                }
//            }
        }
            //activity gets recreated


        receiver = new BroadcastReceiver () {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "enter onReceive(Context context, Intent intent)");
                Map<String, String> tempData1 = new HashMap<>();
                if (!intent.getAction().equals(UiUtils.RESET_PASSWORD)) {
                        // the broadcast may receive the User data
                        if (intent.getSerializableExtra(UiUtils.USER)!=null) {
                            User user = (User) intent.getSerializableExtra(UiUtils.USER);
                            viewModel.updateUser(user);
                            viewModel.updateAction(UiUtils.SEE_USER_DATA);
                            try {
                                tempData1.put("progr1", "REMOVE");

                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }

//                            if (UiUtils.checkIfFragmentAdded("login",  getSupportFragmentManager())==true) {
//                                UiUtils.manageFragments(logInFragment, getSupportFragmentManager(), false,
//                                        R.id.user_container, "REMOVE", "login");
//                            }
                            UiUtils.showToast(getApplicationContext(), "Done!");

                            Log.d(TAG, "user added");
                            //TODO update UI (replace one fragment with another)

                        } else {
                            //no User data received from the service, the reason may be either an error or the lack of user data
                            if (intent.getAction().equals(UiUtils.LOG_IN)) { //TODO intent.getAction().equals(UiUtils.USER_CREATED)
                                try {
                                    if (intent.getBooleanExtra((UiUtils.LOGGED_IN), true)==false) {
                                        UiUtils.showToast(getApplicationContext(), "We have not find these credentials");

                                    } else {
                                        UiUtils.showToast(getApplicationContext(), "Error with data submittion");
                                    }
                                    viewModel.updateAction(UiUtils.LOG_IN);
                                } catch (Exception e) {
                                    UiUtils.showToast(getApplicationContext(), "Error with data submittion");
                                    viewModel.updateAction(UiUtils.LOG_IN);
                                }

                                try {
                                    tempData1.put("progr1", "REMOVE");
                                } catch (Exception e) {
                                    Log.e(TAG, e.getMessage());
                                }

                                try {
                                    tempData1.put("login", "ADD");
                                } catch (Exception e) {
                                    Log.e(TAG, e.getMessage());
                                }
                            } else if (intent.getAction().equals(UiUtils.UPDATE_PASSWORD)||
                                    intent.getAction().equals(UiUtils.USER_UPDATED)){
                                tempData1.put("progr1", "REMOVE");
                                UiUtils.showToast(getApplicationContext(), "We have not find these credentials, data has" +
                                        "not been changed");
                            }

                        }
                    } else {
                    //TODO test!!! user`s email or password tried to get modified

                    tempData1.put("progr1", "REMOVE");
                    if (viewModel.getUser().getValue().getLoggedIn()==false) {
                        try {
                            tempData1.put("login", "ADD");
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                    if (intent.getBooleanExtra(UiUtils.RESET_PASSWORD, true)==true) {
                        UiUtils.showToast(getApplicationContext(), "Email has been sent to you");
                    } else {
                        UiUtils.showToast(getApplicationContext(), "Something wrong with sending email, try again later");
                    }
                }

                UiUtils.manageFragments(getSupportFragmentManager(), false,
                        R.id.user_container, tempData1);
                Log.d(TAG, "exit onReceive(Context context, Intent intent)");
                }
          //  }

        };
        UiUtils.manageFragments(getSupportFragmentManager(), false,
                R.id.user_container, temp);

        //TODO this is a temporary functionality fotr the first release only!!!!!!!!
//        final Observer<Boolean> addRequestFormObserver = new Observer<Boolean>() {
//            @Override
//            public void onChanged(@Nullable final Boolean addForm) {
//                Log.d(TAG, "enter onChanged(@Nullable final Boolean addForm)");
//                Map<String, String> temp = new HashMap<>();
//                if (addForm==true) {
//                    viewModel.updateAction(UiUtils.UPDATE_USER);
//                    temp.put("login", "ADD");
//                    // Update the UI.
//                    //TODO add login form
//                } else {
//                    viewModel.updateAction(null);
//                    temp.put("login", "REMOVE");
//                }
//                UiUtils.manageFragments(getSupportFragmentManager(), true,
//                        R.id.user_container, temp);
//            }
//        };
//        viewModel.getEmailFormRequested().observe(this, addRequestFormObserver);
//
//        Log.d(TAG, "exit onCreate(Bundle savedInstanceState)");

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
        Map<String, String> tmp = new HashMap<>();
        tmp.put("login", "REMOVE");
        tmp.put("progr1", "ADD");
        try {
            UiUtils.manageFragments(getSupportFragmentManager(), false,
                    R.id.user_container, tmp);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }


        Log.i(TAG, "exit onLogInOrRegisterButtonClickedListener()");

    }

    @Override
    public void onUserUpdateListener(String action) {
        //here is the listener from UserFragment. Allows user to log out or to show fragment where
        //user data may be updated
        Log.i(TAG, "enter onUserUpdateListener(String action)");
        Log.i(TAG, "action: "+action);
        if (action.equals(UiUtils.LOG_OUT)) {
            SharedPreferences.Editor editor = UiUtils.getEditor(this);
            editor.putBoolean(UiUtils.LOGGED_IN, false);
            editor.commit();
            viewModel.updateAction(null);
            viewModel.updateUser(null);
            Intent i = new Intent(this, MapsActivity.class);
            i.setAction("RESTART");
            startActivity(i);

        } else {//if (action.equals(UiUtils.UPDATE_USER))
            viewModel.updateAction(action);
            try {
                Bundle args = new Bundle();
                args.putString("ACTION", action);
                Map<String, String> tmp = new HashMap<>();
                tmp.put("login", "ADD");
                logInFragment.setArguments(args);
                UiUtils.manageFragments(getSupportFragmentManager(), true,
                        R.id.user_container, tmp);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

        }

        Log.i(TAG, "exit onUserUpdateListener()");

    }

    @Override
    public void onSubmitUser() {
        //Listener in EnterUserFragment, adding UserFragment
        Log.i(TAG, "enter onSubmitUser()");
        viewModel.updateAction(UiUtils.USER_CREATED);
        Map<String, String> tmp = new HashMap<>();
        tmp.put("progr1", "ADD");
        try {
            UiUtils.manageFragments(getSupportFragmentManager(), true,
                    R.id.user_container, tmp );
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

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
        intentFilter.addAction(UiUtils.USER_UPDATED);
        intentFilter.addAction(UiUtils.UPDATE_PASSWORD);
        intentFilter.addAction(UiUtils.RESET_PASSWORD);

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
