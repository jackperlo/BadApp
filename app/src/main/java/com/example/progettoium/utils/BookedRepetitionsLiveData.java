package com.example.progettoium.utils;

import com.example.progettoium.data.BookedRepetitions;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class BookedRepetitionsLiveData extends MutableLiveData<List<BookedRepetitions>> {
    public void updateBookedRepetitions(List<BookedRepetitions> bookedRepetitions) {
        postValue(bookedRepetitions);
    }
}