package com.mybooks.mybooks.activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mybooks.mybooks.R;

public class AddressActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mDelName, mDelMobileNo, mDelHouseNameNumber, mDelLocality, mDelPincode;
    private Button mDelSaveBtn;

    String name;
    String contact;
    String email;
    String addressline1;
    String addressline2;
    String city;
    String state;
    String pincode;

    String isVerified;

    String updated_contact;

    SharedPreferences sharedPreferences;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        setToolbar();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCancelable(false);

        mDelName = (TextView) findViewById(R.id.delName);
        mDelMobileNo = (TextView) findViewById(R.id.delmobileNumber);
        mDelHouseNameNumber = (TextView) findViewById(R.id.delHouseNameNumber);
        mDelLocality = (TextView) findViewById(R.id.delLocality);
        mDelPincode = (TextView) findViewById(R.id.delPincode);
        mDelSaveBtn = (Button) findViewById(R.id.delSaveBtn);
        mDelSaveBtn.setOnClickListener(this);

        sharedPreferences = getSharedPreferences(getString(R.string.sharedPrefDeliveryAddress), MODE_PRIVATE);

        getAddressFromServer();
    }

    @Override
    public void onClick(View v) {
        mDelName.setError(null);
        mDelMobileNo.setError(null);
        mDelHouseNameNumber.setError(null);
        mDelLocality.setError(null);
        mDelPincode.setError(null);

        if (v.getId() == mDelSaveBtn.getId()) {
            validateDate();
        }
    }

    public void setToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Address");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setField() {
        if ( sharedPreferences.getString("Name", null) == null) {
            mDelName.setText("");
        } else {
            mDelName.setText(sharedPreferences.getString("Name", null));
        }

        if ( sharedPreferences.getString("contact", null) == null) {
            mDelMobileNo.setText("");
            contact = "";
        } else {
            contact = sharedPreferences.getString("contact",null);
            mDelMobileNo.setText(contact);
        }

        if ( sharedPreferences.getString("isVerified", null) == null) {
            isVerified = "false";
        } else {
            isVerified = sharedPreferences.getString("isVerified", null);
        }

        if ( sharedPreferences.getString("addressline1", null) == null || sharedPreferences.getString("addressline1", null).equalsIgnoreCase("null")) {
            mDelHouseNameNumber.setText("");
        } else {
            mDelHouseNameNumber.setText(sharedPreferences.getString("addressline1", null));
        }

        if ( sharedPreferences.getString("addressline2", null) == null || sharedPreferences.getString("addressline2", null).equalsIgnoreCase("null")) {
            mDelLocality.setText("");
        } else {
            mDelLocality.setText(sharedPreferences.getString("addressline2", null));
        }

        if ( sharedPreferences.getString("pincode", null) == null || sharedPreferences.getString("pincode", null).equalsIgnoreCase("null")) {
            mDelPincode.setText("");
        } else {
            mDelPincode.setText(sharedPreferences.getString("pincode", null));
        }
    }

    public void getAddressFromServer() {
        progressDialog.setMessage("Loading your data...");
        progressDialog.show();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".","*")).child("address");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = String.valueOf(dataSnapshot.child("name").getValue());
                updated_contact = String.valueOf(dataSnapshot.child("contact").getValue());
                email = String.valueOf(dataSnapshot.child("email").getValue());
                addressline1 = String.valueOf(dataSnapshot.child("addressline1").getValue());
                addressline2 = String.valueOf(dataSnapshot.child("addressline2").getValue());
                isVerified = String.valueOf(dataSnapshot.child("isVerified").getValue());
                city = String.valueOf(dataSnapshot.child("city").getValue());
                state = String.valueOf(dataSnapshot.child("state").getValue());
                pincode = String.valueOf(dataSnapshot.child("pincode").getValue());

                updateOnSharedPref();
                setField();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void validateDate() {
        name = mDelName.getText().toString().trim();

        updated_contact = mDelMobileNo.getText().toString().trim();

        addressline1 = mDelHouseNameNumber.getText().toString().trim();
        addressline2 = mDelLocality.getText().toString().trim();
        city = "Bengaluru";
        state = "Karnataka";
        pincode = mDelPincode.getText().toString().trim();

        if (TextUtils.isEmpty(mDelName.getText().toString())) {
            mDelName.setError(getString(R.string.error_field_required));
            return;
        }

        if (TextUtils.isEmpty(mDelMobileNo.getText().toString())) {
            mDelMobileNo.setError(getString(R.string.error_field_required));
            return;
        } else if (! mDelMobileNo.getText().toString().matches("[0-9]+")) {
            mDelMobileNo.setError("Please enter only numbers");
            return;
        } else if (mDelMobileNo.getText().toString().length() != 10) {
            mDelMobileNo.setError("Please enter 10 digits");
            return;
        }

        if (TextUtils.isEmpty(mDelHouseNameNumber.getText().toString())) {
            mDelHouseNameNumber.setError(getString(R.string.error_field_required));
            return;
        }

        if (TextUtils.isEmpty(mDelLocality.getText().toString())) {
            mDelLocality.setError(getString(R.string.error_field_required));
            return;
        }

        if (TextUtils.isEmpty(mDelPincode.getText().toString())) {
            mDelPincode.setError(getString(R.string.error_field_required));
            return;
        } else if (! mDelPincode.getText().toString().matches("[0-9]+")) {
            mDelPincode.setError("Please enter only numbers");
            return;
        } else if (mDelPincode.getText().toString().length() != 6) {
            mDelPincode.setError("Please enter 6 digits");
            return;
        }
        updateOnFirebase();
    }

    public void updateOnFirebase(){
        if (updated_contact.equals(contact)) {
            //isVerified = "true";
        } else {
            isVerified = "false";
        }

        progressDialog.setMessage("Updating you address details");
        progressDialog.show();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".","*")).child("address");
        databaseReference.child("name").setValue(name);

        databaseReference.child("contact").setValue(updated_contact);
        databaseReference.child("isVerified").setValue(isVerified);

        databaseReference.child("email").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        databaseReference.child("addressline1").setValue(addressline1);
        databaseReference.child("addressline2").setValue(addressline2);
        databaseReference.child("city").setValue(city);
        databaseReference.child("state").setValue(state);
        databaseReference.child("pincode").setValue(pincode).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Address saved", Toast.LENGTH_SHORT).show();
                    updateOnSharedPref();
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong. Try again.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    public void updateOnSharedPref(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Name", name);
        editor.putString("contact", updated_contact);
        editor.putString("email", email);
        editor.putString("isVerified",isVerified);
        editor.putString("addressline1", addressline1);
        editor.putString("addressline2", addressline2);
        editor.putString("city", city);
        editor.putString("state", state);
        editor.putString("pincode", pincode);

        editor.commit();
        progressDialog.dismiss();

        //Toast.makeText(getApplicationContext(), ""+ sharedPreferences.getString("isVerified", null), Toast.LENGTH_SHORT).show();
        //finish();
    }
}
