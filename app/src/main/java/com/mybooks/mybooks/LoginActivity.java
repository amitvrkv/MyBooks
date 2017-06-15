package com.mybooks.mybooks;


import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class LoginActivity extends AppCompatActivity implements OnClickListener {

    private EditText mEmailSignIn, mPasswordSignIn, mEmailSignUp, mPasswordSignUpOne, mPasswordSignUpTwo;
    private Button mEmailSignInButton, mEmailSignUpButton;
    private TextView mSignUpRed, mSignInRed;
    private View mSignInForm, mSignUpForm;

    private ProgressDialog mprogressDialog;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //startActivity(new Intent(getApplicationContext(), HomeActivity.class));

        mprogressDialog = new ProgressDialog(this);

        //Sign In form
        mEmailSignIn = (EditText) findViewById(R.id.email_sign_in);
        mPasswordSignIn = (EditText) findViewById(R.id.password_sign_in);

        mSignInForm = findViewById(R.id.email_sign_in_form);
        mSignInForm.setVisibility(View.VISIBLE);

        mSignInRed = (TextView) findViewById(R.id.sign_in_redirect_button);
        mSignInRed.setOnClickListener(this);

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(this);

        //Sign Up form
        mEmailSignUp = (EditText) findViewById(R.id.email_sign_up);
        mPasswordSignUpOne = (EditText) findViewById(R.id.password_sign_up_one);
        mPasswordSignUpTwo = (EditText) findViewById(R.id.password_sign_up_two);

        mSignUpForm = findViewById(R.id.email_sign_up_form);
        mSignUpForm.setVisibility(View.GONE);

        mSignUpRed = (TextView) findViewById(R.id.sign_up_redirect_button);
        mSignUpRed.setOnClickListener(this);

        mEmailSignUpButton = (Button) findViewById(R.id.email_sign_up_button);
        mEmailSignUpButton.setOnClickListener(this);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                } else {

                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_redirect_button:
                mSignInForm.setVisibility(View.VISIBLE);
                mSignUpForm.setVisibility(View.GONE);
                break;

            case R.id.sign_up_redirect_button:
                mSignUpForm.setVisibility(View.VISIBLE);
                mSignInForm.setVisibility(View.GONE);
                break;

            case R.id.email_sign_up_button:
                if(verifySignUp())
                    attemptSignup();
                break;

            case R.id.email_sign_in_button:
                if (verifySignin())
                    attemptSignin();
                break;
        }
    }

    private boolean verifySignin() {

        // Reset errors.
        mEmailSignIn.setError(null);
        mPasswordSignIn.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailSignIn.getText().toString();
        String password = mPasswordSignIn.getText().toString();


        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailSignIn.setError(getString(R.string.error_field_required));
            return false;

        } else if (!email.contains("@")) {
            mEmailSignIn.setError(getString(R.string.error_invalid_email));
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordSignIn.setError(getString(R.string.error_field_required));
            return false;
        }

        return true;
    }

    private void attemptSignin() {
        mprogressDialog.setTitle("Please wait.");
        mprogressDialog.setMessage("Signing in to your account...");
        mprogressDialog.show();
        String email = mEmailSignIn.getText().toString();
        String password = mPasswordSignIn.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Wrong Username or Password." , Toast.LENGTH_SHORT).show();
                        }
                        mprogressDialog.dismiss();
                    }
                });
    }
    
    private boolean verifySignUp() {
        RelativeLayout parentLayout = (RelativeLayout) findViewById(R.id.parentLayout);

        // Reset errors.
        mEmailSignUp.setError(null);
        mPasswordSignUpOne.setError(null);
        mPasswordSignUpTwo.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailSignUp.getText().toString();
        String passwordOne = mPasswordSignUpOne.getText().toString();
        String passwordTwo = mPasswordSignUpTwo.getText().toString();

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailSignUp.setError(getString(R.string.error_field_required));
            return false;

        } else if (!email.contains("@")) {
            mEmailSignUp.setError(getString(R.string.error_invalid_email));
            return false;
        }

        if (TextUtils.isEmpty(passwordOne)) {
            mPasswordSignUpOne.setError(getString(R.string.error_field_required));
            return false;
        } else if (TextUtils.isEmpty(passwordTwo)) {
            mPasswordSignUpTwo.setError(getString(R.string.error_field_required));
            return false;
        }

        if( ! passwordOne.equals(passwordTwo)) {
            Snackbar snackbar = Snackbar.make(parentLayout, "Passwords do not match.", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return false;
        }

        return true;
    }
    
    private void attemptSignup() {
        mprogressDialog.setTitle("Please wait.");
        mprogressDialog.setMessage("Creating your account...");
        mprogressDialog.show();
        String email = mEmailSignUp.getText().toString();
        String password = mPasswordSignUpOne.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
                            DatabaseReference mRef = mdatabase.getReference().child("User").child(mAuth.getCurrentUser().getUid());
                            mRef.child("name").setValue("NA");
                            mRef.child("email").setValue(mAuth.getCurrentUser().getEmail());
                            mRef.child("contact").setValue("NA");
                            mRef.child("address").setValue("NA");
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Failed to sign-up. Try again!" , Toast.LENGTH_SHORT).show();
                        }
                        mprogressDialog.dismiss();
                    }
                });
    }

}

