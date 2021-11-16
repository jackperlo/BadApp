package com.example.progettoium.utils;

public class myURLs {
    private static final String serverUrlRegistration = "http://10.0.2.2:8080/ProvaAppAndroid_war_exploded/servlet-registration";
    private static final String serverUrlLogin = "http://10.0.2.2:8080/ProvaAppAndroid_war_exploded/servlet-login";
    private static final String serverUrlCheckSession = "http://10.0.2.2:8080/ProvaAppAndroid_war_exploded/servlet-check-session";
    private static final String serverUrlFreeRepetitions = "http://10.0.2.2:8080/ProvaAppAndroid_war_exploded/servlet-get-free-repetitions";
    private static final String serverUrlBookedHistoryRepetitions = "http://10.0.2.2:8080/ProvaAppAndroid_war_exploded/servlet-get-booked-history-repetitions";
    private static final String serverUrlManageRepetitions = "http://10.0.2.2:8080/ProvaAppAndroid_war_exploded/servlet-manage-repetitions";

    public static String getServerUrlRegistration() {
        return serverUrlRegistration;
    }

    public static String getServerUrlLogin() {
        return serverUrlLogin;
    }

    public static String getServerUrlCheckSession() {
        return serverUrlCheckSession;
    }

    public static String getServerUrlFreeRepetitions() {
        return serverUrlFreeRepetitions;
    }

    public static String getServerUrlBookedHistoryRepetitions() {return serverUrlBookedHistoryRepetitions;}

    public static String getServerUrlManageRepetitions() {
        return serverUrlManageRepetitions;
    }
}
