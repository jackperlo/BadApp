package com.example.progettoium;

import android.util.Log;

import com.example.progettoium.model.Users;

import androidx.lifecycle.LiveData;

/*
* Classe di gestione Live Data. Funge da shared memory tra i vari Fragments/Activity e
* da cahce nell'interfacciamento con le risorse richieste in rete.*/
public class NetworkLiveData extends LiveData<Users> {
    public void update(Users result) {
        Log.d("LIVEDATA", "updating");
        postValue(result); // Notifica tutti i listener
    }
}
