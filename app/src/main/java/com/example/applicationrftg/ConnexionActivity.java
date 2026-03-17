package com.example.applicationrftg;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import org.json.JSONObject;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ConnexionActivity extends AppCompatActivity {

    private EditText etIdentifiant, etMdp, etUrlPersonnalisee;
    private Spinner spinnerUrl;
    private View tvUrlPersonnaliseeLabel;

    // URLs disponibles dans le spinner
    private static final String[] URLS_DISPONIBLES = {
        "http://10.0.2.2:8180",
        "http://rftg.mtb111.com",
        "Autre (saisir manuellement)"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);
        AppConfig.setToken(getString(R.string.api_token));

        etIdentifiant = findViewById(R.id.etIdentifiant);
        etMdp = findViewById(R.id.etMdp);
        etUrlPersonnalisee = findViewById(R.id.etUrlPersonnalisee);
        tvUrlPersonnaliseeLabel = findViewById(R.id.tvUrlPersonnaliseeLabel);
        spinnerUrl = findViewById(R.id.spinnerUrl);

        // Remplir le Spinner avec les URLs disponibles
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, URLS_DISPONIBLES);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUrl.setAdapter(adapterSpinner);

        // Pré-sélectionner l'URL actuelle si elle correspond à une entrée
        String urlActuelle = AppConfig.getBaseUrl();
        for (int i = 0; i < URLS_DISPONIBLES.length - 1; i++) {
            if (URLS_DISPONIBLES[i].equals(urlActuelle)) {
                spinnerUrl.setSelection(i);
                break;
            }
        }

        // Afficher/masquer le champ URL personnalisée selon la sélection
        spinnerUrl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean estPersonnalisee = (position == URLS_DISPONIBLES.length - 1);
                int visibilite = estPersonnalisee ? View.VISIBLE : View.GONE;
                etUrlPersonnalisee.setVisibility(visibilite);
                tvUrlPersonnaliseeLabel.setVisibility(visibilite);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public void onFermerAppliClicked(View view) {
        // Fermer toutes les activities et quitter l'appli
        finishAffinity();
    }

    public void onConnexionClicked(View view) {
        String identifiant = etIdentifiant.getText().toString().trim();
        String motDePasse = etMdp.getText().toString().trim();

        if (identifiant.isEmpty() || motDePasse.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // Récupérer l'URL sélectionnée
        int position = spinnerUrl.getSelectedItemPosition();
        if (position == URLS_DISPONIBLES.length - 1) {
            // URL personnalisée
            String urlSaisie = etUrlPersonnalisee.getText().toString().trim();
            if (urlSaisie.isEmpty()) {
                Toast.makeText(this, "Veuillez saisir une URL", Toast.LENGTH_SHORT).show();
                return;
            }
            AppConfig.setBaseUrl(urlSaisie);
        } else {
            AppConfig.setBaseUrl(URLS_DISPONIBLES[position]);
        }

        Log.d("ConnexionActivity", "Tentative de connexion pour : " + identifiant + " sur " + AppConfig.getBaseUrl());
        new ConnexionTask(this, identifiant, motDePasse).execute();
    }

    public void traiterReponseConnexion(String resultat) {
        try {
            JSONObject json = new JSONObject(resultat);
            int customerId = json.getInt("customerId");
            if (customerId > 0) {
                AppConfig.setCustomerId(customerId);
                Toast.makeText(this, "Connexion réussie !", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, ListefilmsActivity.class));
                finish();
            } else {
                new AlertDialog.Builder(this)
                    .setTitle("Erreur de connexion")
                    .setMessage("Email ou mot de passe incorrect.")
                    .setPositiveButton("OK", null)
                    .show();
            }
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                .setTitle("Erreur de connexion")
                .setMessage("Impossible de contacter le serveur : " + AppConfig.getBaseUrl())
                .setPositiveButton("OK", null)
                .show();
            Log.e("ConnexionActivity", "Erreur parsing réponse : " + e.toString());
        }
    }
}
