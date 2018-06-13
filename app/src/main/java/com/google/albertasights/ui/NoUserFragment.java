package com.google.albertasights.ui;

import android.annotation.TargetApi;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.albertasights.DBIntentService;
import com.google.albertasights.R;
import com.google.albertasights.models.User;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NoUserFragment.OnButtonClickedListener} interface
 * to handle interaction events.
 * Use the {@link NoUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoUserFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private TextView text;
    private ImageButton imageButton;
    private Button emailPassw;
    private Button logInButton;
    private Button recreateUser;
    private Button noThanks;
  //  private TextView recreateUserTxt;
    private MapViewModel viewModel;
    private UserViewModel viewModel1;
    private Integer screenH;
    private Integer screenW;
    private String orientation;
    private String device;
    private String action;
    private RelativeLayout scrl;
    private RelativeLayout lowWrapper;
    private EditText email;
   // private EditText password;
    private Boolean recreateAccount = false;
    private Boolean logFormExpanded = false;
    private final static String RECREATE_ACCOUNT = "RECREATE_ACCOUNT";

    private OnButtonClickedListener mListener;
    private static final String TAG = NoUserFragment.class.getSimpleName();

    public NoUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NoUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NoUserFragment newInstance(String param1, String param2) {
        NoUserFragment fragment = new NoUserFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "enter onCreate(Bundle savedInstanceState)");
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null) {
            recreateAccount = savedInstanceState.getBoolean(RECREATE_ACCOUNT);
          //  action = getArguments().getString("ACTION");
        }
        Log.d(TAG, "exit onCreate(Bundle savedInstanceState)");

    }

    @Override
    @TargetApi(22)
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "enter onCreateView");
        View view = inflater.inflate(R.layout.fragment_no_user, container, false);
        if (getActivity().getClass().getCanonicalName().contains("User")) {
            viewModel1 = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
            screenW = viewModel1.getWight().getValue();
            screenH = viewModel1.getHight().getValue();
            device = viewModel1.getDevice().getValue();
            orientation = viewModel1.getOrienr().getValue();
        } else {
            viewModel = ViewModelProviders.of(getActivity()).get(MapViewModel.class);
            screenW = viewModel.getWight().getValue();
            screenH = viewModel.getHight().getValue();
            device = viewModel.getDevice().getValue();
            orientation = viewModel.getOrienr().getValue();
        }
        scrl = (RelativeLayout) view.findViewById(R.id.log_scroll);

        //TODO adding specific components:

        email = (EditText) view.findViewById(R.id.email);
   //     password = (EditText) view.findViewById(R.id.password);
        text = (TextView) view.findViewById(R.id.txt_user);
        noThanks = (Button) view.findViewById(R.id.no_submit);
        noThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel1.updateEmailFormRequested(false);
            }
        });
        imageButton = (ImageButton)view.findViewById(R.id.see_more);
        imageButton.setImageResource(R.drawable.more_small);
        imageButton.getBackground().setAlpha(0);
        //TODO add listener
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "enter onClick(View v)");
                if (logFormExpanded==true){
                    Log.d(TAG, "remove extra element");
                    imageButton.setImageResource(R.drawable.more_small);
                    try {
                        scrl.removeView(lowWrapper);
                    } catch (Exception e) {

                    }
                } else {
                    Log.d(TAG, "add extra element");
                    try {
                        imageButton.setImageResource(R.drawable.show_more_up);
                        scrl.addView(lowWrapper);
                        emailPassw.setTag(UiUtils.RESET_PASSWORD);
                        emailPassw.setText("Email me new password");
                        recreateUser.setTag(UiUtils.RECREATE_USER);
                        recreateUser.setText("Reset my data");
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
                logFormExpanded=!logFormExpanded;

                Log.i(TAG, "exit onClick(View v)");
            }
        });

        emailPassw = (Button)view.findViewById(R.id.emailPassw);
        logInButton = (Button)view.findViewById(R.id.log_button);
        recreateUser = (Button) view.findViewById(R.id.recreate);
        recreateUser.setTag(UiUtils.CREATE_USER);
        lowWrapper = (RelativeLayout)view.findViewById(R.id.low_wrapper);
        //recreateUserTxt = (TextView)view.findViewById(R.id.text_recreate_user);

        if (device.equals(UiUtils.TABLET)) {
            //big screen
            email.setTextSize(getActivity().getResources().getDimension(R.dimen.big_textsize));
         //   password.setTextSize(getActivity().getResources().getDimension(R.dimen.big_textsize));

            //TODO modify buttons size
            if (orientation.equals(UiUtils.LANDSCAPE)) {
                emailPassw.getLayoutParams().height = screenH/14;
                emailPassw.getLayoutParams().width = screenW/5;
                logInButton.getLayoutParams().height = screenH/14;
                logInButton.getLayoutParams().width = screenW/5;
                recreateUser.getLayoutParams().height = screenH/14;
                recreateUser.getLayoutParams().width = screenW/5;
                noThanks.getLayoutParams().height = screenH/14;
                noThanks.getLayoutParams().width = screenW/5;
            } else {

                emailPassw.getLayoutParams().height = screenH/20;
                emailPassw.getLayoutParams().width = screenW/3-20;
                logInButton.getLayoutParams().height = screenH/20;
                logInButton.getLayoutParams().width = screenW/3-20;
                recreateUser.getLayoutParams().height = screenH/20;
                recreateUser.getLayoutParams().width = screenW/3-20;
                noThanks.getLayoutParams().height = screenH/20;
                noThanks.getLayoutParams().width = screenW/3-20;

            }

            view.getLayoutParams().height = screenH/2+50;
            if (orientation.equals(UiUtils.LANDSCAPE)) {
                view.getLayoutParams().width=screenW/2;

            } else {
                view.getLayoutParams().width=screenW/2+100;
                view.getLayoutParams().height = screenH/3+50;
            }
        } else {
            //small screen
            if (orientation.equals(UiUtils.LANDSCAPE)) {
                view.getLayoutParams().width=screenW/100*70;
                view.getLayoutParams().height = screenH/2;
            } else {
                view.getLayoutParams().width=screenW-50;
                view.getLayoutParams().height = screenH/3;
            }
        }
        view.setTranslationZ(100.0f);
        view.setElevation(135.0f);

        View.OnClickListener lstn = new View.OnClickListener() {
            public void onClick(View view) {
                //   Log.d(TAG, "enter showFilters(View view)");
                if (recreateAccount==true) {
                    recreateAccount=false;
                }
                String eMail = "";
                String passWord = "";

                if (view.getTag().toString().equals(UiUtils.RECREATE_USER)) {
                    //no data submittion required, only reset UI and show the form to create user
                    try {
                        viewModel1.updateAction(UiUtils.CREATE_USER);
                    } catch (Exception e) {
                        recreateAccount = true;
                    }
                    updateUI();
                    return;
                }

                if (view.getTag().toString().equals(UiUtils.RESET_PASSWORD)){
                    //only email is requiredviewModel1.updateAction(UiUtils.CREATE_USER);
                    try {
                        eMail = viewModel1.getUser().getValue().getEmail();
                    } catch (Exception e) {
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        if (prefs.contains(UiUtils.EMAIL)) {
                            Log.i(TAG, "user found");
                            eMail = prefs.getString(UiUtils.EMAIL, "email");
                        }

                    }
                    passWord = "blabla";
                } else {
                    try {
                        eMail= email.getText().toString();
                      //  passWord= password.getText().toString();
                    } catch (Exception e) {
                        //values may be null
                    }
                }

                if (eMail.length()<2) {//||passWord.length()<2

                  UiUtils.showToast(getActivity(),"Please enter the data");
                } else {
                      Intent intent = new Intent(getActivity().getApplicationContext(), DBIntentService.class);
                      intent.putExtra(UiUtils.EMAIL, eMail);
                      intent.putExtra(UiUtils.PASSWORD, passWord);
                      intent.setAction(view.getTag().toString());

                      if (view.getTag().toString().equals(UiUtils.LOG_IN)) {
                          Log.d(NoUserFragment.class.getCanonicalName(), "Log in event");

                      } else if (view.getTag().toString().equals(UiUtils.CREATE_USER)){

                          Log.d(NoUserFragment.class.getCanonicalName(), "Create event");
                      } else if (view.getTag().toString().equals(UiUtils.UPDATE_USER)) {
                          Log.d(NoUserFragment.class.getCanonicalName(), "update email event");

                      } else if (view.getTag().toString().equals(UiUtils.UPDATE_PASSWORD)) {
                          Log.d(NoUserFragment.class.getCanonicalName(), "update password event");
                      } else if (view.getTag().toString().equals(UiUtils.RESET_PASSWORD)) {
                          try {
                              viewModel1.updateAction(UiUtils.LOG_IN);
                          } catch (Exception e) {
                              Log.i(TAG, "password reset within Maps Activity");
                          }
                          updateUI();
                          Log.d(NoUserFragment.class.getCanonicalName(), "reset password event");
                      }
                      getActivity().startService(intent);
                      mListener.onLogInOrRegisterButtonClickedListener((String)view.getTag());
                      if (!view.getTag().toString().equals(UiUtils.RESET_PASSWORD)) {
                          action=null;
                      }
                  }
                //    Log.d(TAG, "exit showFilters(View view)");
            }
        };
        emailPassw.setOnClickListener(lstn);
        logInButton.setOnClickListener(lstn);
        recreateUser.setOnClickListener(lstn);

        updateUI();
        Log.d(TAG, "exit onCreateView");
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(RECREATE_ACCOUNT, recreateAccount);
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "enter onAttach");
        super.onAttach(context);

        if (context instanceof OnButtonClickedListener) {
            mListener = (OnButtonClickedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onLogInOrRegisterButtonClickedListener");
        }
        Log.d(TAG, "exit onAttach");
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "enter onDetach");
        super.onDetach();
        mListener = null;
        try {
            if (viewModel1.getCurrentAction().getValue().equals(UiUtils.UPDATE_PASSWORD)||
                    viewModel1.getCurrentAction().getValue().equals(UiUtils.UPDATE_USER)) {
                viewModel1.updateAction(UiUtils.SEE_USER_DATA);
            }
        } catch(Exception e) {

        }
        Log.d(TAG, "exit onDetach");
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnButtonClickedListener {
        // TODO: the action string should be formatted: actionName&&email&&passord
        void onLogInOrRegisterButtonClickedListener(String action);
    }

    private void updateUI () {
        try {
            scrl.removeView(lowWrapper);
        } catch (Exception e) {

        }
        if (getActivity().getClass().getCanonicalName().contains("Map")) {
            //user is being set or logged in
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            //TODO the fragment is shown on the Map Activity, user exists and need to log in
            if (recreateAccount==true) {
                text.setText("Please enter the data to set up your account:");
                logInButton.setTag(UiUtils.CREATE_USER);
                logInButton.setText("Register");
                scrl.removeView(imageButton);
            } else {
                if (prefs.contains(UiUtils.EMAIL)) {
                    Log.i(TAG, "user found");
                    text.setText("Please enter your credentials:");
                    logInButton.setTag(UiUtils.LOG_IN);
                    logInButton.setText("Log in");

                } else {
                    //TODO the fragment is shown on the Map Activity, user not exists
                    text.setText("Please enter the data to set up your account:");
                    logInButton.setTag(UiUtils.CREATE_USER);
                    logInButton.setText("Register");
                    scrl.removeView(imageButton);
                }
            }

        } else {
            action = viewModel1.getCurrentAction().getValue();
            //TODO fragment is shown on the User Activity
            Log.i(TAG, "instance of user activity, "+action);

            if (action.equals(UiUtils.UPDATE_USER)) {
                //TODO user email supposed to be updated
                Log.i(TAG, "update email");
              //  text.setText("Please enter your email:");
              //  email.setHint("Email:");
                logInButton.setTag(UiUtils.UPDATE_USER);
                logInButton.setText("Submit data");
                scrl.removeView(imageButton);
              //  scrl.removeView(password);
                scrl.removeView(emailPassw);

            } else if (action.equals(UiUtils.UPDATE_PASSWORD)){
                //TODO user password supposed to be updated
                Log.i(TAG, "update password");
                text.setText("To change your password, please enter the following data:");
                email.setHint("New password:");
              //  password.setHint("Old password:");
                logInButton.setText("Submit data");
                logInButton.setTag(UiUtils.UPDATE_PASSWORD);
                scrl.removeView(imageButton);

            } else if (action.equals(UiUtils.LOG_IN)) {
                Log.i(TAG, "log in");
                text.setText("Please enter your credentials:");
                email.setHint("Email:");
              //  password.setHint("Password:");
                logInButton.setText("Log in");
                logInButton.setTag(UiUtils.LOG_IN);

            } else if (action.equals(UiUtils.CREATE_USER)) {
                Log.i(TAG, "create user");
                text.setText("Please enter the data to set up your account:");
                logInButton.setTag(UiUtils.CREATE_USER);
                logInButton.setText("Register");
                scrl.removeView(imageButton);

            }
        }

    }
}
