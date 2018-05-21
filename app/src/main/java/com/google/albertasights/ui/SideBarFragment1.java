package com.google.albertasights.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.albertasights.models.Place;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Collections;
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
    private View.OnClickListener checkBoxListener;
    private View.OnClickListener applyRatingListener;
    private boolean restartedOrRecreated = false;
    LinkedList <String> selectedFilters = new LinkedList<>();

    private OnFragmentInteractionListener mListener;

  //  private LinkedList<String> selectedFilters = new LinkedList<>();
    private ArrayList<String> ratings=new ArrayList<>(3);

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
        if (savedInstanceState != null) {
            selectedFilters.addAll(savedInstanceState.getStringArrayList(MapFragment.KEY_SELECTED_FILTERS));
            ratings = savedInstanceState.getStringArrayList("RATINGS");
            restartedOrRecreated = true;
        }
        viewModel = ViewModelProviders.of(getActivity()).get(MapViewModel.class);
        viewModel.updateCurrentFragment(this.getClass().getSimpleName());
        //this listener starts when the user check the checkbox in
        checkBoxListener = new View.OnClickListener() {

            public void onClick(View v) {
                Log.d(TAG, "enter onClick checkBox(View view) ");
                String checked = ((CheckBox) v).getTag().toString();

                if (selectedFilters.contains(checked)) {
                    selectedFilters.remove(checked);
                    Log.i(TAG, "removing: " + checked);
                } else {
                    selectedFilters.add(checked);
                    Log.i(TAG, "adding: " + checked);
                }
                //TODO create the logic to modify the list of points to show
                LinkedList<String> list = new LinkedList<>();

                if (selectedFilters.size() == 0 &&
                        ratings.size() == 0) {
                    for (Place p : viewModel.getRecievedPoints().getValue()) {
                        list.add(p.getName());
                    }
                } else {

                    switch (viewModel.getCurrentFilter().getValue()) {
                        case MapFragment.FILTERS:
                            //TODO move this to separate activities start methods
                            if (ratings.size() > 0 && selectedFilters.size() > 0) {
                                for (Place p : viewModel.getRecievedPoints().getValue()) {
                                    if (selectedFilters.contains(p.getCategory())
                                            && ratings.contains(String.valueOf(p.getRating()))) {
                                        list.add(p.getName());
                                    }
                                }
                            } else if (ratings.size() > 0 && selectedFilters.size() == 0) {
                                for (Place p : viewModel.getRecievedPoints().getValue()) {
                                    if (ratings.contains(String.valueOf(p.getRating()))) {
                                        list.add(p.getName());
                                    }
                                }

                            } else if (ratings.size() == 0 && selectedFilters.size() > 0) {
                                for (Place p : viewModel.getRecievedPoints().getValue()) {
                                    if (selectedFilters.contains(p.getCategory())) {
                                        list.add(p.getName());
                                    }
                                }
                            }

                            break;
                        case MapFragment.LOVED:
                            for (Place p : viewModel.getRecievedPoints().getValue()) {
                                if (selectedFilters.contains(p.getName())) {
                                    list.add(p.getName());
                                }
                            }
                            break;
                        case MapFragment.ALL:
                            for (Place p : viewModel.getRecievedPoints().getValue()) {
                                if (selectedFilters.contains(p.getName())) {
                                    list.add(p.getName());
                                }
                            }
                            break;

                        default:
                            break;
                    }
                }

                //TODO TEST THIS SECTION
                viewModel.updatePointsToShow(list);
                Log.d(TAG, "exit onClick checkBox(View view) ");
            }
        };

        applyRatingListener = new View.OnClickListener() {
            public void onClick(View view) {
                Log.d(TAG, "enter applyRatingListener(View view)");
                String sortedBy =((CheckBox) view).getTag().toString();
                Log.i(TAG, "rating selected: "+ sortedBy);
                if (ratings.contains(sortedBy)) {
                    ratings.remove(sortedBy);
                } else {
                    ratings.add(sortedBy);
                }
                LinkedList<String> tmp = new LinkedList<>();
                if (ratings.size()==0 && selectedFilters.size()==0) {
                    tmp.addAll(
                            viewModel.getNamesSortedByRating().getValue());
                }
                else if (selectedFilters.size()>0 && ratings.size()>0) {
                    for (Place p : viewModel.getRecievedPoints().getValue()) {
                        if (selectedFilters.contains(p.getCategory())
                                && ratings.contains(String.valueOf(p.getRating()))) {
                            tmp.add(p.getName());
                        }
                    }
                } else if (ratings.size()==0 && selectedFilters.size()>0){
                    for (Place p : viewModel.getRecievedPoints().getValue()) {
                        if (selectedFilters.contains(p.getCategory())) {
                            tmp.add(p.getName());
                        }
                    }

                } else if (ratings.size()>0 && selectedFilters.size()==0) {
                    for (Place p : viewModel.getRecievedPoints().getValue()) {
                        if (ratings.contains(String.valueOf(p.getRating()))) {
                            tmp.add(p.getName());
                        }
                    }
                }
                viewModel.updatePointsToShow(tmp);

                Log.d(TAG, "exit applyRatingListener(View view)");
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

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
        if (restartedOrRecreated==true) {
            ll.removeAllViews();
            configureFilters(ll);
        }
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
     //   selectedFilters.clear();
        int globalIndex=0;

        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked}, // unchecked
                        new int[]{android.R.attr.state_checked} , // checked
                },
                new int[]{
                        Color.parseColor("#000000"),
                        Color.parseColor("#40b6ff"),
                }
        );

        TextView txt = new TextView(getActivity());

        txt.setPadding(20,20,20,20);
        if (viewModel.getCurrentFilter().getValue().equals(MapFragment.FILTERS)) {

//            if (viewModel.getSelectedFiltersForCategories().getValue()!=null) {
//                Log.i(TAG, "storing values for filters: "+ viewModel.getSelectedFiltersForCategories().getValue().size());
//                selectedFilters.addAll(viewModel.getSelectedFiltersForCategories().getValue());
//            }
            txt.setText("Rating");
        } else if (viewModel.getCurrentFilter().getValue().equals(MapFragment.ALL)) {
//            if (viewModel.getSelectedFiltersForAll().getValue()!=null) {
//                Log.i(TAG, "storing values for all: "+ viewModel.getSelectedFiltersForAll().getValue().size());
//                selectedFilters.addAll(viewModel.getSelectedFiltersForAll().getValue());
//            }
            txt.setText("All points");
        } else if (viewModel.getCurrentFilter().getValue().equals(MapFragment.LOVED)) {
            if (viewModel.getLoved().getValue()==null) {
                TextView text1 = new TextView(getActivity());
                txt.setText("You have not selected any points yet");
                ll.addView(text1);
                return;
            } else {
//                if (viewModel.getSelectedFiltersForLoved().getValue()!=null) {
//                    selectedFilters.addAll(viewModel.getSelectedFiltersForLoved().getValue());
//                }
                txt.setText("Selected");
            }
        }
        ll.addView(txt, globalIndex++);
        Log.i(TAG, "selected filters found: "+selectedFilters.size());
        String [] sympols = {"*****", "****", "***"};

        if (viewModel.getCurrentFilter().getValue().equals(MapFragment.FILTERS)){
            for (int i=0; i<3; i++) {
                CheckBox checkBox1 = new CheckBox(getActivity());
                //   Log.d(TAG, "adding checkbox");
                checkBox1.setText(sympols[i]);
                checkBox1.setTag(String.valueOf(5-i));
            if (ratings.contains(String.valueOf(5-i))) {
                checkBox1.setChecked(true);
            }
                checkBox1.setPadding(20,20,20,20);
                checkBox1.setOnClickListener(applyRatingListener);
                CompoundButtonCompat.setButtonTintList(checkBox1,colorStateList);
                ll.addView(checkBox1, globalIndex++);
            }

            TextView text1 = new TextView(getActivity());
            text1.setText("Categories:");
            text1.setPadding(20,20,20,20);
            ll.addView(text1, globalIndex++);
        }
        ArrayList<String> lst = new ArrayList<>(viewModel.getDataToFilter().getValue().size());
        lst.addAll(viewModel.getDataToFilter().getValue());
        Collections.sort(lst);
        // adding checkboxes dynamically

        for (int i = 0; i < viewModel.getDataToFilter().getValue().size(); i++) {
            //   Log.d(TAG, "!!!");
            CheckBox checkBox = new CheckBox(getActivity());

            // set the text size depending on the device type
            checkBox.setText(lst.get(i));
            //   Log.i(TAG, lst.get(i));
//            if (deviceType.equals("tablet")) {
//                checkBox.setTextSize(context.getResources().getDimension(R.dimen.avg_textsize));
//            }

            if (selectedFilters.contains(lst.get(i))) {
                // set checked checkbox
                checkBox.setChecked(true);
                Log.i(TAG, "should be checked: " + lst.get(i));
            }
            checkBox.setPadding(20,20,20,20);
            checkBox.setTag(lst.get(i));

            //setting the color of text and the box of check box
            //  checkBox.setTextColor(Color.BLACK);
            CompoundButtonCompat.setButtonTintList(checkBox,colorStateList);
            checkBox.setOnClickListener(checkBoxListener);
            ll.addView(checkBox, globalIndex++);
        }

        Log.d(TAG, "exit configureFilters(LinearLayout ll)");
    };

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(TAG, "enter onSaveInstanceState(@NonNull Bundle outState)");
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("RATINGS", ratings);
        ArrayList<String> tmp = new ArrayList<String>(selectedFilters.size());
        tmp.addAll(selectedFilters);
        outState.putStringArrayList(MapFragment.KEY_SELECTED_FILTERS, tmp);
        Log.d(TAG, "enter onSaveInstanceState(@NonNull Bundle outState)");

    }

    @Override
    public void onStart() {
        Log.d(TAG, "enter onStart()");
        super.onStart();
        Log.d(TAG, "exit onStart()");
    }

    @Override
    public void onResume() {
        Log.d(TAG, "enter onResume()");
        super.onResume();
        Log.d(TAG, "exit onResume()");
    }

    @Override
    public void onPause() {
        Log.d(TAG, "enter onPause()");
        super.onPause();
        Log.d(TAG, "exit onPause()");
    }

    @Override
    public void onStop() {
        Log.d(TAG, "enter onStop()");
        super.onStop();
        Log.d(TAG, "exit onStop()");
    }
}
