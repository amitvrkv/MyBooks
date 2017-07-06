package com.mybooks.mybooks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MyAccountActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mName, mMobile, mEmail, mDelAddress, mUpdateAddBtn;
    ImageView backBtn;

    String address;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        mName = (TextView) findViewById(R.id.myAccName);
        mMobile = (TextView) findViewById(R.id.myAccContactNumber);
        mEmail = (TextView) findViewById(R.id.myAccEmailId);
        mDelAddress = (TextView) findViewById(R.id.myAccAddress);
        mUpdateAddBtn = (TextView) findViewById(R.id.myAccUpdateAddBtn);
        mUpdateAddBtn.setOnClickListener(this);

        backBtn = (ImageView) findViewById(R.id.myAccBackBtn);
        backBtn.setOnClickListener(this);

        sharedPreferences = getSharedPreferences(getString(R.string.sharedPrefDeliveryAddress), MODE_PRIVATE);

        setMyAccDetails();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setMyAccDetails();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myAccBackBtn:
                    finish();
                break;

            case R.id.myAccUpdateAddBtn:
                startActivity(new Intent(this, AddressActivity.class));
                break;
        }
    }

    public String setAddress() {
        address = "";
        address = address + sharedPreferences.getString("Name", null) ;
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
        if(sharedPreferences.getString("Name", null) == null) {
            startActivity(new Intent(this, AddressActivity.class));
            finish();
            return;
        }
        mName.setText(sharedPreferences.getString("Name", null).toUpperCase());
        mMobile.setText(sharedPreferences.getString("contact", null));
        mEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());

        mDelAddress.setText("Delivery Address:\n" + setAddress());
    }

}
