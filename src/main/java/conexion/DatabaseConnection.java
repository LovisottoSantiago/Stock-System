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
    private boolean isConnected = false;

    public Connection getConnection(String username, String password) {
        try {
            Connection conn = DriverManager.getConnection(URL, username, password);
            isConnected = true;
            return conn;
        } catch (SQLException e) {
            isConnected = false;
            return null;
        }
    }

    public void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                logger.log(Level.INFO, "Database connection closed successfully.");
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Error closing the database connection.", e);
            }
        }
    }

    public boolean getIsConnected () {
        return isConnected;
    }

}
