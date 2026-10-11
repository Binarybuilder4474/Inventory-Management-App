package org.example.ia999;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Items_In_Stock implements Initializable {

    @FXML
    private VBox itemsContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadInStockItems();
    }

    private void loadInStockItems() {
        itemsContainer.getChildren().clear();
        String query = "SELECT Product_Name, Product_Number, Quantity, 'Interior' AS Source FROM product WHERE Quantity > 5 " +
                "UNION ALL " +
                "SELECT Product_Name, Product_Number, Quantity, 'Exterior' AS Source FROM product3 WHERE Quantity > 5";

        try (Connection con = new Product_Database().getDatabaselink();
             PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            boolean hasItems = false;

            while (rs.next()) {
                hasItems = true;
                String name = rs.getString("Product_Name");
                String number = rs.getString("Product_Number");
                int quantity = rs.getInt("Quantity");
                String source = rs.getString("Source");

                HBox row = createRow(name, number, quantity, source);
                itemsContainer.getChildren().add(row);
            }

            if (!hasItems) {
                Label emptyLabel = new Label("No items currently in stock.");
                emptyLabel.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 16px;");
                emptyLabel.setFont(new Font(16));
                itemsContainer.getChildren().add(emptyLabel);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private HBox createRow(String name, String number, int quantity, String source) {
        HBox row = new HBox();
        row.setSpacing(0);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPrefHeight(50);
        row.setStyle("-fx-background-color: #1a1a1a; -fx-background-radius: 8; -fx-border-color: #333333; -fx-border-radius: 8; -fx-border-width: 1;");
        row.setPadding(new Insets(0, 15, 0, 15));

        Label nameLabel = createCell(name, 280);
        nameLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px;");

        Label numberLabel = createCell(number, 200);
        numberLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 14px;");

        Label quantityLabel = createCell(String.valueOf(quantity), 130);
        quantityLabel.setStyle("-fx-text-fill: #00ff1e; -fx-font-size: 14px; -fx-font-weight: bold;");

        String sourceBg = source.equals("Interior") ? "#2a1a00" : "#001a2a";
        String sourceColor = source.equals("Interior") ? "#facc15" : "#38bdf8";
        Label sourceLabel = createCell(source, 130);
        sourceLabel.setStyle("-fx-text-fill: " + sourceColor + "; -fx-font-size: 13px; -fx-font-weight: bold; " +
                "-fx-background-color: " + sourceBg + "; -fx-background-radius: 5; -fx-padding: 3 10 3 10;");

        row.getChildren().addAll(nameLabel, numberLabel, quantityLabel, sourceLabel);
        return row;
    }

    private Label createCell(String text, double width) {
        Label label = new Label(text);
        label.setPrefWidth(width);
        label.setFont(new Font(14));
        return label;
    }

    public void goBack(ActionEvent oo) throws IOException {
        The_main u = new The_main();
        u.changescene("Afterlogin.fxml");
    }
}