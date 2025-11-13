package com.example.applicationrftg;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Gestionnaire singleton pour le panier local
 * Permet de gérer les films ajoutés au panier avec leur quantité
 */
public class PanierManager {

    private static PanierManager instance;

    // HashMap pour stocker les films avec leur quantité
    // Clé: ID du film, Valeur: ItemPanier (film + quantité)
    private HashMap<String, ItemPanier> panier;

    private PanierManager() {
        panier = new HashMap<>();
    }

    public static synchronized PanierManager getInstance() {
        if (instance == null) {
            instance = new PanierManager();
        }
        return instance;
    }

    /**
     * Ajouter un film au panier (ou augmenter sa quantité si déjà présent)
     */
    public void ajouterFilm(Film film) {
        String filmId = film.getFilm_id();

        if (panier.containsKey(filmId)) {
            // Le film est déjà dans le panier, augmenter la quantité
            ItemPanier item = panier.get(filmId);
            item.augmenterQuantite();
        } else {
            // Nouveau film dans le panier
            panier.put(filmId, new ItemPanier(film, 1));
        }
    }

    /**
     * Retirer un exemplaire d'un film du panier
     */
    public void retirerFilm(String filmId) {
        if (panier.containsKey(filmId)) {
            ItemPanier item = panier.get(filmId);
            item.diminuerQuantite();

            // Si la quantité tombe à 0, retirer complètement du panier
            if (item.getQuantite() <= 0) {
                panier.remove(filmId);
            }
        }
    }

    /**
     * Supprimer complètement un film du panier (toutes quantités)
     */
    public void supprimerFilm(String filmId) {
        panier.remove(filmId);
    }

    /**
     * Obtenir tous les items du panier
     */
    public ArrayList<ItemPanier> getItems() {
        return new ArrayList<>(panier.values());
    }

    /**
     * Vider complètement le panier
     */
    public void viderPanier() {
        panier.clear();
    }

    /**
     * Obtenir le nombre total d'items dans le panier
     */
    public int getNombreItems() {
        int total = 0;
        for (ItemPanier item : panier.values()) {
            total += item.getQuantite();
        }
        return total;
    }

    /**
     * Vérifier si le panier est vide
     */
    public boolean estVide() {
        return panier.isEmpty();
    }

    /**
     * Classe interne pour représenter un item du panier
     */
    public static class ItemPanier {
        private Film film;
        private int quantite;

        public ItemPanier(Film film, int quantite) {
            this.film = film;
            this.quantite = quantite;
        }

        public Film getFilm() {
            return film;
        }

        public int getQuantite() {
            return quantite;
        }

        public void setQuantite(int quantite) {
            this.quantite = quantite;
        }

        public void augmenterQuantite() {
            this.quantite++;
        }

        public void diminuerQuantite() {
            if (this.quantite > 0) {
                this.quantite--;
            }
        }
    }
}
