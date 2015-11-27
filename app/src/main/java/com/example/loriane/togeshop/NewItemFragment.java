package com.example.loriane.togeshop;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.loriane.togeshop.dummy.ItemsContent;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link NewItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewItemFragment extends Fragment implements ListView.OnItemClickListener {
    private OnFragmentInteractionListener mListener;

    public ArrayList<ItemCourse> getResearchedItems() {
        return researchedItems;
    }

    ArrayList<ItemCourse> researchedItems = new ArrayList<ItemCourse>();

    private LazySearchAdapter ItemSearchAdapter;
    private ListView mListView;

    public static NewItemFragment newInstance(String param1, String param2) {
        NewItemFragment fragment = new NewItemFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public NewItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_item, container, false);
        mListView = (ListView) view.findViewById(R.id.liste_searched_items);
        mListView.setOnItemClickListener(this);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
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
            e.printStackTrace();
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
            Log.d("SONPERE", "item " + researchedItems.get(position).getIdItem() + " est " + researchedItems.get(position).getTaken() + " et choisi par " + researchedItems.get(position).getChosen());
                if(!researchedItems.get(position).getTaken()) {
                    researchedItems.get(position).setChosen(Client.getClient().getUserName());
                    researchedItems.get(position).setTaken(true);
                    ItemSearchAdapter = new LazySearchAdapter(getActivity(), researchedItems);

                    mListView.setAdapter(ItemSearchAdapter);
                    if(ItemSearchAdapter!=null) {
                        ItemSearchAdapter.notifyDataSetChanged();
                    }
                }
                else{
                    researchedItems.get(position).setChosen("personne");
                    researchedItems.get(position).setTaken(false);
                    ItemSearchAdapter = new LazySearchAdapter(getActivity(), researchedItems);

                    mListView.setAdapter(ItemSearchAdapter);
                    if(ItemSearchAdapter!=null) {
                        ItemSearchAdapter.notifyDataSetChanged();
                    }
                }
           // mListener.onFragmentInteraction(String.valueOf(ItemsContent.ITEMS.get(position).id));
        }
    }

    public Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp2.getWidth(), bmp2.getHeight(), bmp1.getConfig());
        float left =(bmp2.getWidth() - (bmp1.getWidth()*((float)bmp2.getHeight()/(float)bmp1.getHeight())))/(float)2.0;
        float bmp1newW = bmp1.getWidth()*((float)bmp2.getHeight()/(float)bmp1.getHeight());
        Bitmap bmp1new = getResizedBitmap(bmp1, bmp2.getHeight(), (int)bmp1newW);
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1new, left ,0 , null);
        canvas.drawBitmap(bmp2, new Matrix(), null);
        return bmOverlay;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        if (bm != null && !bm.isRecycled()) {
            //  bm.recycle();
            bm = null;
        }
        //bm.recycle();
        return resizedBitmap;
    }

    public void refresh(ArrayList<ItemCourse> pouet) {
        researchedItems = new ArrayList<>(pouet);
        new GetSearchedItemsTask().execute();
    }

    public class GetSearchedItemsTask extends AsyncTask<Void, Void, Boolean> {


        GetSearchedItemsTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d("SONPERE","j'ai lancé l'AsyncTask");
                for (int i=0;i<researchedItems.size();i++) {
                    try {
                        URL url = new URL(researchedItems.get(i).getURL());
                        Log.d("SONPERE", "j'ai l'url " + url.toString());
                        InputStream content = (InputStream) url.getContent();
                        Log.d("SONPERE", "je créé donc le drawable a partir de " + content.equals(null));
                        Bitmap image = BitmapFactory.decodeStream(content);
                        if (image != null & content != null) {
                            researchedItems.get(i).setImageitem(Bitmap.createScaledBitmap(image, 150, 150, false));
                        } else {
                            researchedItems.get(i).setImageitem(BitmapFactory.decodeResource(getResources(), R.drawable.no_image));
                        }
                        researchedItems.get(i).setImageitemCheck(overlay(researchedItems.get(i).getImageitem(), BitmapFactory.decodeResource(getResources(), R.drawable.check_vert)));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("SONPERE", "fini d'ajouter l'item " + i);
                    Log.d("SONPERE", "aye c'est fini");
                    //pouet.setLoadingFinished(true);
                }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Log.d("SONPERE","Cchuis en poste execute");
            //((Detail_liste)getActivity()).getSearchSpinner().setVisibility(View.GONE);
            ((Button) getActivity().findViewById(R.id.ysapellepas)).setVisibility(View.VISIBLE);
            ItemSearchAdapter = new LazySearchAdapter(getActivity(), researchedItems);

            mListView.setAdapter(ItemSearchAdapter);
            if(ItemSearchAdapter!=null) {
                ItemSearchAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onCancelled() {
        }
    }

}
