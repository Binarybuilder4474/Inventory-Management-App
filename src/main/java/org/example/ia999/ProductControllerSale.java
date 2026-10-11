package org.example.ia999;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ProductControllerSale implements Initializable {

    @FXML
    private TableView<ProductSaleModel> saleTable;
    @FXML
    private TableColumn<ProductSaleModel, String> saleNameColumn;
    @FXML
    private TableColumn<ProductSaleModel, String> saleNumberColumn;
    @FXML
    private TableColumn<ProductSaleModel, Double> salePriceColumn;
    @FXML
    private TableColumn<ProductSaleModel, Integer> saleQuantityColumn;

    @FXML
    private TextField saleNameField;
    @FXML
    private TextField saleNumberField;
    @FXML
    private TextField salePriceField;
    @FXML
    private TextField saleQuantityField;
    @FXML
    private TextField Serach;
    @FXML
    private Button ClearSoldButton;


    private final ObservableList<ProductSaleModel> saleList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadSalesData();
        Search();
        FieldSelection();
    }



    private void loadSalesData() {
        saleList.clear();
        try (Connection conn = new Product_Database().getDatabaselink()) {
            String query = "SELECT * FROM product_sale";
            ResultSet rs = conn.createStatement().executeQuery(query);

            while (rs.next()) {
                saleList.add(new ProductSaleModel(
                        rs.getString("Product_Name"),
                        rs.getString("Product_Number"),
                        rs.getDouble("Selling_Price"),
                        rs.getInt("Quantity_Sold")
                ));
            }

            saleNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
            saleNumberColumn.setCellValueFactory(new PropertyValueFactory<>("productNumber"));
            salePriceColumn.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));
            saleQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantitySold"));

            Search();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private void Search() {
        FilteredList<ProductSaleModel> filteredData = new FilteredList<>(saleList, b -> true);

        Serach.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(product -> {
                if (newVal == null || newVal.isEmpty()) {
                    return true;
                }

                String searchKeyword = newVal.toLowerCase();

                if (product.getProductName().toLowerCase().contains(searchKeyword)) {
                    return true;
                } else if (product.getProductNumber().toLowerCase().contains(searchKeyword)) {
                    return true;
                } else if (String.valueOf(product.getSellingPrice()).contains(searchKeyword)) {
                    return true;
                } else if (String.valueOf(product.getQuantitySold()).contains(searchKeyword)) {
                    return true;
                } else {
                    return false;
                }
            });
        });

        SortedList<ProductSaleModel> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(saleTable.comparatorProperty());
        saleTable.setItems(sortedData);
    }



    private void FieldSelection() {
        saleTable.setOnMouseClicked(event -> {
            ProductSaleModel selected = saleTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                saleNameField.setText(String.valueOf(selected.getProductName()));
                saleNumberField.setText (selected.getProductNumber());
                salePriceField.setText(String.valueOf(selected.getSellingPrice()));
                saleQuantityField.setText(String.valueOf(selected.getQuantitySold()));

            }
        });
    }


    private void clearSaleFields() {
        saleNameField.clear();
        saleNumberField.clear();
        salePriceField.clear();
        saleQuantityField.clear();
    }




    @FXML
    private void onUpdateSale() {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = new Product_Database().getDatabaselink();
            String query = "UPDATE product_sale SET Selling_Price=?, Quantity_Sold=? WHERE Product_Number=?";
            stmt = conn.prepareStatement(query);
            stmt.setDouble(1, Double.parseDouble(salePriceField.getText()));
            stmt.setInt(2, Integer.parseInt(saleQuantityField.getText()));
            stmt.setString(3, saleNumberField.getText());
            stmt.executeUpdate();
            The_main x = new The_main();
            x.showInfo("Updated Successfully", "Product " + saleNameField.getText()  + " has been updated successfully");

            clearSaleFields();
            loadSalesData();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }





    public void backsales(ActionEvent oo) throws IOException {
        The_main u = new The_main();
        u.changescene("Afterlogin.fxml");
    }






    @FXML
    private void ClearSoldButton(ActionEvent kk ) throws IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = new Product_Database().getDatabaselink();
            String query = "UPDATE product_sale SET Quantity_Sold = 0";
            stmt = conn.prepareStatement(query);
            stmt.executeUpdate();
            The_main x = new The_main();
            x.showInfo("Deleted Successfully", "All the records in the Sold column have been cleared successfully");
            loadSalesData();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }



}
