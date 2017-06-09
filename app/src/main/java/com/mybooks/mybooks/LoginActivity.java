package com.mybooks.mybooks;


import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity implements OnClickListener {

    private EditText mEmailSignIn, mPasswordSignIn, mEmailSignUp, mPasswordSignUpOne, mPasswordSignUpTwo;
    private Button mEmailSignInButton, mEmailSignUpButton;
    private TextView mSignUpRed, mSignInRed;
    private View mSignInForm, mSignUpForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        startActivity(new Intent(getApplicationContext(), HomeActivity.class));


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
        Toast.makeText(getApplicationContext(), "Sign In successful.", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
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
        Toast.makeText(getApplicationContext(), "Sign Up successful.", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

}

