package in.shopy.fragment.login_2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.shopy.Utils.Utils;
import in.shopy.activities.HomeActivity;
import in.shopy.app_pref.AppPref;

/**
 * Created by am361000 on 15/01/18.
 */

public class SplashScreen extends Fragment {

    private static View view;
    private static FragmentManager fragmentManager;

    private ProgressDialog mprogressDialog;
    private FirebaseAuth mAuth;
    //private FirebaseAuth.AuthStateListener mAuthListener;

    public SplashScreen() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(in.shopy.R.layout.splash_screen, container, false);

        initViews();

        return view;
    }

    private void initViews() {
        fragmentManager = getActivity().getSupportFragmentManager();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if (user.isEmailVerified()) {

                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        loadHomepage();
                    }
                };
                handler.postDelayed(runnable, 1500);

            } else {
                loadLoginFragment();
            }
        } else {
            loadLoginFragment();
        }

        /*
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if (user.isEmailVerified()) {

                        mAuth.removeAuthStateListener(mAuthListener);

                        Handler handler = new Handler();
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                loadHomepage();
                            }
                        };
                        handler.postDelayed(runnable, 2000);
                    } else {

                        mAuth.removeAuthStateListener(mAuthListener);
                        loadLoginFragment();
                    }
                } else {
                    mAuth.removeAuthStateListener(mAuthListener);
                    loadLoginFragment();
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
        */

    }

    public void loadLoginFragment() {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(in.shopy.R.anim.right_enter, in.shopy.R.anim.left_out)
                        .replace(in.shopy.R.id.frameContainer,
                                new Login_Fragment(),
                                Utils.Login_Fragment).commit();
            }
        };
        handler.postDelayed(runnable, 2000);
    }

    public void loadHomepage() {
        mprogressDialog = new ProgressDialog(getContext());
        mprogressDialog.setTitle("Please wait.");
        mprogressDialog.setMessage("Verifying your account...");
        mprogressDialog.setCancelable(false);
        //mprogressDialog.show();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "*"));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String live = String.valueOf(dataSnapshot.child("liveness").getValue());
                if (live.equalsIgnoreCase("true")) {

                    //mAuth.removeAuthStateListener(mAuthListener);

                    databaseReference.removeEventListener(this);

                    startActivity(new Intent(getActivity(), HomeActivity.class));

                    mprogressDialog.dismiss();
                    getActivity().finish();
                } else {
                    //Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG).show();
                    //mAuth.removeAuthStateListener(mAuthListener);
                    mAuth.signOut();
                    mprogressDialog.dismiss();
                    AppPref.showAlertDialog(getContext(), "Account Locked", "Your account is locked. Please contact our helpline.");

                    databaseReference.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}