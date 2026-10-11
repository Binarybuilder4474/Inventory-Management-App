package org.example.ia999;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.application.Platform;
import java.io.IOException;

public class The_main extends Application {

    private static Stage stg;

    @Override
    public void start(Stage stage) throws IOException {
        stg = stage;
        stage.setResizable(false);

       FXMLLoader fxmlLoader = new FXMLLoader(The_main.class.getResource("Log_in.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 650);
        stage.setTitle("Inventory management app");
        stage.setScene(scene);
        stage.show();
    }
    public void changescene(String fxml) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(The_main.class.getResource(fxml));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 650);
        stg.setScene(scene);
        stg.show();

    }

        public void showInfo(String title, String message) {

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
            });
        }

    public static void main(String[] args) {

        launch();
    }
}











