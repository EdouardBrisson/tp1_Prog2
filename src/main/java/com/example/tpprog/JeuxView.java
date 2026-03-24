package com.example.tpprog;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class JeuxView {

    private final TextField txtPublisher = new TextField();
    private final TextField txtCompanie = new TextField();
    private final TextField txtNbJoueurs = new TextField();
    private final VBox root;

    public JeuxView(Jeux jeux, ViewMode mode) {
        txtPublisher.setPrefWidth(200);
        txtCompanie.setPrefWidth(200);
        txtNbJoueurs.setPrefWidth(200);

        boolean readOnly = (mode == ViewMode.READ);
        txtPublisher.setEditable(!readOnly);
        txtCompanie.setEditable(!readOnly);
        txtNbJoueurs.setEditable(!readOnly);

        // Pré remplir si un objet est fourni
        if (jeux != null) {
            txtPublisher.setText(jeux.getPublisher());
            txtCompanie.setText(jeux.getCompanie());
        }

        Label titre = new Label("Section jeu");
        titre.setStyle("-fx-font-size:13; -fx-font-weight:bold;");

        root = new VBox(8,
                titre,
                buildRow("Publié par:", txtPublisher),
                buildRow("Développé par:", txtCompanie));
    }

    public VBox getRoot() { return root; }
    public Jeux buildJeux(String nom, double prix, int qte, Status status, String imageFacture) {
        return new Jeux(nom, prix, qte, status, imageFacture,
                txtPublisher.getText().trim(),
                txtCompanie.getText().trim());
    }

    //Accesseurs
    public String getPublisher()  { return txtPublisher.getText().trim(); }
    public String getCompanie()   { return txtCompanie.getText().trim(); }
    public String getNbJoueurs()  { return txtNbJoueurs.getText().trim(); }

    private HBox buildRow(String label, javafx.scene.Node ctrl) {
        Label lbl = new Label(label);
        lbl.setMinWidth(120);
        lbl.setAlignment(Pos.CENTER_LEFT);
        HBox row = new HBox(8, lbl, ctrl);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }
}