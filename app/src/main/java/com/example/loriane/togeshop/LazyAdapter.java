package com.example.loriane.togeshop;

/**
 * Created by Sandjiv on 24/11/2015.
 */
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<ItemCourse> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;
    boolean waiting =false;
    Bitmap pouet =null;
    Drawable d;

    public LazyAdapter(Activity a, ArrayList<ItemCourse> d) {
        Log.d("SONPERE", "j'ai créé mon adapter");
        activity = a;
        data=d;
        Log.d("SONPERE", "il a reçu "+data.size()+" items");
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.size();
    }

    static class ViewHolderItem{
        public TextView titleitem;
        public TextView descriptionitem;
        public ImageView ImageViewitem;
    }
    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        ViewHolderItem viewHolder =null;
        if(convertView==null) {
            vi = inflater.inflate(R.layout.items_cell_content, null);
            viewHolder = new ViewHolderItem();
            viewHolder.titleitem = (TextView) vi.findViewById(R.id.titre); // title
            viewHolder.descriptionitem = (TextView) vi.findViewById(R.id.description);
            viewHolder.ImageViewitem = (ImageView) vi.findViewById(R.id.img); // thumb image
            vi.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolderItem) convertView.getTag();
        }
        final  ItemCourse Item = data.get(position);
//        GetImageTask roger = new GetImageTask(Item.getURL(),viewHolder);
//        roger.execute();
//        Log.d("SONPERE", "je commence à attendre");
//        long cpt=0;
//        while (!waiting&cpt<60000000) {
//            cpt++;
//        }
//        if (cpt==60000000){
//            Log.d("SONPERE","timeout :/");
//            viewHolder.ImageViewitem.setImageResource(R.drawable.no_image);
//        }
//        else{
//            Log.d("SONPERE", "j'ai fini d'attendre !!");
//            //imageLoader.DisplayImage(data.get(position).get("url"), thumb_image);
//        }

        Log.d("SONPERE", "position :" + position);
        Log.d("SONPERE", "je traite l'item " + Item.getNom() + "qui est " + Item.getTaken() + "pris");
        // Setting all values in listview
        viewHolder.titleitem.setText(Item.getNom());
        viewHolder.descriptionitem.setText(String.format("apporté par %s", Item.getChosen()));
        if (Item.getChosen().equals(Client.getClient().getUserName())) {
            if (Item.getTaken()) {
                viewHolder.ImageViewitem.setImageBitmap(Item.getImageitemCheck());
            }
            else{
                viewHolder.ImageViewitem.setImageBitmap(Item.getImageitem());
            }
        }
        else if (Item.getChosen().equals("personne")||Item.getChosen().equals("")){
            if (Item.getTaken()) {
                viewHolder.ImageViewitem.setImageBitmap(Item.getImageitemCheck());
            }
            else{
                viewHolder.ImageViewitem.setImageBitmap(Item.getImageitem());
            }
        }else{
            viewHolder.ImageViewitem.setImageBitmap(Item.getImageitemCheck());
        }

       // imageLoader.DisplayImage(Item.get("url"), viewHolder.ImageViewitem);

        return vi;
    }

    public class GetImageTask extends AsyncTask<String,String,Boolean> {
        String url;
        ViewHolderItem viewHolder;

        public GetImageTask(String url, ViewHolderItem viewHolder) {
            this.url = url;
            this.viewHolder = viewHolder;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                waiting = false;
                Log.d("SONPERE", "je traite l'url "+url);
                URL url = new URL(this.url);
                InputStream content = (InputStream)url.getContent();
                Log.d("SONPERE","je créé donc le drawable");
                d = Drawable.createFromStream(content, "src");
            } catch (IOException e) {
                waiting = true;
                e.printStackTrace();
                return true;
            }
            waiting = true;
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            //Log.d("SONPERE", "je passe en post execute");
            viewHolder.ImageViewitem.setImageDrawable(d);
        }

        @Override
        protected void onCancelled() {
        }

    }

}