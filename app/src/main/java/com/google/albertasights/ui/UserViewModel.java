package com.google.albertasights.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.google.albertasights.models.User;

import java.util.Map;

public class UserViewModel extends ViewModel {

    private static final String TAG = UserViewModel.class.getSimpleName();
    public final MutableLiveData<User> user = new MutableLiveData<>();
    public final MutableLiveData<String> currentAction = new MutableLiveData<>();

    public UserViewModel() {
        Log.d(TAG, "enter UserViewModel()");
        Log.d(TAG, "exit UserViewModel()");
    }

    public void updateUser (User updatedUser) {
        Log.d(TAG, "enter updateFilterMap (Map<String, Boolean> newFilter)");
        user.setValue(updatedUser);
        Log.d(TAG, "exit updateFilterMap (Map<String, Boolean> newFilter)");
    }

    public void updateAction (String newAction) {
        Log.d(TAG, "enter updateAction");
        currentAction.setValue(newAction);
        Log.d(TAG, "exit updateAction");
    }

    public LiveData<User> getUser () {

        return user;
    }

    public LiveData<String> getCurrentAction () {

        return currentAction;
    }
}