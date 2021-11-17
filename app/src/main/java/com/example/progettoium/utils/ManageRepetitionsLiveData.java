package com.example.progettoium.utils;

import androidx.lifecycle.MutableLiveData;

import com.example.progettoium.data.FreeRepetitions;

import java.util.List;

public class ManageRepetitionsLiveData extends MutableLiveData<Boolean> {
    public void updateRepetitions(Boolean managed) {
        postValue(managed);
    }
}
