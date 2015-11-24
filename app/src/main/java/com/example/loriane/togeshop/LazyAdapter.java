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
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;
    boolean waiting =false;
    Bitmap pouet =null;
    Drawable d;

    public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;

        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("SONPERE", "je devrais passer par là");
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.items_cell_content, null);
        TextView title = (TextView)vi.findViewById(R.id.titre); // title
        TextView description = (TextView)vi.findViewById(R.id.description); // artist name
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.img); // thumb image
        Log.d("SONPERE","position :"+ position);
        Log.d("SONPERE","je traite l'item "+data.get(position).get("titre"));
//        HashMap<String, String> song = new HashMap<String, String>();
//        song = data.get(position);

        // Setting all values in listview
        title.setText(data.get(position).get("titre"));
        description.setText("description");
        new GetImageTask().execute(data.get(position).get("url"));
        Log.d("SONPERE", "je commence à attendre");
        while(!waiting){}
        Log.d("SONPERE", "j'ai fini d'attendre !!");
        waiting =false;
        thumb_image.setImageDrawable(d);
        //imageLoader.DisplayImage(data.get(position).get("url"), thumb_image);
        return vi;
    }

    public class GetImageTask extends AsyncTask<String,String,Boolean> {


        @Override
        protected Boolean doInBackground(String... params) {
            try {
                Log.d("SONPERE", "je traite l'url "+params[0]);
                URL url = new URL(params[0]);
                InputStream content = (InputStream)url.getContent();
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
        }

        @Override
        protected void onCancelled() {
        }
    }

}