package com.example.loriane.togeshop;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;

import com.example.loriane.togeshop.dummy.ListesContent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private LatLng locationEvent;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
        }
    }

    private final static LatLng GARDANNE = new LatLng(43.455669,5.47064899999998) ;
    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getAdress();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        SupportMapFragment supportMapFragment = ((SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map));
        GoogleMap mMap = supportMapFragment.getMap();

        if (mMap != null) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mMap.animateCamera(CameraUpdateFactory.newLatLng(locationEvent), 20000, null);
            mMap.addMarker(new MarkerOptions().position(locationEvent));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public boolean getAdress() {

        getAdressTask nuage = new getAdressTask();
        nuage.execute();
        return true;
    }

    public class getAdressTask extends AsyncTask<Void, Void, Boolean> {
        String text;
        getAdressTask() {
            text = ListesContent.ITEMS.get(Client.getClient().getIdCurrentList()).adresse;
            text = text.replace(" ","%20");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean retour = false;
            String request = "http://maps.googleapis.com/maps/api/geocode/json?address="+text+"&sensor=true";
            System.out.println("je cherche l'adresse");
            try {
                String result = Client.getHTML(request);
                JSONObject addressData = new JSONObject(result);
                double lat = addressData.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                double lng = addressData.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                locationEvent = new LatLng(lat,lng);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return retour;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(success){
            }
        }

        @Override
        protected void onCancelled() {
        }
    }
}
