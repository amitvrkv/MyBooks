package com.mybooks.mybooks.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.mybooks.mybooks.BuildConfig;
import com.mybooks.mybooks.R;
import com.mybooks.mybooks.app_pref.MyFormat;

import java.util.HashMap;
import java.util.Map;

public class About extends AppCompatActivity {


    private Intent intent;

    Object createdTimestamp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setToolbar();

        TextView textView = (TextView) findViewById(R.id.version);
        textView.setText("Version: " + BuildConfig.VERSION_NAME);
    }

    public void setToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("About");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void startTransaction(View view){
        Toast.makeText(getApplicationContext(), "startTransaction", Toast.LENGTH_SHORT).show();
    }

}

