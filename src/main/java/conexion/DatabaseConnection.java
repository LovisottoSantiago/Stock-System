package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {

    private static final Logger logger = Logger.getLogger(DatabaseConnection.class.getName());
    private static final String URL = "jdbc:postgresql://localhost:5432/stock-system";

    public Connection getConnection(String username, String password) {
        try {
            System.out.println("Connection successful");
            return DriverManager.getConnection(URL, username, password);
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "An exception has occurred", e);
            return  null;
        }
    }

}
