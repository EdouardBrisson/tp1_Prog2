package com.example.tpprog;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public final class Global {

    public static void afficherInformation(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }

    public static void afficherErreur(String message) {
        new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).showAndWait();
    }

}
