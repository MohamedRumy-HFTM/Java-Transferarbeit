package ch.hftm.medisys.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton-Klasse für Datenbankverbindung gemäss GR07 (Referentielle Integrität)
 */
public class DatabaseConnection {
    
    private static DatabaseConnection instance;
    private Connection connection;
    private Properties properties;
    
    private DatabaseConnection() throws SQLException, IOException {
        loadProperties();
        connect();
    }
    
    private void loadProperties() throws IOException {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new IOException("Database configuration file not found");
            }
            properties.load(input);
        }
    }
    
    private void connect() throws SQLException {
    String url = properties.getProperty("db.url");
    String user = properties.getProperty("db.username");
    String password = properties.getProperty("db.password");
    
    // DEBUG: Ausgabe der Verbindungsparameter
    System.out.println("\n=== DATABASE CONNECTION DEBUG ===");
    System.out.println("URL: " + url);
    System.out.println("User: " + user);
    System.out.println("Password: " + (password == null || password.isEmpty() ? "(leer)" : "(gesetzt)"));
    System.out.println("Driver: " + properties.getProperty("db.driver"));
    
    try {
        connection = DriverManager.getConnection(url, user, password);
        System.out.println("✅ Verbindung erfolgreich hergestellt!");
        System.out.println("=================================\n");
    } catch (SQLException e) {
        System.err.println("❌ FEHLER bei Verbindung:");
        System.err.println("SQL State: " + e.getSQLState());
        System.err.println("Error Code: " + e.getErrorCode());
        System.err.println("Message: " + e.getMessage());
        System.err.println("=================================\n");
        throw e;
    }
}
    
    public static DatabaseConnection getInstance() throws SQLException, IOException {
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    public Connection getConnection() {
        return connection;
    }
    
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}