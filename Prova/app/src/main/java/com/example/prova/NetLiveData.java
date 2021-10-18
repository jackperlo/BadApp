package com.example.prova;

import android.util.Log;

import androidx.lifecycle.LiveData;

public class NetLiveData extends LiveData<User> {
    public void aggiorna(User result) {
        Log.d("LIVEDATA", "aggiorno");
        postValue(result); // Notifica tutti i listener
    }
}
