package com.example.progettoium.utils;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.progettoium.data.BookedRepetitions;
import com.example.progettoium.data.User;
import com.google.gson.JsonObject;

import androidx.lifecycle.AndroidViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/*
 * View Model di interfaccia con la rete internet
 */
public class NetworkViewModel extends AndroidViewModel {
    private final UserLiveData usersData;
    private final BookedRepetitionsLiveData bookedRepetitionsData;
    private int selectedHistoryTab = 0;
    private final ConnectionLiveData isConnected;

    public NetworkViewModel(Application application) {
        super(application);

        usersData = new UserLiveData();
        bookedRepetitionsData = new BookedRepetitionsLiveData();
        isConnected = new ConnectionLiveData();
    }

    /*GETTING DATA FROM LIVE DATA ALREADY FILLED UP FROM DB QUERIES*/
    public UserLiveData getRegisteredUser() {
        return usersData;
    }

    public BookedRepetitionsLiveData getBookedRepetitions() {
        return bookedRepetitionsData;
    }

    public ConnectionLiveData getIsConnected() {
        return isConnected;
    }
    /*END GETTING DATA FROM LIVE DATA*/

    /*GETTING DATA FROM DB VIA JAVA SERVLETS*/
    /*public boolean checkSession() {
        boolean isDone = false;

        if (getIsConnected().getValue()) {
            JSONObject json = launchThread(myURLs.getServerUrlCheckSession(), new HashMap<>(), "GET");

            try {
                isDone = json.getBoolean("done");

                if (isDone)
                    usersData.updateUser(new User(json.getString("account"), json.getString("name"), json.getString("surname")));
                else {
                    //TODO: non c'è connessione, cosa fare?
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return isDone;
    }*/

    public void registerUser(String account, String password, String name, String surname) {
        if (getIsConnected().getValue()) {
            HashMap<String, String> items = new HashMap<String, String>();
            items.put("account", account);
            items.put("password", password);
            items.put("name", name);
            items.put("surname", surname);

            launchThread(myURLs.getServerUrlRegistration(), items, "POST", "registration");
        }
    }

    public void loginUser(String account, String password) {
        if (getIsConnected().getValue()) {
            HashMap<String, String> items = new HashMap<String, String>();
            items.put("account", account);
            items.put("password", password);

            launchThread(myURLs.getServerUrlLogin(), items, "POST", "login");
        }
    }

    public void testServerConnection(String timeout, String type) {
        new CheckConnectionAsync().execute(myURLs.getServerUrlCheckSession(), timeout, type);
    }

    public void setSelectedHistoryTab(int selectedHistoryTab) {
        this.selectedHistoryTab = selectedHistoryTab;
    }

    public void fetchBookedHistory() {
        if (getIsConnected().getValue()) {
            HashMap<String, String> items = new HashMap<>();

            String state = "Active";
            if (selectedHistoryTab == 0)
                state = "Active";
            else if (selectedHistoryTab == 1)
                state = "Canelled";
            else
                state = "Done";

            items.put("state", state);
            launchThread(myURLs.getServerUrlBookedHistoryRepetitions(), items, "POST", "booked");
        }
    }

    public void fetchBookedRepetitions() {
        if (getIsConnected().getValue()) {
            launchThread(myURLs.getServerUrlBookedRepetitions(), new HashMap<>(), "POST", "booking");
        }
    }
    /*END GETTING DATA FROM DB VIA JAVA SERVLETS*/


    /*utility methods*/
    private String sendPOSTRequest(String urlServer, HashMap<String, String> params) {
        HttpURLConnection conn = null;

        JsonObject jsonObject = new JsonObject();
        for (String itemKey : params.keySet()) {
            jsonObject.addProperty(itemKey, params.get(itemKey));
        }

        try {
            URL url = new URL(urlServer);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(8000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(String.valueOf(jsonObject));
                writer.flush();
                writer.close();
            }

            conn.connect();
            int response = conn.getResponseCode();
            Log.d("HttpURLConnection", "The response is: " + response);
            // Converti  InputStream in JSON
            return readIt(conn.getInputStream());
        } catch (Exception ex) {
            Log.e("async", ex.getMessage());
            return "no_connection";
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

    }

    private String sendGETRequest(String urlServer, HashMap<String, String> params) {
        HttpURLConnection conn = null;

        urlServer += "?";
        JsonObject jsonObject = new JsonObject();
        int i = 0;
        for (String itemKey : params.keySet()) {
            jsonObject.addProperty(itemKey, params.get(itemKey));
            if (i == 0)
                urlServer += itemKey + "=" + params.get(itemKey);
            else
                urlServer += "&" + itemKey + "=" + params.get(itemKey);
            i++;
        }

        try {
            URL url = new URL(urlServer);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(8000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();
            int response = conn.getResponseCode();
            Log.d("HttpURLConnection", "The response is: " + response);
            // Converti  InputStream in JSON
            return readIt(conn.getInputStream());
        } catch (Exception ex) {
            Log.e("async", ex.getMessage());
            return "no_connection";
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

    }

    private String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            result.append(line).append("\n");
        }
        return result.toString();
    }

    public void launchThread(String url, HashMap<String, String> params, String connType, String service) {
        AtomicReference<JSONObject> json = new AtomicReference<>();
        Executor e = Executors.newSingleThreadExecutor();
        e.execute(() -> {
            String val;
            if (connType.equals("GET"))
                val = sendGETRequest(url, params);
            else if (connType.equals("POST"))
                val = sendPOSTRequest(url, params);
            else
                val = null;
            //TODO: quando si entra nella pagina di login arrviano dei dati dal server sulle prenotazioni
            //TODO: dividere i live data delle booking e delle booked perchè quando si passa della booked alle booking rimangono visualizzate quelle booked
            try {
                if (val.equals("no_connection")) {
                    if (service.equals("login") || service.equals("registration"))
                        usersData.updateUser(null);
                    else if (service.equals("booking") || service.equals("booked")) {
                        bookedRepetitionsData.updateBookedRepetitions(null);
                    }

                    isConnected.setValue(false);
                } else {
                    json.set(new JSONObject(val));
                    boolean isDone = json.get().getBoolean("done");

                    if (service.equals("login") || service.equals("registration")) {
                        if (isDone)
                            if (service.equals("login"))
                                usersData.updateUser(new User(json.get().getString("account"), json.get().getString("name"), json.get().getString("surname"))); // aggiorna i live data
                            else
                                usersData.updateUser(new User(json.get().getString("account"), json.get().getString("pwd"), json.get().getString("role"), json.get().getString("name"), json.get().getString("surname"))); // aggiorna i live data
                        else
                            usersData.updateUser(null);
                    } else if (service.equals("booking")) {
                        ArrayList<BookedRepetitions> bookedRepetitions = new ArrayList<>();
                        if (isDone) {
                            JSONArray jsonArray = json.get().getJSONArray("results");

                            // DATA FORMAT EXAMPLE
                            /*
                             * JSONObject myJSON = {
                             *   done : true,
                             *   results : [
                             *               {
                             *                day:monday,
                             *                startTime:15:00,
                             *                IDCourse:5,
                             *                IDTeacher:3
                             *               },
                             *               {
                             *               day:tuesday,
                             *                startTime:17:00,
                             *               IDCourse:3,
                             *               IDTeacher:1
                             *              }
                             *             ]
                             * }
                             * */

                            for (int i = 0; i < jsonArray.length(); ++i) {
                                JSONObject jsonItem = jsonArray.getJSONObject(i);
                                BookedRepetitions item = new BookedRepetitions(jsonItem.getString("day"), jsonItem.getString("startTime"), jsonItem.getInt("IDCourse"), jsonItem.getInt("IDTeacher"));
                                bookedRepetitions.add(item);
                            }
                            bookedRepetitionsData.updateBookedRepetitions(bookedRepetitions);
                        } else {
                            Toast.makeText(getApplication().getApplicationContext(), json.get().getString("error"), Toast.LENGTH_LONG).show();
                            bookedRepetitionsData.updateBookedRepetitions(bookedRepetitions);
                        }
                    } else if (service.equals("booked")) {
                        ArrayList<BookedRepetitions> bookedRepetitions = new ArrayList<BookedRepetitions>();
                        if (isDone) {
                            JSONArray jsonArray = json.get().getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); ++i) {
                                JSONObject jsonItem = jsonArray.getJSONObject(i);
                                BookedRepetitions item = new BookedRepetitions(jsonItem.getString("day"), jsonItem.getString("startTime"), jsonItem.getInt("IDCourse"), jsonItem.getInt("IDTeacher"));
                                bookedRepetitions.add(item);
                            }
                            bookedRepetitionsData.updateBookedRepetitions(bookedRepetitions);
                        } else {
                            Toast.makeText(getApplication().getApplicationContext(), json.get().getString("error"), Toast.LENGTH_LONG).show();
                            bookedRepetitionsData.updateBookedRepetitions(bookedRepetitions);
                        }
                    }
                }
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
            }
        });
    }

    public class CheckConnectionAsync extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                Thread.sleep(Integer.parseInt(strings[1]));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            HashMap<String, String> param = new HashMap<>();
            param.put("type", strings[2]);
            return sendGETRequest(strings[0], param);
        }

        @Override
        protected void onPostExecute(String s) {
            isConnected.setValue(!(s.equals("no_connection") || s.equals("no_connection\n"))); //Il server risponde con un \n alla fine
        }
    }
}