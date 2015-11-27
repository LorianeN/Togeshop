package com.example.loriane.togeshop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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
import android.widget.ListView;
import android.widget.TextView;

import com.example.loriane.togeshop.dummy.ItemsContent;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ItemsFragment extends Fragment implements ListView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ArrayList<ItemCourse> jean = new ArrayList<>();

    NavigationController roger = new NavigationController();
    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private ListView mListView;

    ArrayList<ItemCourse> listItem = new ArrayList<ItemCourse>();
    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private LazyAdapter ItemAdapter;
    private ArrayAdapter mAdapter;
    private Detail_liste pouet =null;

    // TODO: Rename and change types of parameters
    public static ItemsFragment newInstance(Activity pouet) {
        Log.d("SONPERE","nouvelle instance d'Item Fragment");
        ItemsFragment fragment = new ItemsFragment(pouet);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ItemsFragment(){

    }
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     * @param pouet
     */
    @SuppressLint("ValidFragment")
    public ItemsFragment(Activity pouet) {
        //this.pouet=null;
        this.pouet = (Detail_liste) pouet;
        new GetItemsTask().execute();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SONPERE", "suis passé par le constructeur une fois");
        // TODO: Change Adapter to display your content
        mAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, ItemsContent.ITEMS);
        // TODO: Change Adapter to display your content
        ItemAdapter = new LazyAdapter(pouet, listItem);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_items, container, false);


        Log.d("SONPERE", "je créé la view de mon fragment list detail_liste");
        mListView = (ListView) view.findViewById(R.id.liste_items);
        mListView.setAdapter(ItemAdapter);
        Log.d("SONPERE","c'est fait (view Fragment item)!");
        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        ItemAdapter.notifyDataSetChanged();

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
            Log.d("SONPERE", "item " + listItem.get(position).getIdItem() + " est " + listItem.get(position).getTaken() + " et choisi par " + listItem.get(position).getChosen());
            if (listItem.get(position).getChosen().equals(Client.getClient().getUserName()) || listItem.get(position).getChosen().equals("personne")) {
                if(!listItem.get(position).getTaken()) {
                    listItem.get(position).setChosen(Client.getClient().getUserName());
                    listItem.get(position).setTaken(true);
                    ItemAdapter.notifyDataSetChanged();

                    roger.itemUpdated(true,listItem.get(position).getIdItem());
                }
                else{
                    listItem.get(position).setChosen("personne");
                    listItem.get(position).setTaken(false);
                    ItemAdapter.notifyDataSetChanged();
                    roger.itemUpdated(false, listItem.get(position).getIdItem());
                }
            }
                mListener.onFragmentInteraction(String.valueOf(ItemsContent.ITEMS.get(position).id));
        }
    }

    public void refresh(){
        pouet.spinner.setVisibility(View.VISIBLE);
        new GetItemsTask().execute();
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

    public class GetItemsTask extends AsyncTask<Void, Void, Boolean> {


        GetItemsTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d("SONPERE","j'ai lancé l'AsyncTask");
            ItemsContent.ITEMS.clear();
            ItemsContent.ITEM_MAP.clear();
            NavigationController roger = new NavigationController();
            Client jonas = Client.getClient();
            jean = roger.getselectItem(jonas.getIdCurrentList());
            Log.d("SONPERE","j'ai fini de récupérer côté client !!");
            Log.d("SONPERE", "on passe au poste execute, j'ai "+jean.size()+" items");
            for (int i =0;i<jean.size();i++){
               ItemCourse jose = new ItemCourse();
                jose.setNom(jean.get(i).getNom());
                jose.setIdItem(jean.get(i).getIdItem());
                jose.setURL(jean.get(i).getURL());
                if(!jean.get(i).getChosen().equals("")){
                    jose.setChosen(jean.get(i).getChosen());
                    jose.setTaken(true);
                }
                else{
                    jose.setChosen("personne");
                    jose.setTaken(false);
                }
                try {
                    URL url = new URL(jose.getURL());
                    Log.d("SONPERE", "j'ai l'url " + url.toString());
                    InputStream content = (InputStream) url.getContent();
                    Log.d("SONPERE", "je créé donc le drawable a partir de " + content.equals(null));
                    Bitmap image = BitmapFactory.decodeStream(content);
                    if (image != null & content != null) {
                        jose.setImageitem(Bitmap.createScaledBitmap(image, 150, 150, false));
                    } else {
                        jose.setImageitem(BitmapFactory.decodeResource(getResources(), R.drawable.no_image));
                    }
                    jose.setImageitemCheck(overlay(jose.getImageitem(), BitmapFactory.decodeResource(getResources(), R.drawable.check_vert)));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ItemsContent.addItem(new ItemsContent.DummyItem(jean.get(i).getIdItem(), jean.get(i).getNom()));
                listItem.add(jose);
                Log.d("SONPERE","fini d'ajouter l'item "+i);
            }
            Log.d("SONPERE", "aye c'est fini");
            //pouet.setLoadingFinished(true);

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            pouet.spinner.setVisibility(View.GONE);
            if(ItemAdapter!=null) {
                ItemAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onCancelled() {
        }
    }

}
