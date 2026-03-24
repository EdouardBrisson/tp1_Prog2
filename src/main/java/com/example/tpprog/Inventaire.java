package com.example.tpprog;

public class Inventaire {

    private Objet[] listeObjets = new Objet[10];
    private int nbObjet = 0;

    //Ajoute un objet dans l'inventaire
    public void addObjet(Objet nvObjet){
        if(nbObjet == listeObjets.length){
            Objet[] nvlisteObjets = new Objet[listeObjets.length+ 10];
            for(int i = 0; i < listeObjets.length; i++){
                nvlisteObjets[i] = listeObjets[i];
            }
            this.listeObjets = nvlisteObjets;
        }

        this.listeObjets[nbObjet] = nvObjet;
        nbObjet++;
    }

    //Enleve l'objet de l'inventaire
    public void delObjet(int index){
        for(int i = index; i < nbObjet -1; i++){
            listeObjets[i] = listeObjets[i + 1];
        }
        nbObjet--;
        listeObjets[nbObjet] = null;
    }

    //Ajoute l'objet a l'endroit specifier
    public void addObjetAt(int index, Objet nvObjet) {
        if (nbObjet == listeObjets.length) {
            Objet[] nvlisteObjets = new Objet[listeObjets.length + 10];
            for (int i = 0; i < listeObjets.length; i++) nvlisteObjets[i] = listeObjets[i];
            this.listeObjets = nvlisteObjets;
        }
        // Décale les éléments vers la droite
        for (int i = nbObjet; i > index; i--) listeObjets[i] = listeObjets[i - 1];
        listeObjets[index] = nvObjet;
        nbObjet++;
    }

    //Debug le backend
    public void afficherInventaire() {
        System.out.println("Inventaire actuel :");
        for (int i = 0; i < nbObjet; i++) {
            System.out.println((i + 1) + ". " + listeObjets[i].getNom());
        }
    }


}
