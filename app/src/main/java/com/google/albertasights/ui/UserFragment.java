package com.google.albertasights.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.albertasights.R;
import com.google.albertasights.models.User;

import java.io.Serializable;

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
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = UserFragment.class.getSimpleName();
    private BroadcastReceiver receiver;

    // TODO: Rename and change types of parameters
    private User user;
    private TextView email;
    private TextView firstName;
    private TextView lastName;
    private TextView role;

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
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User)getArguments().getSerializable(UiUtils.USER);
//            for (String s : getArguments().keySet()) {
//                Log.d(TAG, "in args: "+s);
//            };
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "enter onCreateView");
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        receiver = new BroadcastReceiver () {
            @Override
            public void onReceive(Context context, Intent intent) {

                Log.d(TAG, "enter onReceive(Context context, Intent intent)");
                if (intent.getAction().equals(UiUtils.USER_CREATED)) {
                    //TODO the broadcast may receive the User data
                    if (intent.getSerializableExtra(UiUtils.USER)!=null) {
                        User user = (User) intent.getSerializableExtra(UiUtils.USER);
                        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
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
                        UiUtils.showToast(getActivity().getApplicationContext(), "user is null");
                    }
                    Log.d(TAG, "exit onReceive(Context context, Intent intent)");
                }
            }

        };
        registerReceiver();
        Log.d(TAG, "exit onCreateView");
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().unregisterReceiver(this.receiver);
        mListener = null;
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
    public interface OnUserUpdateListener {
        // TODO: Update argument type and name
        void onUserUpdateListener();
    }

    private void registerReceiver() {
        Log.d(TAG, "enter registerReceiver()");
        // Create an intent filter for DATA_RECEIVED.
        IntentFilter intentFilter =
                new IntentFilter();
        intentFilter.addAction(UiUtils.USER_CREATED);

        // Register the receiver and the intent filter.
        getActivity().registerReceiver(receiver,
                intentFilter);
        Log.d(TAG, "exit registerReceiver()");
    }

}
