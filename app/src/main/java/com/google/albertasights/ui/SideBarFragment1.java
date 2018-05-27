package com.google.albertasights.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CompoundButtonCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.albertasights.R;
import com.google.albertasights.models.Place;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

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
   // private LinearLayout ll;
    private View.OnClickListener checkBoxListener;
    private View.OnClickListener applyRatingListener;
    private boolean restartedOrRecreated = false;
    private  LinkedList <String> selectedFilters = new LinkedList<>();
    private ImageButton clearAll;
    private ImageButton hide;
    private TextView header;
  //  private ScrollView scrollView;
    private ListView listFilter1;
    private ListView listFilter2;
    private RelativeLayout sideBar;

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
//                String checked = ((CheckBox) v).getTag().toString();
//
//                if (selectedFilters.contains(checked)) {
//                    selectedFilters.remove(checked);
//                    Log.i(TAG, "removing: " + checked);
//                } else {
//                    selectedFilters.add(checked);
//                    Log.i(TAG, "adding: " + checked);
//                }
//                //TODO create the logic to modify the list of points to show
//                LinkedList<String> list = new LinkedList<>();
//
//                if (selectedFilters.size() == 0 &&
//                        ratings.size() == 0) {
//                    for (Place p : viewModel.getRecievedPoints().getValue()) {
//                        list.add(p.getName());
//                    }
//                } else {
//
//                    switch (viewModel.getCurrentFilter().getValue()) {
//                        case MapFragment.FILTERS:
//                            //TODO move this to separate activities start methods
//                            if (ratings.size() > 0 && selectedFilters.size() > 0) {
//                                for (Place p : viewModel.getRecievedPoints().getValue()) {
//                                    if (selectedFilters.contains(p.getCategory())
//                                            && ratings.contains(String.valueOf(p.getRating()))) {
//                                        list.add(p.getName());
//                                    }
//                                }
//                            } else if (ratings.size() > 0 && selectedFilters.size() == 0) {
//                                for (Place p : viewModel.getRecievedPoints().getValue()) {
//                                    if (ratings.contains(String.valueOf(p.getRating()))) {
//                                        list.add(p.getName());
//                                    }
//                                }
//
//                            } else if (ratings.size() == 0 && selectedFilters.size() > 0) {
//                                for (Place p : viewModel.getRecievedPoints().getValue()) {
//                                    if (selectedFilters.contains(p.getCategory())) {
//                                        list.add(p.getName());
//                                    }
//                                }
//                            }
//
//                            break;
//                        case MapFragment.LOVED:
//                            for (Place p : viewModel.getRecievedPoints().getValue()) {
//                                if (selectedFilters.contains(p.getName())) {
//                                    list.add(p.getName());
//                                }
//                            }
//                            break;
//                        case MapFragment.ALL:
//                            for (Place p : viewModel.getRecievedPoints().getValue()) {
//                                if (selectedFilters.contains(p.getName())) {
//                                    list.add(p.getName());
//                                }
//                            }
//                            break;
//
//                        default:
//                            break;
//                    }
//                }
//
//                //TODO TEST THIS SECTION
//                viewModel.updatePointsToShow(list);
                Log.d(TAG, "exit onClick checkBox(View view) ");
            }
        };

        applyRatingListener = new View.OnClickListener() {
            public void onClick(View view) {
//                Log.d(TAG, "enter applyRatingListener(View view)");
//                String sortedBy =((CheckBox) view).getTag().toString();
//                Log.i(TAG, "rating selected: "+ sortedBy);
//                if (ratings.contains(sortedBy)) {
//                    ratings.remove(sortedBy);
//                } else {
//                    ratings.add(sortedBy);
//                }
//                LinkedList<String> tmp = new LinkedList<>();
//                if (ratings.size()==0 && selectedFilters.size()==0) {
//                    tmp.addAll(
//                            viewModel.getNamesSortedByRating().getValue());
//                }
//                else if (selectedFilters.size()>0 && ratings.size()>0) {
//                    for (Place p : viewModel.getRecievedPoints().getValue()) {
//                        if (selectedFilters.contains(p.getCategory())
//                                && ratings.contains(String.valueOf(p.getRating()))) {
//                            tmp.add(p.getName());
//                        }
//                    }
//                } else if (ratings.size()==0 && selectedFilters.size()>0){
//                    for (Place p : viewModel.getRecievedPoints().getValue()) {
//                        if (selectedFilters.contains(p.getCategory())) {
//                            tmp.add(p.getName());
//                        }
//                    }
//
//                } else if (ratings.size()>0 && selectedFilters.size()==0) {
//                    for (Place p : viewModel.getRecievedPoints().getValue()) {
//                        if (ratings.contains(String.valueOf(p.getRating()))) {
//                            tmp.add(p.getName());
//                        }
//                    }
//                }
//                viewModel.updatePointsToShow(tmp);

                Log.d(TAG, "exit applyRatingListener(View view)");
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_side_bar_fragment1, container, false);
        sideBar = (RelativeLayout) v.findViewById(R.id.sidebar);
        if (viewModel.getOrienr().getValue().equals(UiUtils.PORTRAIT)) {
            v.getLayoutParams().width =  viewModel.getWight().getValue()/2+70;
        } else {
            v.getLayoutParams().width =  viewModel.getWight().getValue()/2;
        }
        //TODO configure the top margin dinamically depending on the size of the buttons container of the map fragment

//        ll = (LinearLayout)v.findViewById(R.id.test_layout);
//        scrollView = (ScrollView) v.findViewById(R.id.scrollV);
        listFilter1 =  (ListView) v.findViewById(R.id.filter_list);
        header = (TextView)  v.findViewById(R.id.header);

        listFilter1.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
              //  Log.d(TAG, "enter onScrollChanged()");
                if ( viewModel.getScrollY().getValue()==null) {
                    viewModel.updateY(listFilter1.getScrollY());
                }

                // For ScrollView
                //int scrollX = scrollView.getScrollX(); // For HorizontalScrollView
                // DO SOMETHING WITH THE SCROLL COORDINATES
              //  Log.d(TAG, "exit onScrollChanged()");
            }
        });

        clearAll = (ImageButton)v.findViewById(R.id.clearAll);
        hide = (ImageButton) v.findViewById(R.id.hideSidebar);

        hide.setImageResource(R.drawable.show_more_up);
        hide.getBackground().setAlpha(0);

        clearAll.setImageResource(R.drawable.clear);
        clearAll.getBackground().setAlpha(0);
        View.OnClickListener hideSidebarListener = new View.OnClickListener() {
            public void onClick(View view) {
                Log.d(TAG, "enter hideSidebarButtonsListener(View view)");
                viewModel.upateShowSidebar(false);

                Log.d(TAG, "exit hideSidebarButtonsListener(View view)");
            }
        };
        View.OnClickListener clearAllListener = new View.OnClickListener() {
            public void onClick(View view) {

                Log.d(TAG, "enter clearAllListener(View view)");
                viewModel.updateCurrentFilter(null);
                viewModel.updatePointsToShow(viewModel.getNamesSortedByRating().getValue());
                viewModel.upateShowSidebar(false);
                viewModel.updateDataToFilter(null);

                Log.d(TAG, "exit clearAllListener(View view)");
            }
        };
        hide.setOnClickListener(hideSidebarListener);
        clearAll.setOnClickListener(clearAllListener);

        final Observer<LinkedList<String>> scrollDataObserver = new Observer<LinkedList<String>>() {

            @Override
            public void onChanged(@Nullable final LinkedList<String> filt) {
                Log.i(TAG, "enter onChanged(@Nullable final LinkedList<String> filt)");
                if (filt!=null) {
                   // ll.removeAllViews();
                    configureFilters(sideBar);
                }

                //TODO declare the listener in sidebar to change the content
            }
        };
        viewModel.getDataToFilter().observe(this, scrollDataObserver);
        if (restartedOrRecreated==true) {
          //  ll.removeAllViews();
            configureFilters(sideBar);
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

    @TargetApi(17)
    private void configureFilters(RelativeLayout sideBar) {
        Log.d(TAG, "enter configureFilters(LinearLayout ll)");
        sideBar.removeAllViews();
        //TODO adding back the views including tho bottom element
        try {
            sideBar.addView(header);
            header.setPadding(20,20,20,20);
        } catch (Exception e) {

        }

        try {
            sideBar.addView(listFilter1);
        } catch (Exception e) {

        }

        if (viewModel.getCurrentFilter().getValue().equals(MapFragment.FILTERS)) {

//TODO add extra listview for rating
//            if (viewModel.getSelectedFiltersForCategories().getValue()!=null) {
//                Log.i(TAG, "storing values for filters: "+ viewModel.getSelectedFiltersForCategories().getValue().size());
//                selectedFilters.addAll(viewModel.getSelectedFiltersForCategories().getValue());
//            }
            //TODO toppest element
            TextView text1 = new TextView(getActivity());
            text1.setText("Rating");
            text1.setPadding(20,20,20,20);
            text1.setId(R.id.rating_header_id);
            RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            sideBar.addView(text1);

            //TODO second element under the toppest
            listFilter2 = new ListView(getActivity());
            final String [] symbols = {"*****", "****", "***"};
            ArrayAdapter adapter2 = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, symbols);
            listFilter2.setAdapter(adapter2);
            RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp2.addRule(RelativeLayout.BELOW, text1.getId());
            listFilter2.setLayoutParams(lp2);
            listFilter2.setId(R.id.rating_listView_id);
            listFilter2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Log.d(TAG, "enter applyRatingListener(View view)");
                    String sortedBy = "";
                    if(position==0) {
                        sortedBy="5";
                    } else if(position==1) {
                        sortedBy="4";
                    } else {
                        sortedBy="3";
                    }
                    Log.i(TAG, "listview children: "+listFilter2.getChildCount());
                    for (int i = 0; i < listFilter2.getChildCount(); i++) {
                        if(position == i){
                            Log.i(TAG, "element found");
                            if (ratings.contains(sortedBy)) {
                                ratings.remove(sortedBy);
                                listFilter2.getChildAt(i).setBackgroundColor(Color.WHITE);
                            } else {
                                ratings.add(sortedBy);
                                listFilter2.getChildAt(i).setBackgroundColor(Color.BLUE);
                            }

//                        }else{
//                            listFilter2.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                            break;
                        }

                    }


                    Log.i(TAG, "rating selected: "+ sortedBy);

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

                }
            });

            sideBar.addView(listFilter2);

            //TODO third element under the toppest
            header.setText("Categories");
            RelativeLayout.LayoutParams lp3 = (RelativeLayout.LayoutParams)header.getLayoutParams();
            header.setId(R.id.categories_header_id);
            lp3.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
            lp3.addRule(RelativeLayout.BELOW, listFilter2.getId());
            header.setLayoutParams(lp3);

            //TODO last element under the toppest
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) listFilter1.getLayoutParams();
            lp.addRule(RelativeLayout.BELOW, header.getId());
            listFilter1.setLayoutParams(lp);


        } else if (viewModel.getCurrentFilter().getValue().equals(MapFragment.ALL)) {
//            if (viewModel.getSelectedFiltersForAll().getValue()!=null) {
//                Log.i(TAG, "storing values for all: "+ viewModel.getSelectedFiltersForAll().getValue().size());
//                selectedFilters.addAll(viewModel.getSelectedFiltersForAll().getValue());
//            }
            header.setText("All points");

        } else if (viewModel.getCurrentFilter().getValue().equals(MapFragment.LOVED)) {
            if (viewModel.getLoved().getValue()==null || viewModel.getLoved().getValue().size()==0) {

                header.setText("You have not selected any points yet. To add point to your collection, expand the info window of the marker and click the heart icon");
               // ll.addView(txt);
                return;
            } else {
//                if (viewModel.getSelectedFiltersForLoved().getValue()!=null) {
//                    selectedFilters.addAll(viewModel.getSelectedFiltersForLoved().getValue());
//                }
                header.setText("Selected");
            }
        }
    //    ll.addView(txt, globalIndex++);
        Log.i(TAG, "selected filters found: "+selectedFilters.size());
//
//        if (viewModel.getCurrentFilter().getValue().equals(MapFragment.FILTERS)){
//            for (int i=0; i<3; i++) {
//                CheckBox checkBox1 = new CheckBox(getActivity());
//                //   Log.d(TAG, "adding checkbox");
//                checkBox1.setText(sympols[i]);
//                checkBox1.setTag(String.valueOf(5-i));
//            if (ratings.contains(String.valueOf(5-i))) {
//                checkBox1.setChecked(true);
//            }
//                checkBox1.setPadding(20,20,20,20);
//                checkBox1.setOnClickListener(applyRatingListener);
//                CompoundButtonCompat.setButtonTintList(checkBox1,colorStateList);
//                ll.addView(checkBox1, globalIndex++);
//            }
//
//            TextView text1 = new TextView(getActivity());
//            text1.setText("Categories:");
//            text1.setPadding(20,20,20,20);
//            ll.addView(text1, globalIndex++);
//        }
//
////        //TODO test dynamic scroll modification
//
//        if (!viewModel.getCurrentFilter().getValue().equals(MapFragment.ALL)) {
//            for (int i = 0; i < viewModel.getNamesToShowInScroll().getValue().size(); i++) {
//                //   Log.d(TAG, "!!!");
//                CheckBox checkBox = new CheckBox(getActivity());
//
//                // set the text size depending on the device type
//                checkBox.setText(viewModel.getNamesToShowInScroll().getValue().get(i));
//                //   Log.i(TAG, lst.get(i));
////            if (deviceType.equals("tablet")) {
////                checkBox.setTextSize(context.getResources().getDimension(R.dimen.avg_textsize));
////            }
//
//                if (selectedFilters.contains(viewModel.getDataToFilter().getValue().get(i))) {
//                    // set checked checkbox
//                    checkBox.setChecked(true);
//                    Log.i(TAG, "should be checked: " + viewModel.getDataToFilter().getValue().get(i));
//                }
//                checkBox.setPadding(20,20,20,20);
//                checkBox.setTag(viewModel.getNamesToShowInScroll().getValue().get(i));
//
//                //setting the color of text and the box of check box
//                //  checkBox.setTextColor(Color.BLACK);
//                CompoundButtonCompat.setButtonTintList(checkBox,colorStateList);
//                checkBox.setOnClickListener(checkBoxListener);
//                ll.addView(checkBox, globalIndex++);
//            }
//        } else {
//
//        }
        ArrayList <String> tmp = new ArrayList<>(viewModel.getDataToFilter().getValue().size());
        tmp.addAll(viewModel.getDataToFilter().getValue());
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, tmp);
        listFilter1.setAdapter(adapter);
        listFilter1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            //TODO!!!!!!!!!!!!!!!!!!!!!!!!!
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "enter onItemClick(AdapterView<?> parent, View view, int position, long id)");
                String checked = viewModel.getDataToFilter().getValue().get(position);//((CheckBox) v).getTag().toString();

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

                Log.d(TAG, "exit onItemClick(AdapterView<?> parent, View view, int position, long id)");

            }
        });

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
