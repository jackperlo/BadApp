package com.example.progettoium.utils;

import com.example.progettoium.data.User;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/*
* Classe di gestione Live Data. Funge da shared memory tra i vari Fragments/Activity e
* da cahce nell'interfacciamento con le risorse richieste in rete.*/
public class UserLiveData extends MutableLiveData<User> {
    public void updateUser(User result) {
        postValue(result); // Notifica tutti i listener
    }
}
