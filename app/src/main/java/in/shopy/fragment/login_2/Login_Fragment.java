package in.shopy.fragment.login_2;

/**
 * Created by am361000 on 15/01/18.
 */

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.shopy.R;
import in.shopy.Utils.CustomToast;
import in.shopy.Utils.Utils;
import in.shopy.activities.HomeActivity;
import in.shopy.app_pref.AppPref;

import static android.content.Context.MODE_PRIVATE;

public class Login_Fragment extends Fragment implements OnClickListener {
    public static final int RC_SIGN_IN = 1;
    private static View view;
    private static EditText emailid, password;
    private static Button loginButton;
    private static TextView forgotPassword, signUp;
    private static CheckBox show_hide_password;
    private static LinearLayout loginLayout;
    private static Animation shakeAnimation;
    private static FragmentManager fragmentManager;
    ///
    SignInButton btn_signInWithGoogle;
    private ProgressDialog mprogressDialog;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleSignInClient;
    ///

    public Login_Fragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(in.shopy.R.layout.login_layout, container, false);
        initViews();
        setListeners();
        return view;
    }

    // Initiate Views
    private void initViews() {
        fragmentManager = getActivity().getSupportFragmentManager();

        mprogressDialog = new ProgressDialog(getContext());
        mprogressDialog.setTitle("Please wait.");
        mprogressDialog.setMessage("Signing in to your account...");
        mprogressDialog.setCancelable(false);

        emailid = (EditText) view.findViewById(in.shopy.R.id.login_emailid);
        password = (EditText) view.findViewById(in.shopy.R.id.login_password);
        loginButton = (Button) view.findViewById(in.shopy.R.id.loginBtn);
        forgotPassword = (TextView) view.findViewById(in.shopy.R.id.forgot_password);
        signUp = (TextView) view.findViewById(in.shopy.R.id.createAccount);
        show_hide_password = (CheckBox) view
                .findViewById(in.shopy.R.id.show_hide_password);
        loginLayout = (LinearLayout) view.findViewById(in.shopy.R.id.login_layout);

        // Load ShakeAnimation
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(),
                in.shopy.R.anim.shake);

        // Setting text selector over textviews
        XmlResourceParser xrp = getResources().getXml(in.shopy.R.drawable.text_selector);
        try {
            ColorStateList csl = ColorStateList.createFromXml(getResources(),
                    xrp);

            forgotPassword.setTextColor(csl);
            show_hide_password.setTextColor(csl);
            signUp.setTextColor(csl);
        } catch (Exception e) {
        }

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            mprogressDialog.show();
            //mAuth.removeAuthStateListener(mAuthListener);
            checkIfEmailVerified();
        }

        /////
        btn_signInWithGoogle = (SignInButton) view.findViewById(R.id.btn_signInWithGoogle);
        /////
        checkIfUserLoginAgain();

    }

    // Set Listeners
    private void setListeners() {
        loginButton.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        signUp.setOnClickListener(this);
        btn_signInWithGoogle.setOnClickListener(this);

        // Set check listener over checkbox for showing and hiding password
        show_hide_password
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton button,
                                                 boolean isChecked) {

                        // If it is checkec then show password else hide
                        // password
                        if (isChecked) {

                            show_hide_password.setText(in.shopy.R.string.hide_pwd);// change
                            // checkbox
                            // text

                            password.setInputType(InputType.TYPE_CLASS_TEXT);
                            password.setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());// show password
                        } else {
                            show_hide_password.setText(in.shopy.R.string.show_pwd);// change
                            // checkbox
                            // text

                            password.setInputType(InputType.TYPE_CLASS_TEXT
                                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            password.setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());// hide password

                        }

                    }
                });


    }

    private void checkIfUserLoginAgain() {
        SharedPreferences sharedPreferences;
        sharedPreferences = getContext().getSharedPreferences(getString(in.shopy.R.string.sharedPrefDeliveryAddress), MODE_PRIVATE);
        if (sharedPreferences.getString("email", null) == null) {
            emailid.setText("");
        } else {
            emailid.setText(sharedPreferences.getString("email", null));
        }
    }

    @Override
    public void onClick(View v) {

        if (mGoogleSignInClient != null && mGoogleSignInClient.isConnected()) {
            mGoogleSignInClient.stopAutoManage((getActivity()));
            mGoogleSignInClient.disconnect();
        }

        switch (v.getId()) {
            case in.shopy.R.id.loginBtn:
                checkValidation();
                break;

            case in.shopy.R.id.forgot_password:

                // Replace forgot password fragment with animation
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(in.shopy.R.anim.right_enter, in.shopy.R.anim.left_out)
                        .replace(in.shopy.R.id.frameContainer,
                                new ForgotPassword_Fragment(),
                                Utils.ForgotPassword_Fragment).commit();
                break;

            case in.shopy.R.id.createAccount:
                // Replace signup frgament with animation
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(in.shopy.R.anim.right_enter, in.shopy.R.anim.left_out)
                        .replace(in.shopy.R.id.frameContainer, new SignUp_Fragment(),
                                Utils.SignUp_Fragment).commit();
                break;

            case R.id.btn_signInWithGoogle:
                signInWithGoogle();
                break;
        }

    }

    private void signInWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(), new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getContext(), "onConnectionFailed", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        //Toast.makeText(getContext(), "signInWithGoogle", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = result.getSignInAccount();
            if (account != null)
                firebaseAuthWithGoogle(account);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        mprogressDialog.setMessage("Signing in using " + account.getEmail());
        mprogressDialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //Toast.makeText(getContext(), ">>" + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                    mGoogleSignInClient.clearDefaultAccountAndReconnect();
                    startActivity(new Intent(getActivity(), HomeActivity.class));
                    mprogressDialog.dismiss();
                }
            }
        });
    }


    // Check Validation before login
    private void checkValidation() {
        // Get email id and password
        String getEmailId = emailid.getText().toString();
        String getPassword = password.getText().toString();

        // Check patter for email id
        Pattern p = Pattern.compile(Utils.regEx);

        Matcher m = p.matcher(getEmailId);

        // Check for both field is empty or not
        if (getEmailId.equals("") || getEmailId.length() == 0 || getPassword.equals("") || getPassword.length() == 0) {
            loginLayout.startAnimation(shakeAnimation);
            new CustomToast().Show_Toast(getActivity(), view,
                    "Enter both credentials.");

        }
        // Check if email id is valid or not
        else if (!m.find())
            new CustomToast().Show_Toast(getActivity(), view,
                    "Your Email ID is Invalid.");
            // Else do login and do your stuff
        else {
            //Toast.makeText(getActivity(), "Do Login.", Toast.LENGTH_SHORT).show();
            attempSignIn(getEmailId, getPassword);
        }

    }

    private void attempSignIn(String email, String password) {
        mprogressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            checkIfEmailVerified();
                        } else {
                            //Failed login
                            mprogressDialog.dismiss();
                            AppPref.showAlertDialog(getContext(), "Error", "Wrong credentials. Try again!!!");
                        }
                    }
                });
    }

    private void checkIfEmailVerified() {
        //mAuth.removeAuthStateListener(mAuthListener);

        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            if (firebaseUser.isEmailVerified()) {
                loadHomepage();
            } else {

                mprogressDialog.dismiss();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setTitle("Alert");
                alertDialogBuilder.setMessage("Please verify your Email ID.");
                alertDialogBuilder.setPositiveButton("VERIFY NOW",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                firebaseUser.sendEmailVerification();
                                mAuth.signOut();

                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                                startActivity(intent);
                            }
                        });
                alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }
    }

    public void loadHomepage() {
        //mAuth.removeAuthStateListener(mAuthListener);
        startActivity(new Intent(getActivity(), HomeActivity.class));
        mprogressDialog.dismiss();
        getActivity().finish();
        /*
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "*"));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String live = String.valueOf(dataSnapshot.child("liveness").getValue());
                if (live.equalsIgnoreCase("true")) {

                    databaseReference.removeEventListener(this);

                    startActivity(new Intent(getActivity(), HomeActivity.class));

                    mprogressDialog.dismiss();
                    getActivity().finish();
                } else {
                    //Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG).show();
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
        */
    }
}
