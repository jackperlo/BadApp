package com.example.progettoium.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.example.progettoium.utils.NetworkViewModel;
import com.example.progettoium.R;
import com.google.android.material.navigation.NavigationView;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.progettoium.databinding.ActivityMainBinding;

import java.net.CookieHandler;
import java.net.CookieManager;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding mainActivityBinding;

    private NetworkViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivityBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainActivityBinding.getRoot());

        CookieHandler.setDefault(new CookieManager());

        /*it must stay here because is shared between fragments (it's the side menu)*/
        model = new ViewModelProvider(this).get(NetworkViewModel.class);

        model.getRegisteredUser().observe(this, ite -> {
            TextView txtNome = findViewById(R.id.txtNameSurname);
            TextView txtMail = findViewById(R.id.txtMail);

            txtNome.setText(ite.getName() + " " + ite.getSurname());
            txtMail.setText(ite.getAccount());
        });

        setSupportActionBar(mainActivityBinding.appBarMain.toolbar);
        DrawerLayout drawer = mainActivityBinding.drawerLayout;
        NavigationView navigationView = mainActivityBinding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_login, R.id.nav_booking)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //mainActivityBinding.loading.setVisibility(View.VISIBLE);
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Connessione...");
        progressDialog.show();
        model.testServerConnection("0", "check_connection_server");
        model.getIsConnected().observe(this, connected -> {
            if(connected) {
                progressDialog.dismiss();
                //mainActivityBinding.loading.setVisibility(View.INVISIBLE);
                model.fetchBookedRepetitions();
            } else {
                progressDialog.show();
                //mainActivityBinding.loading.setVisibility(View.VISIBLE);
                model.testServerConnection("5000", "check_connection_server");
            }
        });

        //model.checkSession();
        //TODO: renderlo invisibile solo se non è loggato. Da fare dopo la check session
        navigationView.getMenu().findItem(R.id.nav_booking).setVisible(false);
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
}