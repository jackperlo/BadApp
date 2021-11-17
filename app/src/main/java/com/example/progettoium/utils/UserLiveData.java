package com.example.progettoium.utils;

import android.util.Pair;

import com.example.progettoium.data.User;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;

/*
* Classe di gestione Live Data. Funge da shared memory tra i vari Fragments/Activity e
* da cahce nell'interfacciamento con le risorse richieste in rete.*/
public class UserLiveData extends MutableLiveData<Pair<User, String >> {
    public void updateUser(Pair<User, String > result) {
        postValue(result); // Notifica tutti i listener
    }
}
