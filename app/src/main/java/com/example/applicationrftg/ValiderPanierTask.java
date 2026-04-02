package com.example.applicationrftg;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ValiderPanierTask extends AsyncTask<Void, Integer, String> {

    private volatile PanierActivity screen;
    private static final String TAG = "ValiderPanierTask";

    public ValiderPanierTask(PanierActivity s) {
        this.screen = s;
    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG, "Début de la validation du panier...");
    }

    @Override
    protected String doInBackground(Void... voids) {
        int customerId = AppConfig.getCustomerId();

        // Les films sont déjà ajoutés via AjouterPanierTask, on fait uniquement le checkout
        try {
            URL url = new URL(AppConfig.getBaseUrl() + "/cart/checkout");
            String json = "{\"customerId\":" + customerId + "}";
            Log.d(TAG, "cart/checkout : " + json);
            return envoyerPost(url, json);
        } catch (Exception e) {
            Log.e(TAG, "Erreur cart/checkout : " + e.toString());
            return "ERREUR_CHECKOUT";
        }
    }

    @Override
    protected void onPostExecute(String resultat) {
        Log.d(TAG, "Résultat de la validation : " + resultat);
        this.screen.traiterReponseValidation(resultat);
    }

    private String envoyerPost(URL url, String jsonBody) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + AppConfig.getToken());
            conn.setRequestProperty("User-Agent", System.getProperty("http.agent"));
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonBody);
            writer.flush();
            writer.close();
            os.close();

            int code = conn.getResponseCode();
            Log.d(TAG, "HTTP " + code + " pour " + url);

            if (code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_CREATED) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) sb.append(line);
                in.close();
                return sb.toString();
            } else if (conn.getErrorStream() != null) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) sb.append(line);
                in.close();
                return sb.toString();
            } else {
                return "ERREUR_HTTP_" + code;
            }
        } finally {
            if (conn != null) conn.disconnect();
        }
    }
}
