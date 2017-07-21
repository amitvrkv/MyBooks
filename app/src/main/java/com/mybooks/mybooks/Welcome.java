package com.mybooks.mybooks;


import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Welcome extends AppCompatActivity {

    ImageView mAppLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mAppLogo = (ImageView) findViewById(R.id.app_logo);

    }

    @Override
    protected void onStart() {
        super.onStart();
        animateLogo();
    }


    private int getDisplayHeight() {
        return this.getResources().getDisplayMetrics().heightPixels;
    }

    public void animateLogo() {
        TranslateAnimation transAnim = new TranslateAnimation(0, 0, 0,
                getDisplayHeight() / 2 - 100);
        transAnim.setStartOffset(500);
        transAnim.setDuration(2000);
        transAnim.setFillAfter(true);
        transAnim.setInterpolator(new BounceInterpolator());
        transAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
        mAppLogo.startAnimation(transAnim);
    }
}
