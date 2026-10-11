package org.example.ia999;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class To_Do_List implements Initializable {

    @FXML
    private TextField Idbar;
    @FXML
    private TextField Taskbar;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Button Addbutton;
    @FXML
    private Button Updatebutton;
    @FXML
    private Button Deletebutton;
    @FXML
    private VBox Vvbox;
    @FXML
    private ScrollPane scrollPane;

    private final ObservableList<Model_Class> Tasklist = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Data();
    }

    public void Data() {
        Tasklist.clear();
        Vvbox.getChildren().clear();

        try {
            Connection con = new Product_Database().getDatabaselink();
            String query = "SELECT * FROM to_do_list";
            Statement stmt = con.createStatement();
            ResultSet rst = stmt.executeQuery(query);
            while (rst.next()) {
                LocalDate date = null;
                Date sqlDate = rst.getDate("task_date");
                if (sqlDate != null) {
                    date = sqlDate.toLocalDate();
                }

                Model_Class task = new Model_Class(
                        rst.getInt("id"),
                        rst.getString("task_text"),
                        date,
                        rst.getBoolean("completed")
                );
                Tasklist.add(task);
                HBox taskBox = createBox(task);
                Vvbox.getChildren().add(taskBox);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HBox createBox(Model_Class task) {
        HBox box = new HBox(15);
        box.setStyle("-fx-padding: 10px; -fx-border-color: #333333; -fx-border-width: 0 0 1 0; -fx-background-color: #1a1a1a; -fx-background-radius: 6;");
        box.setMinWidth(HBox.USE_PREF_SIZE);

        Label idLabel = new Label(String.valueOf(task.getId()));
        idLabel.setMinWidth(40);
        idLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #facc15; -fx-font-weight: bold;");

        Label taskLabel = new Label(task.getTask_text());
        taskLabel.setMinWidth(300);
        taskLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + (task.isCompleted() ? "#888888; -fx-strikethrough: true;" : "#ffffff;"));

        String dateText = task.getTask_date() != null ? task.getTask_date().toString() : "No date";
        Label dateLabel = new Label(dateText);
        dateLabel.setMinWidth(120);
        dateLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #aaaaaa;");

        Button doneButton = new Button(task.isCompleted() ? "Done" : "Mark Done");
        doneButton.setMinWidth(100);
        doneButton.setStyle(task.isCompleted()
                ? "-fx-background-color: #166534; -fx-text-fill: #4ade80; -fx-background-radius: 5; -fx-font-size: 13px;"
                : "-fx-background-color: #333333; -fx-text-fill: #facc15; -fx-background-radius: 5; -fx-font-size: 13px;");

        doneButton.setOnAction(e -> {
            if (task.isCompleted()) {
                task.setCompleted(false);
                taskLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #ffffff;");
                doneButton.setText("Mark Done");
                doneButton.setStyle("-fx-background-color: #333333; -fx-text-fill: #facc15; -fx-background-radius: 5; -fx-font-size: 13px;");
                updateCompletionInDB(task.getId(), false);
            } else {
                task.setCompleted(true);
                taskLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #888888; -fx-strikethrough: true;");
                doneButton.setText("Done");
                doneButton.setStyle("-fx-background-color: #166534; -fx-text-fill: #4ade80; -fx-background-radius: 5; -fx-font-size: 13px;");
                updateCompletionInDB(task.getId(), true);
            }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        box.setOnMouseClicked(event -> mouseClick(task, box));
        box.getChildren().addAll(idLabel, taskLabel, dateLabel, spacer, doneButton);
        return box;
    }

    private void updateCompletionInDB(int id, boolean completed) {
        try {
            Connection con = new Product_Database().getDatabaselink();
            String sql = "UPDATE to_do_list SET completed = ? WHERE id = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setBoolean(1, completed);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mouseClick(Model_Class task, HBox selectedBox) {
        Idbar.setText(String.valueOf(task.getId()));
        Taskbar.setText(task.getTask_text());
        if (task.getTask_date() != null) {
            datePicker.setValue(task.getTask_date());
        } else {
            datePicker.setValue(null);
        }

        for (javafx.scene.Node node : Vvbox.getChildren()) {
            if (node instanceof HBox) {
                node.setStyle("-fx-padding: 10px; -fx-border-color: #333333; -fx-border-width: 0 0 1 0; -fx-background-color: #1a1a1a; -fx-background-radius: 6;");
            }
        }
        selectedBox.setStyle("-fx-padding: 10px; -fx-border-color: #facc15; -fx-border-width: 1; -fx-background-color: #2a2200; -fx-background-radius: 6;");
    }

    @FXML
    private void OnAdd() {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = new Product_Database().getDatabaselink();
            con.setAutoCommit(false);

            String query = "INSERT INTO to_do_list (id, task_text, task_date) VALUES (?, ?, ?)";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(Idbar.getText()));
            stmt.setString(2, Taskbar.getText());
            stmt.setDate(3, datePicker.getValue() != null ? Date.valueOf(datePicker.getValue()) : null);
            stmt.executeUpdate();
            con.commit();
            clear();
            Data();

        } catch (Exception e) {
            e.printStackTrace();
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (con != null) con.setAutoCommit(true);
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @FXML
    private void OnUpdate() {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = new Product_Database().getDatabaselink();
            con.setAutoCommit(false);

            String query = "UPDATE to_do_list SET task_text = ?, task_date = ? WHERE id = ?";
            stmt = con.prepareStatement(query);
            stmt.setString(1, Taskbar.getText());
            stmt.setDate(2, datePicker.getValue() != null ? Date.valueOf(datePicker.getValue()) : null);
            stmt.setInt(3, Integer.parseInt(Idbar.getText()));
            stmt.executeUpdate();
            con.commit();
            clear();
            Data();

        } catch (Exception e) {
            e.printStackTrace();
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (con != null) con.setAutoCommit(true);
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @FXML
    private void OnDelete() {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = new Product_Database().getDatabaselink();
            con.setAutoCommit(false);

            String query = "DELETE FROM to_do_list WHERE id = ?";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(Idbar.getText()));
            stmt.executeUpdate();
            con.commit();
            clear();
            Data();

        } catch (Exception e) {
            e.printStackTrace();
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (con != null) con.setAutoCommit(true);
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @FXML
    private void OnClearAll() {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = new Product_Database().getDatabaselink();
            con.setAutoCommit(false);

            String query = "DELETE FROM to_do_list";
            stmt = con.prepareStatement(query);
            stmt.executeUpdate();
            con.commit();
            clear();
            Data();

        } catch (Exception e) {
            e.printStackTrace();
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (con != null) con.setAutoCommit(true);
                if (con != null) con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void clear() {
        Idbar.clear();
        Taskbar.clear();
        datePicker.setValue(null);
    }

    public void goBack(ActionEvent oo) throws IOException {
        The_main u = new The_main();
        u.changescene("Afterlogin.fxml");
    }
}