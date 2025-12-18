package ch.hftm.medisys.controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

import ch.hftm.medisys.model.Patient;
import ch.hftm.medisys.model.PatientDAO;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

/**
 * Controller für Patientenverwaltung (MVC-Pattern)
 * Entspricht Use Case Diagramm Patientenverwaltung (Seite 17)
 * Implementiert FA01: Patientenverwaltung mit CRUD-Operationen
 */
public class PatientController {
    
    // Tabelle und Spalten
    @FXML private TableView<Patient> patientTable;
    @FXML private TableColumn<Patient, Integer> colPatientID;
    @FXML private TableColumn<Patient, String> colVorname;
    @FXML private TableColumn<Patient, String> colNachname;
    @FXML private TableColumn<Patient, LocalDate> colGeburtsdatum;
    @FXML private TableColumn<Patient, String> colGeschlecht;
    @FXML private TableColumn<Patient, String> colTelefon;
    @FXML private TableColumn<Patient, String> colEmail;
    @FXML private TableColumn<Patient, String> colKrankenkasse;
    
    // Eingabefelder
    @FXML private TextField txtVorname;
    @FXML private TextField txtNachname;
    @FXML private DatePicker dpGeburtsdatum;
    @FXML private ComboBox<String> cmbGeschlecht;
    @FXML private TextField txtStrasse;
    @FXML private TextField txtPLZ;
    @FXML private TextField txtOrt;
    @FXML private TextField txtTelefon;
    @FXML private TextField txtEmail;
    @FXML private TextField txtVersicherungsnummer;
    @FXML private TextField txtKrankenkasse;
    
    // Buttons
    @FXML private Button btnNeu;
    @FXML private Button btnSpeichern;
    @FXML private Button btnLoeschen;
    @FXML private Button btnAbbrechen;
    
    // Suche
    @FXML private TextField txtSuche;
    @FXML private Button btnSuchen;
    
    // Status-Label
    @FXML private Label lblStatus;
    
    // DAO für Datenbankzugriff
    private PatientDAO patientDAO;
    
    // Aktuell ausgewählter Patient (für Update)
    private Patient selectedPatient;
    
    /**
     * Initialisierung des Controllers (wird automatisch nach FXML-Laden aufgerufen)
     */
    @FXML
    public void initialize() {
        try {
            // DAO initialisieren
            patientDAO = new PatientDAO();
            
            // TableView konfigurieren
            setupTableView();
            
            // ComboBox für Geschlecht befüllen (gemäss GR06: M, W, D)
            cmbGeschlecht.getItems().addAll("M", "W", "D");
            
            // Daten laden
            loadAllPatients();
            
            // Initial: Eingabefelder deaktivieren
            setInputFieldsDisabled(true);
            btnSpeichern.setDisable(true);
            btnLoeschen.setDisable(true);
            btnAbbrechen.setDisable(true);
            
            showStatus("Bereit. " + patientTable.getItems().size() + " Patienten geladen.", false);
            
        } catch (SQLException e) {
            showError("Fehler bei der Initialisierung", e.getMessage());
        }
    }
    
    /**
     * TableView-Spalten konfigurieren
     */
    private void setupTableView() {
        colPatientID.setCellValueFactory(new PropertyValueFactory<>("patientID"));
        colVorname.setCellValueFactory(new PropertyValueFactory<>("vorname"));
        colNachname.setCellValueFactory(new PropertyValueFactory<>("nachname"));
        colGeburtsdatum.setCellValueFactory(new PropertyValueFactory<>("geburtsdatum"));
        colGeschlecht.setCellValueFactory(new PropertyValueFactory<>("geschlecht"));
        colTelefon.setCellValueFactory(new PropertyValueFactory<>("telefon"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colKrankenkasse.setCellValueFactory(new PropertyValueFactory<>("krankenkasse"));
        
        // Bei Auswahl in Tabelle: Daten in Formular laden
        patientTable.setOnMouseClicked(this::handleTableRowSelect);
    }
    
    /**
     * Alle Patienten aus Datenbank laden und in TableView anzeigen
     */
    private void loadAllPatients() {
        try {
            ObservableList<Patient> patients = patientDAO.getAllPatients();
            patientTable.setItems(patients);
            showStatus(patients.size() + " Patienten geladen.", false);
        } catch (SQLException e) {
            showError("Fehler beim Laden der Patienten", e.getMessage());
        }
    }
    
    /**
     * Button: Neuen Patienten anlegen (UC01)
     */
    @FXML
    private void handleNeuClick() {
        clearInputFields();
        setInputFieldsDisabled(false);
        btnSpeichern.setDisable(false);
        btnAbbrechen.setDisable(false);
        btnLoeschen.setDisable(true);
        selectedPatient = null;
        
        txtVorname.requestFocus();
        showStatus("Neue Patientenaufnahme...", false);
    }
    
    /**
     * Button: Patient speichern (INSERT oder UPDATE)
     */
    @FXML
    private void handleSpeichernClick() {
        try {
            // Validierung der Pflichtfelder
            if (!validateInput()) {
                return;
            }
            
            // Patient-Objekt erstellen/aktualisieren
            if (selectedPatient == null) {
                // Neuer Patient (INSERT)
                Patient newPatient = createPatientFromInput();
                
                if (patientDAO.insertPatient(newPatient)) {
                    showStatus("Patient '" + newPatient.getVorname() + " " + 
                              newPatient.getNachname() + "' erfolgreich angelegt.", false);
                    loadAllPatients();
                    clearInputFields();
                    setInputFieldsDisabled(true);
                    btnSpeichern.setDisable(true);
                    btnAbbrechen.setDisable(true);
                } else {
                    showError("Fehler", "Patient konnte nicht gespeichert werden.");
                }
                
            } else {
                // Bestehenden Patient aktualisieren (UPDATE)
                updatePatientFromInput(selectedPatient);
                
                if (patientDAO.updatePatient(selectedPatient)) {
                    showStatus("Patient '" + selectedPatient.getVorname() + " " + 
                              selectedPatient.getNachname() + "' erfolgreich aktualisiert.", false);
                    loadAllPatients();
                    clearInputFields();
                    setInputFieldsDisabled(true);
                    btnSpeichern.setDisable(true);
                    btnLoeschen.setDisable(true);
                    btnAbbrechen.setDisable(true);
                    selectedPatient = null;
                } else {
                    showError("Fehler", "Patient konnte nicht aktualisiert werden.");
                }
            }
            
        } catch (SQLException e) {
            showError("Datenbankfehler", e.getMessage());
        }
    }
    
    /**
     * Button: Patient löschen (UC05)
     * Berücksichtigt GR07: Löschrestriktionen
     */
    @FXML
    private void handleLoeschenClick() {
        if (selectedPatient == null) {
            showError("Kein Patient ausgewählt", "Bitte wählen Sie einen Patienten aus der Liste.");
            return;
        }
        
        // Sicherheitsabfrage
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Patient löschen");
        confirmDialog.setHeaderText("Möchten Sie diesen Patienten wirklich löschen?");
        confirmDialog.setContentText(selectedPatient.getVorname() + " " + 
                                    selectedPatient.getNachname() + " (ID: " + 
                                    selectedPatient.getPatientID() + ")");
        
        Optional<ButtonType> result = confirmDialog.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (patientDAO.deletePatient(selectedPatient.getPatientID())) {
                    showStatus("Patient erfolgreich gelöscht.", false);
                    loadAllPatients();
                    clearInputFields();
                    setInputFieldsDisabled(true);
                    btnLoeschen.setDisable(true);
                    btnAbbrechen.setDisable(true);
                    selectedPatient = null;
                } else {
                    showError("Fehler", "Patient konnte nicht gelöscht werden.");
                }
            } catch (SQLException e) {
                // Fehler durch DELETE RESTRICT (GR07)
                showError("Löschen nicht möglich", e.getMessage());
            }
        }
    }
    
    /**
     * Button: Bearbeitung abbrechen
     */
    @FXML
    private void handleAbbrechenClick() {
        clearInputFields();
        setInputFieldsDisabled(true);
        btnSpeichern.setDisable(true);
        btnLoeschen.setDisable(true);
        btnAbbrechen.setDisable(true);
        selectedPatient = null;
        patientTable.getSelectionModel().clearSelection();
        showStatus("Bearbeitung abgebrochen.", false);
    }
    
    /**
     * Button: Patienten suchen (UC02)
     */
    @FXML
    private void handleSuchenClick() {
        String searchTerm = txtSuche.getText().trim();
        
        if (searchTerm.isEmpty()) {
            loadAllPatients();
            return;
        }
        
        try {
            ObservableList<Patient> results = patientDAO.searchPatients(searchTerm);
            patientTable.setItems(results);
            showStatus(results.size() + " Patient(en) gefunden.", false);
        } catch (SQLException e) {
            showError("Suchfehler", e.getMessage());
        }
    }
    
    /**
     * Event: Zeile in Tabelle wurde angeklickt (UC04: Patientenakte einsehen)
     */
    private void handleTableRowSelect(MouseEvent event) {
        Patient patient = patientTable.getSelectionModel().getSelectedItem();
        
        if (patient != null) {
            selectedPatient = patient;
            loadPatientIntoForm(patient);
            setInputFieldsDisabled(false);
            btnSpeichern.setDisable(false);
            btnLoeschen.setDisable(false);
            btnAbbrechen.setDisable(false);
            showStatus("Patient ausgewählt: " + patient.getVorname() + " " + 
                      patient.getNachname(), false);
        }
    }
    
    /**
     * Patient-Daten in Formular laden (UC03)
     */
    private void loadPatientIntoForm(Patient patient) {
        txtVorname.setText(patient.getVorname());
        txtNachname.setText(patient.getNachname());
        dpGeburtsdatum.setValue(patient.getGeburtsdatum());
        cmbGeschlecht.setValue(patient.getGeschlecht());
        txtStrasse.setText(patient.getStrasse());
        txtPLZ.setText(patient.getPlz());
        txtOrt.setText(patient.getOrt());
        txtTelefon.setText(patient.getTelefon());
        txtEmail.setText(patient.getEmail());
        txtVersicherungsnummer.setText(patient.getVersicherungsnummer());
        txtKrankenkasse.setText(patient.getKrankenkasse());
    }
    
    /**
     * Neuen Patient aus Formulardaten erstellen
     */
    private Patient createPatientFromInput() {
        Patient patient = new Patient(
            txtVorname.getText().trim(),
            txtNachname.getText().trim(),
            dpGeburtsdatum.getValue(),
            cmbGeschlecht.getValue(),
            txtVersicherungsnummer.getText().trim(),
            txtKrankenkasse.getText().trim()
        );
        
        patient.setStrasse(txtStrasse.getText().trim());
        patient.setPlz(txtPLZ.getText().trim());
        patient.setOrt(txtOrt.getText().trim());
        patient.setTelefon(txtTelefon.getText().trim());
        patient.setEmail(txtEmail.getText().trim());
        
        return patient;
    }
    
    /**
     * Bestehenden Patient mit Formulardaten aktualisieren
     */
    private void updatePatientFromInput(Patient patient) {
        patient.setVorname(txtVorname.getText().trim());
        patient.setNachname(txtNachname.getText().trim());
        patient.setGeburtsdatum(dpGeburtsdatum.getValue());
        patient.setGeschlecht(cmbGeschlecht.getValue());
        patient.setStrasse(txtStrasse.getText().trim());
        patient.setPlz(txtPLZ.getText().trim());
        patient.setOrt(txtOrt.getText().trim());
        patient.setTelefon(txtTelefon.getText().trim());
        patient.setEmail(txtEmail.getText().trim());
        patient.setVersicherungsnummer(txtVersicherungsnummer.getText().trim());
        patient.setKrankenkasse(txtKrankenkasse.getText().trim());
    }
    
    /**
     * Eingabevalidierung (gemäss GR06)
     */
    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();
        
        // Pflichtfelder prüfen
        if (txtVorname.getText().trim().isEmpty()) {
            errors.append("- Vorname ist erforderlich\n");
        }
        if (txtNachname.getText().trim().isEmpty()) {
            errors.append("- Nachname ist erforderlich\n");
        }
        if (dpGeburtsdatum.getValue() == null) {
            errors.append("- Geburtsdatum ist erforderlich\n");
        } else if (dpGeburtsdatum.getValue().isAfter(LocalDate.now())) {
            errors.append("- Geburtsdatum darf nicht in der Zukunft liegen (GR06)\n");
        }
        if (cmbGeschlecht.getValue() == null) {
            errors.append("- Geschlecht ist erforderlich\n");
        }
        if (txtVersicherungsnummer.getText().trim().isEmpty()) {
            errors.append("- Versicherungsnummer ist erforderlich\n");
        }
        if (txtKrankenkasse.getText().trim().isEmpty()) {
            errors.append("- Krankenkasse ist erforderlich\n");
        }
        
        // Email-Format validieren (optional, aber wenn vorhanden)
        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.append("- Email-Format ist ungültig\n");
        }
        
        if (errors.length() > 0) {
            showError("Validierungsfehler", errors.toString());
            return false;
        }
        
        return true;
    }
    
    /**
     * Alle Eingabefelder leeren
     */
    private void clearInputFields() {
        txtVorname.clear();
        txtNachname.clear();
        dpGeburtsdatum.setValue(null);
        cmbGeschlecht.setValue(null);
        txtStrasse.clear();
        txtPLZ.clear();
        txtOrt.clear();
        txtTelefon.clear();
        txtEmail.clear();
        txtVersicherungsnummer.clear();
        txtKrankenkasse.clear();
    }
    
    /**
     * Eingabefelder aktivieren/deaktivieren
     */
    private void setInputFieldsDisabled(boolean disabled) {
        txtVorname.setDisable(disabled);
        txtNachname.setDisable(disabled);
        dpGeburtsdatum.setDisable(disabled);
        cmbGeschlecht.setDisable(disabled);
        txtStrasse.setDisable(disabled);
        txtPLZ.setDisable(disabled);
        txtOrt.setDisable(disabled);
        txtTelefon.setDisable(disabled);
        txtEmail.setDisable(disabled);
        txtVersicherungsnummer.setDisable(disabled);
        txtKrankenkasse.setDisable(disabled);
    }
    
    /**
     * Statusmeldung anzeigen
     */
    private void showStatus(String message, boolean isError) {
        lblStatus.setText(message);
        lblStatus.setStyle(isError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
    }
    
    /**
     * Fehler-Dialog anzeigen
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        showStatus(title + ": " + message, true);
    }
    
    /**
 * Cleanup beim Schliessen
 */
public void shutdown() {
    try {
        if (patientDAO != null) {
            patientDAO.closeConnection();
        }
    } catch (SQLException e) {
        System.err.println("Fehler beim Schliessen der Datenbankverbindung: " + e.getMessage());
    }
}

}
