package com.example.applicationrftg;

import com.google.gson.annotations.SerializedName;

/**
 * Classe représentant un réalisateur (Director)
 * Conforme à la structure JSON du service REST
 */
public class Director {

    @SerializedName("directorId")
    private int directorId;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    // Constructeur par défaut requis par Gson
    public Director() {
    }

    // Getters et setters
    public int getDirectorId() {
        return directorId;
    }

    public void setDirectorId(int directorId) {
        this.directorId = directorId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // Méthode utilitaire pour obtenir le nom complet
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
