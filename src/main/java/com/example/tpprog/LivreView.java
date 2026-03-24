package com.example.tpprog;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class LivreView {

    private final TextField txtAuteur = new TextField();
    private final TextField txtMaisonEdition = new TextField();
    private final VBox root;

    public LivreView(Livre livre, ViewMode mode) {
        txtAuteur.setPrefWidth(200);
        txtMaisonEdition.setPrefWidth(200);

        boolean readOnly = (mode == ViewMode.READ);
        txtAuteur.setEditable(!readOnly);
        txtMaisonEdition.setEditable(!readOnly);

        // Pré remplir si un objet est fourni
        if (livre != null) {
            txtAuteur.setText(livre.getAuteur());
            txtMaisonEdition.setText(livre.getMaisonEdition());
        }

        Label titre = new Label("Section livre");
        titre.setStyle("-fx-font-size:13; -fx-font-weight:bold;");

        root = new VBox(8,
                titre,
                buildRow("Auteur:",              txtAuteur),
                buildRow("Maison d'édition:",    txtMaisonEdition)
        );
    }

    public VBox getRoot() { return root; }

    public Livre buildLivre(String nom, double prix, int qte, Status status, String image) {
        return new Livre(nom, prix, qte, status, image,
                txtAuteur.getText().trim(),
                txtMaisonEdition.getText().trim());
    }

    //Accesseurs
    public String getAuteur()        { return txtAuteur.getText().trim(); }
    public String getMaisonEdition() { return txtMaisonEdition.getText().trim(); }

    private HBox buildRow(String label, javafx.scene.Node ctrl) {
        Label lbl = new Label(label);
        lbl.setMinWidth(120);
        lbl.setAlignment(Pos.CENTER_LEFT);
        HBox row = new HBox(8, lbl, ctrl);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }
}