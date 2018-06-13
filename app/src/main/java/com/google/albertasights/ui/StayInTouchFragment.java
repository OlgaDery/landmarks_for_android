package com.google.albertasights.ui;

import android.annotation.TargetApi;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.albertasights.R;
import com.google.albertasights.models.User;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StayInTouchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StayInTouchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StayInTouchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private UserViewModel viewModel;
    private AdView mAdView;
    private static final String TAG = StayInTouchFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;

    public StayInTouchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StayInTouchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StayInTouchFragment newInstance(String param1, String param2) {
        StayInTouchFragment fragment = new StayInTouchFragment();
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
    @TargetApi(22)
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "enter onCreateView(LayoutInflater inflater, ViewGroup container,\n" +
                "                             Bundle savedInstanceState)");
        View view = inflater.inflate(R.layout.fragment_stay_in_touch, container, false);
        viewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
        view.setTranslationZ(10.0f);
        view.setElevation(5.0f);
        RelativeLayout.LayoutParams lp;
        if (viewModel.getOrienr().getValue().equals(UiUtils.LANDSCAPE)) {
            Log.i(TAG, "recounting the size");
            lp = new RelativeLayout.LayoutParams(viewModel.getWight().getValue()/100*80,
                    viewModel.getHight().getValue()/100*60);
            lp.addRule(Gravity.LEFT);
            view.setLayoutParams(lp);

        } else {
            if (viewModel.getDevice().getValue().equals(UiUtils.TABLET)) {
                lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        viewModel.getHight().getValue()/100*80);
            } else {
                lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        viewModel.getHight().getValue()/100*70);
            }
            view.setLayoutParams(lp);
        }

        MobileAds.initialize(getActivity(), "ca-app-pub-9273347200561604~7518194920");
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

     //   email = (TextView) view.findViewById(R.id.email_data);
        TextView companyMail = (TextView) view.findViewById(R.id.our_email);
        TextView presentation = (TextView) view.findViewById(R.id.present);
        if (viewModel.getDevice().getValue().equals(UiUtils.TABLET)) {
            presentation.setTextSize(getActivity().getResources().getDimension(R.dimen.big_textsize));
            companyMail.setTextSize(getActivity().getResources().getDimension(R.dimen.big_textsize));

            if (viewModel.getOrienr().getValue().equals(UiUtils.LANDSCAPE)) {
                view.setPadding(viewModel.getHight().getValue()/30, viewModel.getHight().getValue()/30
                        , viewModel.getHight().getValue()/30, viewModel.getHight().getValue()/30);
            } else {
                view.setPadding(viewModel.getWight().getValue()/30, viewModel.getWight().getValue()/30
                        , viewModel.getWight().getValue()/30, viewModel.getWight().getValue()/30);
            }
        } else {
            if (viewModel.getOrienr().getValue().equals(UiUtils.PORTRAIT)) {
             //   email.getLayoutParams().width = viewModel.getWight().getValue()/100*60;
                companyMail.getLayoutParams().width = viewModel.getWight().getValue()/100*60;
            } else {
             //   email.getLayoutParams().width = viewModel.getWight().getValue()/100*65;
                companyMail.getLayoutParams().width = viewModel.getWight().getValue()/100*60;
            }
        }

//        ImageButton button1 = (ImageButton)view.findViewById(R.id.updt_button);
//        button1.setImageResource(R.drawable.edit);
//        button1.getBackground().setAlpha(0);
//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                viewModel.updateEmailFormRequested(true);
//            }
//        });

        ImageButton button2 = (ImageButton)view.findViewById(R.id.send_email);
        button2.getBackground().setAlpha(0);
        button2.setImageResource(R.drawable.email);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO send email to the company
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                String [] to  = {"your.naviguide@gmail.com"};
                emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Request");
                //add selected records as a text
                emailIntent.setType("message/rfc822");
                try {
                    startActivity(Intent.createChooser(emailIntent, "Send Email"));
                } catch (android.content.ActivityNotFoundException ex) {
                    UiUtils.showToast(getActivity(), "There are no email apps installed.");
                }
            }
        });
//
//        if (viewModel.getUser().getValue()==null) {
//            email.setText("We will be happy to get your email! Just click on the icon.");
//        } else {
//            email.setText("Your email: " +viewModel.getUser().getValue().getEmail());
//        }

        final Observer<User> userObserver = new Observer<User>() {
            @Override
            public void onChanged(@Nullable final User updatedUser) {
                // Update the UI.
                if (updatedUser!=null) {
                    updateUI();
                }
            }
        };
        viewModel.getUser().observe(this, userObserver);

        return view;
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

    private void updateUI () {
      //  email.setText("Your email: "+viewModel.getUser().getValue().getEmail());

    }
}
