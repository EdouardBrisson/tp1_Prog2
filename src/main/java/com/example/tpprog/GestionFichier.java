package com.example.tpprog;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class GestionFichier {
    private Path fichierCourant = null;

    public Path getFichierCourant() { return fichierCourant; }

    public boolean nouveau(Stage stage, Inventaire inventaire, ObservableList<Objet> masterList, ObservableList<Objet> items) {
        FileChooser fc = nouveauFileChooser("Nouveau fichier d'inventaire");
        File choix = fc.showSaveDialog(stage);
        if (choix == null) return false;

        Path cible = assureExtensionDat(choix.toPath());

        // Créer le répertoire si nécessaire
        try {
            Files.createDirectories(cible.getParent());
        } catch (IOException e) {
            Global.afficherErreur("Impossible de créer le répertoire : " + e.getMessage());
            return false;
        }

        // Écrire un fichier vide (liste sérialisée vide)
        if (!ecrireFichier(cible, new ArrayList<>())) return false;

        fichierCourant = cible;

        // Vide les listes en mémoire
        masterList.clear();
        items.clear();
        // Réinitialise linventaire
        reinitialiserInventaire(inventaire, new ArrayList<>());
        return true;
    }

    // Ouvrir
    public boolean ouvrir(Stage stage, Inventaire inventaire, ObservableList<Objet> masterList, ObservableList<Objet> items) {

        FileChooser fc = nouveauFileChooser("Ouvrir un inventaire");
        File choix = fc.showOpenDialog(stage);
        if (choix == null) return false;

        Path cible = choix.toPath();
        List<Objet> objets = lireFichier(cible);
        if (objets == null) return false; // erreur déjà affichée

        fichierCourant = cible;
        masterList.setAll(objets);
        items.setAll(objets);
        reinitialiserInventaire(inventaire, objets);

        // Résoudre les chemins d'images relatifs au dossier images/
        Path dossierImages = dossierImages(fichierCourant);
        for (Objet o : objets) {
            if (o.getImageFacture() != null && !o.getImageFacture().isBlank()) {
                Path img = dossierImages.resolve(o.getImageFacture());
                if (Files.exists(img)) {
                    o.setImageFacture(img.toAbsolutePath().toString());
                }
            }
        }

        return true;
    }

    //Sauvegarder
    public boolean sauvegarder(Stage stage, ObservableList<Objet> items) {
        if (fichierCourant == null) return sauvegarderSous(stage, items);
        return sauvegarderVers(fichierCourant, items);
    }

    //Sauvegarder sous
    public boolean sauvegarderSous(Stage stage, ObservableList<Objet> items) {
        FileChooser fc = nouveauFileChooser("Sauvegarder sous…");
        File choix = fc.showSaveDialog(stage);
        if (choix == null) return false;

        Path cible = assureExtensionDat(choix.toPath());

        if (Files.exists(cible)) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Le fichier « " + cible.getFileName() + " » existe déjà.\nVoulez-vous le remplacer ?",
                    ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Confirmer l'écrasement");
            Optional<ButtonType> rep = confirm.showAndWait();
            if (rep.isEmpty() || rep.get() == ButtonType.NO) return false;
        }

        if (!sauvegarderVers(cible, items)) return false;
        fichierCourant = cible;
        return true;
    }

    private Path dossierImages(Path fichier) {
        return fichier.getParent().resolve("images");
    }

    private boolean sauvegarderVers(Path cible, ObservableList<Objet> items) {
        // Copie les images avant d'écrire
        List<Objet> copie = new ArrayList<>(items);

        // Créer une version de sauvegarde avec les noms d'images relatifs
        List<String> cheminsOriginaux = new ArrayList<>();
        for (Objet o : copie) {
            cheminsOriginaux.add(o.getImageFacture());
            if (o.getImageFacture() != null && !o.getImageFacture().isBlank()) {
                Path p = Paths.get(o.getImageFacture());
                if (p.isAbsolute() && Files.exists(p)) {
                    // Copie l'image dans images/
                    Path dossierImages = dossierImages(cible);
                    try {
                        Files.createDirectories(dossierImages);
                        Path dest = dossierImages.resolve(p.getFileName());
                        if (!p.toAbsolutePath().equals(dest.toAbsolutePath())) {
                            Files.copy(p, dest, StandardCopyOption.REPLACE_EXISTING);
                        }
                        // Stocker temporairement le nom relatif
                        o.setImageFacture(p.getFileName().toString());
                    } catch (IOException e) {
                        System.out.println("Avertissement : image non copiée : " + e.getMessage());
                    }
                }
            }
        }

        boolean succes = ecrireFichier(cible, copie);

        for (int i = 0; i < copie.size(); i++) {
            copie.get(i).setImageFacture(cheminsOriginaux.get(i));
        }

        return succes;
    }

    private boolean ecrireFichier(Path cible, List<Objet> objets) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(Files.newOutputStream(cible,
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)))) {
            oos.writeObject(objets);
            System.out.println("Sauvegarde réussie : " + cible);
            return true;
        } catch (IOException e) {
            Global.afficherErreur("Erreur lors de la sauvegarde :\n" + e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private List<Objet> lireFichier(Path cible) {
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(cible)))) {
            Object lu = ois.readObject();
            if (lu instanceof List<?> liste) {
                return (List<Objet>) liste;
            }
            Global.afficherErreur("Format de fichier non reconnu.");
            return null;
        } catch (InvalidClassException e) {
            Global.afficherErreur("Le fichier est incompatible avec cette version de l'application.\n" + e.getMessage());
            return null;
        } catch (IOException e) {
            Global.afficherErreur("Erreur lors de l'ouverture du fichier :\n" + e.getMessage());
            return null;
        } catch (ClassNotFoundException e) {
            Global.afficherErreur("Classe introuvable lors de la désérialisation :\n" + e.getMessage());
            return null;
        }
    }

    private void reinitialiserInventaire(Inventaire inventaire, List<Objet> objets) {
        // Vide l'inventaire en supprimant depuis la fin
        for (Objet o : objets) {
            inventaire.addObjet(o);
        }
    }

    //Juste pour assurer que le fichier se sauvegarde en .dat
    private Path assureExtensionDat(Path p) {
        String nom = p.getFileName().toString();
        if (!nom.toLowerCase().endsWith(".dat")) {
            return p.getParent().resolve(nom + ".dat");
        }
        return p;
    }

    private FileChooser nouveauFileChooser(String titre) {
        FileChooser fc = new FileChooser();
        fc.setTitle(titre);
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers d'inventaire (*.dat)", "*.dat"));
        return fc;
    }

    //chemin utilisé pour stocker les sauvegardes/exports
    private final Path cheminJSON = Paths.get("C:\\Users\\2583138\\OneDrive - Cegep de Lanaudiere\\Programmation\\Session 2\\TpProg\\src\\main\\java\\Save");

    private void exportJSON(String nom, String contenu) {
        File file = cheminJSON.resolve(nom).toFile();
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(contenu);
            writer.close();
            System.out.println("Fichier JSON exporté avec succès !");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Creer les strings pour les fichiers JSON
    public static void buildString(ObservableList<Objet> items) {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < items.size(); i++) {
            Objet o = items.get(i);
            sb.append("  { \"nom\": \"").append(o.getNom())
                    .append("\", \"prix\": ").append(o.getPrix())
                    .append(", \"quantite\": ").append(o.getQuantite())
                    .append(", \"status\": \"").append(o.getStatus().name())
                    .append("\", \"image\": \"").append(o.getImageFacture())
                    .append("\", \"dateAchat\": \"").append(o.getDateAchat()).append("\" }");
            if (i < items.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");

        GestionFichier gestion = new GestionFichier();
        gestion.exportJSON("inventaire.json", sb.toString());
    }
}