package ch.hftm.medisys.model;

import java.time.LocalDate;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Patient Entity gemäss Klassendiagramm (System Modelling Seite 4)
 * Implementiert GR06: Datumvalidierung
 */
public class Patient {
    
    private final IntegerProperty patientID;
    private final StringProperty vorname;
    private final StringProperty nachname;
    private final ObjectProperty<LocalDate> geburtsdatum;
    private final StringProperty geschlecht;
    private final StringProperty strasse;
    private final StringProperty plz;
    private final StringProperty ort;
    private final StringProperty telefon;
    private final StringProperty email;
    private final StringProperty versicherungsnummer;
    private final StringProperty krankenkasse;
    
    // Konstruktor für neue Patienten
    public Patient(String vorname, String nachname, LocalDate geburtsdatum,
                   String geschlecht, String versicherungsnummer, String krankenkasse) {
        this.patientID = new SimpleIntegerProperty(0);
        this.vorname = new SimpleStringProperty(vorname);
        this.nachname = new SimpleStringProperty(nachname);
        this.geburtsdatum = new SimpleObjectProperty<>(geburtsdatum);
        this.geschlecht = new SimpleStringProperty(geschlecht);
        this.versicherungsnummer = new SimpleStringProperty(versicherungsnummer);
        this.krankenkasse = new SimpleStringProperty(krankenkasse);
        this.strasse = new SimpleStringProperty("");
        this.plz = new SimpleStringProperty("");
        this.ort = new SimpleStringProperty("");
        this.telefon = new SimpleStringProperty("");
        this.email = new SimpleStringProperty("");
    }
    
    // Konstruktor für existierende Patienten (aus Datenbank)
    public Patient(int patientID, String vorname, String nachname, 
                   LocalDate geburtsdatum, String geschlecht, String strasse,
                   String plz, String ort, String telefon, String email,
                   String versicherungsnummer, String krankenkasse) {
        this(vorname, nachname, geburtsdatum, geschlecht, 
             versicherungsnummer, krankenkasse);
        this.patientID.set(patientID);
        this.strasse.set(strasse);
        this.plz.set(plz);
        this.ort.set(ort);
        this.telefon.set(telefon);
        this.email.set(email);
    }
    
    // Getter und Setter für alle Felder
    public int getPatientID() { return patientID.get(); }
    public void setPatientID(int value) { patientID.set(value); }
    public IntegerProperty patientIDProperty() { return patientID; }
    
    public String getVorname() { return vorname.get(); }
    public void setVorname(String value) { vorname.set(value); }
    public StringProperty vornameProperty() { return vorname; }
    
    public String getNachname() { return nachname.get(); }
    public void setNachname(String value) { nachname.set(value); }
    public StringProperty nachnameProperty() { return nachname; }
    
    public LocalDate getGeburtsdatum() { return geburtsdatum.get(); }
    public void setGeburtsdatum(LocalDate value) { geburtsdatum.set(value); }
    public ObjectProperty<LocalDate> geburtsdatumProperty() { return geburtsdatum; }
    
    public String getGeschlecht() { return geschlecht.get(); }
    public void setGeschlecht(String value) { geschlecht.set(value); }
    public StringProperty geschlechtProperty() { return geschlecht; }
    
    public String getStrasse() { return strasse.get(); }
    public void setStrasse(String value) { strasse.set(value); }
    public StringProperty strasseProperty() { return strasse; }
    
    public String getPlz() { return plz.get(); }
    public void setPlz(String value) { plz.set(value); }
    public StringProperty plzProperty() { return plz; }
    
    public String getOrt() { return ort.get(); }
    public void setOrt(String value) { ort.set(value); }
    public StringProperty ortProperty() { return ort; }
    
    public String getTelefon() { return telefon.get(); }
    public void setTelefon(String value) { telefon.set(value); }
    public StringProperty telefonProperty() { return telefon; }
    
    public String getEmail() { return email.get(); }
    public void setEmail(String value) { email.set(value); }
    public StringProperty emailProperty() { return email; }
    
    public String getVersicherungsnummer() { return versicherungsnummer.get(); }
    public void setVersicherungsnummer(String value) { versicherungsnummer.set(value); }
    public StringProperty versicherungsnummerProperty() { return versicherungsnummer; }
    
    public String getKrankenkasse() { return krankenkasse.get(); }
    public void setKrankenkasse(String value) { krankenkasse.set(value); }
    public StringProperty krankenkasseProperty() { return krankenkasse; }
    
    /**
     * Validiert Geschlecht gemäss GR06 Constraint
     */
    public boolean isValidGeschlecht() {
        return geschlecht.get() != null && 
               geschlecht.get().matches("[MWD]");
    }
    
    /**
     * Validiert Geburtsdatum gemäss GR06: nicht in der Zukunft
     */
    public boolean isValidGeburtsdatum() {
        return geburtsdatum.get() != null && 
               !geburtsdatum.get().isAfter(LocalDate.now());
    }
    
    @Override
    public String toString() {
        return String.format("%s %s (ID: %d)", vorname.get(), nachname.get(), patientID.get());
    }
}