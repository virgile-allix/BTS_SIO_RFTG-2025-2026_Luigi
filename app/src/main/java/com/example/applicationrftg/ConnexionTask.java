package com.example.applicationrftg;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class ConnexionTask extends AsyncTask<Void, Integer, String> {

    private volatile ConnexionActivity screen;
    private String identifiant;
    private String motDePasse;

    public ConnexionTask(ConnexionActivity s, String identifiant, String motDePasse) {
        this.screen = s;
        this.identifiant = identifiant;
        this.motDePasse = motDePasse;
    }

    @Override
    protected void onPreExecute() {
        Log.d("ConnexionTask", "Début de la connexion...");
    }

    @Override
    protected String doInBackground(Void... voids) {
        String sResultatAppel = "";

        try {
            URL urlAAppeler = new URL("http://10.0.2.2:8180/connexion");

            sResultatAppel = appelerServiceRestHttpPost(urlAAppeler, identifiant, motDePasse);

        } catch (Exception e) {
            Log.e("ConnexionTask", "Erreur lors de la connexion : " + e.toString());
            sResultatAppel = "KO";
        }

        return sResultatAppel;
    }

    @Override
    protected void onPostExecute(String resultat) {
        Log.d("ConnexionTask", "Résultat de la connexion : " + resultat);

        this.screen.traiterReponseConnexion(resultat);
    }

    private String appelerServiceRestHttpPost(URL urlAAppeler, String identifiant, String motDePasse) {
        HttpURLConnection urlConnection = null;
        int responseCode = -1;
        String sResultatAppel = "";

        try {
            urlConnection = (HttpURLConnection) urlAAppeler.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Authorization","Bearer eyJhbGciOiJIUzI1NiJ9.e30.jg2m4pLbAlZv1h5uPQ6fU38X23g65eXMX8q-SXuIPDg");

            String jsonInputString = "{\"nomUtilisateur\":\"" + identifiant + "\", \"motdepasseUtilisateur\":\"" + motDePasse + "\"}";
            Log.d("ConnexionTask", "JSON envoyé : " + jsonInputString);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonInputString);
            writer.flush();
            writer.close();
            os.close();

            responseCode = urlConnection.getResponseCode();
            Log.d("ConnexionTask", "Code de réponse HTTP : " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                int codeCaractere = -1;
                while ((codeCaractere = in.read()) != -1) {
                    sResultatAppel = sResultatAppel + (char) codeCaractere;
                }
                in.close();

                Log.d("ConnexionTask", "Résultat reçu : " + sResultatAppel);
            } else {
                Log.e("ConnexionTask", "Erreur HTTP : " + responseCode);
                sResultatAppel = "KO";
            }

        } catch (IOException ioe) {
            Log.e("ConnexionTask", "IOException : " + ioe.toString());
            sResultatAppel = "KO";
        } catch (Exception e) {
            Log.e("ConnexionTask", "Exception : " + e.toString());
            sResultatAppel = "KO";
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return sResultatAppel;
    }
}
