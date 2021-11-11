package com.example.progettoium.utils;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.progettoium.data.User;

/*
* Classe di gestione Live Data. Funge da shared memory tra i vari Fragments/Activity e
* da cahce nell'interfacciamento con le risorse richieste in rete.*/
public class ConnectionLiveData extends MutableLiveData<Boolean> {
    public void updateConnection(Boolean result) {
        postValue(result); // Notifica tutti i listener
    }
}
