package com.google.albertasights.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.albertasights.R;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SocialButtonsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SocialButtonsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SocialButtonsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = SocialButtonsFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MapViewModel viewModel;
    private UserViewModel viewModel1;
    private Integer screenH;
    private Integer screenW;
    private String orientation;
    private String device;

    private OnFragmentInteractionListener mListener;

    public SocialButtonsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SocialButtonsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SocialButtonsFragment newInstance(String param1, String param2) {
        SocialButtonsFragment fragment = new SocialButtonsFragment();
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
        Log.d(TAG, "enter onCreateView");
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_social_buttons, container, false);
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

        ViewGroup.LayoutParams lp = v.getLayoutParams();
        //   UserViewModel viewModel1 = ViewModelProviders.of(getActivity()).get(UserViewModel.class);

        if (orientation.equals(UiUtils.LANDSCAPE)) {
            Log.i(TAG, getActivity().getClass().getCanonicalName());
            if (getActivity().getClass().getCanonicalName().contains("User")) {
                lp.width = screenW/100*80;
            }
        }
     //   TextView txt = (TextView) v.findViewById(R.id.txt);
     //   UiUtils.setTextSize(screenH, txt, orientation, false);
     //   lp.height = screenW/13;

        ImageButton fb = (ImageButton)v.findViewById(R.id.facebook_gr);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent myWebLink = new Intent(android.content.Intent.ACTION_VIEW);
                    myWebLink.setData(Uri.parse("https://www.facebook.com/groups/1717083371714334/"));
                    getActivity().startActivity(myWebLink);
                } catch (Exception e) {
                    UiUtils.showToast(getActivity(), "Error, maybe no browsers have been installed");
                }

            }
        });
        fb.getBackground().setAlpha(0);
        ImageButton insta = (ImageButton)v.findViewById(R.id.instagram_gr);
        insta.getBackground().setAlpha(0);
        insta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent myWebLink = new Intent(android.content.Intent.ACTION_VIEW);
                    myWebLink.setData(Uri.parse("https://www.instagram.com/weloveyoualberta/"));
                    getActivity().startActivity(myWebLink);
                } catch (Exception e) {
                    UiUtils.showToast(getActivity(), "Error, maybe no browsers have been installed");
                }

            }
        });
        int squareDiment=0;

        if (orientation.equals(UiUtils.LANDSCAPE)) {
            squareDiment=screenW/13;
        } else {
            squareDiment=screenH/13;
        }

        Picasso.with(getActivity())
                .load(R.raw.fb)
                .resize(squareDiment, squareDiment)
                .into(fb);

        Picasso.with(getActivity())
                .load(R.raw.inst)
                .resize(squareDiment, squareDiment)
                .into(insta);
        Log.d(TAG, "exit onCreateView");

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
        Log.d(TAG, "enter onAttach(Context context)");
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
       } //else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
        Log.d(TAG, "exit onAttach(Context context)");
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
}
