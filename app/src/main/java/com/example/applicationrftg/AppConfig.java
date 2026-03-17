package com.example.applicationrftg;

/**
 * Configuration globale de l'application
 * Stocke l'URL de base du serveur, modifiable depuis l'écran de connexion
 */
public class AppConfig {

    private static String baseUrl = "http://10.0.2.2:8180";
    private static String token = "";
    private static int customerId = -1;

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static void setBaseUrl(String url) {
        // Supprimer le slash final si présent
        if (url != null && url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        baseUrl = url;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String t) {
        token = t;
    }

    public static int getCustomerId() {
        return customerId;
    }

    public static void setCustomerId(int id) {
        customerId = id;
    }
}
