package com.example.tpprog;
import javafx.application.Application;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    public GlobalView globalView = new GlobalView();

    @Override
    public void start(Stage stage) {
        stage.setTitle("Gestionnaire d'inventaire");
        this.globalView.tableView = this.globalView.buildTable();
        VBox leftPane = new VBox(globalView.buildToolBar(), this.globalView.tableView);
        VBox.setVgrow(this.globalView.tableView, Priority.ALWAYS);
        ScrollPane scroll = new ScrollPane(this.globalView.buildRightPanel());
        scroll.setFitToWidth(true); scroll.setMinWidth(320); scroll.setMaxWidth(400);
        HBox main = new HBox(leftPane, scroll);
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        VBox root = new VBox(this.globalView.buildMenuBar(stage), main);
        VBox.setVgrow(main, Priority.ALWAYS);
        stage.setScene(new Scene(root, 1050, 700));
        stage.show();
    }
}