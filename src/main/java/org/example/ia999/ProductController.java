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

public class ProductController implements Initializable {
    @FXML
    private TableView<ProductModel> productTable;
    @FXML
    private TableColumn<ProductModel, Integer> idColumn;
    @FXML
    private TableColumn<ProductModel, String> nameColumn;
    @FXML
    private TableColumn<ProductModel, String> numberColumn;
    @FXML
    private TableColumn<ProductModel, String> contactColumn;
    @FXML
    private TableColumn<ProductModel, Double> initialPriceColumn;
    @FXML
    private TableColumn<ProductModel, Integer> quantityColumn;
    @FXML
    private TextField searchBar;
    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField numberField;
    @FXML
    private TextField contactField;
    @FXML
    private TextField initialPriceField;
    @FXML
    private TextField quantityField;
    @FXML
    private String originalProductNumber;

    private final ObservableList<ProductModel> productList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Data();
        Search();
        RowSelection();
    }

    private void Data() {
        productList.clear();
        try {
            Connection conn = new Product_Database().getDatabaselink();
            String query = "SELECT * FROM product";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                productList.add(new ProductModel(
                        rs.getInt("ID"),
                        rs.getString("Product_Name"),
                        rs.getString("Product_Number"),
                        rs.getString("Supplier_Contact"),
                        rs.getDouble("Initial_Price"),
                        rs.getInt("Quantity")

                ));
            }

            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
            numberColumn.setCellValueFactory(new PropertyValueFactory<>("productNumber"));
            contactColumn.setCellValueFactory(new PropertyValueFactory<>("supplierContact"));
            initialPriceColumn.setCellValueFactory(new PropertyValueFactory<>("initialPrice"));
            quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Search() {
        FilteredList<ProductModel> filteredData = new FilteredList<>(productList, b -> true);

        searchBar.textProperty().addListener((obs, oldVal, newVal) -> {
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
                } else if ((String.valueOf(productSearchModel.getInitialPrice()).contains(searchKeyword))) {
                    return true;

            } else {
                    return false;
                }
            });
        });
        SortedList<ProductModel> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(productTable.comparatorProperty());
        productTable.setItems(sortedData);
    }

    private void RowSelection() {
        productTable.setOnMouseClicked(event -> {
            ProductModel selected = productTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                idField.setText(String.valueOf(selected.getId()));
                nameField.setText(selected.getProductName());
                numberField.setText(selected.getProductNumber());
                contactField.setText(selected.getSupplierContact());
                initialPriceField.setText(String.valueOf(selected.getInitialPrice()));
                quantityField.setText(String.valueOf(selected.getQuantity()));

                originalProductNumber = selected.getProductNumber();
            }
        });
    }

    private void clear() {
        idField.clear();
        nameField.clear();
        numberField.clear();
        contactField.clear();
        quantityField.clear();
        initialPriceField.clear();
    }

    @FXML
    private void onAdd() {
        Connection conn = null;
        PreparedStatement stmt1 = null;
        PreparedStatement stmt2 = null;
        PreparedStatement checkIDStmt = null;
        PreparedStatement checkNumberStmt = null;

        try {
            conn = new Product_Database().getDatabaselink();

            String checkIDQuery = "SELECT COUNT(*) FROM product WHERE ID = ?";
            checkIDStmt = conn.prepareStatement(checkIDQuery);
            checkIDStmt.setInt(1, Integer.parseInt(idField.getText()));
            ResultSet rsID = checkIDStmt.executeQuery();

            if (rsID.next() && rsID.getInt(1) > 0) {
                The_main x = new The_main();
                x.showInfo("Duplicate ID", "A product with ID: " + idField.getText() + " already exists.");
                return;
            }

            String checkNumberQuery = "SELECT COUNT(*) FROM product WHERE Product_Number = ?";
            checkNumberStmt = conn.prepareStatement(checkNumberQuery);
            checkNumberStmt.setString(1, numberField.getText());
            ResultSet rsNumber = checkNumberStmt.executeQuery();

            if (rsNumber.next() && rsNumber.getInt(1) > 0) {
                The_main x = new The_main();
                x.showInfo("Duplicate Product Number", "A product with Product Number: " + numberField.getText() + " already exists.");
                return;
            }

            conn.setAutoCommit(false);

            String query1 = "INSERT INTO product (ID, Product_Name, Product_Number, Supplier_Contact, Initial_Price, Quantity) VALUES (?, ?, ?, ?, ?, ?)";
            stmt1 = conn.prepareStatement(query1);
            stmt1.setInt(1, Integer.parseInt(idField.getText()));
            stmt1.setString(2, nameField.getText());
            stmt1.setString(3, numberField.getText());
            stmt1.setString(4, contactField.getText());
            stmt1.setDouble(5, Double.parseDouble(initialPriceField.getText()));
            stmt1.setInt(6, Integer.parseInt(quantityField.getText()));
            stmt1.executeUpdate();

            String query2 = "INSERT INTO product_sale (Product_Name, Product_Number, Selling_Price, Quantity_Sold) VALUES (?, ?, ?, ?)";
            stmt2 = conn.prepareStatement(query2);
            stmt2.setString(1, nameField.getText());
            stmt2.setString(2, numberField.getText());
            stmt2.setDouble(3, 0);
            stmt2.setInt(4, 0);
            stmt2.executeUpdate();

            conn.commit();
            The_main x = new The_main();
            x.showInfo("Added Sucessfully","Product added successfully");
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
                if (checkIDStmt != null) checkIDStmt.close();
                if (checkNumberStmt != null) checkNumberStmt.close();
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
    private void onUpdate() {
        Connection conn = null;
        PreparedStatement stmt1 = null;
        PreparedStatement stmt2 = null;

        try {
            conn = new Product_Database().getDatabaselink();

            conn.setAutoCommit(false);


            String query1 = "UPDATE product SET Product_Name=?, Product_Number=?, Supplier_Contact=?, Initial_Price=?, Quantity=? WHERE ID=?";
            stmt1 = conn.prepareStatement(query1);
            stmt1.setString(1, nameField.getText());
            stmt1.setString(2, numberField.getText());
            stmt1.setString(3, contactField.getText());
            stmt1.setDouble(4, Double.parseDouble(initialPriceField.getText()));
            stmt1.setInt(5, Integer.parseInt(quantityField.getText()));
            stmt1.setInt(6, Integer.parseInt(idField.getText()));
            stmt1.executeUpdate();


            String query2 = "UPDATE product_sale SET Product_Name=?, Product_Number=? WHERE Product_Number=?";
            stmt2 = conn.prepareStatement(query2);
            stmt2.setString(1, nameField.getText());
            stmt2.setString(2, numberField.getText());
            stmt2.setString(3, originalProductNumber);
            stmt2.executeUpdate();

            conn.commit();
            The_main x = new The_main();
            x.showInfo("Updated Sucessfully","Product " +nameField.getText()+ " (ID: "  + idField.getText()+")" + " has been updated successfully");
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
    private void onDelete() {
        Connection conn = null;
        PreparedStatement stmt1 = null;
        PreparedStatement stmt2 = null;

        try {
            conn = new Product_Database().getDatabaselink();
            conn.setAutoCommit(false);

            String query1 = "DELETE FROM product WHERE ID = ?";
            stmt1 = conn.prepareStatement(query1);
            stmt1.setInt(1, Integer.parseInt(idField.getText()));
            stmt1.executeUpdate();

            String query2 = "DELETE FROM product_sale WHERE Product_Number = ?";
            stmt2 = conn.prepareStatement(query2);
            stmt2.setString(1, numberField.getText());
            stmt2.executeUpdate();

            conn.commit();
            The_main x = new The_main();
            x.showInfo("Deleted Sucessfully","Product " +nameField.getText()+ " (ID: "  + idField.getText()+")" + " has been deleted successfully");
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
    private void onRefresh() {
        Data();
    }

    public void Back1 (ActionEvent ct) throws IOException {
        The_main c = new The_main();
        c.changescene("afterlogin.fxml");
    }

}

