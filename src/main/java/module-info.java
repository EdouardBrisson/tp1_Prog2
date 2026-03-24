module com.example.tpprog {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.tpprog to javafx.fxml;
    exports com.example.tpprog;
}