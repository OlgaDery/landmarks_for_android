package com.google.albertasights.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.albertasights.R;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoadingFragment.OnRetryConnectionListener} interface
 * to handle interaction events.
 * Use the {@link LoadingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoadingFragment extends Fragment {
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

    private OnRetryConnectionListener mListener;

    public LoadingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoadingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoadingFragment newInstance(String param1, String param2) {
        LoadingFragment fragment = new LoadingFragment();
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
        View v = inflater.inflate(R.layout.fragment_loading, container, false);
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

        final Button btn = (Button) v.findViewById(R.id.retry);
        ImageView img = (ImageView) v.findViewById(R.id.img);
        ViewGroup.LayoutParams params = img.getLayoutParams();
        params.height=screenH/100*80;
        params.width = screenW/100*80;
        if (orientation.equals(UiUtils.PORTRAIT)) {
            try {
                Picasso.with(getActivity())
                        .load(R.raw.albertasights_vertical)
                        .resize(screenW/100*80, screenH/100*80)
                        //    .onlyScaleDown()
                        .centerInside()
                        .into(img);
            } catch (Exception e) {
                Log.e(LoadingFragment.class.getCanonicalName(), e.toString());
            }


        } else {
            try {
                Picasso.with(getActivity())
                        .load(R.raw.albertasights_horizontal)
                        .resize(screenW/100*80, screenH/100*80)
                        //    .onlyScaleDown()
                        .centerInside()
                        .into(img);
            } catch (Exception e) {
                Log.e(LoadingFragment.class.getCanonicalName(), e.toString());
            }

        }
        btn.setAlpha(0.0f);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed("RETRY");
            }
        });

        final Observer<Boolean> loadingDataObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable final Boolean data) {
                if (data==false) {
                    showButton(btn);
                }
            }

        };
        viewModel.getDataReceived().observe(getActivity(), loadingDataObserver);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String action) {
        if (mListener != null) {
            mListener.onRetryConnection(action);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnRetryConnectionListener) {
            mListener = (OnRetryConnectionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void showButton (Button btn) {
        btn.setAlpha(1.0f);
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
    public interface OnRetryConnectionListener {
        // TODO: Update argument type and name
        void onRetryConnection(String action);
    }
}
