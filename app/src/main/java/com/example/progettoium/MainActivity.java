package com.example.progettoium;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.example.progettoium.model.coursestimetable.CoursesCustomViewAdapter;
import com.example.progettoium.model.coursestimetable.CoursesTimeTableViewModel;
import com.example.progettoium.model.coursestimetable.CoursesTimeTableViewModelFactory;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding mainActivityBinding;

    private NetworkViewModel model;
    CoursesCustomViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivityBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainActivityBinding.getRoot());

        model = new ViewModelProvider(this).get(NetworkViewModel.class);
        model.getRegisteredUser().observe(this, ite -> {
            TextView txtNome = findViewById(R.id.txtNameSurname);
            TextView txtMail = findViewById(R.id.txtMail);

            txtNome.setText(ite.getName());
            txtMail.setText(ite.getEmail());
        });

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

        //setting up recycler view to show courses
        CoursesTimeTableViewModelFactory factory = new CoursesTimeTableViewModelFactory(this);
        CoursesTimeTableViewModel viewModel = new ViewModelProvider(this, factory).get(CoursesTimeTableViewModel.class);


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.courses_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CoursesCustomViewAdapter(this, new ArrayList());
        recyclerView.setAdapter(adapter);
        viewModel.fetchCourses();

        viewModel.getCourses().observe(this, userObjects -> {
            Log.i("Home Fragment", "numero corsi: " + userObjects.size());
            if (userObjects.size() > 0) {
                Log.i("Home Fragment", "Corso: " + userObjects.get(0).getCodCourse() + " | Start: " + userObjects.get(0).getStartTime());
            }
            adapter.setData(userObjects);  // setta i dati nella recyclerView
        });
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
}