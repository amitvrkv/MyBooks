package com.mybooks.mybooks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class PaymentPageActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mDeliveryAddress, mUpdateAddressBtn;
    private Button mPlaceOrder;
    private RadioButton mModeCOD;
    private ImageView mplaceOrderBackBtn;

    private String address;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_page);

        mDeliveryAddress = (TextView) findViewById(R.id.deliveryAddress);
        mUpdateAddressBtn = (TextView) findViewById(R.id.updateAddress);
        mUpdateAddressBtn.setOnClickListener(this);
        mPlaceOrder = (Button) findViewById(R.id.placeOrderBtn);
        mPlaceOrder.setOnClickListener(this);
        mModeCOD = (RadioButton) findViewById(R.id.modeCOD);
        mplaceOrderBackBtn = (ImageView) findViewById(R.id.placeOrderBackBtn);
        mplaceOrderBackBtn.setOnClickListener(this);

        sharedPreferences = getSharedPreferences(getString(R.string.sharedPrefDeliveryAddress), MODE_PRIVATE);

        setAddress();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setAddress();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.placeOrderBackBtn:
                finish();
                break;

            case R.id.updateAddress:
                startActivity(new Intent(getApplicationContext(), AddressActivity.class));
                setAddress();
                break;

            case R.id.placeOrderBtn:
                if (mModeCOD.isChecked())
                    Toast.makeText(getApplicationContext(), "Order Placed", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "Please select mode of payments", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void setAddress() {
        address = "";
        address = address + sharedPreferences.getString("Name", null) ;
        address = address + "\n" + sharedPreferences.getString("contact", null);
        address = address + "\n" + sharedPreferences.getString("addressline1", null);
        address = address + "\n" + sharedPreferences.getString("addressline2", null);
        address = address + "\n" + sharedPreferences.getString("city", null);
        address = address + " - " + sharedPreferences.getString("pincode", null);
        address = address + "\n" + sharedPreferences.getString("state", null);
        mDeliveryAddress.setText(address);
    }
}
