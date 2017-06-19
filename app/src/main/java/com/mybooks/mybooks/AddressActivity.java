package com.mybooks.mybooks;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AddressActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mDelName, mDelMobileNo, mDelHouseNameNumber, mDelLocality, mDelPincode;
    private Button mDelSaveBtn;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        mDelName = (TextView) findViewById(R.id.delName);
        mDelMobileNo = (TextView) findViewById(R.id.delmobileNumber);
        mDelHouseNameNumber = (TextView) findViewById(R.id.delHouseNameNumber);
        mDelLocality = (TextView) findViewById(R.id.delLocality);
        mDelPincode = (TextView) findViewById(R.id.delPincode);
        mDelSaveBtn = (Button) findViewById(R.id.delSaveBtn);
        mDelSaveBtn.setOnClickListener(this);

        sharedPreferences = getSharedPreferences("delivery_address", MODE_PRIVATE);

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

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Name", mDelName.getText().toString());
            editor.putString("contact", mDelMobileNo.getText().toString());
            editor.putString("addressline1", mDelHouseNameNumber.getText().toString());
            editor.putString("addressline2", mDelLocality.getText().toString());
            editor.putString("pincode", mDelPincode.getText().toString());
            editor.putString("city", "Bengaluru");
            editor.putString("state", "Karnataka");
            editor.commit();

            Toast.makeText(getApplicationContext(), "Adsress saved", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
