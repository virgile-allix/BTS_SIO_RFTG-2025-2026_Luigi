package com.example.applicationrftg;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ListefilmsTask extends AsyncTask<URL,Integer,String> {

    private volatile ListefilmsActivity screen;

    public ListefilmsTask(ListefilmsActivity s) {
        this.screen = s ;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(URL... urls) {
        String sResultatAppel = null;
        URL urlAAppeler = urls[0];
        sResultatAppel = appelerServiceRestHttp(urlAAppeler);
        return sResultatAppel;
    }

    @Override
    protected void onPostExecute(String resultat) {
        System.out.println(">>>onPostExecute / resultat="+resultat);
        this.screen.mettreAJourActivityApresAppelRest(resultat);
    }

    private String appelerServiceRestHttp(URL urlAAppeler ) {
        HttpURLConnection urlConnection = null;
        int responseCode = -1;
        String sResultatAppel = "";
        try {
            urlConnection = (HttpURLConnection) urlAAppeler.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
            urlConnection.setRequestProperty("Authorization","Bearer " + AppConfig.getToken());


            responseCode = urlConnection.getResponseCode();
            Log.d("mydebug", ">>>Code de réponse HTTP : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            in.close();
            sResultatAppel = sb.toString();
            Log.d("mydebug", ">>>Résultat obtenu : " + sResultatAppel.substring(0, Math.min(100, sResultatAppel.length())));
        } catch (IOException ioe) {
            Log.d("mydebug", ">>>Pour appelerServiceRestHttp - IOException ioe =" + ioe.toString());
        } catch (Exception e) {
            Log.d("mydebug",">>>Pour appelerServiceRestHttp - Exception="+e.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return sResultatAppel;
    }


}
 