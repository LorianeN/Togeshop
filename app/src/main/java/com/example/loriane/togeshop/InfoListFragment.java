package com.example.loriane.togeshop;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.example.loriane.togeshop.OnFragmentInteractionListener;
import com.example.loriane.togeshop.R;
import com.example.loriane.togeshop.dummy.ListesContent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class InfoListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private boolean finished=false;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InfoListFragment newInstance(String param1, String param2) {
        InfoListFragment fragment = new InfoListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public InfoListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

//    @Override
//    public void onActivityCreated (Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//        //SupportMapFragment supportMapFragment = (SupportMapFragment)
//        fragmentManager.findFragmentById(R.id.map);
//        SupportMapFragment supportMapFragment = ((SupportMapFragment)
//                getChildFragmentManager()
//                        .findFragmentById(R.id.map));
//        GoogleMap mMap = supportMapFragment.getMap();
//        if (mMap != null) {
//            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//            mMap.animateCamera(CameraUpdateFactory.newLatLng(GARDANNE), 20000, null);
//        }
//    }
    private final static LatLng GARDANNE = new LatLng(43.455669,5.47064899999998) ;
    private LatLng locationEvent=new LatLng(43.455669,5.47064899999998);
    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getAdress();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        SupportMapFragment supportMapFragment = ((SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map));
        GoogleMap mMap = supportMapFragment.getMap();

        if (mMap != null) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

            finished = false;
            mMap.animateCamera(CameraUpdateFactory.newLatLng(locationEvent), 20000, null);
            mMap.addMarker(new MarkerOptions().position(locationEvent));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = new View(getActivity());
        // Inflate the layout for this fragment
        try {
            view = inflater.inflate(R.layout.fragment_info_list, container, false);
            ((TextView)getActivity().findViewById(R.id.onvalechercher)).setText("Informations sur la liste "+ Client.getClient().getNameCurrentList());
            ((TextView)getActivity().findViewById(R.id.descriptionRepas)).setText(ListesContent.ITEMS.get((Client.getClient().getIdCurrentList())-1).description);
            ((TextView) getActivity().findViewById(R.id.dateRepas)).setText(ListesContent.ITEMS.get((Client.getClient().getIdCurrentList())-1).date);
        }catch(Exception e){
            e.printStackTrace();
        }
        return view;
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

    public boolean getAdress() {

//        getAdressTask nuage = new getAdressTask();
//        nuage.execute();
        String request = "http://maps.googleapis.com/maps/api/geocode/json?address="+ListesContent.ITEMS.get((Client.getClient().getIdCurrentList())-1).adresse+"&sensor=true";
        request = request.replace(" ","%20");
        Client.getClient().execHTMLRequete(request);
        JSONObject addressData = null;
        try {
            addressData = new JSONObject(Client.getClient().resultSearch.toString());
            Log.d("SONPERE", "j'ai un JSON résultat");
            double lat = addressData.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
            double lng = addressData.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
            locationEvent = new LatLng(lat,lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }

}
