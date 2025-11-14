package com.example.applicationrftg;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * AsyncTask pour valider le panier et envoyer les locations au serveur
 * Appel POST à l'API REST
 */
public class ValiderPanierTask extends AsyncTask<RentalRequest, Integer, String> {

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
    protected String doInBackground(RentalRequest... requests) {
        String sResultatAppel = "";

        try {
            // URL de l'API pour créer des rentals
            URL urlAAppeler = new URL("http://10.0.2.2:8180/rentals");

            RentalRequest rentalRequest = requests[0];

            // Convertir l'objet RentalRequest en JSON
            Gson gson = new Gson();
            String jsonRequestBody = gson.toJson(rentalRequest);

            Log.d(TAG, "JSON à envoyer : " + jsonRequestBody);

            sResultatAppel = appelerServiceRestHttpPost(urlAAppeler, jsonRequestBody);

        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la validation du panier : " + e.toString());
            sResultatAppel = "ERREUR";
        }

        return sResultatAppel;
    }

    @Override
    protected void onPostExecute(String resultat) {
        Log.d(TAG, "Résultat de la validation : " + resultat);
        this.screen.traiterReponseValidation(resultat);
    }

    /**
     * Méthode pour envoyer la requête POST au serveur
     */
    private String appelerServiceRestHttpPost(URL urlAAppeler, String jsonBody) {
        HttpURLConnection urlConnection = null;
        int responseCode = -1;
        String sResultatAppel = "";

        try {
            urlConnection = (HttpURLConnection) urlAAppeler.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
            urlConnection.setRequestProperty("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.e30.jg2m4pLbAlZv1h5uPQ6fU38X23g65eXMX8q-SXuIPDg");
            urlConnection.setDoOutput(true);

            // Écrire le corps JSON de la requête
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonBody);
            writer.flush();
            writer.close();
            os.close();

            responseCode = urlConnection.getResponseCode();
            Log.d(TAG, "Code de réponse HTTP : " + responseCode);

            // Lire la réponse
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                int codeCaractere = -1;
                while ((codeCaractere = in.read()) != -1) {
                    sResultatAppel = sResultatAppel + (char) codeCaractere;
                }
                in.close();

                Log.d(TAG, "Résultat reçu : " + sResultatAppel);
            } else {
                Log.e(TAG, "Erreur HTTP : " + responseCode);
                sResultatAppel = "ERREUR_HTTP_" + responseCode;
            }

        } catch (IOException ioe) {
            Log.e(TAG, "IOException : " + ioe.toString());
            sResultatAppel = "ERREUR_CONNEXION";
        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e.toString());
            sResultatAppel = "ERREUR";
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return sResultatAppel;
    }
}
