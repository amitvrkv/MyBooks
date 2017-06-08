package com.mybooks.mybooks;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;



public class LoginActivity extends AppCompatActivity implements OnClickListener {



    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private Button mEmailSignInButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.email_sign_in_button :
                if(verifyLogin())
                    attemptLogin();
                break;
        }
    }


    private boolean verifyLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();


        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            return false;

        } else if (! email.contains("@")) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            return false;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            return false;
        }

        return true;
    }

    private void attemptLogin() {

    }


}

