package com.example.tpprog;

import java.io.Serializable;
import java.time.LocalDate;

public abstract class Objet implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String nom;
    protected double prix;
    protected int quantite;
    protected LocalDate dateAchat = LocalDate.now();
    protected Status status = Status.POSSESSION;
    protected String imageFacture;


    protected Objet(String nom, double prix, int quantite, Status status, String imageFacture) {
        this.nom = nom;
        this.prix = prix;
        this.quantite = quantite;
        this.status = status;
        this.imageFacture = imageFacture;
    }

    protected void setNom(String nom) {this.nom = nom;}
    protected String getNom() {return nom;}

    protected void setPrix(double prix) {this.prix = prix;}
    protected double getPrix() {return prix;}

    protected void setQuantite(int quantite) {this.quantite = quantite;}
    protected int getQuantite() {return quantite;}

    protected void setDateAchat(LocalDate dateAchat) { this.dateAchat = dateAchat; }
    protected LocalDate getDateAchat() { return dateAchat; }

    protected void setStatus(Status status){ this.status = status; }
    protected Status getStatus(){ return status; }

    protected String getImageFacture() {return imageFacture; }
    protected void setImageFacture(String imageFacture) { this.imageFacture = imageFacture; }

    //Debug le backend
    protected void afficherDescription() {
        System.out.printf("\n%s %s %d %s %s", this.nom, this.prix + "$", this.quantite, this.dateAchat, this.status);
    }
}
