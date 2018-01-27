package in.shopy.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import in.shopy.Utils.MySharedPreference;

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
        setContentView(in.shopy.R.layout.activity_my_account);
        setToolbar();

        initViews();
        setListeners();


        progressDialog.show();


        if (sharedPreferences.getString("isVerified", null).equals("true")) {
            verify_btn.setVisibility(View.GONE);
        } else {
            verify_btn.setVisibility(View.VISIBLE);
        }

        setMyAccDetails();

        setWallet();
        getCustomerCareNumber();
    }

    private void initViews() {
        mName = (TextView) findViewById(in.shopy.R.id.myAccName);
        mMobile = (TextView) findViewById(in.shopy.R.id.myAccContactNumber);
        mEmail = (TextView) findViewById(in.shopy.R.id.myAccEmailId);
        mDelAddress = (TextView) findViewById(in.shopy.R.id.myAccAddress);
        mUpdateAddBtn = (TextView) findViewById(in.shopy.R.id.myAccUpdateAddBtn);
        verify_btn = (TextView) findViewById(in.shopy.R.id.verify_btn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        sharedPreferences = getSharedPreferences(getString(in.shopy.R.string.sharedPrefDeliveryAddress), MODE_PRIVATE);
    }

    private void setListeners() {
        mUpdateAddBtn.setOnClickListener(this);
    }

    public void setToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(in.shopy.R.id.toolbar);
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
            case in.shopy.R.id.myAccUpdateAddBtn:
                startActivity(new Intent(this, AddressActivity.class));
                break;
        }
    }

    public String setAddress() {
        address = "";

        if ( ! MySharedPreference.isDeliveryAddressCorrect(getApplicationContext())) {
            address = "Please update your delivery address.";
        } else {
            address = MySharedPreference.getAddress(getApplicationContext());
        }

        //mDeliveryAddress.setText(address);

        return address;
    }

    public void setMyAccDetails() {
        if (MySharedPreference.getDataFromAddress(getApplicationContext(), "Name") == null) {
            startActivity(new Intent(this, AddressActivity.class));
            finish();
            return;
        }

        mName.setText(MySharedPreference.getDataFromAddress(getApplicationContext(), "Name").toUpperCase());

        if (sharedPreferences.getString("contact", null) == null
                || sharedPreferences.getString("contact", null).equalsIgnoreCase("null")) {
            mMobile.setVisibility(View.GONE);
            verify_btn.setVisibility(View.GONE);
        } else {
            mMobile.setText(sharedPreferences.getString("contact", null));
        }

        mEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());

        mDelAddress.setText(setAddress());
    }

    private void setWallet() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "*"));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String walletAmount = String.valueOf(dataSnapshot.child("wallet").getValue());
                TextView walletAmt = (TextView) findViewById(in.shopy.R.id.walletAmt);
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
                //verify_btn.setText("Verification pending...\nCALL NOW ( Give a miss-call to " + customerCareNumber + " )");
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void callNow(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("VERIFY NOW");
        builder.setMessage("Give a miss-call to " + customerCareNumber);
        builder.setCancelable(false);
        builder.setPositiveButton("CALL NOW", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + customerCareNumber));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MyAccountActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                    Toast.makeText(getApplicationContext(), "Allow PHONE CALLS permission and try again.", Toast.LENGTH_LONG).show();
                    return;
                }
                startActivity(intent);
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
