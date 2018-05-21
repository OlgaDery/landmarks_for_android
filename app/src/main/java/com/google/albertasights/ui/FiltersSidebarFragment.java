package com.google.albertasights.ui;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v4.widget.Space;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.albertasights.R;
import com.google.albertasights.models.Place;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static com.google.albertasights.ui.UiUtils.getOrientation;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FiltersSidebarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FiltersSidebarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FiltersSidebarFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = FiltersSidebarFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MapViewModel viewModel;
    //filters selected by user
    private ArrayList<String> selectedFilters = new ArrayList<>();
    //filters received from APIs
 //   private ArrayList<String> receivedFilters = new ArrayList<>();
    private ArrayList<String> ratings=new ArrayList<>(3);
    private String sortedBy;
    private LinearLayout filter;
    private View.OnClickListener checkBoxListener;
    private View.OnClickListener applyRatingListener;

    private OnFragmentInteractionListener mListener;

    public FiltersSidebarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FiltersSidebarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FiltersSidebarFragment newInstance(String param1, String param2) {
        FiltersSidebarFragment fragment = new FiltersSidebarFragment();
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
           // receivedFilters = savedInstanceState.getStringArrayList(MapFragment.KEY_RECEIVED_FILTERS);
            selectedFilters = savedInstanceState.getStringArrayList(MapFragment.KEY_SELECTED_FILTERS);
            ratings = savedInstanceState.getStringArrayList("RATINGS");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(TAG, "enter onSaveInstanceState(@NonNull Bundle outState)");
        super.onSaveInstanceState(outState);
       // outState.putStringArrayList(MapFragment.KEY_RECEIVED_FILTERS, receivedFilters);
        outState.putStringArrayList("RATINGS", ratings);
        outState.putStringArrayList(MapFragment.KEY_SELECTED_FILTERS, selectedFilters);

        Log.d(TAG, "enter onSaveInstanceState(@NonNull Bundle outState)");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //TODO temporary remove buttons from the bottom
        View v = inflater.inflate(R.layout.fragment_filters_sidebar, container, false);
        WindowManager wm = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int screen_width = metrics.widthPixels;
    //    v.getLayoutParams().width = screen_width/2;

        filter = (LinearLayout) v.findViewById(R.id.filters);
        TextView txt = new TextView(getActivity());
        txt.setText("Jopa");
        filter.addView(txt);

        //this listener starts when the user check the checkbox in
        checkBoxListener = new View.OnClickListener() {

            public void onClick(View v) {
                Log.d(TAG, "enter onClick checkBox(View view) ");
                String checked =((CheckBox) v).getTag().toString();
                if (selectedFilters.contains(checked)) {
                    selectedFilters.remove(checked);
                    Log.i(TAG, "removing: "+checked);
                } else {
                    selectedFilters.add(checked);
                    Log.i(TAG, "adding: "+checked);
                }
                //TODO create the logic to modify the list of points to show
                LinkedList<String> list = new LinkedList<>();

                if (selectedFilters.size()==0 &&
                        ratings.size()==0) {
                    for (Place p : viewModel.getRecievedPoints().getValue()) {
                        list.add(p.getName());
                    }
                } else {
                    ArrayList <String>tmp = new ArrayList<>(1);
                    tmp.addAll(viewModel.getFilters().getValue().keySet());
                    String currentFilter = tmp.get(0);
                    switch (currentFilter) {
                        case MapFragment.FILTERS:
                            //TODO move this to separate activities start methods
                            if (ratings.size()>0&& selectedFilters.size()>0) {
                                for (Place p : viewModel.getRecievedPoints().getValue()) {
                                    if (selectedFilters.contains(p.getCategory())
                                            && ratings.contains(String.valueOf(p.getRating()))) {
                                        list.add(p.getName());
                                    }
                                }
                            } else if (ratings.size()>0 && selectedFilters.size()==0){
                                for (Place p : viewModel.getRecievedPoints().getValue()) {
                                    if (ratings.contains(String.valueOf(p.getRating()))) {
                                        list.add(p.getName());
                                    }
                                }

                            } else if (ratings.size()==0 && selectedFilters.size()>0) {
                                for (Place p : viewModel.getRecievedPoints().getValue()) {
                                    if (selectedFilters.contains(p.getCategory())) {
                                        list.add(p.getName());
                                    }
                                }
                            }

                            break;
                        case MapFragment.LOVED:
                            for (Place p :viewModel.getRecievedPoints().getValue()) {
                                if (selectedFilters.contains(p.getName())) {
                                    list.add(p.getName());
                                }
                            }
                            break;
                        case MapFragment.ALL:
                            for (Place p :viewModel.getRecievedPoints().getValue()) {
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

        View.OnClickListener showMoreButtonsListener = new View.OnClickListener() {
            public void onClick(View view) {
                Log.d(TAG, "enter showFilters(View view)");
                ArrayList <String>tmp = new ArrayList<>(1);
                tmp.addAll(viewModel.getFilters().getValue().keySet());
                String currentFilter = tmp.get(0);
                Map<String, Boolean> tmp1 = new HashMap<String, Boolean>();
                tmp1.put(currentFilter, false);
                viewModel.updateFilterMap(tmp1);

                Log.d(TAG, "exit showFilters(View view)");
            }
        };

        applyRatingListener = new View.OnClickListener() {
            public void onClick(View view) {
                Log.d(TAG, "enter applyRatingListener(View view)");
                sortedBy =((CheckBox) view).getTag().toString();
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

//        LinearLayout bottomWr = (LinearLayout) v.findViewById(R.id.bottomWr);
//        ImageButton showFilterSection = new ImageButton(getActivity());//(ImageButton) v.findViewById(R.id.imageB);
//        showFilterSection.setImageResource(R.drawable.show_more_up);
//        showFilterSection.getBackground().setAlpha(0);
//        //   showFilterSection.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN));
//
//        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT, 1);
//        Space space = new Space(getActivity());
//        Space space1 = new Space(getActivity());
//        Space space2 = new Space(getActivity());
//        space.setLayoutParams(param);
//        space1.setLayoutParams(param);
//        space2.setLayoutParams(param);
//
//        bottomWr.addView(space, 0);
//        bottomWr.addView(showFilterSection, 1);
//        bottomWr.addView(space1, 2);
       // bottomWr.addView(clearAll, 3);
       // bottomWr.addView(space2, 4);


        final Observer<LinkedList<String>> filtersObserver = new Observer<LinkedList<String>>() {

            @Override
            public void onChanged(@Nullable final LinkedList<String> filt) {
                Log.i(TAG, "enter onChanged(@Nullable final LinkedList<String> filt)");
                //
               // configureFilters(filter, "Portrait", selectedFilters, checkBoxListener, applyRatingListener, ratings );

                //TODO declare the listener in sidebar to change the content
            }
        };
        viewModel.getDataToFilter().observe(this, filtersObserver);

      //  configureFilters(filter, "Portrait", selectedFilters, checkBoxListener, applyRatingListener, ratings );

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
        viewModel = ViewModelProviders.of(getActivity()).get(MapViewModel.class);
        viewModel.updateCurrentFragment(this.getClass().getSimpleName());
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;

            //TODO filling up the RecievedPoints

        } else {
//            throw new RuntimeException(context.toString()
 //                   + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "enter on detach");
        super.onDetach();
        mListener = null;
        Log.d(TAG, "exit on detach");
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

    public void configureFilters (LinearLayout filter, String deviceType,
                                        // ArrayList<String> receivedFilters,
                                         ArrayList<String> selectedFilters,
                                         View.OnClickListener checkBoxListener,
                                      //   View.OnClickListener clearButtonList,
                                      //   View.OnClickListener seeMoreBList,
                                         View.OnClickListener sortPointsButtListenet,
                                         ArrayList<String> ratings) {
        Log.d(TAG, "enter configureFilters");
        Log.d(TAG, "received filters: "+viewModel.getDataToFilter().getValue().size());
        Log.d(TAG, "current filter: "+viewModel.getCurrentFilter().getValue());

        int globalIndex=0;
        WindowManager wm = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int screen_width = metrics.widthPixels;
        int elementWight = 0;

        TextView text = new TextView(getActivity());
        text.setPadding(20,20,20,20);
        if (getOrientation(getActivity()).equals("Portrait")) {
            switch (viewModel.getCurrentFilter().getValue()) {
                case MapFragment.FILTERS:
                    text.setText("Rating:");
                    filter.addView(text, globalIndex++);
                    elementWight = screen_width - 300;
                    break;
                case MapFragment.ALL:

                    text.setText("Sorted be name:");
                    filter.addView(text, globalIndex++);
                    //  filter.addView(b, 2);
                    elementWight = screen_width - 200;
                    break;
                case MapFragment.LOVED:
                    text.setText("Selected points:");
                    filter.addView(text, globalIndex++);
                    elementWight = screen_width - 200;
                    break;
            }
        } else {
            switch (viewModel.getCurrentFilter().getValue()) {
                case MapFragment.FILTERS:
                    text.setText("Rating:");
                    Log.i(TAG, "setting rating");
                    filter.addView(text, globalIndex++);
                    elementWight = screen_width - 550;
                    break;
                case MapFragment.ALL:
                    text.setText("Sorted be name:");
                    filter.addView(text, globalIndex);
                    //  filter.addView(b, 2);
                    elementWight = screen_width - 450;
                    break;
                case MapFragment.LOVED:
                    text.setText("Selected points:");
                    filter.addView(text, globalIndex++);
                    elementWight = screen_width - 450;
                    break;
            }
        }
     //   filter.setLayoutParams(new FrameLayout.LayoutParams(elementWight, FrameLayout.LayoutParams.WRAP_CONTENT));

        if (viewModel.getCurrentFilter().getValue().equals(MapFragment.LOVED)&& viewModel.getDataToFilter().getValue()==null) {
            TextView text1 = new TextView(getActivity());
            text.setText("You have not selected any points yet");
            filter.addView(text1);
            return;
        }

        ArrayList<String> lst = new ArrayList<>(viewModel.getDataToFilter().getValue().size());
        lst.addAll(viewModel.getDataToFilter().getValue());
        Collections.sort(lst);
        // adding checkboxes dynamically

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

        if (viewModel.getCurrentFilter().getValue().equals(MapFragment.FILTERS)){

            CheckBox checkBox1 = new CheckBox(getActivity());
            Log.d(TAG, "adding checkbox");
            checkBox1.setText("*****");
            checkBox1.setTag("5");
            if (ratings.contains("5")) {
                checkBox1.setChecked(true);
            }
            checkBox1.setPadding(20,20,20,20);
            checkBox1.setOnClickListener(sortPointsButtListenet);
            CompoundButtonCompat.setButtonTintList(checkBox1,colorStateList);

            filter.addView(checkBox1, globalIndex++);
            CheckBox checkBox2 = new CheckBox(getActivity());
            checkBox2.setText("****");
            if (ratings.contains("4")) {
                checkBox2.setChecked(true);
            }
            checkBox2.setTag("4");
            checkBox2.setPadding(20,20,20,20);
            checkBox2.setOnClickListener(sortPointsButtListenet);
            CompoundButtonCompat.setButtonTintList(checkBox2,colorStateList);

            filter.addView(checkBox2, globalIndex++);
            CheckBox checkBox3 = new CheckBox(getActivity());
            checkBox3.setTag("3");
            checkBox3.setPadding(20,20,20,20);
            if (ratings.contains("3")) {
                checkBox3.setChecked(true);
            }
            checkBox3.setOnClickListener(sortPointsButtListenet);
            checkBox3.setText("***");
            CompoundButtonCompat.setButtonTintList(checkBox3,colorStateList);
            filter.addView(checkBox3, globalIndex++);

            TextView text1 = new TextView(getActivity());
            text1.setText("Categories:");
            text1.setPadding(20,20,20,20);
            filter.addView(text1, globalIndex++);
        }
        //TODO TEST!!!!!!
        Handler handler = new Handler();
//        populateFiltersInSeparateThread(handler, lst, selectedFilters,
//                checkBoxListener, getActivity(), filter, deviceType, globalIndex);

//        for (int i = 0; i < receivedFilters.size(); i++) {
//            //   Log.d(TAG, "!!!");
//            CheckBox checkBox = new CheckBox(context);
//
//            // set the text size depending on the device type
//            checkBox.setText(lst.get(i));
//            //   Log.i(TAG, lst.get(i));
//            if (deviceType.equals("tablet")) {
//                checkBox.setTextSize(context.getResources().getDimension(R.dimen.avg_textsize));
//            }
//
//            if (selectedFilters.contains(lst.get(i))) {
//                // set checked checkbox
//                checkBox.setChecked(true);
//                Log.i(TAG, "should be checked: " + lst.get(i));
//            }
//            checkBox.setPadding(20,20,20,20);
//            checkBox.setTag(lst.get(i));
//
//            //setting the color of text and the box of check box
//            //  checkBox.setTextColor(Color.BLACK);
//            CompoundButtonCompat.setButtonTintList(checkBox,colorStateList);
//            checkBox.setOnClickListener(checkBoxListener);
//            filter.addView(checkBox, globalIndex++);
//        }

    }

    private static void populateFiltersInSeparateThread(final Handler handler, final ArrayList<String> receivedFilters,
                                                        final ArrayList<String> selectedFilters, final View.OnClickListener checkBoxListener,
                                                        final Activity context, final LinearLayout filter, final String deviceType,
                                                        int globalIndex) {
        for (int i = 0; i < receivedFilters.size(); i++) {
            final int newIndex = i;
            final int newGlobalIndex = globalIndex++;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    CheckBox checkBox = new CheckBox(context);
                    // set the text size depending on the device type
                    checkBox.setText(receivedFilters.get(newIndex));
                      Log.i(TAG, receivedFilters.get(newIndex));
                    if (deviceType.equals("tablet")) {
                        checkBox.setTextSize(context.getResources().getDimension(R.dimen.avg_textsize));
                    }

                    if (selectedFilters.contains(receivedFilters.get(newIndex))) {
                        // set checked checkbox
                        checkBox.setChecked(true);
                        Log.i(TAG, "should be checked: " + receivedFilters.get(newIndex));
                    }
                    checkBox.setPadding(20,20,20,20);
                    checkBox.setTag(receivedFilters.get(newIndex));

                    //setting the color of text and the box of check box
                    //  CompoundButtonCompat.setButtonTintList(checkBox,colorStateList);
                    checkBox.setOnClickListener(checkBoxListener);
                    filter.addView(checkBox, newGlobalIndex);
                }
            });
        }
    }
}
