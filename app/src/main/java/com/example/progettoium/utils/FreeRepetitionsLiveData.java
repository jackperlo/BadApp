package com.example.progettoium.utils;

import com.example.progettoium.data.FreeRepetitions;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class FreeRepetitionsLiveData extends MutableLiveData<List<FreeRepetitions>> {
    public void updateFreeRepetitions(List<FreeRepetitions> freeRepetitions) {
         postValue(freeRepetitions);
    }
}