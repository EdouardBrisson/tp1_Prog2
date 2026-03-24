package com.example.tpprog;

import java.io.Serializable;
import java.util.Scanner;

public class Jeux extends Objet implements Serializable {
    private static final long serialVersionUID = 1L;
    private String publisher;
    private String companie;

    public Jeux(String nom, double prix, int quantite, Status status, String imageFacture, String publisher, String companie) {
        super(nom, prix, quantite, status, imageFacture);
        this.publisher = publisher;
        this.companie = companie;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCompanie() {
        return companie;
    }

    public void setCompanie(String companie) {
        this.companie = companie;
    }

    //Debug le backend
    @Override
    public void afficherDescription() {
        super.afficherDescription();
        System.out.printf(" %s %s", this.publisher, this.companie);
    }
}
