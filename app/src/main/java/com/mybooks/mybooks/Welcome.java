package com.mybooks.mybooks;


import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Welcome extends AppCompatActivity {

    ImageView mAppLogo;

FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mAppLogo = (ImageView) findViewById(R.id.app_logo);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        mAppLogo.startAnimation(animation);

        /*final Handler mHandler = new Handler();
        Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
            }
        };

        mHandler.postDelayed(mRunnable, 2000);*/

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                //finish();
            }
        }, 3000);
    }
}
