package com.example.progettoium.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;

import com.example.progettoium.R;
import com.example.progettoium.utils.NetworkViewModel;

public class SplashScreen extends AppCompatActivity {

    private NetworkViewModel networkViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        setContentView(R.layout.activity_splash_screen);

        getWindow().setExitTransition(new Fade(Fade.MODE_OUT));

        networkViewModel = new ViewModelProvider(this).get(NetworkViewModel.class);
    }

    @Override
    protected void onStart() {
        networkViewModel.testServerConnection();

        //TODO: non viene visualizzato lo splash screen
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this).toBundle());
        finishAfterTransition();

        super.onStart();
    }
}