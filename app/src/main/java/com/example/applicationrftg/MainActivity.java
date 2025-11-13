package com.example.applicationrftg;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etIdentifiant, etMdp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etIdentifiant = findViewById(R.id.etIdentifiant);
        etMdp = findViewById(R.id.etMdp);
    }

    public void onConnexionClicked(View view) {
        String identifiant = etIdentifiant.getText().toString().trim();
        String motDePasse = etMdp.getText().toString().trim();

        if (identifiant.isEmpty() || motDePasse.isEmpty()) {
            android.widget.Toast.makeText(this, "Veuillez remplir tous les champs", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("MainActivity", "Tentative de connexion pour : " + identifiant);
        new ConnexionTask(this, identifiant, motDePasse).execute();
    }

    public void traiterReponseConnexion(String resultat) {
        if ("OK".equals(resultat)) {
            android.widget.Toast.makeText(this, "Connexion réussie !", android.widget.Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ListefilmsActivity.class));
            finish();
        } else {
            android.widget.Toast.makeText(this, "Identifiant ou mot de passe incorrect", android.widget.Toast.LENGTH_LONG).show();
            Log.e("MainActivity", "Connexion échouée : " + resultat);
        }
    }
}
