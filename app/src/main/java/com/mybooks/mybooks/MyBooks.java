package com.mybooks.mybooks;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by am361000 on 13/06/17.
 */

public class MyBooks extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        //Picasso.Builder builder = new Picasso.Builder(this);
        //builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        //Picasso built = builder.build();
        //built.setIndicatorsEnabled(false);
        //built.setLoggingEnabled(true);
        //Picasso.setSingletonInstance(built);

    }
}
