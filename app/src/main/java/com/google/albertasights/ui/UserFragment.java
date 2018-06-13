package com.google.albertasights.ui;

import android.annotation.TargetApi;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.albertasights.R;
import com.google.albertasights.models.User;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnUserUpdateOrLogoutListener} interface
 * to handle interaction events.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String TAG = UserFragment.class.getSimpleName();
  //  private BroadcastReceiver receiver;

    // TODO: Rename and change types of parameters
    private User user;
    private TextView email;
  //  private TextView firstName;
  //  private TextView lastName;
    private TextView selectedPoints;
    private ImageButton button1;
    private ImageButton button2;
    private ImageButton button3;
    private UserViewModel viewModel;
    private AdView mAdView;
    private Boolean reStarted = false;

    private OnUserUpdateOrLogoutListener mListener;

    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "enter onCreate");
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
        final Observer<User> userObserver = new Observer<User>() {
            @Override
            public void onChanged(@Nullable final User updatedUser) {
                // Update the UI.
                if (updatedUser!=null) {
                    updateUI(updatedUser);
                }
            }
        };
        viewModel.getUser().observe(this, userObserver);

        final Observer<String> actionObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String action) {
                Log.d(TAG, "enter onChanged(@Nullable final String action)");
                if (action!=null) {
                    // Update the UI.
                    if (!action.equals(UiUtils.SEE_USER_DATA)) {
                        activateButtons(false);
                    } else {
                        activateButtons(true);
                    }
                }

            }
        };
        viewModel.getCurrentAction().observe(this, actionObserver);
        Log.d(TAG, "exit onCreate");
    }

    @Override
    @TargetApi(22)
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "enter onCreateView");
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        view.setTranslationZ(10.0f);
        view.setElevation(5.0f);
        if (viewModel.getOrienr().getValue().equals(UiUtils.LANDSCAPE)) {
            Log.i(TAG, "recounting the size");
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(viewModel.getWight().getValue()/100*80,
                    viewModel.getHight().getValue()/100*60);
            lp.addRule(Gravity.LEFT);
            view.setLayoutParams(lp);

        } else {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    viewModel.getHight().getValue()/100*70);
            view.setLayoutParams(lp);
        }

        MobileAds.initialize(getActivity(), "ca-app-pub-3940256099942544~3347511713");
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        email = (TextView) view.findViewById(R.id.email_data);
        TextView password = (TextView) view.findViewById(R.id.password);
        TextView logoutTxt = (TextView) view.findViewById(R.id.log_out_txt);
        if (viewModel.getDevice().getValue().equals(UiUtils.TABLET)) {
            email.setTextSize(getActivity().getResources().getDimension(R.dimen.big_textsize));
            password.setTextSize(getActivity().getResources().getDimension(R.dimen.big_textsize));
            logoutTxt.setTextSize(getActivity().getResources().getDimension(R.dimen.big_textsize));
            selectedPoints.setTextSize(getActivity().getResources().getDimension(R.dimen.big_textsize));

            if (viewModel.getOrienr().getValue().equals(UiUtils.LANDSCAPE)) {
                view.setPadding(viewModel.getHight().getValue()/30, viewModel.getHight().getValue()/30
                        , viewModel.getHight().getValue()/30, viewModel.getHight().getValue()/30);
            } else {
                view.setPadding(viewModel.getWight().getValue()/30, viewModel.getWight().getValue()/30
                        , viewModel.getWight().getValue()/30, viewModel.getWight().getValue()/30);
            }
        } else {
            if (viewModel.getOrienr().getValue().equals(UiUtils.PORTRAIT)) {
                email.getLayoutParams().width = viewModel.getWight().getValue()/100*60;
            } else {
                email.getLayoutParams().width = viewModel.getWight().getValue()/100*70;
            }
        }

        button1 = (ImageButton)view.findViewById(R.id.updt_button);
        button1.setImageResource(R.drawable.edit);
        button1.getBackground().setAlpha(0);

        button2 = (ImageButton)view.findViewById(R.id.log_out);
        button2.setImageResource(R.drawable.logout);
        button2.getBackground().setAlpha(0);
        if (viewModel.getUser().getValue()!=null) {
            updateUI(viewModel.getUser().getValue());
        }

        button3 = (ImageButton) view.findViewById(R.id.updt_passw_button);
        button3.setImageResource(R.drawable.edit);
        button3.getBackground().setAlpha(0);

        View.OnClickListener lstn = new View.OnClickListener() {
            public void onClick(View view) {
                onUpdateUserButtonPressed(view.getTag().toString());
            }
        };
        button1.setOnClickListener(lstn);
        button1.setTag(UiUtils.UPDATE_USER);
        button2.setOnClickListener(lstn);
        button2.setTag(UiUtils.LOG_OUT);
        button3.setTag(UiUtils.UPDATE_PASSWORD);
        button3.setOnClickListener(lstn);

      //  viewModel.updateAction("SEE_DATA");
        Log.d(TAG, "exit onCreateView");
        return view;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "enter onResume()");
        super.onResume();
        if (reStarted=true) {
            activateButtons(true);
        }
        Log.d(TAG, "exit onResume()");
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onUpdateUserButtonPressed(String tag) {
        Log.d(TAG, "enter onUpdateUserButtonPressed");
        if (mListener != null) {
            mListener.onUserUpdateListener(tag);
        }
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "enter onAttach(Context context)");
        super.onAttach(context);
        if (context instanceof OnUserUpdateOrLogoutListener) {
            mListener = (OnUserUpdateOrLogoutListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPointDataExtendedListener");
        }
        Log.d(TAG, "exit onAttach(Context context)");
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "enter onDetach(Context context)");
        super.onDetach();
//        getActivity().unregisterReceiver(this.receiver);
        mListener = null;
        Log.d(TAG, "enter onDetach(Context context)");
    }

    public interface OnUserUpdateOrLogoutListener {
        // TODO: Update argument type and name
        void onUserUpdateListener(String action);
    }

    private void updateUI (User user) {
        Log.d(TAG, "enter updateUI (User user)");

        email.setText("Email: " +user.getEmail());
    //    firstName.setText("First name: " + user.getFirstName());
    //    lastName.setText("Last name: " + user.getLastName());
        Log.d(TAG, "exit updateUI (User user)");

    }

    private void activateButtons (Boolean activate) {
        Log.d(TAG, "enter activateButtons (Boolean activate): "+activate);
//        button1.setClickable(activate);
//        button2.setClickable(activate);
//        button3.setClickable(activate);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        reStarted = true;
    }


}
