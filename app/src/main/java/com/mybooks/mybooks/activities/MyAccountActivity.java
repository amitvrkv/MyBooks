package com.mybooks.mybooks.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mybooks.mybooks.R;

public class MyAccountActivity extends AppCompatActivity implements View.OnClickListener {

    String address;
    SharedPreferences sharedPreferences;
    TextView verify_btn;
    String customerCareNumber = "";
    ProgressDialog progressDialog;
    private TextView mName, mMobile, mEmail, mDelAddress, mUpdateAddBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        setToolbar();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mName = (TextView) findViewById(R.id.myAccName);
        mMobile = (TextView) findViewById(R.id.myAccContactNumber);
        mEmail = (TextView) findViewById(R.id.myAccEmailId);
        mDelAddress = (TextView) findViewById(R.id.myAccAddress);
        mUpdateAddBtn = (TextView) findViewById(R.id.myAccUpdateAddBtn);
        mUpdateAddBtn.setOnClickListener(this);

        verify_btn = (TextView) findViewById(R.id.verify_btn);

        sharedPreferences = getSharedPreferences(getString(R.string.sharedPrefDeliveryAddress), MODE_PRIVATE);


        if (sharedPreferences.getString("isVerified", null).equals("true")) {
            verify_btn.setVisibility(View.GONE);
        } else {
            verify_btn.setVisibility(View.VISIBLE);
        }

        setMyAccDetails();
        setWallet();
        getCustomerCareNumber();
    }

    public void setToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("My Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setMyAccDetails();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myAccUpdateAddBtn:
                startActivity(new Intent(this, AddressActivity.class));
                break;
        }
    }

    public String setAddress() {
        address = "";
        address = address + sharedPreferences.getString("Name", null);
        address = address + "\n" + sharedPreferences.getString("contact", null);
        address = address + "\n" + sharedPreferences.getString("addressline1", null);
        address = address + "\n" + sharedPreferences.getString("addressline2", null);
        address = address + "\n" + sharedPreferences.getString("city", null);
        address = address + " - " + sharedPreferences.getString("pincode", null);
        address = address + "\n" + sharedPreferences.getString("state", null);
        //mDeliveryAddress.setText(address);

        return address;
    }

    public void setMyAccDetails() {
        if (sharedPreferences.getString("Name", null) == null) {
            startActivity(new Intent(this, AddressActivity.class));
            finish();
            return;
        }
        mName.setText(sharedPreferences.getString("Name", null).toUpperCase());
        mMobile.setText(sharedPreferences.getString("contact", null));
        mEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());

        mDelAddress.setText(setAddress());
    }

    private void setWallet() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "*"));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String walletAmount = String.valueOf(dataSnapshot.child("wallet").getValue());
                TextView walletAmt = (TextView) findViewById(R.id.walletAmt);
                walletAmt.setText("\u20B9 " + walletAmount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getCustomerCareNumber() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("MOBILE_VERIFICATION").child("SETTING").child("CALL_TO");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                customerCareNumber = dataSnapshot.getValue().toString();
                //callNow(number);
                verify_btn.setText("Verification pending...\nCALL NOW ( Give a miss-call to " + customerCareNumber + " )");
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void callNow(View view) {



        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + customerCareNumber));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
            Toast.makeText(this, "Allow PHONE CALLS permission and try again.", Toast.LENGTH_LONG).show();
            return;
        }
        startActivity(intent);
    }
}
