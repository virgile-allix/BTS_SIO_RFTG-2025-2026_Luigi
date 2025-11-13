package com.example.applicationrftg;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Adaptateur pour afficher les items du panier dans un RecyclerView
 */
public class PanierAdapter extends RecyclerView.Adapter<PanierAdapter.PanierViewHolder> {

    private ArrayList<PanierManager.ItemPanier> items;
    private OnItemChangeListener listener;

    public interface OnItemChangeListener {
        void onQuantiteChange(String filmId, int nouvelleQuantite);
        void onSupprimer(String filmId);
    }

    public PanierAdapter(ArrayList<PanierManager.ItemPanier> items, OnItemChangeListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PanierViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ligne_panier, parent, false);
        return new PanierViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PanierViewHolder holder, int position) {
        PanierManager.ItemPanier item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateItems(ArrayList<PanierManager.ItemPanier> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    class PanierViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvSupport;
        TextView tvQuantite;
        TextView btnDiminuer;
        TextView btnAugmenter;
        TextView tvSupprimer;

        public PanierViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvItemTitle);
            tvSupport = itemView.findViewById(R.id.tvItemSupport);
            tvQuantite = itemView.findViewById(R.id.tvQuantite);
            btnDiminuer = itemView.findViewById(R.id.btnDiminuer);
            btnAugmenter = itemView.findViewById(R.id.btnAugmenter);
            tvSupprimer = itemView.findViewById(R.id.tvSupprimer);
        }

        public void bind(PanierManager.ItemPanier item) {
            Film film = item.getFilm();

            tvTitle.setText(film.getTitle());
            tvSupport.setText("Sur place");
            tvQuantite.setText(String.valueOf(item.getQuantite()));

            // Bouton diminuer
            btnDiminuer.setOnClickListener(v -> {
                int nouvelleQuantite = item.getQuantite() - 1;
                if (nouvelleQuantite <= 0) {
                    // Supprimer l'item si la quantité tombe à 0
                    listener.onSupprimer(film.getFilm_id());
                } else {
                    listener.onQuantiteChange(film.getFilm_id(), nouvelleQuantite);
                }
            });

            // Bouton augmenter
            btnAugmenter.setOnClickListener(v -> {
                int nouvelleQuantite = item.getQuantite() + 1;
                listener.onQuantiteChange(film.getFilm_id(), nouvelleQuantite);
            });

            // Bouton supprimer
            tvSupprimer.setOnClickListener(v -> {
                listener.onSupprimer(film.getFilm_id());
            });
        }
    }
}
