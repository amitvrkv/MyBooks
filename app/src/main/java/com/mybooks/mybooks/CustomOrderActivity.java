package com.mybooks.mybooks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
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

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomOrderActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    private TextView mtitle;
    private TextView mAuthor;
    private TextView mPublisher;
    private TextView mCourseSemYear;
    private TextView mDesc;
    private TextView mPlaceOrderBtn;
    String key = "null";

    private DatabaseReference mDatabase;

    public static String getDate() {
        String dateInMilliseconds = String.valueOf(new Date().getTime());
        String dateFormat = "dd/MM/yyyy hh:mm:ss aa";
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_order);

        setToolbar();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        mtitle = (TextView) findViewById(R.id.customTitle);
        mAuthor = (TextView) findViewById(R.id.customAuthor);
        mPublisher = (TextView) findViewById(R.id.customPublisher);
        mCourseSemYear = (TextView) findViewById(R.id.customCourse);
        mDesc = (TextView) findViewById(R.id.customDesc);

        mPlaceOrderBtn = (TextView) findViewById(R.id.customPlaceOrder);
        mPlaceOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mtitle.setError(null);
                mAuthor.setError(null);
                mCourseSemYear.setError(null);
                mDesc.setError(null);

                if (TextUtils.isEmpty(mtitle.getText().toString())) {
                    mtitle.setError("This field is required.");
                    return;
                } else if (TextUtils.isEmpty(mAuthor.getText().toString())) {
                    mAuthor.setError("This field is required.");
                    return;
                } else if (TextUtils.isEmpty(mPublisher.getText().toString())) {
                    mPublisher.setError("This field is required.");
                    return;
                } else if (TextUtils.isEmpty(mCourseSemYear.getText().toString())) {
                    mCourseSemYear.setError("This field is required.");
                    return;
                } else if (TextUtils.isEmpty(mDesc.getText().toString())) {
                    mDesc.setError("This field is required.");
                    return;
                }
                progressDialog.setTitle("Please wait...");
                progressDialog.setMessage("Placing your order,\nPlease do not close the application.");
                getAppLiveness();
            }
        });

        Bundle bundle = getIntent().getExtras();
        key = bundle.getString("key");
        if ( ! key.equalsIgnoreCase("null")) {
            setData(key);
        }
    }

    private void setData(String key) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Products").child(key);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ModelProductList modelProductList = dataSnapshot.getValue(ModelProductList.class);
                mtitle.setText(capitalizeEveryWord(modelProductList.getF2()));
                mtitle.setEnabled(false);
                mPublisher.setText(capitalizeEveryWord(modelProductList.getF3()));
                mPublisher.setEnabled(false);
                mAuthor.setText(capitalizeEveryWord(modelProductList.getF4()));
                mAuthor.setEnabled(false);
                mCourseSemYear.setText(modelProductList.getF5() + " / " + modelProductList.getF6());
                mCourseSemYear.setEnabled(false);
                mDesc.setText("NA");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Customise Order");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void placeCustomOrder() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.sharedPrefDeliveryAddress), MODE_PRIVATE);
        if (sharedPreferences.getString("Name", null) == null) {
            Toast.makeText(getApplicationContext(), "Please update your address to continue.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), AddressActivity.class));
            return;
        }

        if (haveNetworkConnection() == false) {
            showAlertDialog("Error", "Please check your internet connection!!!");
            return;
        }

        progressDialog.show();

        final int[] ordernumber = {0};
        databaseReference = FirebaseDatabase.getInstance().getReference().child("mybooks").child("order");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ordernumber[0] = Integer.parseInt(dataSnapshot.getValue().toString());
                ordernumber[0] = ordernumber[0] + 1;
                databaseReference.setValue(String.valueOf(ordernumber[0]));

                placeOrderOnFirebase(String.valueOf(ordernumber[0]));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showAlertDialog("Error", "Failed to place order. Try again!!!");
            }
        });
    }

    //Updataing order details on firebase
    public void placeOrderOnFirebase(final String ordernumber) {

        databaseReference = FirebaseDatabase.getInstance().getReference().child("OrderCustom").child(ordernumber);
        databaseReference.child("orderid").setValue(ordernumber);
        databaseReference.child("from").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());
        databaseReference.child("date").setValue(String.valueOf(getDate()));

        databaseReference.child("title").setValue(mtitle.getText().toString());
        databaseReference.child("author").setValue(mAuthor.getText().toString());
        databaseReference.child("publisher").setValue(mPublisher.getText().toString());
        databaseReference.child("desc1").setValue(mCourseSemYear.getText().toString());
        databaseReference.child("desc2").setValue(mDesc.getText().toString());

        databaseReference.child("comment").setValue("");

        databaseReference.child("getdetails").setValue(key);

        databaseReference.child("status").setValue("Pending for My Books team approval.").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();

                    finish();
                } else {
                    showAlertDialog("Error", "Failed to place order. Try again!!!");
                }
            }
        });
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public void showAlertDialog(String title, String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void getAppLiveness(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Configs").child("appset");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String live = String.valueOf(dataSnapshot.child("liveness").getValue());
                if (live.equals("1")) {
                    placeCustomOrder();
                } else {
                    Toast.makeText(getApplicationContext(), "Somthing went wrong.\nOrder can not be place at this movement", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public String capitalizeEveryWord(String str) {

        if (str == null)
            return "";

        System.out.println(str);
        StringBuffer stringbf = new StringBuffer();
        Matcher m = Pattern.compile(
                "([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(str);

        while (m.find()) {
            m.appendReplacement(
                    stringbf, m.group(1).toUpperCase() + m.group(2).toLowerCase());
        }
        return m.appendTail(stringbf).toString();
    }

}
