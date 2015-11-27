package com.example.loriane.togeshop;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.loriane.togeshop.dummy.ListesContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Sandjiv on 22/11/2015.
 */
public class Client {
    public static final int BACKGROUND_COLOR = Color.rgb(253, 175, 112);
    public static final int BACKGROUND_INV_COLOR = Color.rgb(68, 154, 151);
    ControllerLoginInscription controllerLoginInscription;
    private static Client client;
    Socket sock;
    DataInputStream curIn ;
    DataOutputStream curOut ;
    JSONArray resultSearch;
    private boolean finished = false;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    String userName;
    private ListeCourse listeCourse;
    boolean connected = false ;
    ArrayList<String> userList = new ArrayList<>();

    int idCurrentList=1;
    String nameCurrentList ="";


    public int getIdCurrentList() {
        return idCurrentList;
    }

    public void setIdCurrentList(int idCurrentList) {
        this.idCurrentList = idCurrentList;
    }




    public static Client getClient () { // récup de la référence unique
        if (client == null){
            client = new Client() ;
        }
        return client ;
    }
    /**
     * connect to the server
     **/

    private Client() {
    }

    /**
     * start socket
     */
    boolean connect(String nickName) {
        try {
            Log.d("SONPERE", "J'essaie de me connecter");
            sock = new Socket("192.168.1.21", 10000);
            curIn = new DataInputStream(sock.getInputStream());
            curOut = new DataOutputStream(sock.getOutputStream());
            System.out.println("j'écris mon nom via "+sock.toString());
            Log.d("SONPERE","j'écris mon nom");
            curOut.writeUTF(nickName);
            System.out.println("done, j'attends un ordre via "+sock.toString());
            if(!curIn.readBoolean()){
                //TODO ya un problème là, faudrait renvoyer un false sinon le programme continue comme si de rien était
                //controllerLoginInscription.infoBox("cette personne est déjà connectée","illegal Login");
                connected = false;
                sock.close();
                return false;
            }
            else {
                connected = true;
                System.out.println(nickName + " est connecté");
                return true;
            }

        }
        catch (IOException e) {
            //TODO toast
            //controllerLoginInscription.infoBox("problème à la connexion","erreur");
            e.printStackTrace();
        }
        return false;
    }


    /**
     *
     * @param nickName
     */
    void disconnect(String nickName) {
        try {
            curOut.writeUTF("disconnect/1/" + nickName);
            connected = false ;

            sock.close();

        } catch (IOException e1) {
            //TODO toast
            //controllerLoginInscription.infoBox("problème à la fermeture","erreur");
            e1.printStackTrace();
        }
    }

    void inscription(String login, String psw){
        JSONObject nouveauinscrit = new JSONObject();
        try {
            nouveauinscrit.accumulate("login",login);
            nouveauinscrit.accumulate("psw",psw);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            curOut.writeUTF("inscription/1/"+nouveauinscrit.toString());
            System.out.println("J attends une réponse ... via"+ sock.toString());
            boolean unique = curIn.readBoolean();
            System.out.println("c'est fait !");
            if(!unique){
                System.out.println("login existe déjà !!!");
                //TODO toast
                //controllerLoginInscription.infoBox("ce login est déjà pris","dommage");
            }
            else{
                System.out.println("je déconnecte pouet");
                //TODO toast
                //controllerLoginInscription.infoBox("inscription réussie","félicitation");
                disconnect("pouet");
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    void inscriptionAbort(){
        if(!sock.isClosed()) disconnect("pouet");
    }

    boolean login(String login, String psw){
        connect(login);
        JSONObject nouveaumonsieur = new JSONObject();
        try {
            nouveaumonsieur.accumulate("login",login);
            nouveaumonsieur.accumulate("psw",psw);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(nouveaumonsieur.toString());
        try {
            curOut.writeUTF("connexion/1/"+nouveaumonsieur.toString());
            System.out.println("J attends une réponse ... via"+ sock.toString());
            boolean unique = curIn.readBoolean();
            System.out.println("c'est fait !");
            if(!unique){
                System.out.println("login existe déjà !!!");
                return false;
                //TODO toast
                //controllerLoginInscription.infoBox("erreur login/mdp","dommage");
            }
            else{
                System.out.println("on passe sur login");
                userName = login;
                return true;
                //controllerLoginInscription.nextFen();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void keepLogin(String login, String psw) {
        FileWriter fw;
        JSONObject Jason;
        try {
            Jason = new JSONObject();
            Jason.put("login",login);
            Jason.put("psw",psw);
            Jason.put("retenir", "true");
            fw = new FileWriter("sharedPref.json",false);
            BufferedWriter output = new BufferedWriter(fw);
            output.write(Jason.toString());
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
        }
    }

    public String[] getSharedPageLogin(){
        String[] shared = new String[3];
        FileReader fr;
        JSONObject Jason;

        try {
            fr = new FileReader("sharedPref.json");

            BufferedReader input = new BufferedReader(fr);
            Jason = new JSONObject(input.readLine());
            shared[0] = Jason.get("login").toString();
            shared[1] = Jason.get("psw").toString();
            shared[2] = Jason.get("retenir").toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return shared;
    }


    public boolean addListe(String text) {

        addListTask nuage = new addListTask(text);
        nuage.execute();
        return true;
    }

    public boolean addItem(String text) {
        boolean retour = false;
        try {
            curOut.writeUTF(text);
            System.out.println("done, j'attend des retours éventuels");
            retour = curIn.readBoolean();
            System.out.println("done");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retour;
    }

    public String getListes(){

        System.out.println("je récupère mes listes");
        try {
            curOut.writeUTF("getGlobalListe/1/");

            String pouet = curIn.readUTF();
            System.out.println("cote client j'ai recu : "+pouet);
            return pouet;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return"WTFFFFFFFFF";

    }


    public void execHTMLRequete(String requete) {

        new execHTMLRequeteTask(requete).execute();
        while(!finished){
            Log.d("SAMERE","j'attend");
        }
        finished = false;

    }

    public String execStringRequete(String requete) {
        try {
            return getHTML(requete);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getHTML(String urlToRead) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return result.toString();
    }

    public String getSelectItem(int p0) {

        try {
            curOut.writeUTF("getListe/0/"+String.valueOf(p0));
            return curIn.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "WTFFFFFF";
    }

    public boolean sendRequest(String requete) {
        execRequeteTask nuage = new execRequeteTask(requete);
        nuage.execute();
        return true;
    }

    public void suppressLogin() {
        FileWriter fw;
        JSONObject Jason;
        try {
            Jason = new JSONObject();
            Jason.put("login", "");
            Jason.put("psw","");
            Jason.put("retenir","false");
            fw = new FileWriter("sharedPref.json",false);
            BufferedWriter output = new BufferedWriter(fw);
            output.write(Jason.toString());
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setNameCurrentList(String nameCurrentList) {
        this.nameCurrentList = nameCurrentList;
    }

    public String getNameCurrentList(){
        return nameCurrentList;
    }


    public void getUsers(){

        try {
            curOut.writeUTF("getUsers/0/0");
            JSONArray jonarray = new JSONArray(curIn.readUTF());
            for (int i=0;i<jonarray.length();i++){
                if(jonarray.getJSONObject(i).getString("login").equals("")||jonarray.getJSONObject(i).getString("login").equals(getUserName())||jonarray.getJSONObject(i).getString("login").equals("pouet")){

                }else{
                    userList.add(jonarray.getJSONObject(i).getString("login"));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addUserToList(String username) {
        String requete = "addUsertoList/"+username+"/"+getIdCurrentList();
        new execRequeteTask(requete).execute();
    }

    public void addItems(ArrayList<ItemCourse> pouet) {
        new addItemTask(pouet).execute();
    }

    public class execHTMLRequeteTask extends AsyncTask<Void, Void, Boolean> {
        String text;
        execHTMLRequeteTask(String jason) {
            text =jason;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                resultSearch = new JSONArray(getHTML(text));
            } catch (Exception e) {
                e.printStackTrace();
            }
            finished = true;
            return true;
        }
    }

    public class addItemTask extends AsyncTask<Void, Void, Boolean> {
        ArrayList<ItemCourse> text=null;
        addItemTask(ArrayList<ItemCourse> jason) {
            text =jason;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            for (int i= 0; i<text.size();i++){
                ItemCourse res = new ItemCourse(text.get(i));
                JSONObject tmp = new JSONObject();
                if(res.getTaken()) {
                    try {
                        tmp.put("idItem", res.getIdItem());
                        tmp.put("nom", res.getNom());
                        tmp.put("taken", false);
                        tmp.put("disable", false);
                        tmp.put("prix", res.getPrix());
                        tmp.put("chosen_by", "");
                        tmp.put("disable", res.getDisable());
                        tmp.put("chain_id", res.getChainId());
                        if (res.getURL().equals(null)) {
                            tmp.put("url", "http://www.vernon-encheres.fr/_images/banniere_404.jpg");
                        } else {
                            tmp.put("url", res.getURL());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String requete = "addItem/" + Client.getClient().getIdCurrentList() + "/" + tmp.toString();
                    System.out.println("j'écris : " + requete);
                    try {
                        curOut.writeUTF(requete);
                        System.out.println("done, j'attend des retours éventuels");
                        curIn.readBoolean();
                        System.out.println("done");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return true;
        }
    }

    public class execRequeteTask extends AsyncTask<Void, Void, Boolean> {
        String text;
        execRequeteTask(String jason) {
            text =jason;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean retour = false;
            try{
                curOut.writeUTF(text);
                return curIn.readBoolean();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    public class addListTask extends AsyncTask<Void, Void, Boolean> {
String text;
        addListTask(String jason) {
            text =jason;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean retour = false;
            String requete = "ajoutListe/0/"+text;
            System.out.println("j'ajoute une liste");
            try {
                curOut.writeUTF(requete);
                System.out.println("done, j'attend des retours éventuels");
                retour = curIn.readBoolean();
                System.out.println("done");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return retour;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(success){
                ((ListeFragment)ChoixListe.principalFragment[0]).refresh();
            }
        }
    }
}
