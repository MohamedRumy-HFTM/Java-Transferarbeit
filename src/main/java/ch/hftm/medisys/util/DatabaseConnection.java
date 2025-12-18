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
        
        connection = DriverManager.getConnection(url, user, password);
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