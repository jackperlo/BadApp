package com.example.progettoium.utils;

import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.example.progettoium.data.BookedRepetitions;
import com.example.progettoium.data.Courses;
import com.example.progettoium.data.FreeRepetitions;
import com.example.progettoium.data.Teachers;
import com.example.progettoium.data.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import androidx.lifecycle.AndroidViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
    private final FreeRepetitionsLiveData freeRepetitionsData;
    private final BookedRepetitionsLiveData bookedRepetitionsData;
    private final ConnectionLiveData isConnected;
    private final ManageRepetitionsLiveData managedData;
    private final BookRepetitionLiveData bookRepetitionData;
    private String onDay="";
    private String onState="";
    private final String SHARED_NAME = "SESSION";
    private String sessionToken="";

    public NetworkViewModel(Application application) {
        super(application);

        usersData = new UserLiveData();
        freeRepetitionsData = new FreeRepetitionsLiveData();
        bookedRepetitionsData = new BookedRepetitionsLiveData();
        isConnected = new ConnectionLiveData();
        managedData = new ManageRepetitionsLiveData();
        bookRepetitionData = new BookRepetitionLiveData();
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public void setOnDay(String onDay){
        this.onDay = onDay;
    }

    public void setOnState(String onState) {
        this.onState = onState;
    }

    public String getOnState() {
        return onState;
    }

    /*GETTING DATA FROM LIVE DATA ALREADY FILLED UP FROM DB QUERIES*/
    public UserLiveData getRegisteredUser() {
        return usersData;
    }

    public BookedRepetitionsLiveData getBookedRepetitions() {
        return bookedRepetitionsData;
    }

    public FreeRepetitionsLiveData getFreeRepetitions() {
        return freeRepetitionsData;
    }

    public ConnectionLiveData getIsConnected() {
        return isConnected;
    }

    public ManageRepetitionsLiveData getManaged() {
        return managedData;
    }
    
    public BookRepetitionLiveData getbookRepetitionData() {
        return bookRepetitionData;
    }
    /*END GETTING DATA FROM LIVE DATA*/

    /*GETTING DATA FROM DB VIA JAVA SERVLETS*/
    public void checkSession() {
        HashMap<String, String> items = new HashMap<String, String>();
        launchThread(myURLs.getServerUrlCheckSession(), items, "POST", "check session", sessionToken);
    }

    public void registerUser(String account, String password, String name, String surname) {
        if (getIsConnected().getValue() != null && getIsConnected().getValue()) {
            HashMap<String, String> items = new HashMap<String, String>();
            items.put("account", account);
            items.put("password", password);
            items.put("name", name);
            items.put("surname", surname);
            items.put("role", "Client");

            launchThread(myURLs.getServerUrlRegistration(), items, "POST", "registration", "");
        }
    }

    public void loginUser(String account, String password) {
        if (getIsConnected().getValue() != null && getIsConnected().getValue()) {
            HashMap<String, String> items = new HashMap<String, String>();
            items.put("account", account);
            items.put("password", password);

            launchThread(myURLs.getServerUrlLogin(), items, "POST", "login", "");
        }
    }

    public void logoutUser() {
        launchThread(myURLs.getServerUrlLogout(), new HashMap<>(), "GET", "logout", "");
    }

    public void testServerConnection(String timeout, String type) {
        new CheckConnectionAsync().execute(myURLs.getServerUrlCheckSession(), timeout, type);
    }

    public void fetchBookedHistory() {
        if (getIsConnected().getValue()) {
            HashMap<String, String> items = new HashMap<>();

            items.put("state", this.onState);
            items.put("account", usersData.getValue().first.getAccount());
            launchThread(myURLs.getServerUrlBookedHistoryRepetitions(), items, "POST", "booked", sessionToken);
        }
    }

    public void fetchFreeRepetitions(String day) {
        HashMap<String, String> items = new HashMap<>();
        items.put("day", day);

        if(usersData.getValue()!=null && usersData.getValue().first.getAccount()!=null && usersData.getValue().first.getSurname()!=null && usersData.getValue().first.getName()!=null)
            items.put("account", usersData.getValue().first.getAccount());

        launchThread(myURLs.getServerUrlFreeRepetitions(), items, "POST", "free", "");

    }

    public void bookARepetition(Courses selectedCourse, Teachers selectedTeacher, String startTime){
        new BookARepetition().execute(myURLs.getServerUrlBookARepetition(), String.valueOf(3000), this.onDay, startTime, String.valueOf(selectedCourse.getIDCourse()), String.valueOf(selectedTeacher.getIDTeacher()), usersData.getValue().first.getAccount());
    }

    public void changeRepetitionState(String IDRepetition, String newState) {
        new ManageBookedRepetition().execute(IDRepetition, newState);
    }
    /*END GETTING DATA FROM DB VIA JAVA SERVLETS*/

    /*utility methods*/
    private String sendPOSTRequest(String urlServer, HashMap<String, String> params, String token) {
        HttpURLConnection conn = null;

        String parameters = "";
        int i = 0;
        for (String itemKey : params.keySet()) {
            if (i == 0)
                parameters += itemKey + "=" + params.get(itemKey);
            else
                parameters += "&" + itemKey + "=" + params.get(itemKey);
            i++;
        }
        byte[] postData = parameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;

        try {
            URL url = new URL(urlServer);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(20000 /* milliseconds */);
            conn.setConnectTimeout(20000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setUseCaches(false);
            conn.setDoOutput(true);

            if(!token.equals(""))
                conn.setRequestProperty("Cookie", "JSESSIONID=" + token);

            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(postData);
                wr.flush();
            }

            conn.connect();
            int response = conn.getResponseCode();
            // Converti  InputStream in JSON
            return readIt(conn.getInputStream());
        } catch (Exception ex) {
            Log.e("async", ex.getMessage());
            return "{'done': false, 'error': 'no_connection'}";
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

    }

    private String sendGETRequest(String urlServer, HashMap<String, String> params, String token) {
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
            conn.setReadTimeout(20000 /* milliseconds */);
            conn.setConnectTimeout(20000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            if(!token.equals(""))
                conn.setRequestProperty("Cookie", "JSESSIONID=" + token);

            conn.connect();
            int response = conn.getResponseCode();

            // Converti  InputStream in JSON
            return readIt(conn.getInputStream());
        } catch (Exception ex) {
            Log.e("async", ex.getMessage());
            return "{'done': false, 'error': 'no_connection'}";
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

    public void launchThread(String url, HashMap<String, String> params, String connType, String service, String token) {
        AtomicReference<JSONObject> json = new AtomicReference<>();
        Executor e = Executors.newSingleThreadExecutor();
        e.execute(() -> {
            String val;
            if (connType.equals("GET"))
                val = sendGETRequest(url, params, token);
            else if (connType.equals("POST"))
                val = sendPOSTRequest(url, params, token);
            else
                val = null;

            try {
                json.set(new JSONObject(val));
                boolean isDone = json.get().getBoolean("done");

                if (!isDone) {
                    if(json.get().getString("error").equals("no_connection"))
                        isConnected.updateConnection(false);

                    if (service.equals("login") || service.equals("registration"))
                        if(json.get().getString("error").contains("Duplicate entry")) {
                            usersData.updateUser(new Pair<>(new User(), "duplicate email"));
                        } else if(json.get().getString("error").equals("registration failed") || json.get().getString("error").equals("user not found")) {
                            usersData.updateUser(new Pair<>(new User(), service));
                        } else{
                            //Errore sul db --> il db non è connesso...
                            usersData.updateUser(new Pair<>(null, service));
                        }
                    else if (service.equals("booked")) {
                        if(json.get().getString("error").equals("no session")) {
                            usersData.updateUser(new Pair<>(new User(), "session expired"));
                        } else {
                            //Errore sul db --> il db non è connesso...
                            bookedRepetitionsData.updateBookedRepetitions(null);
                        }
                    } else if(service.equals("free")){
                        //Errore sul db --> il db non è connesso...
                        freeRepetitionsData.updateFreeRepetitions(null);
                    } else if(service.equals("check session")) {
                        if(!json.get().getString("error").equals("no session")) {
                            //Errore sul db --> il db non è connesso...
                            usersData.updateUser(new Pair<>(null, service));
                        } else
                            usersData.updateUser(new Pair<>(new User(), service));
                    }
                } else {
                    if (service.equals("login") || service.equals("registration")) {
                        if (service.equals("login"))
                            usersData.updateUser(new Pair<>(new User(json.get().getString("account"), json.get().getString("name"), json.get().getString("surname"), json.get().getString("token")), service)); // aggiorna i live data
                        else
                            usersData.updateUser(new Pair<>(new User(json.get().getString("account"), json.get().getString("pwd"), json.get().getString("role"), json.get().getString("name"), json.get().getString("surname"), json.get().getString("token")), service)); // aggiorna i live data
                    } else if (service.equals("free")) {
                        ArrayList<FreeRepetitions> freeRepetitions = new ArrayList<FreeRepetitions>();
                        ArrayList<Courses> courses = null;
                        ArrayList<Teachers> teachers = null;

                        JSONArray jsonArray = json.get().getJSONArray("results");
                        for (int i = 0; i < jsonArray.length(); ++i) { //for every hour of that day
                            JSONObject jsonItem = jsonArray.getJSONObject(i);
                            JSONArray coursesList = jsonItem.getJSONArray("coursesList");
                            courses = new ArrayList<>();
                            for (int j = 0; j < coursesList.length(); ++j) { //for every available course
                                JSONObject courseItem = coursesList.getJSONObject(j);
                                JSONArray teachersList = courseItem.getJSONArray("teachersList");
                                teachers = new ArrayList<>();
                                for (int k = 0; k < teachersList.length(); ++k) { //for every teacher available to do repetition for that course at that time
                                    JSONObject teacherItem = teachersList.getJSONObject(k);
                                    Teachers teacher = new Teachers(teacherItem.getInt("IDTeacher"), teacherItem.getString("Name"), teacherItem.getString("Surname"));
                                    teachers.add(teacher);
                                }
                                Courses course = new Courses(courseItem.getInt("IDCourse"), courseItem.getString("Title"), teachers);
                                courses.add(course);
                            }
                            FreeRepetitions item = new FreeRepetitions(params.get("day"), jsonItem.getString("startTime"), courses);
                            freeRepetitions.add(item);
                        }
                        freeRepetitionsData.updateFreeRepetitions(freeRepetitions);
                    } else if (service.equals("booked")) {
                        ArrayList<BookedRepetitions> bookedRepetitions = new ArrayList<BookedRepetitions>();
                        JSONArray jsonArray = json.get().getJSONArray("results");
                        for (int i = 0; i < jsonArray.length(); ++i) {
                            JSONObject jsonItem = jsonArray.getJSONObject(i);
                            BookedRepetitions item = new BookedRepetitions(jsonItem.getString("IDRepetition"), jsonItem.getString("day"), jsonItem.getString("startTime"), jsonItem.getString("title"), jsonItem.getString("surname"), jsonItem.getString("name"), jsonItem.getInt("idCourse"), jsonItem.getInt("idTeacher"));
                            bookedRepetitions.add(item);
                        }
                        bookedRepetitionsData.updateBookedRepetitions(bookedRepetitions);
                    } else if(service.equals("logout")) {
                        usersData.updateUser(new Pair<>(new User(), service));
                    } else if(service.equals("check session")) {
                        usersData.updateUser(new Pair<>(new User(json.get().getString("account"), json.get().getString("name"), json.get().getString("surname"), json.get().getString("token")), service)); // aggiorna i live data
                        SharedPreferences sharedPreferences = getApplication().getSharedPreferences(this.SHARED_NAME, 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("session_token", json.get().getString("token"));
                        editor.apply();
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
            HashMap<String, String> param = new HashMap<>();
            param.put("type", strings[2]);
            return sendGETRequest(strings[0], param, "");
        }

        @Override
        protected void onPostExecute(String s) {
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(s);
                boolean connection = jsonObject.getBoolean("done");

                isConnected.setValue(connection);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class ManageBookedRepetition extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> items = new HashMap<>();
            /*IDRepetition, newState, day, startTime, String.valueOf(idCourse), String.valueOf(idTeacher)*/
            items.put("IDRepetition", strings[0]);
            items.put("newState", strings[1]);

            return sendGETRequest(myURLs.getServerUrlManageRepetitions(), items, sessionToken);
        }

        @Override
        protected void onPostExecute(String retVal) {
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(retVal);
                boolean isManaged = jsonObject.getBoolean("done");

                if(isManaged) {
                    managedData.setValue(isManaged);
                } else {
                    if(jsonObject.getString("error").equals("no session")) {
                        usersData.updateUser(new Pair<>(new User(), "session expired"));
                        managedData.setValue(false);
                    } else
                        managedData.setValue(null);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class BookARepetition extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> items = new HashMap<>();
            items.put("day", strings[2]);
            items.put("startTime", strings[3]);
            items.put("IDCourse", strings[4]);
            items.put("IDTeacher", strings[5]);
            items.put("account", strings[6]);

            return sendPOSTRequest(strings[0], items, sessionToken);
        }

        @Override
        protected void onPostExecute(String s) {
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(s);

                if(jsonObject.getBoolean("done")) {
                    String result = jsonObject.getString("results");
                    bookRepetitionData.setValue(result);
                } else {
                    if(jsonObject.getString("error").equals("no session")) {
                        usersData.updateUser(new Pair<>(new User(), "session expired"));
                        bookRepetitionData.setValue("no session");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}