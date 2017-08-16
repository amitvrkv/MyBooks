package com.mybooks.mybooks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import static android.R.attr.order;

public class PaymentPageActivity extends AppCompatActivity implements View.OnClickListener{

    private ProgressDialog progressDialog;

    private TextView mDeliveryAddress, mUpdateAddressBtn;
    private Button mPlaceOrder;
    private RadioButton mModeCOD;
    private ImageView mplaceOrderBackBtn;

    private String address;

    SharedPreferences sharedPreferences;

    View parentLayoutView;

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

        progressDialog = new ProgressDialog(this);

        parentLayoutView = findViewById(R.id.paymentActicityParentView);

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
                if (mModeCOD.isChecked()) {

                    progressDialog.setTitle("Please wait...");
                    progressDialog.setMessage("Placing your order,\nPlease do not close the application.");
                    progressDialog.setCancelable(false);

                    placeOrder();
                }
                else
                    //Toast.makeText(getApplicationContext(), "Please select mode of payments", Toast.LENGTH_SHORT).show();
                    Snackbar.make(parentLayoutView, "Please select mode of payment", Snackbar.LENGTH_SHORT).show();
                break;
        }
    }

    public String setAddress() {
        address = "";
        address = address + sharedPreferences.getString("Name", null) ;
        address = address + "\n" + sharedPreferences.getString("addressline1", null);
        address = address + "\n" + sharedPreferences.getString("addressline2", null);
        address = address + "\n" + sharedPreferences.getString("city", null);
        address = address + " - " + sharedPreferences.getString("pincode", null);
        address = address + "\n" + sharedPreferences.getString("state", null);
        address = address + "\n" + sharedPreferences.getString("contact", null);
        mDeliveryAddress.setText(address);
        return address;
    }

    //Getting order number and updating
    public void placeOrder() {

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.sharedPrefDeliveryAddress), MODE_PRIVATE);
        if ( sharedPreferences.getString("Name", null) == null ) {
            Toast.makeText(getApplicationContext(), "Please update your address to continue.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), AddressActivity.class));
            return;
        }

        if( haveNetworkConnection() == false) {
            Snackbar.make(parentLayoutView, "Please check your internet connection.", Snackbar.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        final int[] ordernumber = {0};
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("mybooks").child("order");
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
                orderPlaceFailed("Failed to place order. Try again!!!");
            }
        });
    }

    //Updataing order details on firebase
    public void placeOrderOnFirebase(final String ordernumber) {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.sharedPrefDeliveryAddress), MODE_PRIVATE);
        String grandTotal = sharedPreferences.getString("GrandTotal", null);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Order").child(ordernumber);
        databaseReference.child("orderid").setValue(ordernumber);
        databaseReference.child("from").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());
        databaseReference.child("date").setValue(String.valueOf(getDate()));
        databaseReference.child("grandtotal").setValue(grandTotal);
        databaseReference.child("status").setValue("Order placed");
        databaseReference.child("deliveryaddress").setValue(setAddress().replace("\n", ", "));
        databaseReference.child("discount").setValue("0");
        databaseReference.child("comment").setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    placeOrderedProductOnFirebase(ordernumber);
                } else {
                    orderPlaceFailed("Failed to place order. Try again!!!");
                }
            }
        });
    }

    //Updating product details on firebase
    public void placeOrderedProductOnFirebase(String ordernumber) {
        int count = 1;
        DatabaseReference databaseReference;
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(getString(R.string.database_path), null);
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from CART", null);

        if (cursor.moveToFirst() == false) {
            Toast.makeText(getApplicationContext(), "Your Cart is empty!", Toast.LENGTH_SHORT).show();
        } else {
            do {
                databaseReference = FirebaseDatabase.getInstance().getReference().child("OrderDetails")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString().replace(".", "*"))
                        .child(ordernumber)
                        .child("prod" + count);
                databaseReference.child("bookkey").setValue(cursor.getString(cursor.getColumnIndex("key")));
                databaseReference.child("title").setValue(cursor.getString(cursor.getColumnIndex("title")));
                databaseReference.child("publisher").setValue(cursor.getString(cursor.getColumnIndex("publisher")));
                databaseReference.child("author").setValue(cursor.getString(cursor.getColumnIndex("author")));
                databaseReference.child("course").setValue(cursor.getString(cursor.getColumnIndex("course")));
                databaseReference.child("sem").setValue(cursor.getString(cursor.getColumnIndex("sem")));
                databaseReference.child("booktype").setValue(cursor.getString(cursor.getColumnIndex("booktype")));

                if(cursor.getString(cursor.getColumnIndex("booktype")).equals("new")) {
                    databaseReference.child("price").setValue(cursor.getString(cursor.getColumnIndex("priceNew")));
                } else {
                    databaseReference.child("price").setValue(cursor.getString(cursor.getColumnIndex("priceOld")));
                }
                databaseReference.child("quantity").setValue(cursor.getString(cursor.getColumnIndex("qty"))).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        } else {
                            orderPlaceFailed("Failed to update your product to server. Try again!!!");
                        }
                    }
                });

                count++;

            } while (cursor.moveToNext());

            orderPlacedSuccessfully();
        }
    }

    //order placed successfully
    public void orderPlacedSuccessfully(){
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(getString(R.string.database_path), null);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS CART");
        progressDialog.dismiss();
        startActivity(new Intent(getApplicationContext(), OrderPageActivity.class));
        finish();
    }

    public void orderPlaceFailed(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
    }

    public static String getDate() {
        String dateInMilliseconds = String.valueOf(new Date().getTime());
        String dateFormat = "dd/MM/yyyy hh:mm:ss aa";
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
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
}
