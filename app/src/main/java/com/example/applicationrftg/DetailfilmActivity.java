package com.example.applicationrftg;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.net.MalformedURLException;
import java.net.URL;

public class DetailfilmActivity extends AppCompatActivity {

    private String detailFilmResultat = "";
    private String filmId = "";
    private String filmTitle = "";
    private Film currentFilm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailfilm);

        // Récupérer l'ID du film passé depuis ListefilmsActivity
        Intent intent = getIntent();
        filmId = intent.getStringExtra("FILM_ID");
        filmTitle = intent.getStringExtra("FILM_TITLE");

        Log.d("DetailfilmActivity", "Page détail affichée pour film ID: " + filmId);

        // Appeler le service REST pour récupérer les détails du film
        URL urlAAppeler = null;
        try {
            urlAAppeler = new URL("http://10.0.2.2:8180/films/" + filmId);
            new DetailfilmTask(this).execute(urlAAppeler);
        } catch (MalformedURLException mue) {
            Log.d("mydebug",">>>Pour DetailfilmTask - MalformedURLException mue="+mue.toString());
        } finally {
            urlAAppeler = null;
        }
    }

    public void mettreAJourActivityApresAppelRest(String resultatAppelRest) {
        detailFilmResultat = resultatAppelRest;
        Log.d("mydebug",">>>Pour DetailfilmActivity - mettreAJourActivityApresAppelRest="+detailFilmResultat);

        // Vérifier que le résultat n'est pas vide avant d'afficher
        if (resultatAppelRest != null && !resultatAppelRest.trim().isEmpty()) {
            afficherDetailFilm(resultatAppelRest);
        } else {
            Log.e("mydebug", ">>>Erreur : Le résultat de l'appel REST est vide ou null");
        }
    }

    // Affichage des détails du film
    public void afficherDetailFilm(String filmJson) {
        Gson gson = new Gson();
        Film film = null;

        // Vérifier si le JSON commence par '[' (tableau) ou '{' (objet)
        if (filmJson.trim().startsWith("[")) {
            // Le serveur retourne un tableau, on cherche le film avec le bon ID
            com.google.gson.reflect.TypeToken<java.util.ArrayList<Film>> typeToken =
                new com.google.gson.reflect.TypeToken<java.util.ArrayList<Film>>(){};
            java.util.ArrayList<Film> filmArray = gson.fromJson(filmJson, typeToken.getType());

            if (filmArray != null && !filmArray.isEmpty()) {
                // Chercher le film correspondant à l'ID passé en paramètre
                for (Film f : filmArray) {
                    if (f.getFilm_id().equals(filmId)) {
                        film = f;
                        break;
                    }
                }
                // Si on ne trouve pas le film, prendre le premier par défaut
                if (film == null) {
                    film = filmArray.get(0);
                    Log.w("mydebug", ">>>Film ID " + filmId + " non trouvé, affichage du premier film");
                }
            }
        } else {
            // Le serveur retourne un objet unique
            film = gson.fromJson(filmJson, Film.class);
        }

        // Vérifier que le JSON a été correctement parsé
        if (film == null) {
            Log.e("mydebug", ">>>Erreur : Impossible de parser le JSON en Film");
            return;
        }

        // Sauvegarder le film actuel pour l'utiliser dans les boutons
        currentFilm = film;

        // Afficher les informations dans les TextViews
        TextView tvTitle = findViewById(R.id.tvFilmTitle);
        TextView tvDescription = findViewById(R.id.tvFilmDescription);
        TextView tvLength = findViewById(R.id.tvFilmLength);
        TextView tvRating = findViewById(R.id.tvFilmRating);
        TextView tvReleaseYear = findViewById(R.id.tvFilmReleaseYear);
        TextView tvActorPrincipal = findViewById(R.id.tvFilmActorPrincipal);
        TextView tvSupport = findViewById(R.id.tvFilmSupport);
        TextView tvReplacementCost = findViewById(R.id.tvFilmReplacementCost);
        TextView tvSpecialFeatures = findViewById(R.id.tvFilmSpecialFeatures);
        TextView tvDirectors = findViewById(R.id.tvFilmDirectors);
        TextView tvActors = findViewById(R.id.tvFilmActors);
        TextView tvCategories = findViewById(R.id.tvFilmCategories);

        tvTitle.setText(film.getTitle());
        tvDescription.setText(film.getDescription());
        tvLength.setText("Durée: " + film.getLength() + " min");
        tvRating.setText("Classification: " + film.getRating());

        // Afficher année et première catégorie dans le format "2023 • Action"
        String yearAndCategory = film.getRelease_year();
        if (film.getCategories() != null && !film.getCategories().isEmpty()) {
            yearAndCategory += " • " + film.getCategories().get(0).getName();
        }
        tvReleaseYear.setText(yearAndCategory);

        // Afficher le premier acteur comme acteur principal
        if (film.getActors() != null && !film.getActors().isEmpty()) {
            tvActorPrincipal.setText(film.getActors().get(0).getFullName());
        } else {
            tvActorPrincipal.setText("Acteur non disponible");
        }

        // Support (DVD, Blu-ray, Sur place, etc.) - à définir selon les données
        tvSupport.setText("Sur place");

        // Afficher uniquement le coût de remplacement
        tvReplacementCost.setText("Coût de remplacement: " + film.getReplacement_cost() + "€");

        // Fonctionnalités spéciales
        String specialFeatures = film.getSpecial_features();
        if (specialFeatures != null && !specialFeatures.trim().isEmpty()) {
            tvSpecialFeatures.setText("Bonus: " + specialFeatures);
        } else {
            tvSpecialFeatures.setText("Aucun bonus");
        }

        // Réalisateurs
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            StringBuilder directorsText = new StringBuilder();
            for (int i = 0; i < film.getDirectors().size(); i++) {
                Director director = film.getDirectors().get(i);
                directorsText.append(director.getFullName());
                if (i < film.getDirectors().size() - 1) {
                    directorsText.append(", ");
                }
            }
            tvDirectors.setText(directorsText.toString());
        } else {
            tvDirectors.setText("Information non disponible");
        }

        // Acteurs
        if (film.getActors() != null && !film.getActors().isEmpty()) {
            StringBuilder actorsText = new StringBuilder();
            for (int i = 0; i < film.getActors().size(); i++) {
                Actor actor = film.getActors().get(i);
                actorsText.append(actor.getFullName());
                if (i < film.getActors().size() - 1) {
                    actorsText.append(", ");
                }
            }
            tvActors.setText(actorsText.toString());
        } else {
            tvActors.setText("Information non disponible");
        }

        // Catégories
        if (film.getCategories() != null && !film.getCategories().isEmpty()) {
            StringBuilder categoriesText = new StringBuilder();
            for (int i = 0; i < film.getCategories().size(); i++) {
                Category category = film.getCategories().get(i);
                categoriesText.append(category.getName());
                if (i < film.getCategories().size() - 1) {
                    categoriesText.append(", ");
                }
            }
            tvCategories.setText(categoriesText.toString());
        } else {
            tvCategories.setText("Information non disponible");
        }
    }

    public void onRetourClicked(android.view.View view) {
        Log.d("DetailfilmActivity", "Retour à la liste des films");
        finish();
    }

    public void onAjouterPanierClicked(android.view.View view) {
        if (currentFilm != null) {
            // Ajouter le film au panier local
            PanierManager.getInstance().ajouterFilm(currentFilm);

            Toast.makeText(this, "Film ajouté au panier", Toast.LENGTH_SHORT).show();
            Log.d("DetailfilmActivity", "Film ajouté au panier: " + currentFilm.getTitle());
        } else {
            Toast.makeText(this, "Erreur: Film non disponible", Toast.LENGTH_SHORT).show();
        }
    }

    public void onReserverMaintenantClicked(android.view.View view) {
        if (currentFilm != null) {
            // Ajouter le film au panier et aller directement au panier
            PanierManager.getInstance().ajouterFilm(currentFilm);

            Toast.makeText(this, "Redirection vers le panier...", Toast.LENGTH_SHORT).show();
            Log.d("DetailfilmActivity", "Réservation maintenant pour: " + currentFilm.getTitle());

            // Rediriger vers l'activité Panier
            Intent intent = new Intent(this, PanierActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Erreur: Film non disponible", Toast.LENGTH_SHORT).show();
        }
    }
}
