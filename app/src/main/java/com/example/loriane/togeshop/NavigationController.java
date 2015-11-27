package com.example.loriane.togeshop;

import android.util.Log;

import com.example.loriane.togeshop.Client;
import com.example.loriane.togeshop.ItemCourse;
import com.example.loriane.togeshop.ListeCourse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Sandjiv on 11/11/2015.
 */
public class NavigationController{

    public NavigationController() {
    }

    public boolean addListe(String text, String descriptionListeText, String dateListeText, String endroitListeText) {
        JSONObject envoi = new JSONObject();
        try {
            envoi.put("nomListe",text);
            envoi.put("description",descriptionListeText);
            envoi.put("date",dateListeText);
            envoi.put("endroit",endroitListeText);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Client.getClient().addListe(envoi.toString());
    }

    public ArrayList<ListeCourse> getListe(){
        ArrayList<ListeCourse> retour = new ArrayList<ListeCourse>();
        JSONArray listes = null;
        try {
            listes = new JSONArray(Client.getClient().getListes());
            for(int i=0;i<listes.length();i++){
                ListeCourse objet = new ListeCourse();
                objet.setNom(listes.getJSONObject(i).getString("nomListe"));
                objet.setBudget(listes.getJSONObject(i).getString("BudgetListe"));
                objet.setDate(listes.getJSONObject(i).getString("date"));
                objet.setIdListe(listes.getJSONObject(i).getInt("id"));
                objet.setLieu(listes.getJSONObject(i).getString("endroit"));
                objet.setDescription(listes.getJSONObject(i).getString("description"));
                retour.add(objet);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return retour;
    }

    public boolean getSelectedSearchItem(ArrayList<ItemCourse> pouet) {
        ArrayList<ItemCourse> demande = new ArrayList<>();
        for (int i =0; i< pouet.size();i++){
            if(pouet.get(i).getTaken()){
                demande.add(pouet.get(i));
                System.out.println("le produit "+pouet.get(i).getNom()+"a été ajouté de la chaine"+pouet.get(i).getChainId());
                System.out.println("le produit "+pouet.get(i).getNom()+"a été ajouté de la chaine"+demande.get(demande.size()-1).getChainId());
            }
        }
        addSelectedItem(demande);
        return true;
    }

    private void addSelectedItem(ArrayList<ItemCourse> demande) {
        for (int i= 0; i<demande.size();i++){
            ItemCourse res = new ItemCourse(demande.get(i));
            JSONObject tmp = new JSONObject();
            try {
                tmp.put("idItem",res.getIdItem());
                tmp.put("nom",res.getNom());
                tmp.put("taken",false);
                tmp.put("disable",false);
                tmp.put("prix",res.getPrix());
                tmp.put("chosen_by",res.getChosen());
                tmp.put("disable", res.getDisable());
                tmp.put("chain_id",res.getChainId());
                if(res.getURL().equals(null)){
                    tmp.put("url","http://www.vernon-encheres.fr/_images/banniere_404.jpg");
                }
                else{
                    tmp.put("url",res.getURL());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String requete = "addItem/"+Client.getClient().getIdCurrentList()+"/"+tmp.toString();
            System.out.println("j'écris : "+requete);
            Client.getClient().addItem(requete);
        }
    }


    public ArrayList<ItemCourse> getselectItem(int id) {
        JSONArray contenu = null;
        ArrayList<ItemCourse> retour = new ArrayList<>();
        try {
            contenu = new JSONArray(Client.getClient().getSelectItem(id));
            for (int i = 0;i<contenu.length();i++){
                Log.d("SONPERE","après demande au serveur, j'ai reçu : "+contenu.getJSONObject(i).getString("nom"));
                ItemCourse tmp = new ItemCourse();
                tmp.setPrix(contenu.getJSONObject(i).getString("prix"));
                tmp.setTaken(contenu.getJSONObject(i).getBoolean("taken"));
                tmp.setIdItem(contenu.getJSONObject(i).getInt("idItem"));
                tmp.setDisable(contenu.getJSONObject(i).getBoolean("disable"));
                tmp.setURL(contenu.getJSONObject(i).getString("url"));
                tmp.setChosen(contenu.getJSONObject(i).getString("chosen_by"));
                tmp.setNom(contenu.getJSONObject(i).getString("nom"));
                tmp.setChainId(contenu.getJSONObject(i).getInt("chain_id"));
                retour.add(tmp);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return retour;
    }

    public boolean addUserToList(String text, int selectedItemId) {
        String requete = "addUsertoList/"+text+"/"+selectedItemId;
        return Client.getClient().sendRequest(requete);
    }


    public boolean itemUpdated(boolean disable, int selectedItemId) {
        String requete;
        if (disable) {
            requete = "disableItem/" +Client.getClient().getIdCurrentList() + "/" + selectedItemId;
            return Client.getClient().sendRequest(requete);
        } else {
            requete = "enableItem/" +Client.getClient().getIdCurrentList()+ "/" + selectedItemId;
            return Client.getClient().sendRequest(requete);
        }
    }

    public String execRequeteGeoLoc() {
        String requete = "https://context.skyhookwireless.com/accelerator/ip?version=2.0&prettyPrint=true&key=eJwVwckNACAIALC3w5CAgMcTBZYy7m5sqRB-MkTLqeaLc0wQRgJsJoDOG_rU7RZhSXkfERwLSw&user=eval&timestamp=1362089701";
        return (Client.getClient().execStringRequete(requete));

    }

//    public String execRequeteChaineLoc(Integer integer,double lat,double longi) {
//        String requete = "https://www.mastercourses.com/api2/chains/"+integer+"/stores/locator/?lat="+lat+"&lon="+longi+"&scope=min&mct=hieCaig6Oth2thiem7eiRiechufooWix";
//        JSONArray resul = Client.getClient().execRequete(requete);
//        String ret =null;
//        try {
//            System.out.println("le plus proche se trouve au "+resul.getJSONObject(0).toString());
//            ret = resul.getJSONObject(0).toString();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return ret;
//    }
}
