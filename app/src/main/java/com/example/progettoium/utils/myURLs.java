package com.example.progettoium.utils;

public class myURLs {
    private static final String serverUrlRegistration = "http://10.0.2.2:8080/ProvaAppAndroid_war_exploded/servlet-registration";
    private static final String serverUrlLogin = "http://10.0.2.2:8080/ProvaAppAndroid_war_exploded/servlet-login";

    public static String getServerUrlRegistration() {
        return serverUrlRegistration;
    }

    public static String getServerUrlLogin() {
        return serverUrlLogin;
    }
}
