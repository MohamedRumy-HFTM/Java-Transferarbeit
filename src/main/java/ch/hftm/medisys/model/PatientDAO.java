package ch.hftm.medisys.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import ch.hftm.medisys.util.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Data Access Object für Patient-Entität
 * Implementiert CRUD-Operationen gemäss FA01 (Patientenverwaltung)
 * Berücksichtigt GR07 (Löschrestriktionen) und GR06 (Validierung)
 */
public class PatientDAO {
    
    private Connection connection;
    
    public PatientDAO() throws SQLException {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
        } catch (IOException e) {
            throw new SQLException("Datenbankverbindung konnte nicht hergestellt werden", e);
        }
    }
    
    /**
     * CREATE: Neuen Patienten in Datenbank einfügen
     * Entspricht UC01 (Use Case Diagramm Seite 17)
     */
    public boolean insertPatient(Patient patient) throws SQLException {
        String sql = "INSERT INTO Patient (Vorname, Nachname, Geburtsdatum, Geschlecht, " +
                     "Strasse, PLZ, Ort, Telefon, Email, Versicherungsnummer, Krankenkasse) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, 
                Statement.RETURN_GENERATED_KEYS)) {
            
            // Validierung vor dem Einfügen
            if (!patient.isValidGeburtsdatum()) {
                throw new SQLException("Ungültiges Geburtsdatum: darf nicht in der Zukunft liegen");
            }
            if (!patient.isValidGeschlecht()) {
                throw new SQLException("Ungültiges Geschlecht: muss M, W oder D sein");
            }
            
            pstmt.setString(1, patient.getVorname());
            pstmt.setString(2, patient.getNachname());
            pstmt.setDate(3, Date.valueOf(patient.getGeburtsdatum()));
            pstmt.setString(4, patient.getGeschlecht());
            pstmt.setString(5, patient.getStrasse());
            pstmt.setString(6, patient.getPlz());
            pstmt.setString(7, patient.getOrt());
            pstmt.setString(8, patient.getTelefon());
            pstmt.setString(9, patient.getEmail());
            pstmt.setString(10, patient.getVersicherungsnummer());
            pstmt.setString(11, patient.getKrankenkasse());
            
            int affectedRows = pstmt.executeUpdate();
            
            // PatientID aus Auto-Increment holen
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        patient.setPatientID(generatedKeys.getInt(1));
                    }
                }
            }
            
            return affectedRows > 0;
        }
    }
    
    /**
     * READ: Alle Patienten aus Datenbank laden
     * Entspricht UC02 (Patienten suchen)
     * Nutzt idx_patient_name Index für Performance (Spezifikation Seite 16)
     */
    public ObservableList<Patient> getAllPatients() throws SQLException {
        ObservableList<Patient> patients = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Patient ORDER BY Nachname, Vorname";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Patient patient = extractPatientFromResultSet(rs);
                patients.add(patient);
            }
        }
        
        return patients;
    }
    
    /**
     * READ: Patient anhand ID suchen
     */
    public Patient getPatientById(int patientID) throws SQLException {
        String sql = "SELECT * FROM Patient WHERE PatientID = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, patientID);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractPatientFromResultSet(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * READ: Patienten nach Namen suchen (Volltextsuche)
     * Entspricht FA01: Patientensuche über verschiedene Kriterien
     */
    public ObservableList<Patient> searchPatients(String searchTerm) throws SQLException {
        ObservableList<Patient> patients = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Patient WHERE " +
                     "Vorname LIKE ? OR Nachname LIKE ? OR " +
                     "Versicherungsnummer LIKE ? OR Email LIKE ? " +
                     "ORDER BY Nachname, Vorname";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Patient patient = extractPatientFromResultSet(rs);
                    patients.add(patient);
                }
            }
        }
        
        return patients;
    }
    
    /**
     * UPDATE: Patientendaten aktualisieren
     * Entspricht UC03 (Patientendaten bearbeiten)
     */
    public boolean updatePatient(Patient patient) throws SQLException {
        String sql = "UPDATE Patient SET " +
                     "Vorname = ?, Nachname = ?, Geburtsdatum = ?, Geschlecht = ?, " +
                     "Strasse = ?, PLZ = ?, Ort = ?, Telefon = ?, Email = ?, " +
                     "Versicherungsnummer = ?, Krankenkasse = ? " +
                     "WHERE PatientID = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            // Validierung vor dem Update
            if (!patient.isValidGeburtsdatum()) {
                throw new SQLException("Ungültiges Geburtsdatum: darf nicht in der Zukunft liegen");
            }
            if (!patient.isValidGeschlecht()) {
                throw new SQLException("Ungültiges Geschlecht: muss M, W oder D sein");
            }
            
            pstmt.setString(1, patient.getVorname());
            pstmt.setString(2, patient.getNachname());
            pstmt.setDate(3, Date.valueOf(patient.getGeburtsdatum()));
            pstmt.setString(4, patient.getGeschlecht());
            pstmt.setString(5, patient.getStrasse());
            pstmt.setString(6, patient.getPlz());
            pstmt.setString(7, patient.getOrt());
            pstmt.setString(8, patient.getTelefon());
            pstmt.setString(9, patient.getEmail());
            pstmt.setString(10, patient.getVersicherungsnummer());
            pstmt.setString(11, patient.getKrankenkasse());
            pstmt.setInt(12, patient.getPatientID());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * DELETE: Patient löschen
     * Entspricht UC05 (Patient archivieren)
     * ACHTUNG: Berücksichtigt GR07 - in der Praxis würde man nur als "inaktiv" markieren
     * Hier vereinfacht als echtes DELETE implementiert
     */
    public boolean deletePatient(int patientID) throws SQLException {
        // Prüfen ob Patient Termine hat (DELETE RESTRICT aus Schema)
        if (hasRelatedRecords(patientID)) {
            throw new SQLException(
                "Patient kann nicht gelöscht werden: Es existieren verknüpfte Termine oder Rechnungen. " +
                "Gemäss GR07 sollten Patientendaten nur als inaktiv markiert werden."
            );
        }
        
        String sql = "DELETE FROM Patient WHERE PatientID = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, patientID);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * Prüft ob Patient verknüpfte Datensätze hat (für DELETE RESTRICT)
     */
    private boolean hasRelatedRecords(int patientID) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Termin WHERE PatientID = ? " +
                     "UNION ALL " +
                     "SELECT COUNT(*) FROM Rechnung WHERE PatientID = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, patientID);
            pstmt.setInt(2, patientID);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    if (rs.getInt(1) > 0) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * Hilfsmethode: Patient-Objekt aus ResultSet erstellen
     */
    private Patient extractPatientFromResultSet(ResultSet rs) throws SQLException {
        int patientID = rs.getInt("PatientID");
        String vorname = rs.getString("Vorname");
        String nachname = rs.getString("Nachname");
        LocalDate geburtsdatum = rs.getDate("Geburtsdatum").toLocalDate();
        String geschlecht = rs.getString("Geschlecht");
        String strasse = rs.getString("Strasse");
        String plz = rs.getString("PLZ");
        String ort = rs.getString("Ort");
        String telefon = rs.getString("Telefon");
        String email = rs.getString("Email");
        String versicherungsnummer = rs.getString("Versicherungsnummer");
        String krankenkasse = rs.getString("Krankenkasse");
        
        return new Patient(patientID, vorname, nachname, geburtsdatum, geschlecht,
                          strasse, plz, ort, telefon, email, versicherungsnummer, krankenkasse);
    }
    
    /**
     * Datenbankverbindung schliessen
     */
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}