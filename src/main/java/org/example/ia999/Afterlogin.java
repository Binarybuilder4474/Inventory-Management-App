package org.example.ia999;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Afterlogin implements Initializable {

    @FXML
    private Label TotalStockItems;
    @FXML
    private Label LowStockItems;
    @FXML
    private Label OutStockItems;
    @FXML
    private Label investmentLabel;
    @FXML
    private Label profitLabel;
    @FXML
    private Label profitMarginLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SummaryInventory();
        SummaryFinance();

        TotalStockItems.setOnMouseClicked(event -> {
            try {
                The_main b = new The_main();
                b.changescene("Items_In_Stock.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        LowStockItems.setOnMouseClicked(event -> {
            try {
                The_main b = new The_main();
                b.changescene("Items_Low_Stock.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        OutStockItems.setOnMouseClicked(event -> {
            try {
                The_main b = new The_main();
                b.changescene("Items_Out_Stock.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }



    public void userlogout(ActionEvent ab) throws IOException {
        The_main a = new The_main();
        a.changescene("Log_in.fxml");
    }

    public void Inventory_1(ActionEvent bc) throws IOException {
        The_main b = new The_main();
        b.changescene("product.fxml");
    }

    public void Inventory_2(ActionEvent kk) throws IOException {
        The_main d = new The_main();
        d.changescene("product2.fxml");
    }

    public void Finance(ActionEvent kl) throws IOException {
        The_main e = new The_main();
        e.changescene("product_sale.fxml");
    }

    public void List(ActionEvent kl) throws IOException {
        The_main e = new The_main();
        e.changescene("To_Do_List.fxml");
    }



    private void SummaryInventory() {
        int ProductLowInStock = 0;
        int ProductOutOfStock = 0;
        int ProductInStock = 0;

        String query = "SELECT Quantity FROM product UNION ALL SELECT Quantity FROM product3";

        try (Connection con = new Product_Database().getDatabaselink();
             PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rst = stmt.executeQuery()) {

            while (rst.next()) {
                int quantity = rst.getInt("Quantity");

                if (quantity > 5) {
                    ProductInStock++;
                } else if (quantity > 0) {
                    ProductLowInStock++;
                } else {
                    ProductOutOfStock++;
                }
            }

            TotalStockItems.setText(String.valueOf(ProductInStock));
            LowStockItems.setText(String.valueOf(ProductLowInStock));
            OutStockItems.setText(String.valueOf(ProductOutOfStock));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }





    private void SummaryFinance() {
        String query = """
        SELECT
            -- Total investment: (stock + sold) * initial price
            (
                SELECT SUM((stock.Quantity + IFNULL(sale.Quantity_Sold, 0)) * stock.Initial_Price)
                FROM (
                    SELECT Product_Name, Product_Number, Initial_Price, Quantity
                    FROM product
                    UNION ALL
                    SELECT Product_Name, Product_Number, Initial_Price, Quantity
                    FROM product3
                ) stock
                LEFT JOIN (
                    SELECT Product_Name, Product_Number, SUM(Quantity_Sold) AS Quantity_Sold
                    FROM product_sale
                    GROUP BY Product_Name, Product_Number
                ) sale
                ON LOWER(TRIM(stock.Product_Name)) = LOWER(TRIM(sale.Product_Name))
                AND stock.Product_Number = sale.Product_Number
            ) AS Total_Investment,

            -- Total revenue: selling price * quantity sold
            (
                SELECT SUM(ps.Selling_Price * ps.Quantity_Sold)
                FROM product_sale ps
            ) AS Total_Revenue;
    """;

        try (Connection con = new Product_Database().getDatabaselink();
             PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                double investment = rs.getDouble("Total_Investment");
                double revenue = rs.getDouble("Total_Revenue");
                double profit;
                if (revenue == 0.0) {
                    profit = 0.0;
                } else {
                    profit = revenue - investment;
                }
                investmentLabel.setText(String.format("Rs %.2f", investment));
                profitLabel.setText(String.format("Rs %.2f", profit));
                profitMarginLabel.setText(String.format("Rs %.2f", revenue));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}






