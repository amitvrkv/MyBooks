package com.mybooks.mybooks;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by am361000 on 13/06/17.
 */

public class
MyBooks extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
