package com.example.applicationrftg;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class AjouterPanierTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = "AjouterPanierTask";
    private final Context context;
    private final Film film;
    private int rentalId = -1;

    public AjouterPanierTask(Context context, Film film) {
        this.context = context;
        this.film = film;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            int customerId = AppConfig.getCustomerId();
            int filmId = Integer.parseInt(film.getFilm_id());
            URL url = new URL(AppConfig.getBaseUrl() + "/cart/add");
            String json = "{\"customerId\":" + customerId + ",\"filmId\":" + filmId + "}";

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + AppConfig.getToken());
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(json);
            writer.flush();
            writer.close();
            os.close();

            int code = conn.getResponseCode();
            Log.d(TAG, "HTTP " + code + " pour " + url);

            BufferedReader in;
            if (code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_CREATED) {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else if (conn.getErrorStream() != null) {
                in = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
            } else {
                return "ERREUR_HTTP_" + code;
            }

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) sb.append(line);
            in.close();
            conn.disconnect();

            String body = sb.toString();
            Log.d(TAG, "Réponse: " + body);

            if (code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_CREATED) {
                try {
                    JSONObject jsonReponse = new JSONObject(body);
                    if (jsonReponse.has("rental")) {
                        rentalId = jsonReponse.getJSONObject("rental").getInt("rentalId");
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Impossible de parser rentalId: " + e.getMessage());
                }
                return "OK";
            } else {
                return "INDISPONIBLE";
            }
        } catch (IOException e) {
            Log.e(TAG, "Erreur: " + e.toString());
            return "ERREUR";
        }
    }

    @Override
    protected void onPostExecute(String resultat) {
        if ("OK".equals(resultat)) {
            PanierManager.ItemPanier item = PanierManager.getInstance().ajouterFilm(film);
            item.setStatut("Dans le panier");
            item.setRentalId(rentalId);
            Toast.makeText(context, "Film ajouté au panier", Toast.LENGTH_SHORT).show();
        } else if ("INDISPONIBLE".equals(resultat)) {
            Toast.makeText(context, "Aucun exemplaire disponible", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Erreur lors de l'ajout au panier", Toast.LENGTH_SHORT).show();
        }
    }
}
