package com.mybooks.mybooks.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mybooks.mybooks.BuildConfig;
import com.mybooks.mybooks.R;
import com.mybooks.mybooks.app_pref.MyFormat;

public class About extends AppCompatActivity {


    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setToolbar();

        TextView textView = (TextView) findViewById(R.id.version);
        textView.setText("Version: " + BuildConfig.VERSION_NAME);
    }

    public void setToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("About");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                onStartTransaction();
            }
        });
    }

    public void onStartTransaction() {
        Toast.makeText(this, "Started", Toast.LENGTH_SHORT).show();
    }

    public void navigateToBaseActivity() {

/*
        intent = new Intent(getApplicationContext(), PayUBaseActivity.class);
        mPaymentParams = new PaymentParams();
        payuConfig = new PayuConfig();

        mPaymentParams.setKey(merchantKey);
        key = merchantKey;
        // txt_totals.getText()+""
        mPaymentParams.setAmount("3533");

        mPaymentParams.setProductInfo(size + "_" + qty + "_" + pid);

        mPaymentParams.setFirstName("sandeep khandelwal");

        mPaymentParams.setEmail("tutorialsee9@gmail.com");

        mPaymentParams.setTxnId("" + System.currentTimeMillis());

        mPaymentParams.setSurl("https://payu.herokuapp.com/success");

        mPaymentParams.setFurl("https://payu.herokuapp.com/failure");

        mPaymentParams.setUdf1("");

        mPaymentParams.setUdf2("");

        mPaymentParams.setUdf3("");

        mPaymentParams.setUdf4("");

        mPaymentParams.setUdf5("");
        mPaymentParams.setUserCredentials(merchantKey + ":payutest@payu.in");
        var1 = merchantKey + ":payutest@payu.in";
        mPaymentParams.setOfferKey("");
        intent.putExtra(PayuConstants.SALT, merchantSalt);
        salt = merchantSalt;
        String environment = "0";
        payuConfig.setEnvironment(0);
        cardBin = "";

        generateHashFromSDK(mPaymentParams, merchantSalt);

        */

    }


}

