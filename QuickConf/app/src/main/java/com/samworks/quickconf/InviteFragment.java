package com.samworks.quickconf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InviteFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InviteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InviteFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View _view;

    private static final String DEBUG_TAG = "QuickC";
    private static final int CONTACT_PICKER_RESULT = 1001;

    ImageButton _btnWhatsapp;
    ImageButton _btnAddIntivees;
    TextView _lstInvitees;
    ArrayList<String> _arr = new ArrayList<>();



    private OnFragmentInteractionListener mListener;

    public InviteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InviteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InviteFragment newInstance(String param1, String param2) {
        InviteFragment fragment = new InviteFragment();
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
        _view = inflater.inflate(R.layout.fragment_invite, container, false);

        _btnWhatsapp  = (ImageButton)_view.findViewById(R.id.btnwhatsapp);
        _btnWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.callintext));
                sendIntent.setType("text/plain");
                sendIntent.setPackage("com.whatsapp");
                startActivity(sendIntent);
                PushToCloudant();
            }
        });

        _btnAddIntivees = (ImageButton) _view.findViewById(R.id.btnAddInvtee);
        _btnAddIntivees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK);
                contactPickerIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);

            }
        });

        _lstInvitees = (TextView) _view.findViewById(R.id.lstInvitees);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }

        return _view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.w(DEBUG_TAG, "in onActivityResult");
        Log.w(DEBUG_TAG, " COMPARE" + ((String.valueOf(resultCode == Activity.RESULT_OK))));
        if (resultCode == Activity.RESULT_OK) {
            Log.w(DEBUG_TAG, "in Compare");
            switch (requestCode) {
                case CONTACT_PICKER_RESULT:
                    Uri contactUri = data.getData();
                    Cursor cursor = _view.getContext().getContentResolver().query(contactUri, null, null, null, null);
                    cursor.moveToFirst();
                    int numColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int nameColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

                    String num = cursor.getString(numColumn);

                    num = num.replaceAll("-","");
                    num = num.replaceAll(" ","");
                    num = num.substring(num.length() -10,num.length());

                    String name = cursor.getString(nameColumn);

                    Log.d("phone number",num);
                    Log.d("name", name);


                    //_arr.add("{ phnum : " + cursor.getString(numColumn) + ", name : " + cursor.getString(nameColumn) + ", confId : " + getString(R.string.confId) + "}");
                    _arr.add(num + "," + name);

                    _lstInvitees.setText(_lstInvitees.getText() + cursor.getString(nameColumn) +" ; ");

                    // handle contact results
                    break;
            }

        } else {
            // gracefully handle failure
            Log.w(DEBUG_TAG, "Warning: activity result not ok");
        }
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
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void PushToCloudant() {
        JSONObject jsonObj = new JSONObject();
        JSONArray jsonArr = new JSONArray();

        try {




            for (String str:_arr) {
                JSONObject member = new JSONObject();
                String[] strArr = str.split(",");
                member.put("phnum", (strArr[0]));
                member.put("name",strArr[1]);
                member.put("confId",(getString(R.string.confId)));

                jsonArr.put(member);
            }

            jsonObj.put("phnum",(getString(R.string.userPhoneNumber)));
            jsonObj.put("name",getString(R.string.userName));
            jsonObj.put("confId",(getString(R.string.confId)));

            jsonObj.put("members",jsonArr);


        }
        catch (JSONException ex)
        {
            Log.e("QuickC", "PushToCloudant ERROR: " + ex.getMessage(),ex );
        }

        HttpURLConnection urlConnection;

        try {

            URL url = new URL(getString(R.string.registerURL));
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.connect();

            //Create JSONObject here

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            String postData = jsonObj.toString();
            out.write(postData);
            out.close();


            int HttpResult =urlConnection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
