package com.google.albertasights.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CompoundButtonCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.albertasights.R;

import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SideBarFragment1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SideBarFragment1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SideBarFragment1 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MapViewModel viewModel;
    private LinearLayout ll;

    private OnFragmentInteractionListener mListener;
    private static final String TAG = SideBarFragment1.class.getSimpleName();

    public SideBarFragment1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SideBarFragment1.
     */
    // TODO: Rename and change types and number of parameters
    public static SideBarFragment1 newInstance(String param1, String param2) {
        SideBarFragment1 fragment = new SideBarFragment1();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewModel = ViewModelProviders.of(getActivity()).get(MapViewModel.class);
        viewModel.updateCurrentFragment(this.getClass().getSimpleName());

        View v = inflater.inflate(R.layout.fragment_side_bar_fragment1, container, false);
        v.getLayoutParams().width = 400;
        ll = (LinearLayout)v.findViewById(R.id.test_layout);
        final Observer<LinkedList<String>> filtersObserver = new Observer<LinkedList<String>>() {

            @Override
            public void onChanged(@Nullable final LinkedList<String> filt) {
                Log.i(TAG, "enter onChanged(@Nullable final LinkedList<String> filt)");
                ll.removeAllViews();
                configureFilters(ll);

                //TODO declare the listener in sidebar to change the content
            }
        };
        viewModel.getDataToFilter().observe(this, filtersObserver);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void configureFilters(LinearLayout ll) {
        Log.d(TAG, "enter configureFilters(LinearLayout ll)");
        Log.i(TAG, "current f: "+ viewModel.getCurrentFilter().getValue());

        TextView txt = new TextView(getActivity());
        if (viewModel.getCurrentFilter().getValue().equals(MapFragment.FILTERS)) {
            txt.setText("Filters selected");
        } else if (viewModel.getCurrentFilter().getValue().equals(MapFragment.ALL)) {
            txt.setText("All selected");
        }
        ll.addView(txt);
        Log.d(TAG, "exit configureFilters(LinearLayout ll)");
    };
}
