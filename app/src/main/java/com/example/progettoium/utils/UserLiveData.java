package com.example.progettoium.utils;

import android.util.Pair;
import com.example.progettoium.data.User;
import androidx.lifecycle.MutableLiveData;


public class UserLiveData extends MutableLiveData<Pair<User, String >> {
    public void updateUser(Pair<User, String > result) {
        postValue(result);
    }
}
