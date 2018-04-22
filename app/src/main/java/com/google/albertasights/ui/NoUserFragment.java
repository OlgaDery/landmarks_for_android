package com.google.albertasights.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
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
import com.squareup.picasso.Picasso;

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
    private Button button1;
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
        EditText email = (EditText) view.findViewById(R.id.email);
        EditText passord = (EditText) view.findViewById(R.id.password);
        text = (TextView) view.findViewById(R.id.txt_user);
        button1 = (Button)view.findViewById(R.id.sign_in_button);
        logInButton = (Button)view.findViewById(R.id.reg_button);

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
              mListener.onButtonClickedListener();
                Intent intent = new Intent(getActivity().getApplicationContext(), DBIntentService.class);
                intent.setAction(UiUtils.CREATE_USER);
                intent.putExtra(UiUtils.EMAIL, "androgeny80@gmail.com");
                intent.putExtra(UiUtils.PASSWORD, "bla");
                intent.putExtra(UiUtils.FIRST_NAME, "Olga");
                intent.putExtra(UiUtils.LAST_NAME, "No");
                getActivity().startService(intent);
                //    Log.d(TAG, "exit showFilters(View view)");
            }
        };
        button1.setOnClickListener(lstn);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnButtonClickedListener) {
            mListener = (OnButtonClickedListener) context;
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
        void onButtonClickedListener();
    }
}
