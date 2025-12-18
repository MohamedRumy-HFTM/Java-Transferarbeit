package ch.hftm.medisys;

import java.io.IOException;

import ch.hftm.medisys.controller.PatientController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Hauptklasse für MediSys Praxisverwaltungssystem
 * JavaFX Application Entry Point
 * 
 * Transferprojekt: Datenbankgestütztes Anwendungssystem
 * Autor: Mohamed Rumy
 * Klasse: BBIN24.2a
 * Abgabe: 27.01.2025
 */
public class MediSysApplication extends Application {
    
    private PatientController patientController;
    
    /**
     * Start-Methode der JavaFX-Anwendung
     * Wird automatisch nach init() aufgerufen
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // FXML laden
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/PatientView.fxml")
            );
            Parent root = loader.load();
            
            // Controller-Referenz speichern für Shutdown
            patientController = loader.getController();
            
            // Scene erstellen
            Scene scene = new Scene(root, 1200, 800);
            
            // CSS ist bereits in FXML referenziert, wird automatisch geladen
            
            // Stage konfigurieren
            primaryStage.setTitle("MediSys - Praxisverwaltungssystem | BBIN24.2a");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(700);
            
            // Optional: Icon setzen (falls vorhanden)
            try {
                Image icon = new Image(
                    getClass().getResourceAsStream("/images/medisys-icon.png")
                );
                primaryStage.getIcons().add(icon);
            } catch (Exception e) {
                // Icon nicht gefunden - kein Problem, einfach ohne Icon
                System.out.println("Info: Kein Anwendungs-Icon gefunden");
            }
            
            // Shutdown-Hook für sauberes Beenden
            primaryStage.setOnCloseRequest(event -> {
                shutdown();
            });
            
            // Fenster anzeigen
            primaryStage.show();
            
            System.out.println("===========================================");
            System.out.println("MediSys Praxisverwaltungssystem gestartet");
            System.out.println("Autor: Mohamed Rumy | BBIN24.2a");
            System.out.println("===========================================");
            
        } catch (IOException e) {
            System.err.println("Fehler beim Laden der FXML-Datei:");
            e.printStackTrace();
            showErrorAndExit("Kritischer Fehler beim Starten der Anwendung", 
                           e.getMessage());
        }
    }
    
    /**
     * Stop-Methode der JavaFX-Anwendung
     * Wird beim Beenden aufgerufen
     */
    @Override
    public void stop() {
        shutdown();
        System.out.println("MediSys wurde beendet.");
    }
    
    /**
     * Cleanup: Datenbankverbindungen schließen
     */
    private void shutdown() {
        if (patientController != null) {
            patientController.shutdown();
        }
    }
    
    /**
     * Fehler-Dialog anzeigen und Anwendung beenden
     */
    private void showErrorAndExit(String title, String message) {
        System.err.println("FEHLER: " + title);
        System.err.println("Details: " + message);
        System.err.println("\nBitte überprüfen Sie:");
        System.err.println("1. Datenbankverbindung (database.properties)");
        System.err.println("2. FXML-Datei vorhanden (/fxml/PatientView.fxml)");
        System.err.println("3. MySQL-Server läuft");
        System.exit(1);
    }
    
    /**
     * Main-Methode - Einstiegspunkt der Anwendung
     */
    public static void main(String[] args) {
        // Systeminfo ausgeben
        System.out.println("===========================================");
        System.out.println("MediSys Praxisverwaltungssystem");
        System.out.println("Transfer-Projekt BBIN24.2a");
        System.out.println("===========================================");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("JavaFX Version: " + System.getProperty("javafx.version"));
        System.out.println("Betriebssystem: " + System.getProperty("os.name"));
        System.out.println("===========================================\n");
        
        // JavaFX-Anwendung starten
        launch(args);
    }
}