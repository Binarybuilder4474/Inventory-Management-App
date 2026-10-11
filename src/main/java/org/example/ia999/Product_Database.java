package org.example.ia999;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Product_Database {

    private Connection databaselink;

    public Connection getDatabaselink() {
        String url = "jdbc:mysql://localhost:3306/iadatabase";
        String username = "root";
        String password = "Your_Password";
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaselink = DriverManager.getConnection(url, username,password);
            System.out.println("connected to database");

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        return databaselink;
    }

}




