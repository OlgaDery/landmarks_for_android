package com.google.albertasights.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.albertasights.R;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AdsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

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

    public AdsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdsFragment newInstance(String param1, String param2) {
        AdsFragment fragment = new AdsFragment();
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
        Log.d(AdsFragment.class.getCanonicalName(), "enter onCreateView");
        View v = inflater.inflate(R.layout.fragment_ads, container, false);
        ImageView banner = (ImageView) v.findViewById(R.id.banner);
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
       // ViewGroup.LayoutParams lp = v.getLayoutParams();
        ViewGroup.LayoutParams bannerParams = banner.getLayoutParams();
        bannerParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        if (orientation.equals(UiUtils.TABLET)) {
            bannerParams.width = screenW/100*20;
        } else {
            bannerParams.width = screenW/100*20;
        }

        if (device.equals(UiUtils.TABLET)) {
            try {
                Picasso.with(getActivity())
                        .load(R.raw.mallorn2)
                        .resize(bannerParams.width, screenH)
                        .centerCrop()
                      //  .fit()
                        .into(banner);
            } catch (Exception e) {
                Log.e(AdsFragment.class.getCanonicalName(), e.toString());
            }
        } else {
            try {
                Picasso.with(getActivity())
                        .load(R.raw.mallorn1)
                        .resize(bannerParams.width, screenH)
                        .centerCrop()
                        .into(banner);
            } catch (Exception e) {
                Log.e(AdsFragment.class.getCanonicalName(), e.toString());
            }
        }

        View.OnClickListener listener = new View.OnClickListener() {

            public void onClick(View v) {
             //   Log.d(TAG, "enter onClick (View view) ");
                try{
                    Intent myWebLink = new Intent(android.content.Intent.ACTION_VIEW);
                    if(getActivity().getClass().getCanonicalName().contains("User")) {
                        myWebLink.setData(Uri.parse("https://www.instagram.com/mallorn.ca/"));
                    } else {
                        myWebLink.setData(Uri.parse("http://www.mallorn.ca/"));
                    }

                    getActivity().startActivity(myWebLink);
                } catch (Exception e) {
                    UiUtils.showToast(getActivity(), "Error, maybe no browsers have been installed");
                }

              //  Log.d(TAG, "exit onClick (View view) ");
            }
        };
        v.setOnClickListener(listener);

        Log.d(AdsFragment.class.getCanonicalName(), "enter onCreateView");
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
        } //else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
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
