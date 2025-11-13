package com.example.applicationrftg;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
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

        // TODO: Envoyer la commande au serveur
        Toast.makeText(this, "Réservation validée ! (à implémenter)", Toast.LENGTH_LONG).show();
        Log.d("PanierActivity", "Réservation validée avec " + PanierManager.getInstance().getNombreItems() + " items");

        PanierManager.getInstance().viderPanier();
        chargerPanier();
    }

    public void onContinuerAchatsClicked(View view) {
        finish();
    }
}
