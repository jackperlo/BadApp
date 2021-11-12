package com.example.progettoium.utils;

import android.app.Application;
import android.util.Log;

import com.example.progettoium.data.Courses;
import com.example.progettoium.data.FreeRepetitions;
import com.example.progettoium.data.Teachers;
import com.example.progettoium.data.User;
import com.google.gson.JsonObject;

import androidx.lifecycle.AndroidViewModel;

import org.json.JSONArray;
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
    private final UserLiveData usersData;
    private final FreeRepetitionsLiveData freeRepetitionsData;

    public NetworkViewModel(Application application) {
        super(application);

        usersData = new UserLiveData();
        freeRepetitionsData = new FreeRepetitionsLiveData();
    }

    /*GETTING DATA FROM LIVE DATA ALREADY FILLED UP FROM DB QUERIES*/
    public UserLiveData getRegisteredUser() { return usersData; }

    public FreeRepetitionsLiveData getFreeRepetitions(){ return freeRepetitionsData; }
    /*END GETTING DATA FROM LIVE DATA*/

    /*GETTING DATA FROM DB VIA JAVA SERVLETS*/
    public boolean checkSession(){
        JSONObject json = launchThread(myURLs.getServerUrlCheckSession(), new HashMap<>(), "GET");
        boolean isDone = false;

        try {
            isDone = json.getBoolean("done");

            if(isDone)
                usersData.updateUser(new User(json.getString("account"), json.getString("name"), json.getString("surname")));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return isDone;
    }

    public boolean registerUser(String account, String password, String name, String surname) {
        HashMap<String, String> items = new HashMap<String, String>();
        items.put("account", account);
        items.put("password", password);
        items.put("name", name);
        items.put("surname", surname);

        JSONObject json = launchThread(myURLs.getServerUrlRegistration(), items, "POST");
        boolean isDone = false;

        try {
            isDone = json.getBoolean("done");

            if(isDone)
                usersData.updateUser(new User(json.getString("account"), json.getString("pwd"), json.getString("role"), json.getString("name"), json.getString("surname"))); // aggiorna i live data

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return isDone;
    }

    public boolean loginUser(String account, String password) {
        HashMap<String, String> items = new HashMap<String, String>();
        items.put("account", account);
        items.put("password", password);

        JSONObject json = launchThread(myURLs.getServerUrlLogin(), items, "POST");
        boolean isDone = false;

        try {
            isDone = json.getBoolean("done");

            if(isDone)
                usersData.updateUser(new User(json.getString("account"), json.getString("name"), json.getString("surname")));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return isDone;
    }

    public boolean fetchFreeRepetitions(String day){
        HashMap<String, String> items = new HashMap<>();
        items.put("day", day);
        JSONObject jsonResult = launchThread(myURLs.getServerUrlFreeRepetitions(), items, "POST");

        // DATA FORMAT EXAMPLE
        /*
        JSONObject myJSON =
         {
           done : true,
           results : [
                   {
                     day:monday,
                     startTime:15:00,
                     coursesList:[
                                   {
                                       Title: Prog III,
                                       IDCourse: 5,
                                       teachersList:[
                                            {
                                                Surname: Esposito,
                                                Name: Roberto,
                                                IDTeacher: 1
                                            },
                                            {
                                                 Surname: Aringhieri,
                                                 Name: Roberto,
                                                 IDTeacher:  6
                                            }
                                       ]
                                   },
                                   {
                                       Title: IUM,
                                       IDCourse: 8,
                                       teachersList:[
                                            {
                                                Surname: Esposito,
                                                Name: Roberto,
                                                IDTeacher: 1
                                            }
                                       ]
                                   },
                                   {....}
                    ]
                  },
                  {
                    day:monday,
                    startTime:16:00,
                    courseList:[...]
                  }
               ]
          }
        * */

        boolean isDone = false;

        try {
            isDone = jsonResult.getBoolean("done");

            if(isDone) {
                ArrayList<FreeRepetitions> freeRepetitions = new ArrayList<FreeRepetitions>();
                ArrayList<Courses> courses = null;
                ArrayList<Teachers> teachers = null;

                JSONArray jsonArray = jsonResult.getJSONArray("results");
                for (int i = 0; i < jsonArray.length(); ++i) { //for every hour of that day
                    JSONObject jsonItem = jsonArray.getJSONObject(i);
                    JSONArray coursesList = jsonItem.getJSONArray("coursesList");
                    courses = new ArrayList<>();
                    for (int j = 0; j < coursesList.length(); ++j){ //for every available course
                        JSONObject courseItem = coursesList.getJSONObject(j);
                        JSONArray teachersList = courseItem.getJSONArray("teachersList");
                        teachers = new ArrayList<>();
                        for (int k = 0; k < teachersList.length(); ++k){ //for every teacher available to do repetition for that course at that time
                            JSONObject teacherItem = teachersList.getJSONObject(k);
                            Teachers teacher = new Teachers(teacherItem.getInt("IDTeacher"), teacherItem.getString("Name"), teacherItem.getString("Surname"));
                            teachers.add(teacher);
                        }
                        Courses course = new Courses(courseItem.getInt("IDCourse"), courseItem.getString("Title"), teachers);
                        courses.add(course);
                    }
                    FreeRepetitions item = new FreeRepetitions(day, jsonItem.getString("startTime"), courses);
                    freeRepetitions.add(item);
                }
                freeRepetitionsData.updateFreeRepetitions(freeRepetitions);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return isDone;
    }
    /*END GETTING DATA FROM DB VIA JAVA SERVLETS*/


    /*utility methods*/
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

}