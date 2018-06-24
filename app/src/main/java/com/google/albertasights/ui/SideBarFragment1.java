package com.google.albertasights.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CompoundButtonCompat;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
public class SideBarFragment1 extends Fragment implements View.OnTouchListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    private MapViewModel viewModel;
   // private LinearLayout ll;
    private View.OnClickListener checkBoxListener;
    private View.OnClickListener applyRatingListener;
    private boolean restartedOrRecreated = false;
    private ImageButton clearAll;
    private ImageButton hide;
    private TextView header;
  //  private ScrollView scrollView;
    private ListView listFilter1;
    private ListView listFilter2;
    private RelativeLayout sideBar;
    private GestureDetector mDetector;
    private float mInitialX, mInitialY;
    private int listViewTopPosit;

    private OnFragmentInteractionListener mListener;
  //  private ArrayList<String> ratings=new ArrayList<>(3);

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
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            try {
                listViewTopPosit = savedInstanceState.getInt("LIST_VIEW_POSIT");
            } catch (Exception e) {

            }
            restartedOrRecreated = true;
        }
        viewModel = ViewModelProviders.of(getActivity()).get(MapViewModel.class);
       // viewModel.updateCurrentFragment(this.getClass().getSimpleName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_side_bar_fragment1, container, false);
        v.setPadding(viewModel.getHight().getValue()/35, viewModel.getHight().getValue()/35,
                viewModel.getHight().getValue()/35, viewModel.getHight().getValue()/35);
        sideBar = (RelativeLayout) v.findViewById(R.id.sidebar);
        mDetector = new GestureDetector(getActivity(), new MyGestureListener());
        v.setOnTouchListener(this);

        if (viewModel.getOrienr().getValue().equals(UiUtils.PORTRAIT)) {
            //vertical
            if (viewModel.getDevice().getValue().equals(UiUtils.TABLET)) {
                v.getLayoutParams().width =  viewModel.getWight().getValue()/3+100;

            } else {
                v.getLayoutParams().width =  viewModel.getWight().getValue()/2+70;
            }

        } else {
            //horizontal
            if (viewModel.getDevice().getValue().equals(UiUtils.TABLET)) {
                v.getLayoutParams().width =  viewModel.getWight().getValue()/3;

            } else {
                v.getLayoutParams().width =  viewModel.getWight().getValue()/2;
            }

        }
        //TODO configure the top margin dinamically depending on the size of the buttons container of the map fragment

//        ll = (LinearLayout)v.findViewById(R.id.test_layout);
//        scrollView = (ScrollView) v.findViewById(R.id.scrollV);
        listFilter1 =  v.findViewById(R.id.filter_list);
        header = v.findViewById(R.id.header);

        listFilter1.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (viewModel.getDataToFilter().getValue()!=null) {
                    if (viewModel.getDataToFilter().getValue().size()>0) {
                        try {
                            View child = listFilter1.getChildAt(0);
                            Log.i(TAG, "top view: "+ child.getTag().toString());
                            viewModel.updateY(viewModel.getDataToFilter().getValue()
                                    .indexOf(child.getTag().toString()));
                        } catch (Exception e) {

                        }
                    }
                }

            }
        });

        clearAll = v.findViewById(R.id.clearAll);
        hide = v.findViewById(R.id.hideSidebar);

        hide.setImageResource(R.drawable.forward);
        hide.getBackground().setAlpha(0);
        hide.setColorFilter(new PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN));

        clearAll.setImageResource(R.drawable.clear);
        clearAll.setColorFilter(new PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN));
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
                viewModel.updateNamesToShowInScroll(null);
                viewModel.updateRatings(null);
                viewModel.updateY(null);

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
                    configureFilters(sideBar);
                }

                //TODO declare the listener in sidebar to change the content
            }
        };
        viewModel.getDataToFilter().observe(this, scrollDataObserver);

        final Observer<LinkedList<String>> selectedFiltersObserver = new Observer<LinkedList<String>>() {

            @Override
            public void onChanged(@Nullable final LinkedList<String> selectedF) {

                Log.i(TAG, "enter onChanged selectedFiltersObserver(@Nullable final LinkedList<String> filt)");
                if (selectedF!=null) {
                    FilterListviewAdapter.setCurrenrFilter(MapFragment.FILTERS);
                    //TODO to provide the updated list of selected filters to ListViewAdaptor
                    FilterListviewAdapter.updateListOfSelectedFilters(selectedF);
                }
            }
        };
        viewModel.getNamesToShowInScroll().observe(this, selectedFiltersObserver);

        final Observer<LinkedList<String>> ratingsObserver = new Observer<LinkedList<String>>() {

            @Override
            public void onChanged(@Nullable final LinkedList<String> selectedF) {

                Log.i(TAG, "enter onChanged ratingsObserver(@Nullable final LinkedList<String> filt)");
                if (selectedF!=null) {
                    FilterListviewAdapter.setCurrenrFilter("RATING");
                    //TODO to provide the updated list of selected filters to ListViewAdaptor
                    FilterListviewAdapter.setRating(selectedF);
                }
            }
        };
        viewModel.getRatings().observe(this, ratingsObserver);

        if (viewModel.getCurrentFilter().getValue()!=null) {
            configureFilters(sideBar);
        }


        //TODO making the sidebar draggable

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

    @Override
    public boolean onTouch(View v, MotionEvent motionEvent) {
        // pass the events to the gesture detector
        // a return value of true means the detector is handling it
        // a return value of false means the detector didn't
        // recognize the event
        Log.i(TAG, "enter onTouch(View v, MotionEvent motionEvent)");

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
               mInitialX = motionEvent.getRawX();

                break;

            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "x: "+motionEvent.getRawX());
                float deltaX = motionEvent.getRawX() - mInitialX;
                if (Math.abs(deltaX) > 30)
                {
                    // Left to Right swipe action
                    if (motionEvent.getRawX() > mInitialX)
                    {
                        viewModel.upateShowSidebar(false);
                    }

                }

                break;

            case MotionEvent.ACTION_UP:
                Log.i(TAG, "up");
               // mMovingView = null;
                break;
        }

        return true;
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
        if (viewModel.getNamesToShowInScroll().getValue()==null) {
            viewModel.updateNamesToShowInScroll(new LinkedList<String>());
        }
        if (viewModel.getRatings().getValue()==null) {
            viewModel.updateRatings(new LinkedList<String>());
        }
       // restartedOrRecreated=false;
        sideBar.removeAllViews();
        //TODO adding back the views including tho bottom element
        try {
            sideBar.addView(header);
            header.setPadding(0,0,
                    0,0);
            UiUtils.setTextSize(viewModel.getHight().getValue(), header,
                    viewModel.getOrienr().getValue(), true);
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
            //toppest element
            TextView text1 = new TextView(getActivity());
            text1.setText("Rating:");
            text1.setPadding(0,0,
                    0,0);
            UiUtils.setTextSize(viewModel.getHight().getValue(), text1,
                    viewModel.getOrienr().getValue(), true);
            text1.setId(R.id.rating_header_id);
            RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            sideBar.addView(text1);

            //TODO second element under the toppest
            listFilter2 = new ListView(getActivity());

            final String [] symbols = {"3", "2", "1"};
            LinkedList<String> tmp = new LinkedList<>();
            for (String s: symbols) {
                tmp.add(s);
            }
            FilterListviewAdapter adapter2 = new FilterListviewAdapter(tmp, getActivity(), viewModel.getDevice().getValue(),
                    viewModel.getHight().getValue(), viewModel.getWight().getValue());
            listFilter2.setAdapter(adapter2);
            RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp2.addRule(RelativeLayout.BELOW, text1.getId());

            listFilter2.setLayoutParams(lp2);
            if(viewModel.getOrienr().getValue().equals(UiUtils.LANDSCAPE)) {
                lp2.width = 150;
            }
            listFilter2.setId(R.id.rating_listView_id);
            listFilter2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Log.d(TAG, "enter applyRatingListener(View view)");
                    String sortedBy = "";
                    if(position==0) {
                        sortedBy="3";
                    } else if(position==1) {
                        sortedBy="2";
                    } else {
                        sortedBy="1";
                    }
                    LinkedList<String> tmp1 = new LinkedList<>();
                    tmp1.addAll(viewModel.getRatings().getValue());

                    for (int i = 0; i < 3; i++) {
                        if(position == i){
                            Log.i(TAG, "element found");
                            if (viewModel.getRatings().getValue().contains(sortedBy)) {

                                tmp1.remove(sortedBy);
                                view.setBackgroundColor(Color.WHITE);
                            } else {
                                tmp1.add(sortedBy);
                                view.setBackgroundColor(Color.LTGRAY);
                            }

                            break;
                        }

                    }
                    viewModel.updateRatings(tmp1);

                    Log.i(TAG, "rating selected: "+ sortedBy);

                    LinkedList<String> tmp = new LinkedList<>();
                    if (viewModel.getRatings().getValue().size()==0 && viewModel.getNamesToShowInScroll().getValue().size()==0) {
                        tmp.addAll(
                                viewModel.getNamesSortedByRating().getValue());
                    }
                    else if (viewModel.getNamesToShowInScroll().getValue().size()>0 && viewModel.getRatings().getValue().size()>0) {
                        for (Place p : viewModel.getRecievedPoints().getValue()) {
                            if (viewModel.getNamesToShowInScroll().getValue().contains(p.getCategory())
                                    && viewModel.getRatings().getValue().contains(String.valueOf(p.getRating()))) {
                                tmp.add(p.getName());
                            }
                        }
                    } else if (viewModel.getRatings().getValue().size()==0 && viewModel.getNamesToShowInScroll().getValue().size()>0){
                        for (Place p : viewModel.getRecievedPoints().getValue()) {
                            if (viewModel.getNamesToShowInScroll().getValue().contains(p.getCategory())) {
                                tmp.add(p.getName());
                            }
                        }

                    } else if (viewModel.getRatings().getValue().size()>0 && viewModel.getNamesToShowInScroll().getValue().size()==0) {
                        for (Place p : viewModel.getRecievedPoints().getValue()) {
                            if (viewModel.getRatings().getValue().contains(String.valueOf(p.getRating()))) {
                                tmp.add(p.getName());
                            }
                        }
                    }
                    viewModel.updatePointsToShow(tmp);

                }
            });

            sideBar.addView(listFilter2);

            // third element under the toppest
            header.setText("Categories:");

            header.setId(R.id.categories_header_id);
            RelativeLayout.LayoutParams lp3 = (RelativeLayout.LayoutParams)header.getLayoutParams();
            if(viewModel.getOrienr().getValue().equals(UiUtils.LANDSCAPE)) {
                lp3.addRule(RelativeLayout.RIGHT_OF, text1.getId());
                lp3.addRule(RelativeLayout.ALIGN_LEFT, listFilter1.getId());
                header.setPadding(15,0,
                        0,0);

            } else {
                lp3.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
                lp3.addRule(RelativeLayout.BELOW, listFilter2.getId());
            }
            header.setLayoutParams(lp3);

            // last element under the toppest
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) listFilter1.getLayoutParams();
            if(viewModel.getOrienr().getValue().equals(UiUtils.LANDSCAPE)) {
                lp.addRule(RelativeLayout.RIGHT_OF, listFilter2.getId());
            }
            lp.addRule(RelativeLayout.BELOW, header.getId());
            listFilter1.setLayoutParams(lp);


        } else if (viewModel.getCurrentFilter().getValue().equals(MapFragment.ALL)) {
//            if (viewModel.getSelectedFiltersForAll().getValue()!=null) {
//                Log.i(TAG, "storing values for all: "+ viewModel.getSelectedFiltersForAll().getValue().size());
//                selectedFilters.addAll(viewModel.getSelectedFiltersForAll().getValue());
//            }
            header.setText("All points:");

        } else if (viewModel.getCurrentFilter().getValue().equals(MapFragment.LOVED)) {
            if (viewModel.getLoved().getValue()==null || viewModel.getLoved().getValue().size()==0) {

                header.setText("You have not selected any points yet. To add point to your collection, expand the info window of the marker and click the heart icon");
               // ll.addView(txt);
                //return;
            } else {
//                if (viewModel.getSelectedFiltersForLoved().getValue()!=null) {
//                    selectedFilters.addAll(viewModel.getSelectedFiltersForLoved().getValue());
//                }
                header.setText("Selected");
            }
        }

        ArrayList <String> tmp = new ArrayList<>(viewModel.getDataToFilter().getValue().size());
        tmp.addAll(viewModel.getDataToFilter().getValue());
        FilterListviewAdapter adapter = new FilterListviewAdapter(viewModel.getDataToFilter().getValue(), getActivity(), viewModel.getDevice().getValue(),
                viewModel.getHight().getValue(), viewModel.getWight().getValue());
        listFilter1.setAdapter(adapter);
        listFilter1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            //TODO!!!!!!!!!!!!!!!!!!!!!!!!!
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "enter onItemClick(AdapterView<?> parent, View view, int position, long id)");
                String checked = viewModel.getDataToFilter().getValue().get(position);//((CheckBox) v).getTag().toString();
                Log.i(TAG, "position" +position);
                LinkedList<String> temp = new LinkedList<>();
                try {
                    temp.addAll(viewModel.getNamesToShowInScroll().getValue());
                }catch (NullPointerException e) {

                }

                for (int i = 0; i < viewModel.getDataToFilter().getValue().size(); i++) {
                    if(position == i){
                        Log.i(TAG, "element found");
                        Log.i(TAG, "index" +i);
                        if (temp.contains(checked)) {
                            temp.remove(checked);
                            view.setBackgroundColor(Color.WHITE);
                        } else {
                            temp.add(checked);
                            view.setBackgroundColor(Color.LTGRAY);
                        }

//                        }else{
//                            listFilter2.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                        break;
                    }
                }
                viewModel.updateNamesToShowInScroll(temp);
                Log.i(TAG, "selected filters: "+ viewModel.getNamesToShowInScroll().getValue().size());

                //TODO create the logic to modify the list of points to show
                LinkedList<String> list = new LinkedList<>();

                if (viewModel.getNamesToShowInScroll().getValue().size() == 0 &&
                        viewModel.getRatings().getValue().size() == 0) {
                    for (Place p : viewModel.getRecievedPoints().getValue()) {
                        list.add(p.getName());
                    }
                } else {

                    switch (viewModel.getCurrentFilter().getValue()) {
                        case MapFragment.FILTERS:
                            //TODO move this to separate activities start methods
                            if (viewModel.getRatings().getValue().size() > 0 && viewModel.getNamesToShowInScroll().getValue().size() > 0) {
                                for (Place p : viewModel.getRecievedPoints().getValue()) {
                                    if (viewModel.getNamesToShowInScroll().getValue().contains(p.getCategory())
                                            && viewModel.getRatings().getValue().contains(String.valueOf(p.getRating()))) {
                                        list.add(p.getName());
                                    }
                                }
                            } else if (viewModel.getRatings().getValue().size() > 0 && viewModel.getNamesToShowInScroll().getValue().size() == 0) {
                                for (Place p : viewModel.getRecievedPoints().getValue()) {
                                    if (viewModel.getRatings().getValue().contains(String.valueOf(p.getRating()))) {
                                        list.add(p.getName());
                                    }
                                }

                            } else if (viewModel.getRatings().getValue().size() == 0 && viewModel.getNamesToShowInScroll().getValue().size() > 0) {
                                for (Place p : viewModel.getRecievedPoints().getValue()) {
                                    if (viewModel.getNamesToShowInScroll().getValue().contains(p.getCategory())) {
                                        list.add(p.getName());
                                    }
                                }
                            }

                            break;
                        case MapFragment.LOVED:
                            for (Place p : viewModel.getRecievedPoints().getValue()) {
                                if (viewModel.getNamesToShowInScroll().getValue().contains(p.getName())) {
                                    list.add(p.getName());
                                }
                            }
                            break;
                        case MapFragment.ALL:
                            for (Place p : viewModel.getRecievedPoints().getValue()) {
                                if (viewModel.getNamesToShowInScroll().getValue().contains(p.getName())) {
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
        try {
            outState.putInt("LIST_VIEW_POSIT", viewModel.getScrollY().getValue());

        } catch (Exception e) {
            outState.putInt("LIST_VIEW_POSIT", 0);
        }

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
        if (restartedOrRecreated==true) {
            Log.d(TAG, "scroling to posit");
            listFilter1.setSelection(listViewTopPosit);
            listViewTopPosit=0;
            restartedOrRecreated=false;
        }
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
        restartedOrRecreated=true;
        try {
            listViewTopPosit=viewModel.getScrollY().getValue();
        } catch (Exception e) {

        }
        Log.d(TAG, "exit onStop()");
    }

    public void onSwipeLeft() {
    }

    public void onSwipeRight() {
    }

    // In the SimpleOnGestureListener subclass you should override
    // onDown and any other gesture that you want to detect.
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_DISTANCE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d("TAG","onDown: ");

            // don't return false here or else none of the other
            // gestures will work
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i("TAG", "onSingleTapConfirmed: ");
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.i("TAG", "onLongPress: ");
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i("TAG", "onDoubleTap: ");
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            Log.i("TAG", "onScroll: ");
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d("TAG", "onFling: ");
            float distanceX = event2.getX() - event1.getX();
            float distanceY = event2.getY() - event1.getY();
            if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (distanceX > 0)
                    onSwipeRight();
                else
                    onSwipeLeft();
                return true;
            }
            return true;
        }

    }

}
