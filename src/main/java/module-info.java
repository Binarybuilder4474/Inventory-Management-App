module org.example.ia999 {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;

    requires com.dlsc.formsfx;
    requires java.desktop;
    requires jdk.compiler;

    opens org.example.ia999 to javafx.fxml;
    exports org.example.ia999;
}