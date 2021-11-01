package com.example.progettoium.utils;

import android.app.Application;
import android.util.Log;

import com.example.progettoium.data.Users;
import com.google.gson.JsonObject;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/*
* View Model di interfaccia con la rete internet
*/
public class NetworkViewModel extends AndroidViewModel{
    private final NetworkLiveData networkData;

    public NetworkViewModel(Application application) {
        super(application);

        networkData = new NetworkLiveData();
    }

    public NetworkLiveData getRegisteredUser() {
        return networkData;
    }

    public JSONObject launchThread(String url, HashMap<String, String> params, String type) {
        AtomicReference<JSONObject> returnJson = new AtomicReference<>();
        ExecutorService es = Executors.newSingleThreadExecutor();

        List<Callable<Object>> asyncFunction = new ArrayList<>();
        asyncFunction.add(Executors.callable(() -> {
            String val;
            if(type.equals("GET"))
                val = sendGETRequest(url, params);
            else if(type.equals("POST"))
                val = sendPOSTRequest(url, params);
            else
                val = null;

            try {
                returnJson.set(new JSONObject(val));
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
            }
        }));

        try {
            es.invokeAll(asyncFunction);
            // istruzioni fatte dopo il richiamo
            return returnJson.get();
        } catch (InterruptedException e) {
            e.printStackTrace();

            return returnJson.get();
        }
    }

    public boolean checkSession(){
        JSONObject json = launchThread(myURLs.getServerUrlCheckSession(), new HashMap<>(), "GET");
        boolean isDone = false;

        try {
            isDone = Boolean.parseBoolean(json.get("done").toString());

            if(isDone) {
                Users user = new Users(json.get("name").toString(), json.get("email").toString());
                networkData.updateUser(user); // aggiorna i live data
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return isDone;
    }

    public boolean registerUser(String name, String email, String password) {
        HashMap<String, String> items = new HashMap<String, String>();
        items.put("name", name);
        items.put("email", email);
        items.put("password", password);

        JSONObject json = launchThread(myURLs.getServerUrlRegistration(), items, "POST");
        boolean isDone = false;

        try {
            isDone = Boolean.parseBoolean(json.get("done").toString());

            if(isDone) {
                Users user = new Users(json.get("name").toString(), json.get("email").toString());
                networkData.updateUser(user); // aggiorna i live data
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return isDone;
    }

    public boolean loginUser(String email, String password) {
        HashMap<String, String> items = new HashMap<String, String>();
        items.put("email", email);
        items.put("password", password);

        JSONObject json = launchThread(myURLs.getServerUrlLogin(), items, "POST");
        boolean isDone = false;

        try {
            isDone = Boolean.parseBoolean(json.get("done").toString());

            if(isDone) {
                networkData.updateUser(new Users(json.get("name").toString(), email));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return isDone;
    }

    private String sendPOSTRequest(String urlServer, HashMap<String, String> params) {
        HttpURLConnection conn = null;

        urlServer += "?";
        JsonObject jsonObject = new JsonObject();
        int i = 0;
        for(String itemKey : params.keySet()){
            jsonObject.addProperty(itemKey, params.get(itemKey));
            if(i == 0)
                urlServer += itemKey + "=" + params.get(itemKey);
            else
                urlServer+= "&" + itemKey + "=" +params.get(itemKey);
            i++;
        }

        try {
            URL url = new URL(urlServer);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            conn.setDoOutput(true);

            String data = jsonObject.toString();
            try(OutputStream os = conn.getOutputStream()){
                os.write(data.getBytes(StandardCharsets.UTF_8));
            }

            conn.connect();
            int response = conn.getResponseCode();
            Log.d("HttpURLConnection", "The response is: " + response);
            // Converti  InputStream in JSON
            return readIt(conn.getInputStream());
        } catch (Exception ex) {
            Log.e("async", ex.getMessage());
            return null;
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
        for(String itemKey : params.keySet()){
            jsonObject.addProperty(itemKey, params.get(itemKey));
            if(i == 0)
                urlServer += itemKey + "=" + params.get(itemKey);
            else
                urlServer+= "&" + itemKey + "=" +params.get(itemKey);
            i++;
        }

        try {
            URL url = new URL(urlServer);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();
            int response = conn.getResponseCode();
            Log.d("HttpURLConnection", "The response is: " + response);
            // Converti  InputStream in JSON
            return readIt(conn.getInputStream());
        } catch (Exception ex) {
            Log.e("async", ex.getMessage());
            return null;
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
}