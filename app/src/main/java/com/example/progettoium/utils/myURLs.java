package com.example.progettoium.utils;

public class myURLs {
    private static final String serverUrlRegistration = "http://10.0.2.2:8080/ProvaAppAndroid_war_exploded/servlet-registration";
    private static final String serverUrlLogin = "http://10.0.2.2:8080/ProvaAppAndroid_war_exploded/servlet-login";
    private static final String serverUrlCheckSession = "http://10.0.2.2:8080/ProvaAppAndroid_war_exploded/servlet-check-session";
    private static final String serverUrlBookedRepetitions = "http://10.0.2.2:8080/ProvaAppAndroid_war_exploded/servlet-get-booked-repetitions";
    private static final String serverUrlBookedHistoryRepetitions = "http://10.0.2.2:8080/ProvaAppAndroid_war_exploded/servlet-get-booked-history-repetitions";

    public static String getServerUrlRegistration() {
        return serverUrlRegistration;
    }

    public static String getServerUrlLogin() {
        return serverUrlLogin;
    }

    public static String getServerUrlCheckSession() {
        return serverUrlCheckSession;
    }

    public static String getServerUrlBookedRepetitions() {
        return serverUrlBookedRepetitions;
    }

    public static String getServerUrlBookedHistoryRepetitions() {return serverUrlBookedHistoryRepetitions;}
}
