package com.example.tpprog;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings("unchecked")
public class GlobalView {

    //État fichier
    public final GestionFichier gestionFichier = new GestionFichier();

    //Données
    public Inventaire inventaire = new Inventaire();
    public final ObservableList<Objet> masterList = FXCollections.observableArrayList();
    public final ObservableList<Objet> items = FXCollections.observableArrayList();

    //Boutons requis pour le mode édition
    public Objet objetEnEdition = null;
    public Button btnAdd;

    //Widgets de la table
    public TableView<Objet> tableView;
    public ComboBox<String> comboFiltre;
    public TextField txtRecherche;

    //Widgets du formulaire
    public Label titrePanneau;
    public ComboBox<String> comboType;
    public TextField txtNom, txtPrix, txtImageFacture;
    public Spinner<Integer> spinnerQuantite;
    public DatePicker datePicker;
    public ComboBox<Status> comboStatus;
    public VBox zoneDynamique;
    public LivreView livreView;
    public JeuxView jeuxView;
    public OutilsView outilsView;

    // Menu bar
    @SuppressWarnings("unused")
    public MenuBar buildMenuBar(Stage stage) {

        //Menu Fichier
        MenuItem miNouveau       = new MenuItem("Nouveau");
        MenuItem miOuvrir        = new MenuItem("Ouvrir…");
        MenuItem miSauvegarder   = new MenuItem("Sauvegarder");
        MenuItem miSauvegarderSous = new MenuItem("Sauvegarder sous…");
        MenuItem miExportJSON    = new MenuItem("Exporter en JSON…");
        MenuItem miQuitter       = new MenuItem("Quitter");

        // Raccourcis clavier
        miNouveau.setAccelerator(javafx.scene.input.KeyCombination.keyCombination("Ctrl+N"));
        miOuvrir.setAccelerator(javafx.scene.input.KeyCombination.keyCombination("Ctrl+O"));
        miSauvegarder.setAccelerator(javafx.scene.input.KeyCombination.keyCombination("Ctrl+S"));
        miSauvegarderSous.setAccelerator(javafx.scene.input.KeyCombination.keyCombination("Ctrl+Shift+S"));

        //Actions
        miNouveau.setOnAction(e -> actionNouveau(stage));

        miOuvrir.setOnAction(e -> actionOuvrir(stage));

        miSauvegarder.setOnAction(e -> {
            boolean ok = gestionFichier.sauvegarder(stage, items);
            if (ok) mettreAJourTitre(stage);
        });

        miSauvegarderSous.setOnAction(e -> {
            boolean ok = gestionFichier.sauvegarderSous(stage, items);
            if (ok) mettreAJourTitre(stage);
        });

        miExportJSON.setOnAction(e -> GestionFichier.buildString(items));

        miQuitter.setOnAction(e -> stage.close());

        Menu menuFichier = new Menu("Fichier");
        menuFichier.getItems().addAll(
                miNouveau, miOuvrir,
                new SeparatorMenuItem(),
                miSauvegarder, miSauvegarderSous,
                new SeparatorMenuItem(),
                miExportJSON,
                new SeparatorMenuItem(),
                miQuitter);

        return new MenuBar(menuFichier);
    }

    //Actions menu fichier
    private void actionNouveau(Stage stage) {
        // Réinitialiser l'inventaire avant de passer les références
        inventaire = new Inventaire();
        boolean ok = gestionFichier.nouveau(stage, inventaire, masterList, items);
        if (ok) {
            clearForm();
            mettreAJourTitre(stage);
        }
    }

    private void actionOuvrir(Stage stage) {
        inventaire = new Inventaire();
        boolean ok = gestionFichier.ouvrir(stage, inventaire, masterList, items);
        if (ok) {
            clearForm();
            mettreAJourTitre(stage);
            tableView.refresh();
        }
    }

    private void mettreAJourTitre(Stage stage) {
        Path fichierCourant = gestionFichier.getFichierCourant();
        if (fichierCourant != null) {
            stage.setTitle("Gestionnaire d'inventaire — " + fichierCourant.getFileName());
        } else {
            stage.setTitle("Gestionnaire d'inventaire");
        }
    }

    // Toolbar
    @SuppressWarnings("unused")
    public ToolBar buildToolBar() {
        Button btnA = new Button("+"), btnS = new Button("−"), btnD = new Button("📋");
        btnA.setOnAction(e -> clearForm());
        btnS.setOnAction(e -> supprimerItemView());
        btnD.setOnAction(e -> dupliquerItemView());

        comboFiltre = new ComboBox<>();
        comboFiltre.getItems().add("Tous");
        for (Status s : Status.values()) comboFiltre.getItems().add(s.name());
        comboFiltre.setValue("Tous");
        comboFiltre.setOnAction(e -> appliquerFiltre());

        txtRecherche = new TextField();
        txtRecherche.setPromptText("Recherche par nom");
        txtRecherche.setPrefWidth(180);
        txtRecherche.textProperty().addListener((obs, o, n) -> appliquerFiltre());

        return new ToolBar(btnA, btnS, new Separator(), btnD, new Separator(),
                new Label("Filtre"), comboFiltre, txtRecherche);
    }

    // Table
    @SuppressWarnings({"deprecation", "unused"})
    public TableView<Objet> buildTable() {
        TableView<Objet> tv = new TableView<>(items);

        TableColumn<Objet, String> colDesc = new TableColumn<>("Description");
        colDesc.setCellValueFactory(d -> new SimpleStringProperty(descriptionOf(d.getValue())));
        colDesc.setPrefWidth(380);

        TableColumn<Objet, String> colEtat = new TableColumn<>("État");
        colEtat.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus().name()));
        colEtat.setPrefWidth(90);

        TableColumn<Objet, String> colDate = new TableColumn<>("Date d'achat");
        colDate.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getDateAchat().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        colDate.setPrefWidth(110);

        TableColumn<Objet, String> colPrix = new TableColumn<>("Prix");
        colPrix.setCellValueFactory(d -> new SimpleStringProperty(
                String.format("%.2f$", d.getValue().getPrix())));
        colPrix.setPrefWidth(70);

        tv.getColumns().addAll(colDesc, colEtat, colDate, colPrix);
        tv.setPlaceholder(new Label("Aucun contenu dans la table"));
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> { if (sel != null) peuplerFormulaire(sel); });
        return tv;
    }

    // Panneau droit (formulaire)
    @SuppressWarnings("unused")
    public VBox buildRightPanel() {
        titrePanneau = new Label("Nouvel item d'inventaire");
        titrePanneau.setStyle("-fx-font-size:15; -fx-font-weight:bold;");

        comboType = new ComboBox<>();
        comboType.getItems().addAll("Livre", "Jeux", "Outils");
        comboType.setPromptText("Sélectionner…");
        comboType.setPrefWidth(160);
        comboType.valueProperty().addListener((obs, old, nv) -> changerTypeView(nv));

        txtNom   = new TextField(); txtNom.setPrefWidth(200);
        txtPrix  = new TextField(); txtPrix.setPrefWidth(200);
        spinnerQuantite = new Spinner<>(1, 9999, 1); spinnerQuantite.setEditable(true);
        datePicker = new DatePicker(LocalDate.now());

        comboStatus = new ComboBox<>();
        comboStatus.getItems().addAll(Status.values());
        comboStatus.setValue(Status.POSSESSION);

        txtImageFacture = new TextField();
        txtImageFacture.setEditable(false);
        txtImageFacture.setPrefWidth(148);

        Button btnBrowse = new Button("📄");
        btnBrowse.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
            File file = fc.showOpenDialog(null);
            if (file != null) txtImageFacture.setText(file.getAbsolutePath());
        });

        zoneDynamique = new VBox(8);

        btnAdd = new Button("Add");
        Button btnEffacer = new Button("Effacer");
        btnEffacer.setStyle("-fx-background-color:#e07070; -fx-text-fill:white;");
        btnAdd.setOnAction(e -> refreshItemView());
        btnEffacer.setOnAction(e -> supprimerItemView());

        Label titreGen = new Label("Section générale");
        titreGen.setStyle("-fx-font-size:13;-fx-font-weight:bold;");

        HBox rowBtns = new HBox(8, btnAdd, btnEffacer);
        rowBtns.setAlignment(Pos.CENTER_RIGHT);

        VBox panel = new VBox(12,
                titrePanneau,
                row("Type d'objet:", comboType),
                new Separator(), titreGen,
                row("Nom:", txtNom),
                row("Prix:", txtPrix),
                row("Quantité:", spinnerQuantite),
                row("Date d'achat:", datePicker),
                row("Image facture:", new HBox(4, txtImageFacture, btnBrowse)),
                row("État:", comboStatus),
                new Separator(), zoneDynamique,
                new Separator(), rowBtns);

        panel.setPadding(new Insets(16));
        panel.setStyle("-fx-background-color:#f0f0f0;");
        return panel;
    }

    // Logique formulaire
    public void changerTypeView(String type) {
        zoneDynamique.getChildren().clear();
        if (type == null) return;
        switch (type) {
            case "Livre"  -> { livreView  = new LivreView(null, ViewMode.CREATE);  zoneDynamique.getChildren().add(livreView.getRoot()); }
            case "Jeux"   -> { jeuxView   = new JeuxView(null, ViewMode.CREATE);   zoneDynamique.getChildren().add(jeuxView.getRoot()); }
            case "Outils" -> { outilsView = new OutilsView(null, ViewMode.CREATE); zoneDynamique.getChildren().add(outilsView.getRoot()); }
        }
    }

    public void refreshItemView() {
        String type = comboType.getValue(), nom = txtNom.getText().trim();
        if (type == null || nom.isEmpty()) {
            Global.afficherInformation("Le type et le nom sont obligatoires.");
            return;
        }

        // Validation des champs spécifiques au type
        switch (type) {
            case "Livre" -> {
                if (livreView == null || livreView.getAuteur().isEmpty() || livreView.getMaisonEdition().isEmpty()) {
                    Global.afficherInformation("Veuillez remplir l'auteur et la maison d'édition.");
                    return;
                }
            }
            case "Jeux" -> {
                if (jeuxView == null || jeuxView.getPublisher().isEmpty() || jeuxView.getCompanie().isEmpty()) {
                    Global.afficherInformation("Veuillez remplir le publisher et la compagnie.");
                    return;
                }
            }
            case "Outils" -> {
                if (outilsView == null || outilsView.getMarque().isEmpty() || outilsView.getUtilite().isEmpty()) {
                    Global.afficherInformation("Veuillez remplir la marque et l'utilité.");
                    return;
                }
            }
        }

        double prix = parsePrix(txtPrix.getText());
        int qte  = spinnerQuantite.getValue();
        Status stat = comboStatus.getValue();
        String cheminComplet = txtImageFacture.getText().trim();

        if (objetEnEdition != null) {
            boolean typeChange = !objetEnEdition.getClass().getSimpleName().equalsIgnoreCase(type);

            if (typeChange) {
                String image = cheminComplet.isEmpty() ? "" : Paths.get(cheminComplet).getFileName().toString();
                Objet nouvel = switch (type) {
                    case "Livre"  -> livreView.buildLivre(nom, prix, qte, stat, image);
                    case "Jeux"   -> jeuxView.buildJeux(nom, prix, qte, stat, image);
                    case "Outils" -> outilsView.buildOutils(nom, prix, qte, stat, image);
                    default -> null;
                };
                if (nouvel == null) return;

                if (!cheminComplet.isEmpty()) nouvel.setImageFacture(cheminComplet);
                if (datePicker.getValue() != null) nouvel.setDateAchat(datePicker.getValue());

                int idxMaster = masterList.indexOf(objetEnEdition);
                int idxItems  = items.indexOf(objetEnEdition);

                if (idxMaster >= 0) masterList.set(idxMaster, nouvel);
                if (idxItems  >= 0) items.set(idxItems, nouvel);

                inventaire.delObjet(idxMaster);
                inventaire.addObjetAt(idxMaster, nouvel);

            } else {
                objetEnEdition.setNom(nom);
                objetEnEdition.setPrix(prix);
                objetEnEdition.setQuantite(qte);
                objetEnEdition.setStatus(stat);

                if (!cheminComplet.isEmpty()) objetEnEdition.setImageFacture(cheminComplet);
                if (datePicker.getValue() != null) objetEnEdition.setDateAchat(datePicker.getValue());

                if (objetEnEdition instanceof Livre l && livreView != null) { l.setAuteur(livreView.getAuteur()); l.setMaisonEdition(livreView.getMaisonEdition()); }
                else if (objetEnEdition instanceof Jeux j && jeuxView != null) { j.setPublisher(jeuxView.getPublisher()); j.setCompanie(jeuxView.getCompanie()); }
                else if (objetEnEdition instanceof Outils o && outilsView != null) { o.setMarqueFabrication(outilsView.getMarque()); o.setUtilite(outilsView.getUtilite()); }

                tableView.refresh();
            }
            clearForm();

        } else {
            String image = cheminComplet.isEmpty() ? "" : Paths.get(cheminComplet).getFileName().toString();

            Objet nouvel = switch (type) {
                case "Livre"  -> livreView.buildLivre(nom, prix, qte, stat, image);
                case "Jeux"   -> jeuxView.buildJeux(nom, prix, qte, stat, image);
                case "Outils" -> outilsView.buildOutils(nom, prix, qte, stat, image);
                default -> null;
            };
            if (nouvel == null) return;

            if (!cheminComplet.isEmpty()) nouvel.setImageFacture(cheminComplet);
            if (datePicker.getValue() != null) nouvel.setDateAchat(datePicker.getValue());

            inventaire.addObjet(nouvel);
            masterList.add(nouvel);
            items.add(nouvel);
            clearForm();
        }
    }

    public void supprimerItemView() {
        int idx = tableView.getSelectionModel().getSelectedIndex();
        if (idx < 0) { Global.afficherInformation("Sélectionnez un item à supprimer."); return; }
        new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer « " + items.get(idx).getNom() + " » ?",
                ButtonType.YES, ButtonType.NO)
                .showAndWait()
                .ifPresent(bt -> {
                    if (bt == ButtonType.YES) {
                        masterList.remove(items.get(idx));
                        inventaire.delObjet(idx);
                        items.remove(idx);
                        clearForm();
                    }
                });
    }

    public void dupliquerItemView() {
        Objet sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) { Global.afficherInformation("Sélectionnez un item à dupliquer."); return; }

        Objet copie = null;
        switch (sel) {
            case Livre l ->
                    copie = new Livre(l.getNom() + " (copie)", l.getPrix(), l.getQuantite(), l.getStatus(), l.getImageFacture(), l.getAuteur(), l.getMaisonEdition());
            case Jeux j ->
                    copie = new Jeux(j.getNom() + " (copie)", j.getPrix(), j.getQuantite(), j.getStatus(), j.getImageFacture(), j.getPublisher(), j.getCompanie());
            case Outils o ->
                    copie = new Outils(o.getNom() + " (copie)", o.getPrix(), o.getQuantite(), o.getStatus(), o.getImageFacture(), o.getMarqueFabrication(), o.getUtilite());
            default -> {}
        }

        if (copie != null) {
            copie.setDateAchat(sel.getDateAchat());
            inventaire.addObjet(copie);
            masterList.add(copie);
            items.add(copie);
        }
    }

    public void peuplerFormulaire(Objet o) {
        objetEnEdition = o;
        titrePanneau.setText("Modifier : " + o.getNom());
        txtNom.setText(o.getNom());
        txtPrix.setText(String.format("%.2f", o.getPrix()));
        spinnerQuantite.getValueFactory().setValue(o.getQuantite());
        comboStatus.setValue(o.getStatus());
        datePicker.setValue(o.getDateAchat());

        if (o.getImageFacture() != null && !o.getImageFacture().isEmpty())
            txtImageFacture.setText(o.getImageFacture());
        else
            txtImageFacture.clear();

        //combotype pour affichage de section
        switch (o) {
            case Livre l -> {
                comboType.setValue("Livre");
                livreView = new LivreView(l, ViewMode.EDIT);
                zoneDynamique.getChildren().setAll(livreView.getRoot());
            }
            case Jeux j -> {
                comboType.setValue("Jeux");
                jeuxView = new JeuxView(j, ViewMode.EDIT);
                zoneDynamique.getChildren().setAll(jeuxView.getRoot());
            }
            case Outils ot -> {
                comboType.setValue("Outils");
                outilsView = new OutilsView(ot, ViewMode.EDIT);
                zoneDynamique.getChildren().setAll(outilsView.getRoot());
            }
            default -> {}
        }

        btnAdd.setText("Edit");
    }

    //Clear le panneau de droite
    public void clearForm() {
        objetEnEdition = null;
        titrePanneau.setText("Nouvel item d'inventaire");
        comboType.setValue(null);
        txtNom.clear(); txtPrix.clear();
        spinnerQuantite.getValueFactory().setValue(1);
        comboStatus.setValue(Status.POSSESSION);
        datePicker.setValue(LocalDate.now());
        txtImageFacture.clear();
        zoneDynamique.getChildren().clear();
        tableView.getSelectionModel().clearSelection();
        btnAdd.setText("Add");
    }

    public void appliquerFiltre() {
        String f = comboFiltre.getValue(), q = txtRecherche.getText().toLowerCase();
        items.setAll(masterList.filtered(o ->
                ("Tous".equals(f) || o.getStatus().name().equals(f)) && (q.isBlank() || o.getNom().toLowerCase().contains(q))));
    }

    // Utilitaires statiques
    public static String descriptionOf(Objet o) {
        if (o instanceof Livre  l)  return String.format("%s, auteur %s, maison d'éd. %s", l.getNom(), l.getAuteur(), l.getMaisonEdition());
        if (o instanceof Jeux   j)  return String.format("%s, publié par %s, développé par %s", j.getNom(), j.getPublisher(), j.getCompanie());
        if (o instanceof Outils ot) return String.format("%s, marque %s, utilité %s", ot.getNom(), ot.getMarqueFabrication(), ot.getUtilite());
        return o.getNom();
    }

    public static double parsePrix(String s) {
        try { return Double.parseDouble(s.replace(",", ".")); } catch (NumberFormatException e) { return 0.0; }
    }

    public static HBox row(String label, javafx.scene.Node ctrl) {
        Label lbl = new Label(label);
        lbl.setMinWidth(120);
        lbl.setAlignment(Pos.CENTER_LEFT);
        HBox r = new HBox(8, lbl, ctrl);
        r.setAlignment(Pos.CENTER_LEFT);
        return r;
    }
}