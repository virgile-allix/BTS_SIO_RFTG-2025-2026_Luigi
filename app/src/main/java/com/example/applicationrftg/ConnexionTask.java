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
import java.security.MessageDigest;


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
            URL urlAAppeler = new URL(AppConfig.getBaseUrl() + "/customers/verify");

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
            urlConnection.setRequestProperty("Authorization", "Bearer " + AppConfig.getToken());
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
            urlConnection.setDoOutput(true);

            String jsonInputString = "{\"email\":\"" + identifiant + "\", \"password\":\"" + md5(motDePasse) + "\"}";
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
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }
                in.close();
                sResultatAppel = sb.toString();

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

    private String md5(String chaine) {
        try {
            byte[] hash = MessageDigest.getInstance("MD5").digest(chaine.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(b);
                if (hex.length() == 1) {
                    sb.append('0');
                    sb.append(hex.charAt(hex.length() - 1));
                } else {
                    sb.append(hex.substring(hex.length() - 2));
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return chaine;
        }
    }
}
