package com.mycompany.mavenproject3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/camellya";
    private static final String USER = "root"; // ganti sesuai user MySQL Anda
    private static final String PASS = "";     // ganti sesuai password MySQL Anda

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}