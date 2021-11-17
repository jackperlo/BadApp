package com.example.progettoium.utils;

import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
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

    public NetworkViewModel(Application application) {
        super(application);

        usersData = new UserLiveData();
        freeRepetitionsData = new FreeRepetitionsLiveData();
        bookedRepetitionsData = new BookedRepetitionsLiveData();
        isConnected = new ConnectionLiveData();
        managedData = new ManageRepetitionsLiveData();
        bookRepetitionData = new BookRepetitionLiveData();
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
        // TODO: possibile aggiungere un controllo che guarda se esiste gia un utente con quella mail?
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

    public void logoutUser() {
        launchThread(myURLs.getServerUrlLogin(), new HashMap<>(), "GET", "logout");
    }

    public void testServerConnection(String timeout, String type) {
        new CheckConnectionAsync().execute(myURLs.getServerUrlCheckSession(), timeout, type);
    }

    public void fetchBookedHistory() {
        if (getIsConnected().getValue()) {
            HashMap<String, String> items = new HashMap<>();

            items.put("state", this.onState);
            items.put("account", usersData.getValue().getAccount());
            launchThread(myURLs.getServerUrlBookedHistoryRepetitions(), items, "POST", "booked");
        }
    }

    public void fetchFreeRepetitions(String day) {
        HashMap<String, String> items = new HashMap<>();
        items.put("day", day);
        if(usersData.getValue()!=null && usersData.getValue().getAccount()!=null && usersData.getValue().getSurname()!=null && usersData.getValue().getName()!=null)
            items.put("account", usersData.getValue().getAccount());
        launchThread(myURLs.getServerUrlFreeRepetitions(), items, "POST", "free");

    }

    public void bookARepetition(Courses selectedCourse, Teachers selectedTeacher, String startTime){
        //launchThread(myURLs.getServerUrlBookARepetition(), items, "POST", "book");

        new BookARepetition().execute(myURLs.getServerUrlBookARepetition(), String.valueOf(3000), this.onDay, startTime, String.valueOf(selectedCourse.getIDCourse()), String.valueOf(selectedTeacher.getIDTeacher()), usersData.getValue().getAccount());
    }

    public void changeRepetitionState(String newState, String day, String startTime, int idCourse, int idTeacher) {
        /*HashMap<String, String> items = new HashMap<>();
        items.put("newState", newState);
        items.put("day", day);
        items.put("startTime", startTime);
        items.put("idCourse", String.valueOf(idCourse));
        items.put("idTeacher", String.valueOf(idTeacher));
        items.put("account", usersData.getValue().getAccount());*/
        new ManageBookedRepetition().execute(newState, day, startTime, String.valueOf(idCourse), String.valueOf(idTeacher));
        //launchThread(myURLs.getServerUrlManageRepetitions(), items, "GET", "change_state");
    }

    /*END GETTING DATA FROM DB VIA JAVA SERVLETS*/

    /*utility methods*/
    private String sendPOSTRequest(String urlServer, HashMap<String, String> params) {
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

            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(postData);
                wr.flush();
            }

            conn.connect();
            int response = conn.getResponseCode();
            Log.d("HttpURLConnection", "The response is: " + response);
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
            conn.setReadTimeout(20000 /* milliseconds */);
            conn.setConnectTimeout(20000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();
            int response = conn.getResponseCode();
            Log.d("HttpURLConnection", "The response is: " + response);
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
            try {
                json.set(new JSONObject(val));
                boolean isDone = json.get().getBoolean("done");

                if (!isDone) {
                    if(json.get().getString("error").equals("no_connection"))
                        isConnected.updateConnection(false);

                    if (service.equals("login") || service.equals("registration"))
                        if(json.get().getString("error").equals("registration failed") || json.get().getString("error").equals("user not found"))
                            usersData.updateUser(new User());
                        else{
                            //Errore sul db --> il db non è connesso...
                            usersData.updateUser(null);
                        }
                    else if (service.equals("booked")) {
                        //Errore sul db --> il db non è connesso...
                        bookedRepetitionsData.updateBookedRepetitions(null);
                    } else if(service.equals("free")){
                        //Errore sul db --> il db non è connesso...
                        freeRepetitionsData.updateFreeRepetitions(null);
                    }
                } else {
                    if (service.equals("login"))
                        usersData.updateUser(new User(json.get().getString("account"), json.get().getString("name"), json.get().getString("surname"))); // aggiorna i live data
                    else if (service.equals("registration"))
                        usersData.updateUser(new User(json.get().getString("account"), json.get().getString("pwd"), json.get().getString("role"), json.get().getString("name"), json.get().getString("surname"))); // aggiorna i live data
                    else if (service.equals("free")) {
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
                            BookedRepetitions item = new BookedRepetitions(jsonItem.getString("day"), jsonItem.getString("startTime"), jsonItem.getString("title"), jsonItem.getString("surname"), jsonItem.getString("name"), jsonItem.getInt("idCourse"), jsonItem.getInt("idTeacher"));
                            bookedRepetitions.add(item);
                        }
                        bookedRepetitionsData.updateBookedRepetitions(bookedRepetitions);
                    } else if(service.equals("logout")) {
                        usersData.updateUser(new User());
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
            /*newState, day, startTime, String.valueOf(idCourse), String.valueOf(idTeacher)*/
            items.put("newState", strings[0]);
            items.put("day", strings[1]);
            items.put("startTime", strings[2]);
            items.put("idCourse", String.valueOf(strings[3]));
            items.put("idTeacher", String.valueOf(strings[4]));
            items.put("account", usersData.getValue().getAccount());

            return sendGETRequest(myURLs.getServerUrlManageRepetitions(), items);
        }

        @Override
        protected void onPostExecute(String retVal) {
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(retVal);
                boolean isManaged = jsonObject.getBoolean("done");

                if(!isManaged)
                    managedData.setValue(null);
                else
                    managedData.setValue(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class BookARepetition extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                Thread.sleep(Integer.parseInt(strings[1]));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            HashMap<String, String> items = new HashMap<>();
            items.put("day", strings[2]);
            items.put("startTime", strings[3]);
            items.put("IDCourse", strings[4]);
            items.put("IDTeacher", strings[5]);
            items.put("account", strings[6]);

            return sendPOSTRequest(strings[0], items);
        }

        @Override
        protected void onPostExecute(String s) {
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(s);
                String result = jsonObject.getString("results");
                bookRepetitionData.setValue(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}