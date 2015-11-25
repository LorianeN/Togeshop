package com.example.loriane.togeshop;

import android.content.Context;

/**
 * Created by Sandjiv on 22/11/2015.
 */
public class ControllerLoginInscription {

    Client client;

    ControllerLoginInscription(Context context) {
        client = Client.getClient();
    }

    /**
     * Controle des appels
     * @param psw1
     * @param psw2
     * @param login
     */
    public void verifInscription(String psw1,String psw2, String login) {
        if (psw2.equals(psw1)) {
            System.out.println("les psw sont égaux");
            client.inscription(login, psw1);
        }
        else{
            //TODO toast
           // infoBox("Les mots de passe sont différents", "Erreur");
        }
    }
}
