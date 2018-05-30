package com.google.albertasights.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.albertasights.DBIntentService;
import com.google.albertasights.R;
import com.google.albertasights.models.User;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EnterUserFragment.OnSubmitUserListener} interface
 * to handle interaction events.
 * Use the {@link EnterUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnterUserFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private UserViewModel viewModel;
    private EditText email;
    private EditText password;
    private EditText firstName;
    private EditText lastName;
    private Button submitBt;
    private Button changePasswordBt;
    private static final String TAG = EnterUserFragment.class.getSimpleName();
    private OnSubmitUserListener mListener;

    public EnterUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EnterUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EnterUserFragment newInstance(String param1, String param2) {
        EnterUserFragment fragment = new EnterUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "enter onCreate(Bundle savedInstanceState)");
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
        Log.d(TAG, "exit onCreate(Bundle savedInstanceState)");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "enter onCreateView");
        View view = inflater.inflate(R.layout.fragment_enter_user, container, false);
        email = (EditText) view.findViewById(R.id.email);
        password = (EditText) view.findViewById(R.id.password);
        firstName = (EditText) view.findViewById(R.id.first_name);
        lastName = (EditText) view.findViewById(R.id.last_name);

        if (viewModel.getUser().getValue()!=null) {
            email.setText(viewModel.getUser().getValue().getEmail());
            firstName.setText(viewModel.getUser().getValue().getFirstName());
            lastName.setText(viewModel.getUser().getValue().getLastName());
            TextView role = new TextView(getActivity());
            role.setText(viewModel.getUser().getValue().getFirstName());
            //TODO add to the view dinamically

        }
        submitBt = (Button) view.findViewById(R.id.submit);
        changePasswordBt = (Button) view.findViewById(R.id.updPassword);
        View.OnClickListener lstn = new View.OnClickListener() {
            public void onClick(View view) {

                String firstN = "Dear friend";
                String lastN = "No";

                if (firstName.getText().toString()!=null || (firstName.getText().toString().length()>0)) {
                    firstN = firstName.getText().toString();
                    Log.i(TAG, "first name: "+firstName.getText().toString());
                }
                if (lastName.getText().toString()!=null || (lastName.getText().toString().length()>0)) {
                    lastN = lastName.getText().toString();
                    Log.i(TAG, "last name: "+ lastName.getText().toString());
                }

                onButtonPressed(email.getText().toString(), firstN, lastN);

                //    Log.d(TAG, "exit showFilters(View view)");
            }
        };
        submitBt.setOnClickListener(lstn);

        return view;
    }

    public void onButtonPressed(String email, String fst, String lst) {
        Log.d(TAG, "enter onInfoViewExpanded");
        if (mListener != null) {
            // TODO: send data to IntentService
            mListener.onSubmitUser();
            Intent intent = new Intent(getActivity(), DBIntentService.class);
            intent.setAction(UiUtils.CREATE_USER);
            intent.putExtra(UiUtils.EMAIL, email);
          //  intent.putExtra(UiUtils.PASSWORD, password);
            intent.putExtra(UiUtils.FIRST_NAME, fst);
            intent.putExtra(UiUtils.LAST_NAME, lst);
            getActivity().startService(intent);
        }
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "enter onAttach(Context context)");
        super.onAttach(context);
        if (context instanceof OnSubmitUserListener) {
            mListener = (OnSubmitUserListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPointDataExtendedListener");
        }
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "enter onDetach()");
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
    public interface OnSubmitUserListener {
        // TODO: Update argument type and name
        void onSubmitUser();
    }
}
