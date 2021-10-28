package com.example.progettoium.utils;

import com.example.progettoium.data.Users;

import androidx.lifecycle.LiveData;

/*
* Classe di gestione Live Data. Funge da shared memory tra i vari Fragments/Activity e
* da cahce nell'interfacciamento con le risorse richieste in rete.*/
public class NetworkLiveData extends LiveData<Users> {
    public void updateUser(Users result) {
        postValue(result); // Notifica tutti i listener
    }
}
