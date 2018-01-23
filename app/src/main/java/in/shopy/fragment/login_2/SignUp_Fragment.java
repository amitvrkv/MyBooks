package in.shopy.fragment.login_2;

/**
 * Created by am361000 on 15/01/18.
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import in.shopy.Utils.CustomToast;
import in.shopy.Utils.Utils;
import in.shopy.app_pref.AppPref;

import static android.content.Context.MODE_PRIVATE;

public class SignUp_Fragment extends Fragment implements OnClickListener {
    private static View view;
    private static EditText fullName, emailId, mobileNumber, location,
            password, confirmPassword;
    private static TextView login;
    private static Button signUpButton;
    private static CheckBox terms_conditions;

    private LinearLayout signup_layout;
    private static Animation shakeAnimation;

    private FirebaseAuth mAuth;
    private ProgressDialog mprogressDialog;

    private static FragmentManager fragmentManager;

    SharedPreferences sharedPreferences;

    public SignUp_Fragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(in.shopy.R.layout.signup_layout, container, false);

        fragmentManager = getFragmentManager();

        initViews();
        setListeners();
        return view;
    }

    // Initialize all views
    private void initViews() {
        mAuth = FirebaseAuth.getInstance();
        mprogressDialog = new ProgressDialog(getContext());
        mprogressDialog.setCancelable(false);

        signup_layout = (LinearLayout) view.findViewById(in.shopy.R.id.signup_layout);
        // Load ShakeAnimation
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(),
                in.shopy.R.anim.shake);

        fullName = (EditText) view.findViewById(in.shopy.R.id.fullName);
        emailId = (EditText) view.findViewById(in.shopy.R.id.userEmailId);
        mobileNumber = (EditText) view.findViewById(in.shopy.R.id.mobileNumber);
        location = (EditText) view.findViewById(in.shopy.R.id.location);
        password = (EditText) view.findViewById(in.shopy.R.id.password);
        confirmPassword = (EditText) view.findViewById(in.shopy.R.id.confirmPassword);
        signUpButton = (Button) view.findViewById(in.shopy.R.id.signUpBtn);
        login = (TextView) view.findViewById(in.shopy.R.id.already_user);
        terms_conditions = (CheckBox) view.findViewById(in.shopy.R.id.terms_conditions);

        // Setting text selector over textviews
        //XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
        XmlResourceParser xrp = getResources().getXml(in.shopy.R.drawable.text_selector);

        try {
            ColorStateList csl = ColorStateList.createFromXml(getResources(),
                    xrp);

            login.setTextColor(csl);
            terms_conditions.setTextColor(csl);
        } catch (Exception e) {
        }
    }

    // Set Listeners
    private void setListeners() {
        signUpButton.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case in.shopy.R.id.signUpBtn:

                // Call checkValidation method
                checkValidation();
                break;

            case in.shopy.R.id.already_user:

                // Replace login fragment
                //new MainActivity().replaceLoginFragment();
                replaceLoginFragment();
                break;
        }

    }

    // Check Validation Method
    private void checkValidation() {

        // Get all edittext texts
        String getFullName = fullName.getText().toString();
        String getEmailId = emailId.getText().toString();
        String getMobileNumber = mobileNumber.getText().toString();
        String getLocation = location.getText().toString();
        String getPassword = password.getText().toString();
        String getConfirmPassword = confirmPassword.getText().toString();

        // Pattern match for email id
        Pattern p = Pattern.compile(Utils.regEx);
        Matcher m = p.matcher(getEmailId);

        // Check if all strings are null or not
        if (getFullName.equals("") || getFullName.length() == 0
                || getEmailId.equals("") || getEmailId.length() == 0
                || getMobileNumber.equals("") || getMobileNumber.length() < 10
                || getLocation.equals("") || getLocation.length() == 0
                || getPassword.equals("") || getPassword.length() == 0
                || getConfirmPassword.equals("")
                || getConfirmPassword.length() == 0)
            new CustomToast().Show_Toast(getActivity(), view,
                    "All fields are required.");

            // Check if email id valid or not
        else if (!m.find())
            new CustomToast().Show_Toast(getActivity(), view,
                    "Your Email Id is Invalid.");

            // Check if both password should be equal
        else if (!getConfirmPassword.equals(getPassword))
            new CustomToast().Show_Toast(getActivity(), view,
                    "Both password doesn't match.");

            // Make sure user should check Terms and Conditions checkbox
        else if (!terms_conditions.isChecked())
            new CustomToast().Show_Toast(getActivity(), view,
                    "Please accept Terms and Conditions.");

            // Else do signup or do your stuff
        else {
            //Toast.makeText(getActivity(), "Do SignUp.", Toast.LENGTH_SHORT).show();
            attemptSignup(getEmailId, getPassword, getFullName, getMobileNumber);
        }
    }

    private void attemptSignup(final String email, String password, final String name, final String mobile) {
        mprogressDialog.setTitle("Please wait.");
        mprogressDialog.setMessage("Creating your account...");
        mprogressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
                            DatabaseReference mRef = mdatabase.getReference().child("User").child(email.replace(".", "*"));
                            mRef.child("wallet").setValue("0");
                            mRef.child("liveness").setValue("true");

                            mRef.child("address").child("name").setValue(name);
                            mRef.child("address").child("email").setValue(email);
                            mRef.child("address").child("contact").setValue(mobile);
                            mRef.child("address").child("addressline1").setValue("null");
                            mRef.child("address").child("addressline2").setValue("null");
                            mRef.child("address").child("city").setValue("Bengaluru");
                            mRef.child("address").child("state").setValue("Karnataka");
                            mRef.child("address").child("pincode").setValue("null");
                            mRef.child("address").child("isVerified").setValue("false");

                            sharedPreferences = getContext().getSharedPreferences(getString(in.shopy.R.string.sharedPrefDeliveryAddress), MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("Name", name);
                            editor.putString("contact", mobile);
                            editor.putString("email", email);
                            editor.putString("addressline1", "null");
                            editor.putString("addressline2", "null");
                            editor.putString("city", "Bengaluru");
                            editor.putString("state", "Karnataka");
                            editor.putString("pincode", "null");
                            editor.putString("isVerified","false");
                            editor.commit();

                            mAuth.getCurrentUser().sendEmailVerification();

                            mAuth.signOut();

                            mprogressDialog.dismiss();

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                            alertDialogBuilder.setTitle("Alert");
                            alertDialogBuilder.setMessage("Successfully signed up.\nPlease verify your Email ID.");
                            alertDialogBuilder.setPositiveButton("VERIFY NOW",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            Intent intent = new Intent(Intent.ACTION_MAIN);
                                            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                                            startActivity(intent);
                                            replaceLoginFragment();
                                        }
                                    });
                            alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    replaceLoginFragment();
                                    dialog.cancel();
                                }
                            });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();

                        } else {
                            mprogressDialog.dismiss();
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                AppPref.showAlertDialog(getContext(), "Error", "User with this email already exist.");
                                //Toast.makeText(getContext(), "User with this email already exist.", Toast.LENGTH_SHORT).show();
                            } else {
                                AppPref.showAlertDialog(getContext(), "Error", "Failed to sign-up. Try again!");
                            }
                        }
                    }
                });
    }

    protected void replaceLoginFragment() {
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(in.shopy.R.anim.left_enter, in.shopy.R.anim.right_out)
                .replace(in.shopy.R.id.frameContainer, new Login_Fragment(),
                        Utils.Login_Fragment).commit();
    }
}