package com.example.progettoium.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.progettoium.data.BookedRepetitions;
import com.example.progettoium.data.User;
import com.example.progettoium.databinding.FragmentHomeBinding;
import com.example.progettoium.ui.home.HomeFragment;
import com.example.progettoium.ui.login.LoginFragment;
import com.example.progettoium.utils.NetworkViewModel;
import com.example.progettoium.R;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.progettoium.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding mainActivityBinding;

    private NetworkViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivityBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainActivityBinding.getRoot());

        AtomicReference<Boolean> checkSession = new AtomicReference<>(); //serve per controllare la sessione una volta sola
        checkSession.set(true);

        CookieHandler.setDefault(new CookieManager());

        model = new ViewModelProvider(this).get(NetworkViewModel.class);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("SESSION", 0);
        model.setSessionToken(sharedPreferences.getString("session_token", ""));

        setSupportActionBar(mainActivityBinding.appBarMain.toolbar);
        DrawerLayout drawer = mainActivityBinding.drawerLayout;
        NavigationView navigationView = mainActivityBinding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_login, R.id.nav_booked)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.getMenu().findItem(R.id.nav_booked).setVisible(false);
        mainActivityBinding.btnLogOut.setVisibility(View.INVISIBLE);

        //mainActivityBinding.loading.setVisibility(View.VISIBLE);
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getApplicationContext().getResources().getString(R.string.connection));
        progressDialog.show();
        model.testServerConnection("0", "check_connection_server");
        model.getIsConnected().observe(this, connected -> {
            if (connected) {
                //mainActivityBinding.loading.setVisibility(View.INVISIBLE);
                if(checkSession.get()) {
                    progressDialog.show();
                    model.checkSession();
                    checkSession.set(false);
                }
            } else {
                progressDialog.show();
                //mainActivityBinding.loading.setVisibility(View.VISIBLE);
                model.testServerConnection("2500", "check_connection_server");
            }
        });

        model.getRegisteredUser().observe(this, user -> {
            if (user.first != null) {
                TextView txtNome = findViewById(R.id.txtNameSurname);
                TextView txtMail = findViewById(R.id.txtMail);

                txtNome.setText(user.first.getName() + " " + user.first.getSurname());
                txtMail.setText(user.first.getAccount());
            }

            if (user.second.equals("check session")) {
                if(progressDialog != null)
                    progressDialog.dismiss();

                if (user.first == null) {
                    String out = getApplicationContext().getResources().getString(R.string.no_db_connection);
                    Snackbar.make(this.mainActivityBinding.getRoot(), out, Snackbar.LENGTH_LONG).setAction("Action", null).show();

                    navigationView.getMenu().findItem(R.id.nav_booked).setVisible(false);
                    mainActivityBinding.btnLogOut.setVisibility(View.INVISIBLE);
                } else if (!user.first.isEmpty()) {
                    navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
                    navigationView.getMenu().findItem(R.id.nav_booked).setVisible(true);
                    mainActivityBinding.btnLogOut.setVisibility(View.VISIBLE);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("session_token", user.first.getToken());
                    editor.apply();
                } else {
                    navigationView.getMenu().findItem(R.id.nav_booked).setVisible(false);
                    mainActivityBinding.btnLogOut.setVisibility(View.INVISIBLE);
                }

                int tabPosition = FragmentHomeBinding.inflate(getLayoutInflater()).tabLayout.getSelectedTabPosition();
                model.fetchFreeRepetitions(getWeekDay(tabPosition));
                model.setOnDay(getWeekDay(tabPosition));
            } else if(user.second.equals("session expired")) {
                logOutOperations(navigationView, sharedPreferences, drawer);
                String out = getApplicationContext().getResources().getString(R.string.session_expired);
                Snackbar.make(this.mainActivityBinding.getRoot(), out, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                model.fetchFreeRepetitions(getWeekDay(0));
                model.setOnDay(getWeekDay(0));
            } else if(user.second.equals("logout")) {
                String out = getApplicationContext().getResources().getString(R.string.logout_success);
                Snackbar.make(this.mainActivityBinding.getRoot(), out, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                model.fetchFreeRepetitions(getWeekDay(0));
                model.setOnDay(getWeekDay(0));
            }
        });

        mainActivityBinding.btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOutOperations(navigationView, sharedPreferences, drawer);
            }
        });
    }

    private void logOutOperations(NavigationView navigationView, SharedPreferences sharedPreferences, DrawerLayout drawer) {
        model.logoutUser();
        model.getRegisteredUser().updateUser(new Pair<>(new User(), ""));
        model.getBookedRepetitions().updateBookedRepetitions(new ArrayList<>());
        mainActivityBinding.btnLogOut.setVisibility(View.INVISIBLE);
        navigationView.getMenu().findItem(R.id.nav_booked).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
        model.setSessionToken("");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        drawer.closeDrawer(navigationView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private String getWeekDay(int tabValue) {
        String ret = "";
        switch (tabValue) {
            case 0:
                ret = "Monday";
                break;
            case 1:
                ret = "Tuesday";
                break;
            case 2:
                ret = "Wednesday";
                break;
            case 3:
                ret = "Thursday";
                break;
            case 4:
                ret = "Friday";
                break;
            default:
                break;
        }
        return ret;
    }
}