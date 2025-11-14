package com.example.applicationrftg;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

/**
 * Classe représentant une demande de location à envoyer à l'API
 * Format JSON envoyé au serveur pour valider un panier
 */
public class RentalRequest {

    @SerializedName("customerId")
    private int customerId;

    @SerializedName("rentals")
    private ArrayList<RentalItem> rentals;

    public RentalRequest(int customerId) {
        this.customerId = customerId;
        this.rentals = new ArrayList<>();
    }

    public void ajouterRental(String filmId, int quantite) {
        rentals.add(new RentalItem(filmId, quantite));
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public ArrayList<RentalItem> getRentals() {
        return rentals;
    }

    public void setRentals(ArrayList<RentalItem> rentals) {
        this.rentals = rentals;
    }

    /**
     * Classe interne représentant un item de location
     */
    public static class RentalItem {
        @SerializedName("filmId")
        private String filmId;

        @SerializedName("quantite")
        private int quantite;

        public RentalItem(String filmId, int quantite) {
            this.filmId = filmId;
            this.quantite = quantite;
        }

        public String getFilmId() {
            return filmId;
        }

        public void setFilmId(String filmId) {
            this.filmId = filmId;
        }

        public int getQuantite() {
            return quantite;
        }

        public void setQuantite(int quantite) {
            this.quantite = quantite;
        }
    }
}
