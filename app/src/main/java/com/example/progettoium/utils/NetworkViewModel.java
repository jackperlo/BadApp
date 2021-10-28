package com.example.progettoium.utils;

import android.app.Application;
import android.util.Log;

import com.example.progettoium.data.Users;
import com.google.gson.JsonObject;

import androidx.lifecycle.AndroidViewModel;

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

/*
* View Model di interfaccia con la rete internet
*/
public class NetworkViewModel extends AndroidViewModel {
    private final NetworkLiveData networkData;

    public NetworkViewModel(Application application) {
        super(application);
        networkData = new NetworkLiveData();
    }

    public boolean[] registerUser(String name, String email, String password) {
        final boolean[] correctRegistration = {false};
        ExecutorService es = Executors.newSingleThreadExecutor();

        List<Callable<Object>> todo = new ArrayList<>();
        todo.add(Executors.callable(() -> {
            HashMap<String, String> items = new HashMap<String, String>();
            items.put("name", name);
            items.put("email", email);
            items.put("password", password);
            String val = sendPOSTRequest(myURLs.getServerUrlRegistration(), items);
            try {
                JSONObject jsonObject = new JSONObject(val);
                if(Boolean.parseBoolean(jsonObject.get("done").toString())) {
                    Users user = new Users(jsonObject.get("name").toString(), jsonObject.get("email").toString());
                    networkData.updateUser(user); // aggiorna i live data
                    correctRegistration[0] = true;
                }
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
            }
        }));

        try {
            es.invokeAll(todo);
            // istruzioni fatte dopo il richiamo
            return correctRegistration;
        } catch (InterruptedException e) {
            e.printStackTrace();

            return correctRegistration;
        }
    }

    public NetworkLiveData getRegisteredUser() {
        return networkData;
    }

    public boolean[] loginUser(String email, String password) {
        final boolean[] correctLogin = {false};
        ExecutorService es = Executors.newSingleThreadExecutor();

        List<Callable<Object>> todo = new ArrayList<>();
        todo.add(Executors.callable(() -> {
            HashMap<String, String> items = new HashMap<String, String>();
            items.put("email", email);
            items.put("password", password);
            String val = sendPOSTRequest(myURLs.getServerUrlLogin(), items);
            try {
                JSONObject jsonObject = new JSONObject(val);
                if(Boolean.parseBoolean(jsonObject.get("done").toString())) {
                    networkData.updateUser(new Users(jsonObject.get("name").toString(), email));
                    correctLogin[0] = true;
                }
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
            }
        }));

        try {
            es.invokeAll(todo);
            // istruzioni fatte dopo il richiamo
            return correctLogin;
        } catch (InterruptedException e) {
            e.printStackTrace();

            return correctLogin;
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
    
}



    // TODO: dare un occhio a AsyncHTTPClient come alternativa easy to read agli executor
    /*

    class Model {

    private static AsyncHttpClient client;

    Model() {
        client = new AsyncHttpClient();
    }

    void checkLogin(final Context ctx, final Class classe, String u, String p, RequestParams params) {

        final String username = new String(u);
        final String password = new String(p);

        client.get(MYURL, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(ctx, "Login_success" + username, Toast.LENGTH_SHORT).show();
                Intent i1 = new Intent(ctx, classe);
                Intent i2 = new Intent(ctx, AdministratorActivity.class);

                //Controllo se è amministratore
                Boolean isAdmin = false;

                try {
                    isAdmin = response.getBoolean("ISADMIN");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(!isAdmin) {
                    i1.putExtra("username", username);
                    ctx.startActivity(i1);
                } else {
                    i2.putExtra("username", username);
                    ctx.startActivity(i2);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(ctx, "username o password incorretta", Toast.LENGTH_SHORT).show();
            }
        });
    }







    public void goToURL() {
        Executor e = Executors.newSingleThreadExecutor();
        e.execute(() -> {
            // a potentially time consuming task
            String val = downloadUrl(exampleURL);
            try {
                JSONObject jsonObject = new JSONObject(val);
                JSONArray results = jsonObject.getJSONArray("results");
                JSONObject name = results.getJSONObject(0).getJSONObject("name");
                Users myUsers = new Users(1, name.get("first").toString(), name.get("last").toString(), results.getJSONObject(0).get("email").toString());
                data.update(myUsers); // aggiorna i live data
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
            }
        });
    }*/

    /*private String downloadUrl(String myurl) {
        Log.d("DOWNLOAD", "Download done succesfully");
        HttpURLConnection conn = null;
        try {
            URL url = new URL(myurl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds *///);
/*conn.setConnectTimeout(15000 /* milliseconds *///);
            /*conn.setRequestMethod("GET");
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
    }*/