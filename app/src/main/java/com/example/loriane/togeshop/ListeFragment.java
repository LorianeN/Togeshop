package com.example.loriane.togeshop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.loriane.togeshop.dummy.ListesContent;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ListeFragment extends Fragment implements AbsListView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    ArrayList<ListeCourse> jean = new ArrayList<>();
    private ChoixListe pouet;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ArrayAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static ListeFragment newInstance(Activity pouet) {
        ListeFragment fragment = new ListeFragment(pouet);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListeFragment() {
        new GetListTask().execute();
    }

    @SuppressLint("ValidFragment")
    public ListeFragment(Activity pouet) {
        this.pouet = (ChoixListe) pouet;
        new GetListTask().execute();
    }

    public  BaseAdapter getMAdapter() {
        return mAdapter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: Change Adapter to display your content
        mAdapter = new ArrayAdapter<ListesContent.DummyItem>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, ListesContent.ITEMS);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_liste, container, false);


        
        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        return view;
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            //parent.getChildAt(position).setBackgroundColor(Color.BLUE);
            Client.getClient().setIdCurrentList(ListesContent.ITEMS.get(position).id);
            Client.getClient().setNameCurrentList(ListesContent.ITEMS.get(position).content);
            mListener.onFragmentInteraction(String.valueOf(ListesContent.ITEMS.get(position).id));
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    public class GetListTask extends AsyncTask<Void, Void, Boolean> {
        GetListTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            NavigationController roger = new NavigationController();

            jean = roger.getListe();
            ListesContent.ITEMS.clear();
            ListesContent.ITEM_MAP.clear();
            for (int i =0;i<jean.size();i++){
                Log.d("SONPERE", "j'ai reçu "+ jean.get(i).getNom());
                ListesContent.addItem(new ListesContent.DummyItem(jean.get(i).getIdListe(), jean.get(i).getNom()));
            }
            pouet.setLoadingFinished(true);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

        }

        @Override
        protected void onCancelled() {
        }
    }
}
