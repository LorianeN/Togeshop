package com.example.loriane.togeshop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loriane.togeshop.dummy.ItemsContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Detail_liste extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,OnFragmentInteractionListener {

    ItemsFragsPagerAdapter itemsFragsPagerAdapter;
    ViewPager mViewPager;
    public ProgressBar spinner;



    public ProgressBar searchSpinner;
    Fragment[] ItemActionFragments = new Fragment[5];


    private ArrayList<Map<String, String>> mPeopleList;
    private SimpleAdapter mAdapter;
    private AutoCompleteTextView mTxtPhoneNo;


    boolean loadingFinished =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SONPERE", "j'ai créé mon activité detail_liste");
        setTitle(getIntent().getStringExtra("nom"));
        ItemActionFragments[0] = ItemsFragment.newInstance(this);
        ItemActionFragments[1] = NewItemFragment.newInstance("","");
        ItemActionFragments[2] = InfoListFragment.newInstance("","");
        ItemActionFragments[3] = ShareListFragment.newInstance("","");
        ItemActionFragments[4] = SendSmsNewFragment.newInstance("","");

        Log.d("SONPERE", "j'ai mon fragment item");
        setContentView(R.layout.activity_detail_liste);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        //searchSpinner = (ProgressBar)findViewById(R.id.progressBarSearch);
        Log.d("SONPERE", "j'ai mis la view en place");
        // ViewPager and its adapters use support library fragments, so use getSupportFragmentManager.
        itemsFragsPagerAdapter = new ItemsFragsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.detail_liste_pager);
        Log.d("SONPERE", "show Progress true");
        spinner.setVisibility(View.VISIBLE);
//        while(!loadingFinished){
//            Log.d("SAMERE","j'attend");
//        }
        mViewPager.setAdapter(itemsFragsPagerAdapter);
        Log.d("SONPERE", "J'ai fini de set l'adapter de l'activité détail");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = String.format(Locale.FRENCH, "http://maps.google.com/maps?daddr=%s","chemin de la plaine marseille");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                try
                {
                    startActivity(intent);
                }
                catch(ActivityNotFoundException ex)
                {
                    try
                    {
                        Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(unrestrictedIntent);
                    }
                    catch(ActivityNotFoundException innerEx)
                    {
                        Log.d("SONPERE", "yapadmap");
                    }
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Log.d("SONPERE", "trop tard");
        navigationView.setNavigationItemSelectedListener(this);
    }


    public ProgressBar getSpinner() {
        return spinner;
    }
    public ProgressBar getSearchSpinner() {
        return searchSpinner;
    }
    @Override
    public void onFragmentInteraction(String msg) {
        //Log.d("SONPERE", "j'ai appuyé sur l'item "+ msg);

    }

    public void onFragmentInteraction(Uri uri) {
    }




    public boolean isLoadingFinished() {
        return loadingFinished;
    }

    public void setLoadingFinished(boolean loadingFinished) {
        this.loadingFinished = loadingFinished;
    }

    public void cancelSMS(View view) {
        showListItemFragment();
    }

    public void validateSMS(View view) {
        EditText contenusms = (EditText) findViewById(R.id.contenusms);
        AutoCompleteTextView numerosms = (AutoCompleteTextView) findViewById(R.id.mmWhoNo);
        Log.d("SONPERE","j'ai choisi : "+numerosms.getText().toString());
        String jason = numerosms.getText().toString().substring(numerosms.getText().toString().lastIndexOf('=')+1,(numerosms.getText().toString().length() -1));
        Log.d("SONPERE", "j'envoie "+contenusms.getText().toString()+" au "+jason);
        SmsManager.getDefault().sendTextMessage(jason, null, contenusms.getText().toString(), null, null);
        Toast toast = Toast.makeText(this, "message envoyé", Toast.LENGTH_SHORT);
        toast.show();
        contenusms.setText("");
        numerosms.setText("");
        showListItemFragment();
    }

    public void cancelShare(View view) {
        showListItemFragment();
    }

    public void validateSharet(View view) {
        Client.getClient().addUserToList(((AutoCompleteTextView) findViewById(R.id.shareWithWho)).getText().toString());
    }

    public void cancelNewItem(View view) {
        showListItemFragment();
    }

    public void validateNewItem(View view) {
        //TODO envoyer les items sélectionnés
        ArrayList<ItemCourse> pouet = ((NewItemFragment) ItemActionFragments[1]).getResearchedItems();
        Client.getClient().addItems(pouet);
        showListItemFragment();
        ((ItemsFragment) ItemActionFragments[0]).refresh();
        Toast toast = Toast.makeText(this, "items ajoutés", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void searchItems(View view) {
        String recherche = ((EditText) findViewById(R.id.itemSearched)).getText().toString();
        recherche = recherche.replace(" ","%20");

        //searchSpinner.setVisibility(View.VISIBLE);
        ArrayList<ItemCourse> pouet = getRequeteData(recherche);
        ((NewItemFragment) ItemActionFragments[1]).refresh(pouet);
    }

    // Since this is an object collection, use a FragmentStatePagerAdapter, and NOT a FragmentPagerAdapter.
    public class ItemsFragsPagerAdapter extends FragmentStatePagerAdapter {
        public ItemsFragsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return ItemActionFragments[i];
        }

        @Override
        public int getCount() {
            return ItemActionFragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail_liste, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_infoList) {
            showinfoListFragment();
        } else if (id == R.id.nav_addItem) {
            showNewItemFragment();
        } else if (id == R.id.nav_ListItem) {
            showListItemFragment();
        } else if (id == R.id.nav_shareList) {
            showShareListFragment();
        } else if (id == R.id.nav_sendSMS) {
            showSendSmsFragment();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showNewItemFragment(){
        mViewPager.setCurrentItem(1);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
    }

    public void showListItemFragment(){
        mViewPager.setCurrentItem(0);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
    }

    public void showinfoListFragment(){
        mViewPager.setCurrentItem(2);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
//        Intent intent = new Intent(Detail_liste.this,
//                info.class);
//        intent.putExtra("nom",Client.getClient().getNameCurrentList());
//        startActivity(intent);
    }

    public void showShareListFragment(){
        spinner.setVisibility(View.VISIBLE);
        new getUserTask().execute();
        mViewPager.setCurrentItem(3);
        ArrayAdapter<String> userAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,Client.getClient().userList);
        AutoCompleteTextView nuage = (AutoCompleteTextView) findViewById(R.id.shareWithWho);
        nuage.setAdapter(userAdapter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
    }

    public void showSendSmsFragment(){
        mViewPager.setCurrentItem(4);
        mPeopleList = new ArrayList<Map<String, String>>();
        PopulatePeopleList();
        mAdapter = new SimpleAdapter(this, mPeopleList, R.layout.custcontview ,new String[] { "Name", "Phone" , "Type" }, new int[] { R.id.ccontName, R.id.ccontNo, R.id.ccontType });
        mTxtPhoneNo = (AutoCompleteTextView) findViewById(R.id.mmWhoNo);
        mTxtPhoneNo.setAdapter(mAdapter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
    }

    public void PopulatePeopleList()
    {

        mPeopleList.clear();

        Cursor people = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        while (people.moveToNext())
        {
            String contactName = people.getString(people.getColumnIndex(
                    ContactsContract.Contacts.DISPLAY_NAME));

            String contactId = people.getString(people.getColumnIndex(
                    ContactsContract.Contacts._ID));
            String hasPhone = people.getString(people.getColumnIndex(
                    ContactsContract.Contacts.HAS_PHONE_NUMBER));

            if ((Integer.parseInt(hasPhone) > 0))
            {

                // You know have the number so now query it like this
                Cursor phones = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                        null, null);
                while (phones.moveToNext()) {

                    //store numbers and display a dialog letting the user select which.
                    String phoneNumber = phones.getString(
                            phones.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));

                    String numberType = phones.getString(phones.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.TYPE));

                    Map<String, String> NamePhoneType = new HashMap<String, String>();

                    NamePhoneType.put("Name", contactName);
                    NamePhoneType.put("Phone", phoneNumber);

                    if(numberType.equals("0"))
                        NamePhoneType.put("Type", "Work");
                    else
                    if(numberType.equals("1"))
                        NamePhoneType.put("Type", "Home");
                    else if(numberType.equals("2"))
                        NamePhoneType.put("Type",  "Mobile");
                    else
                        NamePhoneType.put("Type", "Other");

                    //Then add this map to the list.
                    mPeopleList.add(NamePhoneType);
                }
                phones.close();
            }
        }
        people.close();
        startManagingCursor(people);
    }


    public ArrayList<ItemCourse> getRequeteData(String nomItem) {
        nomItem = nomItem.replace(" ","%20");
        String requete = "https://www.mastercourses.com/api2/products/search/?q="+nomItem+"&scope=min&mct=hieCaig6Oth2thiem7eiRiechufooWix";
        ArrayList<ItemCourse> result = new ArrayList<>();
        try {
            Client.getClient().execHTMLRequete(requete);
        JSONArray reqResult = new JSONArray((Client.getClient().resultSearch));
        int max = 0;
        if(reqResult.length()>10) max =10;
        else max = reqResult.length();
        for(int i =0;i< max;i++){
            JSONObject pouet = null;
                pouet = reqResult.getJSONObject(i);
                ItemCourse repouet = new ItemCourse();
                String nom = pouet.getString("name");
                System.out.println(nom);
                repouet.setNom(nom);
                repouet.setChainId(pouet.getInt("chain_id"));
                repouet.setIdItem(pouet.getInt("id"));
                repouet.setTaken(false);
                repouet.setPrix("0");
                repouet.setURL(pouet.getString("image_url"));
                result.add(repouet);
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }


    public class getUserTask extends AsyncTask<Void, Void, Boolean> {
        getUserTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d("SONPERE", "je commence a récup les users");
            Client.getClient().getUsers();
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Log.d("SONPERE","done");
            spinner.setVisibility(View.GONE);

        }

        @Override
        protected void onCancelled() {
        }
    }
}
