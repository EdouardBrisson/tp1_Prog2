package com.example.tpprog;

import java.io.Serializable;

public class Outils extends Objet implements Serializable {
    private static final long serialVersionUID = 1L;
    private String marqueFabrication;
    private String utilite;

    public Outils(String nom, double prix, int quantite, Status status, String imageFacture,String marqueFabrication, String dateSortie) {
        super(nom, prix, quantite, status, imageFacture);
        this.marqueFabrication = marqueFabrication;
        this.utilite = dateSortie;
    }

    public String getMarqueFabrication() {return marqueFabrication;}

    public void setMarqueFabrication(String marqueFabrication) {this.marqueFabrication = marqueFabrication;}

    public String getUtilite() {return utilite;}

    public void setUtilite(String utilite) {this.utilite = utilite;}

    @Override
    public void afficherDescription() {
        super.afficherDescription();

        System.out.printf(" %s %s", this.marqueFabrication, this.utilite);
    }
}
