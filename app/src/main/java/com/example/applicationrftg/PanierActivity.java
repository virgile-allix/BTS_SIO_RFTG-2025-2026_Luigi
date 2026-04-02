package com.example.applicationrftg;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PanierActivity extends AppCompatActivity implements PanierAdapter.OnItemChangeListener {

    private RecyclerView rvPanierItems;
    private TextView tvPanierVide;
    private PanierAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panier);

        rvPanierItems = findViewById(R.id.rvPanierItems);
        tvPanierVide = findViewById(R.id.tvPanierVide);

        rvPanierItems.setLayoutManager(new LinearLayoutManager(this));

        chargerPanier();
    }

    @Override
    protected void onResume() {
        super.onResume();
        chargerPanier();
    }

    private void chargerPanier() {
        ArrayList<PanierManager.ItemPanier> items = PanierManager.getInstance().getItems();

        if (items.isEmpty()) {
            rvPanierItems.setVisibility(View.GONE);
            tvPanierVide.setVisibility(View.VISIBLE);
        } else {
            rvPanierItems.setVisibility(View.VISIBLE);
            tvPanierVide.setVisibility(View.GONE);

            if (adapter == null) {
                adapter = new PanierAdapter(items, this);
                rvPanierItems.setAdapter(adapter);
            } else {
                adapter.updateItems(items);
            }
        }

        Log.d("PanierActivity", "Panier chargé avec " + items.size() + " items");
    }

    @Override
    public void onQuantiteChange(String filmId, int nouvelleQuantite) {
        ArrayList<PanierManager.ItemPanier> items = PanierManager.getInstance().getItems();
        for (PanierManager.ItemPanier item : items) {
            if (item.getFilm().getFilm_id().equals(filmId)) {
                item.setQuantite(nouvelleQuantite);
                break;
            }
        }

        chargerPanier();
        Log.d("PanierActivity", "Quantité mise à jour pour film " + filmId + ": " + nouvelleQuantite);
    }

    @Override
    public void onSupprimer(String filmId) {
        PanierManager.getInstance().supprimerFilm(filmId);
        chargerPanier();
        Toast.makeText(this, "Film retiré du panier", Toast.LENGTH_SHORT).show();
        Log.d("PanierActivity", "Film supprimé: " + filmId);
    }

    public void onRetourClicked(View view) {
        finish();
    }

    public void onViderPanierClicked(View view) {
        PanierManager.getInstance().viderPanier();
        chargerPanier();
        Toast.makeText(this, "Panier vidé", Toast.LENGTH_SHORT).show();
        Log.d("PanierActivity", "Panier vidé");
    }

    public void onValiderReservationClicked(View view) {
        if (PanierManager.getInstance().estVide()) {
            Toast.makeText(this, "Votre panier est vide", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("PanierActivity", "Envoi checkout pour customerId=" + AppConfig.getCustomerId());

        // Envoyer la commande au serveur via AsyncTask
        new ValiderPanierTask(this).execute();
    }

    /**
     * Méthode appelée par ValiderPanierTask après la réponse du serveur
     */
    public void traiterReponseValidation(String resultat) {
        if (resultat.startsWith("ERREUR")) {
            // Erreur lors de l'envoi
            Log.e("PanierActivity", "Erreur validation : " + resultat);
            new AlertDialog.Builder(this)
                .setTitle("Erreur de validation")
                .setMessage("La validation du panier a échoué.\nVérifiez que le serveur est accessible : " + AppConfig.getBaseUrl() + "\nDétail : " + resultat)
                .setPositiveButton("OK", null)
                .show();
        } else if (resultat.contains("\"itemsCount\":0")) {
            new AlertDialog.Builder(this)
                .setTitle("Aucun exemplaire disponible")
                .setMessage("Aucun film de votre panier n'est disponible en stock pour le moment.")
                .setPositiveButton("OK", null)
                .show();
        } else {
            // Succès
            Toast.makeText(this, "Réservation validée avec succès !", Toast.LENGTH_LONG).show();
            Log.d("PanierActivity", "Réservation validée : " + resultat);

            // Vider le panier après succès
            PanierManager.getInstance().viderPanier();
            chargerPanier();

            // Possibilité de rediriger vers une page de confirmation
            // Intent intent = new Intent(this, ConfirmationActivity.class);
            // startActivity(intent);
        }
    }

    public void onContinuerAchatsClicked(View view) {
        finish();
    }
}
