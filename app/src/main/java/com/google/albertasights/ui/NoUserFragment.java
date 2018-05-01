package com.google.albertasights.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.albertasights.DBIntentService;
import com.google.albertasights.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NoUserFragment.OnButtonClickedListener} interface
 * to handle interaction events.
 * Use the {@link NoUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoUserFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView text;
    private ImageView image;
    private Button regBtn;
    private Button logInButton;
    private String deviceType;

    private OnButtonClickedListener mListener;

    public NoUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NoUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NoUserFragment newInstance(String param1, String param2) {
        NoUserFragment fragment = new NoUserFragment();
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
        View view = inflater.inflate(R.layout.fragment_no_user, container, false);
        final EditText email = (EditText) view.findViewById(R.id.email);
        final EditText password = (EditText) view.findViewById(R.id.password);
        text = (TextView) view.findViewById(R.id.txt_user);
        regBtn = (Button)view.findViewById(R.id.reg);
        regBtn.setTag(UiUtils.CREATE_USER);
        logInButton = (Button)view.findViewById(R.id.log_button);
        logInButton.setTag(UiUtils.LOG_IN);

        WindowManager wm = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int screen_height = metrics.heightPixels;
        int screen_width = metrics.widthPixels;
        String orientation = UiUtils.getOrientation(getActivity());
        deviceType = UiUtils.findScreenSize(getActivity());
        if (deviceType.equals("tablet")) {
            text.setTextSize(getActivity().getResources().getDimension(R.dimen.avg_header));
        } else {
            text.setTextSize(getActivity().getResources().getDimension(R.dimen.big_textsize));
        }

        View.OnClickListener lstn = new View.OnClickListener() {
            public void onClick(View view) {
                //   Log.d(TAG, "enter showFilters(View view)");
              Intent intent = new Intent(getActivity().getApplicationContext(), DBIntentService.class);
              if (view.getTag().equals(UiUtils.LOG_IN)) {
                  Log.d(NoUserFragment.class.getCanonicalName(), "Log in event");

                  intent.setAction(UiUtils.LOG_IN);
                  intent.putExtra(UiUtils.EMAIL, email.getText().toString());
                  intent.putExtra(UiUtils.PASSWORD, password.getText().toString());
                  getActivity().startService(intent);
              } else {
                 // intent.setAction(UiUtils.CREATE_USER);
                  Log.d(NoUserFragment.class.getCanonicalName(), "Create event");
              }

                mListener.onLogInOrRegisterButtonClickedListener((String)view.getTag());

                //    Log.d(TAG, "exit showFilters(View view)");
            }
        };
        regBtn.setOnClickListener(lstn);
        logInButton.setOnClickListener(lstn);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnButtonClickedListener) {
            mListener = (OnButtonClickedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onLogInOrRegisterButtonClickedListener");
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
    public interface OnButtonClickedListener {
        // TODO: Update argument type and name
        void onLogInOrRegisterButtonClickedListener(String action);
    }
}
