package com.mybooks.mybooks.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mybooks.mybooks.R;
import com.mybooks.mybooks.Utils.Utils;

/**
 * Created by am361000 on 15/01/18.
 */

public class SplashScreen extends Fragment {

    private static View view;
    private static FragmentManager fragmentManager;

    public SplashScreen() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.splash_screen, container, false);

        initViews();

        return view;
    }

    private void initViews() {
        fragmentManager = getActivity().getSupportFragmentManager();

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer,
                                new Login_Fragment(),
                                Utils.Login_Fragment).commit();
            }
        };
        handler.postDelayed(runnable, 2000);
    }
}