package com.example.applicationrftg;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class SupprimerPanierTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "SupprimerPanierTask";
    private final Context context;
    private final String filmId;
    private final int rentalId;
    private final Runnable onSuccess;

    public SupprimerPanierTask(Context context, String filmId, int rentalId, Runnable onSuccess) {
        this.context = context;
        this.filmId = filmId;
        this.rentalId = rentalId;
        this.onSuccess = onSuccess;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        if (rentalId == -1) return true; // Pas encore envoyé à l'API
        try {
            URL url = new URL(AppConfig.getBaseUrl() + "/cart/" + rentalId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", "Bearer " + AppConfig.getToken());
            conn.setRequestProperty("Accept", "application/json");
            int code = conn.getResponseCode();
            Log.d(TAG, "DELETE /rentals/" + rentalId + " -> HTTP " + code);
            conn.disconnect();
            return code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_NO_CONTENT;
        } catch (IOException e) {
            Log.e(TAG, "Erreur: " + e.toString());
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean succes) {
        if (succes) {
            PanierManager.getInstance().supprimerFilm(filmId);
            if (onSuccess != null) onSuccess.run();
        } else {
            Toast.makeText(context, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
        }
    }
}
