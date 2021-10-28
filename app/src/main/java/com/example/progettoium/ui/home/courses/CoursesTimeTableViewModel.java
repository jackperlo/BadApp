package com.example.progettoium.ui.home.courses;

import android.content.Context;

import com.example.progettoium.data.CoursesTimeTable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class CoursesTimeTableViewModel extends ViewModel {

    private CoursesTimeTableRepository repository;
    private MutableLiveData<List<CoursesTimeTable>> courses;

    public CoursesTimeTableViewModel(Context context) {
        repository = new CoursesTimeTableRepository(context);
        courses = new MutableLiveData<>();
    }

    public void fetchCourses(){
        courses.postValue(repository.fetchCourses());
    }

    public LiveData<List<CoursesTimeTable>> getCourses() {
        return courses;
    }
}