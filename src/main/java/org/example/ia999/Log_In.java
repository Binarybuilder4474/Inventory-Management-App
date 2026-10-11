package org.example.ia999;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import java.io.IOException;

public class Log_In {
    @FXML
    private Button button;
    @FXML
    private Label wrong;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;

    public void userlogin(ActionEvent event)throws IOException{
        checklogin();

    }
    public void checklogin() throws IOException{
        The_main c = new The_main();

        if (username.getText().toString().equals("Boost") && password.getText().toString().equals("123.")) {
           c.changescene("afterlogin.fxml");


        } else if  (username.getText().isEmpty() && password.getText().isEmpty()) {
            wrong.setText("Please Enter Data");
        }
        else {
            wrong.setText("Wrong password or username");
        }
    }
}




