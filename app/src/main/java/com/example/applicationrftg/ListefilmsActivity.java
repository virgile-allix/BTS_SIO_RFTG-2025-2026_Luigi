package com.example.applicationrftg;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ListefilmsActivity extends AppCompatActivity {

    private String listeFilmsResultat = "";
    private ArrayList<Film> tousLesFilms = new ArrayList<>();
    private ArrayAdapter<Film> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listefilms);

        URL urlAAppeler = null;
        try {
            // Appel du service REST pour récupérer la liste des films
            urlAAppeler = new URL("http://10.0.2.2:8180/films");
            new ListefilmsTask(this).execute(urlAAppeler);
        } catch (MalformedURLException mue) {
            Log.d("mydebug",">>>Pour ListefilmsTask - MalformedURLException mue="+mue.toString());
        } finally {
            urlAAppeler = null;
        }
    }

    public void mettreAJourActivityApresAppelRest(String resultatAppelRest) {
        listeFilmsResultat = resultatAppelRest;
        Log.d("mydebug",">>>Pour ListefilmsActivity - mettreAJourActivityApresAppelRest="+listeFilmsResultat);

        // Vérifier que le résultat n'est pas vide avant d'afficher
        if (resultatAppelRest != null && !resultatAppelRest.trim().isEmpty()) {
            afficherListeFilms(listeFilmsResultat);
        } else {
            Log.e("mydebug", ">>>Erreur : Le résultat de l'appel REST est vide ou null");
            // TODO: Afficher un message d'erreur à l'utilisateur
        }
    }

    // Conversion JSON en ArrayList
    public ArrayList<Film> convertitListeFilmsEnArrayList(String filmJson) {
        Gson gson = new Gson();

        Type filmListType = new TypeToken<ArrayList<Film>>(){}.getType();
        ArrayList<Film> filmArray = gson.fromJson(filmJson, filmListType);

        // Vérifier que le JSON a été correctement parsé
        if (filmArray == null) {
            Log.e("mydebug", ">>>Erreur : Impossible de parser le JSON en ArrayList<Film>");
            return new ArrayList<>(); // Retourner une liste vide au lieu de null
        }

        return filmArray;
    }

    // Affichage dans la ListView
    public void afficherListeFilms(String filmJson) {
        tousLesFilms = convertitListeFilmsEnArrayList(filmJson);

        // Création de l'ArrayAdapter
        adapter = new ArrayAdapter<Film>(this, R.layout.ligne_liste_films, tousLesFilms) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Film film = getItem(position);

                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    convertView = inflater.inflate(R.layout.ligne_liste_films, parent, false);
                }

                // Récupération des TextView du modèle ligne_liste_films.xml
                TextView textNomFilm = convertView.findViewById(R.id.textNomFilm);
                TextView textDescription = convertView.findViewById(R.id.textDescription);
                TextView textSupport = convertView.findViewById(R.id.textSupport);
                android.widget.Button btnDetail = convertView.findViewById(R.id.btnDetail);
                android.widget.Button btnAjouter = convertView.findViewById(R.id.btnAjouter);

                // Remplissage avec les données du film
                textNomFilm.setText(film.getTitle());
                textDescription.setText(film.getDescription());
                textSupport.setText("Sur place");

                // Bouton Détail
                btnDetail.setOnClickListener(v -> {
                    Intent intent = new Intent(ListefilmsActivity.this, DetailfilmActivity.class);
                    intent.putExtra("FILM_ID", film.getFilm_id());
                    intent.putExtra("FILM_TITLE", film.getTitle());
                    startActivity(intent);
                });

                // Bouton Ajouter
                btnAjouter.setOnClickListener(v -> {
                    PanierManager.getInstance().ajouterFilm(film);
                    android.widget.Toast.makeText(getContext(), "Film ajouté au panier", android.widget.Toast.LENGTH_SHORT).show();
                    Log.d("ListefilmsActivity", "Film ajouté au panier: " + film.getTitle());
                });

                return convertView;
            }
        };

        // Remplissage de la ListView
        ListView listviewFilms = (ListView) findViewById(R.id.listeFilms);
        listviewFilms.setAdapter(adapter);

        // Listener de gestion des clics sur les lignes de la listView
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View container, int position, long id) {
                Film filmClique = adapter.getItem(position);
                Log.d("mydebug","clic sur film: " + filmClique.getTitle());

                // Afficher le popup
                // afficherPopup(container, filmClique);

                // Redirection vers la page détails avec l'ID du film
                Intent intent = new Intent(ListefilmsActivity.this, DetailfilmActivity.class);
                intent.putExtra("FILM_ID", filmClique.getFilm_id());
                intent.putExtra("FILM_TITLE", filmClique.getTitle());
                startActivity(intent);
            }
        };

        // Utilisation avec notre listview
        listviewFilms.setOnItemClickListener(itemClickListener);

        // Ajout du TextWatcher pour la recherche
        EditText etRecherche = findViewById(R.id.etRecherche);
        etRecherche.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Non utilisé
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filtrer la liste en temps réel
                adapter.getFilter().filter(s);
                Log.d("ListefilmsActivity", "Recherche: " + s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Non utilisé
            }
        });
    }

    // Méthode pour afficher le popup avec le nom du film
    /*
    private void afficherPopup(View view, Film film) {
        // Inflate le layout du popup
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_layout, null);

        // Créer le PopupWindow
        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // Pour fermer en cliquant en dehors
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // Définir un fond semi-transparent
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Récupérer le TextView et afficher le message avec le nom du film
        TextView messagePopup = popupView.findViewById(R.id.messagePopup);
        messagePopup.setText("J'ai choisi : " + film.getTitle());

        // Gérer le bouton de fermeture
        Button closeBtn = popupView.findViewById(R.id.closePopupBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        // Afficher le popup au centre de l'écran
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }
    */

    public void onPanierClicked(View view) {
        Log.d("ListefilmsActivity", "Ouverture du panier");
        startActivity(new Intent(this, PanierActivity.class));
    }

    public void onRetourClicked(View view) {
        Log.d("ListefilmsActivity", "Retour à l'écran de connexion");
        finish();
    }
}
