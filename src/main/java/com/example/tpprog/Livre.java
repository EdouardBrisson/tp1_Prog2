package com.example.tpprog;

import java.io.Serializable;

public class Livre extends Objet implements Serializable {
    private static final long serialVersionUID = 1L;

    private String auteur;
    private String maisonEdition;

    public Livre(String nom, double prix, int quantite, Status status, String imageFacture, String auteur, String maisonEdition) {
        super(nom, prix, quantite, status, imageFacture);
        this.auteur = auteur;
        this.maisonEdition = maisonEdition;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getMaisonEdition() {
        return maisonEdition;
    }

    public void setMaisonEdition(String maisonEdition) {
        this.maisonEdition = maisonEdition;
    }

    //Debug le backend
    @Override
    public void afficherDescription() {
        super.afficherDescription();

        System.out.printf(" %s %s", this.auteur, this.maisonEdition);
    }
}
