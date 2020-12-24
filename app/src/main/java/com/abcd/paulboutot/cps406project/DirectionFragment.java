package com.abcd.paulboutot.cps406project;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DirectionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DirectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DirectionFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText startLocation;
    private EditText finalDestination;
    private Boolean hidden;

    private OnFragmentInteractionListener mListener;

    public DirectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DirectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DirectionFragment newInstance(String param1, String param2) {
        DirectionFragment fragment = new DirectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        startLocation = (EditText)getActivity().findViewById(R.id.starting_location);
        finalDestination = (EditText)getActivity().findViewById(R.id.final_destination);
        hidden = true;

        finalDestination.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    getUserInput();
                }
                return false;
            }
        });
    }


    public Boolean componentHidden() {
        return hidden;
    }

    public void hideComponents() {
        FrameLayout r = (FrameLayout) getActivity().findViewById(R.id.direction_ui);
        r.setVisibility(View.GONE);
        hidden = true;
    }

    public void showComponents() {
        FrameLayout r = (FrameLayout) getActivity().findViewById(R.id.direction_ui);
        r.setVisibility(View.VISIBLE);
        hidden = false;
    }

    public void getUserInput(){
        String s1 = startLocation.getText().toString().trim();
        String s2 = finalDestination.getText().toString().trim();

        // Checks if either of the text boxes are empty.
        if (s1.isEmpty() || s2.isEmpty()) {
            Toast.makeText(getActivity().getApplicationContext(), "Please put building code, name, or address.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the map fragment.
        MapFragment mapFragment =((MainActivity)getActivity()).getMapFragment();
        mapFragment.clearMap();

        // Get the database.
        naviDB data = ((MainActivity)getActivity()).getDatabase();
        HashMap<String, Object> locations = data.locations;

        LatLng coordinates1 = null;
        LatLng coordinates2 = null;

        // Searches for a matching location in the database.
        for (Map.Entry<String, Object> entry : locations.entrySet()) {
            Location location = (Location) entry.getValue();

            // Checks if the first location is equal to the building code, building name, address, or coordinates.
            if ( s1.equalsIgnoreCase(location.getBuildingCode()) || s1.equalsIgnoreCase(location.getBuildingName()) || s1.equalsIgnoreCase(location.getAddress()) || s1.equalsIgnoreCase(location.getCoordinates()) ) {

                // sets the coordinate of this location.
                String[] strings = location.getCoordinates().split(",");
                coordinates1 = new LatLng( Double.parseDouble(strings[0]), Double.parseDouble(strings[1]) );
            }

            // Checks if the second location is equal to the building code, building name, address, or coordinates.
            if ( s2.equalsIgnoreCase(location.getBuildingCode()) || s2.equalsIgnoreCase(location.getBuildingName()) || s2.equalsIgnoreCase(location.getAddress()) || s2.equalsIgnoreCase(location.getCoordinates()) ) {

                // sets the coordinate of this location.
                String[] strings = location.getCoordinates().split(",");
                coordinates2 = new LatLng( Double.parseDouble(strings[0]), Double.parseDouble(strings[1]) );
            }
        }

        // Shows the path from one building to another.
        if (coordinates1 != null && coordinates2 != null) {
            mapFragment.setOrigin(coordinates1, true);
            mapFragment.setDestination(coordinates2, true);
            mapFragment.showPathWithAlternateRoutes();
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Input not valid, please put building code, name, or address.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_direction, container, false);
        ImageButton button = (ImageButton)view.findViewById(R.id.confirm);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                getUserInput();
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
}
