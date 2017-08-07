package com.mybooks.mybooks;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    private TextView mSignUpRed, mSignInRed, mresetPassword;
    private View mSignInForm, mSignUpForm;

    private ProgressDialog mprogressDialog;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    View parentLayoutView;

    ImageView mAppLogo;
    RelativeLayout relativeLayoutLogin, relativeLayoutWelcome;

    boolean user_logged_in = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        parentLayoutView = findViewById(R.id.parentLayout);

        mprogressDialog = new ProgressDialog(this);

        //Sign In form
        mEmailSignIn = (EditText) findViewById(R.id.email_sign_in);
        mPasswordSignIn = (EditText) findViewById(R.id.password_sign_in);

        mSignInForm = findViewById(R.id.email_sign_in_form);
        mSignInForm.setVisibility(View.VISIBLE);
        //Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_up);

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

        mresetPassword = (TextView) findViewById(R.id.forgotPassword);
        mresetPassword.setOnClickListener(this);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //checkIfEmailVerified();
                    user_logged_in = true;
                }
            }
        };

        mAppLogo = (ImageView) findViewById(R.id.app_logo);
        relativeLayoutLogin = (RelativeLayout) findViewById(R.id.Login);
        relativeLayoutLogin.setVisibility(View.GONE);
        relativeLayoutWelcome = (RelativeLayout) findViewById(R.id.Welcome);
        //relativeLayoutWelcome.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        //animateLogo();

        //
        /*ImageView imageView = (ImageView) findViewById(R.id.app_logo);
        Animation animation1 =
                AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.fade);
        imageView.startAnimation(animation1);
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (user_logged_in)
                    checkIfEmailVerified();
                else {
                    relativeLayoutLogin.setVisibility(View.VISIBLE);
                    relativeLayoutWelcome.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });*/


        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.main_welcome_page);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user_logged_in)
                    checkIfEmailVerified();
                else {
                    relativeLayoutLogin.setVisibility(View.VISIBLE);
                    relativeLayout.setVisibility(View.GONE);
                }
            }
        }, 1500);

        //
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
                if (verifySignUp())
                    attemptSignup();
                break;

            case R.id.email_sign_in_button:
                if (verifySignin())
                    attemptSignin();
                break;

            case R.id.forgotPassword:
                sendResetPassword();
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
                        if (task.isSuccessful()) {

                        } else {
                            Snackbar.make(parentLayoutView, "Wrong Username or Password.", Snackbar.LENGTH_LONG).show();
                        }
                        mprogressDialog.dismiss();
                        checkIfEmailVerified();
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

        if (!passwordOne.equals(passwordTwo)) {
            Snackbar.make(parentLayout, "Passwords do not match.", Snackbar.LENGTH_SHORT).show();
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
                        if (task.isSuccessful()) {

                            FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
                            DatabaseReference mRef = mdatabase.getReference().child("User").child(mEmailSignUp.getText().toString().replace(".", "*"));
                            mRef.child("name").setValue("NA");
                            mRef.child("email").setValue(mEmailSignUp.getText().toString());
                            mRef.child("contact").setValue("NA");
                            mRef.child("address").setValue("NA");

                            mSignInForm.setVisibility(View.VISIBLE);
                            mSignUpForm.setVisibility(View.GONE);

                        } else {
                            Snackbar.make(parentLayoutView, "Failed to sign-up. Try again!", Snackbar.LENGTH_LONG).show();
                        }
                        mprogressDialog.dismiss();
                    }
                });
    }

    private void checkIfEmailVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.isEmailVerified()) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        } else {
            sendVerificationEmailToUser();
            Snackbar.make(parentLayoutView, "You have successfully signed up.\\nPlease verify your email address.", Snackbar.LENGTH_LONG).show();
        }
    }

    public void sendVerificationEmailToUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.sendEmailVerification();
        FirebaseAuth.getInstance().signOut();
        relativeLayoutLogin.setVisibility(View.VISIBLE);
        relativeLayoutWelcome.setVisibility(View.GONE);
    }

    public void sendResetPassword() {
        mEmailSignIn.setError(null);
        String email = mEmailSignIn.getText().toString();

        if (TextUtils.isEmpty(email)) {
            mEmailSignIn.setError(getString(R.string.error_field_required));
            return;
        } else if (!email.contains("@")) {
            mEmailSignIn.setError(getString(R.string.error_invalid_email));
            return;
        }
        FirebaseAuth.getInstance().sendPasswordResetEmail(email);
        Toast.makeText(getApplicationContext(), "Password reset email sent successfully.", Toast.LENGTH_SHORT).show();
        //Snackbar.make(parentLayoutView, "Password reset email sent successfully.", Snackbar.LENGTH_LONG).show();
    }

    private int getDisplayHeight() {
        return this.getResources().getDisplayMetrics().heightPixels;
    }

    public void animateLogo() {
        TranslateAnimation transAnim = new TranslateAnimation(0, 0, 0,
                getDisplayHeight() / 2 - 100);
        transAnim.setStartOffset(500);
        transAnim.setDuration(2500);
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
                //startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                //finish();
                if (user_logged_in)
                    checkIfEmailVerified();
                else {
                    relativeLayoutLogin.setVisibility(View.VISIBLE);
                    relativeLayoutWelcome.setVisibility(View.GONE);
                }
            }
        });
        mAppLogo.startAnimation(transAnim);
    }
}

