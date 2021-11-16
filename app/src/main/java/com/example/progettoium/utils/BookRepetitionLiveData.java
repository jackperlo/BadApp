package com.example.progettoium.utils;

import androidx.lifecycle.MutableLiveData;

public class BookRepetitionLiveData extends MutableLiveData<String> {
    public void updateBookRepetition(String result) {
        postValue(result); // Notifica tutti i listener
    }
}
