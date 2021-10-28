package com.example.progettoium.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.example.progettoium.NetworkViewModel;
import com.example.progettoium.R;
import com.example.progettoium.ui.home.courses.CoursesCustomViewAdapter;
import com.example.progettoium.ui.home.courses.CoursesTimeTableViewModel;
import com.example.progettoium.ui.home.courses.CoursesTimeTableViewModelFactory;
import com.google.android.material.navigation.NavigationView;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.progettoium.databinding.ActivityMainBinding;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

            txtNome.setText(ite.getName());
            txtMail.setText(ite.getEmail());
        });

        model.checkSession();

        setSupportActionBar(mainActivityBinding.appBarMain.toolbar);
        DrawerLayout drawer = mainActivityBinding.drawerLayout;
        NavigationView navigationView = mainActivityBinding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_login)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
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