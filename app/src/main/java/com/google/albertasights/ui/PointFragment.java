package com.google.albertasights.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.albertasights.DBIntentService;
import com.google.albertasights.R;
import com.google.albertasights.models.Place;
import com.squareup.picasso.Picasso;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnPointFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PointFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PointFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private OnPointFragmentInteractionListener mListener;
    private Place point;
    private ListView listview;
    private static final String TAG = PointFragment.class.getSimpleName();
    private ImageButton closeButton;
    private String orientation;
    private String deviceType;
    private MapViewModel viewModel;

    public PointFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PointFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PointFragment newInstance(String param1, String param2) {
        PointFragment fragment = new PointFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "enter onCreate(Bundle savedInstanceState)");
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
        viewModel = ViewModelProviders.of(getActivity()).get(MapViewModel.class);
        point = viewModel.getPointToSee().getValue();
        Log.d(TAG, "exit onCreate(Bundle savedInstanceState)");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //getting the type and the orientation of device

        Log.d(TAG, "enter  onCreateView");
        View v = inflater.inflate(R.layout.activity_point, container, false);
        orientation = UiUtils.getOrientation(getActivity());
        deviceType = UiUtils.findScreenSize(getActivity());


        //        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        RelativeLayout allTheView = (RelativeLayout) v.findViewById(R.id.pointView);
        ImageView banner = (ImageView) v.findViewById(R.id.banner);
        WindowManager wm = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        if (orientation.equals("Portrait")) {
            allTheView.removeView(banner);
        } else {
            if (deviceType.equals("tablet")) {
                Picasso.with(getActivity())
                        .load(R.raw.banner)
                        .resize(300, metrics.heightPixels)
                        //    .onlyScaleDown()
                        .centerCrop()
                        .into(banner);
            } else {
                Picasso.with(getActivity())
                        .load(R.raw.banner)
                        .resize(300, metrics.heightPixels)
                        //    .onlyScaleDown()
                        .centerCrop()
                        .into(banner);
            }

        }

        //initializing the buttons

        ImageButton fab = (ImageButton) v.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.directions);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //    Log.d(TAG, "onclick");
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)",
                        point.getLat(), point.getLng(), "Going there");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        final ImageButton likeButton = (ImageButton) v.findViewById(R.id.like);
        if (point.isLoved()==true) {
            likeButton.setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_IN));
        }
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onclick");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                if (prefs.getBoolean(UiUtils.LOGGED_IN, true)==true) {
                    String id =((ImageButton) view).getTag().toString();
                    if (point.isLoved()==false) {
                        Intent writeToFile = new Intent(getActivity(), DBIntentService.class);
                        writeToFile.setAction(UiUtils.ADD_POINT_TO_LOVED);
                        writeToFile.putExtra(UiUtils.POINT_ID, id);
                        getActivity().startService(writeToFile);
                        likeButton.setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_IN));
                    } else {
                        //TODO call the method to remove from selected
                        Intent remove = new Intent(getActivity(), DBIntentService.class);
                        remove.setAction(UiUtils.REMOVE_POINT);
                        remove.putExtra(UiUtils.POINT_ID, id);
                        getActivity().startService(remove);
                        likeButton.setColorFilter(new PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN));
                    }

                } else {
                    UiUtils.showToast(getActivity(), "To use this option, please log in or create an account");
                }
            }
        });

        //TODO set onClick method for the button
        likeButton.setImageResource(R.drawable.like);
        likeButton.setTag(point.getName());
        listview = (ListView) v.findViewById(R.id.listView1);

        //TODO provide dimensions, position and device type via constructor
        PointListviewAdapter adapter = new PointListviewAdapter(point, getActivity());
        listview.setAdapter(adapter);
        Log.d(TAG, "exit  onCreateView");
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
        Log.d(TAG, "enter  onAttach(Context context)");
        if (context instanceof OnPointFragmentInteractionListener) {
            mListener = (OnPointFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPointFragmentInteractionListener");
        }
        Log.d(TAG, "exit  onAttach(Context context)");
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "enter ononDetach()");
        super.onDetach();
        mListener = null;
        Log.d(TAG, "exit onDetach()");
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
    public interface OnPointFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
