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

public class ProductController2 implements Initializable {

    @FXML
    private TableView<ProductModel> productTable2;
    @FXML
    private TableColumn<ProductModel, Integer> idColumn2;
    @FXML
    private TableColumn<ProductModel, String> nameColumn2;
    @FXML
    private TableColumn<ProductModel, String> numberColumn2;
    @FXML
    private TableColumn<ProductModel, String> contactColumn2;
    @FXML
    private TableColumn<ProductModel, Double> initialPriceColumn2;
    @FXML
    private TableColumn<ProductModel, Integer> quantityColumn2;

    @FXML
    private TextField searchBar2;
    @FXML
    private TextField idField2;
    @FXML
    private TextField nameField2;
    @FXML
    private TextField numberField2;
    @FXML
    private TextField contactField2;
    @FXML
    private TextField quantityField2;
    @FXML
    private TextField initialPriceField2;
    @FXML
    private String originalProductNumber2;

    private final ObservableList<ProductModel> productList2 = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Data();
        Search();
        RowSelection();
    }



    private void Data() {

        productList2.clear();
        try {
            Connection conn = new Product_Database().getDatabaselink();
            String query = "SELECT * FROM product3";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);


            while (rs.next()) {
                productList2.add(new ProductModel(
                        rs.getInt("ID"),
                        rs.getString("Product_Name"),
                        rs.getString("Product_Number"),
                        rs.getString("Supplier_Contact"),
                        rs.getDouble("Initial_Price"),
                        rs.getInt("Quantity")
                        ));
            }


            idColumn2.setCellValueFactory(new PropertyValueFactory<>("id"));
            nameColumn2.setCellValueFactory(new PropertyValueFactory<>("productName"));
            numberColumn2.setCellValueFactory(new PropertyValueFactory<>("productNumber"));
            contactColumn2.setCellValueFactory(new PropertyValueFactory<>("supplierContact"));
            initialPriceColumn2.setCellValueFactory(new PropertyValueFactory<>("initialPrice"));
            quantityColumn2.setCellValueFactory(new PropertyValueFactory<>("quantity"));


            Search();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Search() {
        FilteredList<ProductModel> filteredData = new FilteredList<>(productList2, b -> true);

        searchBar2.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(productSearchModel -> {
                if (newVal == null || newVal.isEmpty()) {
                    return true;
                }

                String searchKeyword = newVal.toLowerCase();

                if (productSearchModel.getProductName().toLowerCase().indexOf(searchKeyword) > -1) {
                    return true;
                } else if (productSearchModel.getProductNumber().toLowerCase().indexOf(searchKeyword) > -1) {
                    return true;
                } else if (productSearchModel.getSupplierContact().toLowerCase().indexOf(searchKeyword) > -1) {
                    return true;
                } else if (productSearchModel.getId().toString().indexOf(searchKeyword) > -1) {
                    return true;
                } else if (productSearchModel.getQuantity().toString().indexOf(searchKeyword) > -1) {
                    return true;
                } else {
                    return false;
                }
            });
        });


        SortedList<ProductModel> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(productTable2.comparatorProperty());
        productTable2.setItems(sortedData);
    }

    private void RowSelection() {
        productTable2.setOnMouseClicked(event -> {
            ProductModel selected = productTable2.getSelectionModel().getSelectedItem();
            if (selected != null) {
                idField2.setText(String.valueOf(selected.getId()));
                nameField2.setText(selected.getProductName());
                numberField2.setText(selected.getProductNumber());
                contactField2.setText(selected.getSupplierContact());
                initialPriceField2.setText(String.valueOf(selected.getInitialPrice()));
                quantityField2.setText(String.valueOf(selected.getQuantity()));

                originalProductNumber2 = selected.getProductNumber();
            }
        });
    }

    private void clear() {
        idField2.clear();
        nameField2.clear();
        numberField2.clear();
        contactField2.clear();
        quantityField2.clear();
        initialPriceField2.clear();
    }

    @FXML
    private void onAdd2() {
        Connection conn = null;
        PreparedStatement stmt1 = null;
        PreparedStatement stmt2 = null;
        PreparedStatement checkIDStmt = null;
        PreparedStatement checkNumberStmt = null;


        try {
             conn = new Product_Database().getDatabaselink();

            String checkIDQuery = "SELECT COUNT(*) FROM product3 WHERE ID = ?";
            checkIDStmt = conn.prepareStatement(checkIDQuery);
            checkIDStmt.setInt(1, Integer.parseInt(idField2.getText()));
            ResultSet rsID = checkIDStmt.executeQuery();

            if (rsID.next() && rsID.getInt(1) > 0) {
                The_main x = new The_main();
                x.showInfo("Duplicate ID", "A product with ID: " + idField2.getText() + " already exists.");
                return;
            }

            String checkNumberQuery = "SELECT COUNT(*) FROM product3 WHERE Product_Number = ?";
            checkNumberStmt = conn.prepareStatement(checkNumberQuery);
            checkNumberStmt.setString(1, numberField2.getText());
            ResultSet rsNumber = checkNumberStmt.executeQuery();

            if (rsNumber.next() && rsNumber.getInt(1) > 0) {
                The_main x = new The_main();
                x.showInfo("Duplicate Product Number", "A product with Product Number: " + numberField2.getText() + " already exists.");
                return;
            }

            conn.setAutoCommit(false);

            String query1 = "INSERT INTO product3 (ID, Product_Name, Product_Number, Supplier_Contact,Initial_Price, Quantity) VALUES (?, ?, ?, ?, ?,?)";
            stmt1 = conn.prepareStatement(query1);
            stmt1.setInt(1, Integer.parseInt(idField2.getText()));
            stmt1.setString(2, nameField2.getText());
            stmt1.setString(3, numberField2.getText());
            stmt1.setString(4, contactField2.getText());
            stmt1.setDouble(5,Double.parseDouble(initialPriceField2.getText()));
            stmt1.setInt(6, Integer.parseInt(quantityField2.getText()));
            stmt1.executeUpdate();


            String query2 = "INSERT INTO product_sale (Product_Name, Product_Number, Selling_Price, Quantity_Sold)  VALUES (?,?,?,?) ";
            stmt2 = conn.prepareStatement(query2);
            stmt2.setString(1,nameField2.getText());
            stmt2.setString(2,numberField2.getText());
            stmt2.setDouble(3,0);
            stmt2.setInt(4,0);
            stmt2.executeUpdate();

            conn.commit();
            The_main x = new The_main();
            x.showInfo("Added Sucessfully","Product added successfully");
            Data();
            clear();


        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback(); // rollback on failure
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (stmt1 != null) stmt1.close();
                if (stmt2 != null) stmt2.close();
                if (conn != null) conn.setAutoCommit(true);
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    private void onUpdate2() {
        Connection conn = null;
        PreparedStatement stmt = null;
        PreparedStatement stmt2 = null;

        try {
            conn = new Product_Database().getDatabaselink();
            conn.setAutoCommit(false);

            String query = "UPDATE product3 SET Product_Name=?, Product_Number=?, Supplier_Contact=?,Initial_Price=?, Quantity=? WHERE ID=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, nameField2.getText());
            stmt.setString(2, numberField2.getText());
            stmt.setString(3, contactField2.getText());
            stmt.setDouble(4,Double.parseDouble(initialPriceField2.getText()));
            stmt.setInt(5, Integer.parseInt(quantityField2.getText()));
            stmt.setInt(6, Integer.parseInt(idField2.getText()));
            stmt.executeUpdate();



            String query2 = "UPDATE product_sale SET Product_Name=?, Product_Number=? WHERE Product_Number=?";
            stmt2 = conn.prepareStatement(query2);
            stmt2.setString(1, nameField2.getText());
            stmt2.setString(2, numberField2.getText());
            stmt2.setString(3, originalProductNumber2);
            stmt2.executeUpdate();

            conn.commit();
            The_main x = new The_main();
            x.showInfo("Updated Sucessfully","Product " +nameField2.getText()+ " (ID: "  + idField2.getText()+")" + " has been updated successfully");
            Data();
            clear();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (stmt2 != null) stmt2.close();
                if (conn != null) conn.setAutoCommit(true);
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }




    @FXML
    private void onDelete2() {
        Connection conn = null;
        PreparedStatement stmt1 = null;
        PreparedStatement stmt2 = null;

        try {
            conn = new Product_Database().getDatabaselink();
            conn.setAutoCommit(false);

            String query1 = "DELETE FROM product3 WHERE ID = ?";
            stmt1 = conn.prepareStatement(query1);
            stmt1.setInt(1, Integer.parseInt(idField2.getText()));
            stmt1.executeUpdate();

            String query2 = "DELETE FROM product_sale WHERE Product_Number = ?";
            stmt2 = conn.prepareStatement(query2);
            stmt2.setString(1, numberField2.getText());
            stmt2.executeUpdate();

            conn.commit();
            The_main x = new The_main();
            x.showInfo("Deleted Sucessfully","Product " +nameField2.getText()+ " (ID: "  + idField2.getText()+")" + " has been deleted successfully");
            Data();
            clear();

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (stmt1 != null) stmt1.close();
                if (stmt2 != null) stmt2.close();
                if (conn != null) conn.setAutoCommit(true);
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }




    @FXML
    private void onRefresh2() {
        Data();
    }

    public void Back2 (ActionEvent ct) throws IOException {
        The_main c = new The_main();
        c.changescene("afterlogin.fxml");
    }

}

