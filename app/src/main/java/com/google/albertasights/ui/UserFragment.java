package com.google.albertasights.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.albertasights.R;
import com.google.albertasights.models.User;

import java.io.Serializable;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserFragment.OnUserUpdateListener} interface
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
    private UserViewModel viewModel;
    private User user;
    private TextView email;
    private TextView firstName;
    private TextView lastName;
    private TextView role;
    private Button button1;
    private LinearLayout layout;
    private ProgressBar prBar;

    private OnUserUpdateListener mListener;

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
                updateUI(updatedUser);
            }
        };
        Log.d(TAG, "exit onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "enter onCreateView");
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        layout = (LinearLayout) view.findViewById(R.id.user_data);
        email = (TextView) view.findViewById(R.id.email_data);
        lastName = (TextView) view.findViewById(R.id.last_name_data);
        firstName = (TextView) view.findViewById(R.id.first_name_data);
        role = (TextView) view.findViewById(R.id.role_data);
        button1 = (Button)view.findViewById(R.id.updt_button);
        if (viewModel.getUser().getValue()!=null) {
            updateUI(viewModel.getUser().getValue());
        }
        else {
            prBar = new ProgressBar(getActivity());
            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams
                    (RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);

            lp1.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            prBar.setLayoutParams(lp1);
            layout.addView(prBar);
            View.OnClickListener lstn = new View.OnClickListener() {
                public void onClick(View view) {
                    onUpdateUserButtonPressed();
                }
            };
            button1.setOnClickListener(lstn);
        }

        Log.d(TAG, "exit onCreateView");
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onUpdateUserButtonPressed() {
        if (mListener != null) {
            mListener.onUserUpdateListener();
        }
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "enter onAttach(Context context)");
        super.onAttach(context);
        if (context instanceof OnUserUpdateListener) {
            mListener = (OnUserUpdateListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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

    public interface OnUserUpdateListener {
        // TODO: Update argument type and name
        void onUserUpdateListener();
    }

    private void updateUI (User user) {
        Log.d(TAG, "enter updateUI (User user)");
        try{
            if (prBar.getParent()!=null) {
                layout.removeView(prBar);
            }
        } catch (Exception e) {

        }
        email.setText("Email: " +user.getEmail());
        firstName.setText("First name: " + user.getFirstName());
        lastName.setText("Last name: " + user.getLastName());
        role.setText("Current role: " + user.getRole());
        Log.d(TAG, "exit updateUI (User user)");

    }

}
