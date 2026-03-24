package com.example.tpprog;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class OutilsView {

    private final TextField txtMarque  = new TextField();
    private final TextField txtUtilite = new TextField();
    private final VBox root;

    public OutilsView(Outils outil, ViewMode mode) {
        txtMarque.setPrefWidth(200);
        txtUtilite.setPrefWidth(200);

        boolean readOnly = (mode == ViewMode.READ);
        txtMarque.setEditable(!readOnly);
        txtUtilite.setEditable(!readOnly);

        // Pré remplir si un objet est fourni
        if (outil != null) {
            txtMarque.setText(outil.getMarqueFabrication());
            txtUtilite.setText(outil.getUtilite());
        }

        Label titre = new Label("Section outil");
        titre.setStyle("-fx-font-size:13; -fx-font-weight:bold;");

        root = new VBox(8, titre, buildRow("Marque:", txtMarque), buildRow("Utilité:", txtUtilite));
    }

    public VBox getRoot() { return root; }

    public Outils buildOutils(String nom, double prix, int qte, Status status, String imageFacture) {
        return new Outils(nom, prix, qte, status, imageFacture, txtMarque.getText().trim(), txtUtilite.getText().trim());
    }

    //Accesseurs
    public String getMarque() { return txtMarque.getText().trim(); }
    public String getUtilite() { return txtUtilite.getText().trim(); }

    private HBox buildRow(String label, javafx.scene.Node ctrl) {
        Label lbl = new Label(label);
        lbl.setMinWidth(120);
        lbl.setAlignment(Pos.CENTER_LEFT);
        HBox row = new HBox(8, lbl, ctrl);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }
}