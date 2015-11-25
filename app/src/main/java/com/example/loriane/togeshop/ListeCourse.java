package com.example.loriane.togeshop;

/**
 * Created by Sandjiv on 22/11/2015.
 */
public class ListeCourse {
    String nom;
    int idListe;
    String budget;
    String Date;
    String description;

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    String lieu;
    public int getIdListe() {
        return idListe;
    }

    public void setIdListe(int idListe) {
        this.idListe = idListe;
    }


    public  ListeCourse(){

    }
    public ListeCourse(ListeCourse liste) {
        nom = liste.getNom();
        budget = liste.getBudget();
        Date = liste.getDate();
        idListe = liste.getIdListe();
        description = liste.getDescription();
        lieu = liste.getLieu();
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

}
