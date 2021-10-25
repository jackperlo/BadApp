package com.example.progettoium.model.coursestimetable;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class CoursesTimeTableViewModelFactory implements ViewModelProvider.Factory {

    private Context context;

    // qui memorizzo temporaneamnete il parametro context, che poi passo
    // al ViewModel in un secondo momento, alla sua creazione.
    // questo perche' non posso avere passare args al viewmodel direttamente'
// da SO:
    //  So if I have a ViewModel with multiple arguments, then I need
    //  to use a Factory that I can pass to ViewModelProviders to use
    //  when an instance of MyViewModel is required.

    public CoursesTimeTableViewModelFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CoursesTimeTableViewModel.class)) {
            return (T) new CoursesTimeTableViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
