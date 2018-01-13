package com.mybooks.mybooks.app_pref;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mybooks.mybooks.R;
import com.mybooks.mybooks.activities.AddressActivity;

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
