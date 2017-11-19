package com.mybooks.mybooks;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddressActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mDelName, mDelMobileNo, mDelHouseNameNumber, mDelLocality, mDelPincode;
    private Button mDelSaveBtn;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        setToolbar();

        mDelName = (TextView) findViewById(R.id.delName);
        mDelMobileNo = (TextView) findViewById(R.id.delmobileNumber);
        mDelHouseNameNumber = (TextView) findViewById(R.id.delHouseNameNumber);
        mDelLocality = (TextView) findViewById(R.id.delLocality);
        mDelPincode = (TextView) findViewById(R.id.delPincode);
        mDelSaveBtn = (Button) findViewById(R.id.delSaveBtn);
        mDelSaveBtn.setOnClickListener(this);

        sharedPreferences = getSharedPreferences(getString(R.string.sharedPrefDeliveryAddress), MODE_PRIVATE);

        if ( sharedPreferences.getString("Name", null) == null) {
            mDelName.setText("");
        } else {
            mDelName.setText(sharedPreferences.getString("Name", null));
        }

        if ( sharedPreferences.getString("contact", null) == null) {
            mDelMobileNo.setText("");
        } else {
            mDelMobileNo.setText(sharedPreferences.getString("contact", null));
        }

        if ( sharedPreferences.getString("addressline1", null) == null) {
            mDelHouseNameNumber.setText("");
        } else {
            mDelHouseNameNumber.setText(sharedPreferences.getString("addressline1", null));
        }

        if ( sharedPreferences.getString("addressline2", null) == null) {
            mDelLocality.setText("");
        } else {
            mDelLocality.setText(sharedPreferences.getString("addressline2", null));
        }

        if ( sharedPreferences.getString("pincode", null) == null) {
            mDelPincode.setText("");
        } else {
            mDelPincode.setText(sharedPreferences.getString("pincode", null));
        }
    }

    @Override
    public void onClick(View v) {
        mDelName.setError(null);
        mDelMobileNo.setError(null);
        mDelHouseNameNumber.setError(null);
        mDelLocality.setError(null);
        mDelPincode.setError(null);

        if (v.getId() == mDelSaveBtn.getId()) {
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

            String name = mDelName.getText().toString().trim();
            String contact = mDelMobileNo.getText().toString().trim();
            String addressline1 = mDelHouseNameNumber.getText().toString().trim();
            String addressline2 = mDelLocality.getText().toString().trim();
            String city = "Bengaluru";
            String state = "Karnataka";
            String pincode = mDelPincode.getText().toString().trim();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Name", name);
            editor.putString("contact", contact);
            editor.putString("addressline1", addressline1);
            editor.putString("addressline2", addressline2);
            editor.putString("city", city);
            editor.putString("state", state);
            editor.putString("pincode", pincode);
            editor.commit();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".","*")).child("address");
            databaseReference.child("name").setValue(name);
            databaseReference.child("contact").setValue(contact);
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
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Something went wrong. Try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
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
}
