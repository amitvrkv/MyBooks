package com.mybooks.mybooks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class PaymentPageActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedPreferences;
    View parentLayoutView;
    DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private TextView mDeliveryAddress, mUpdateAddressBtn;
    private Button mPlaceOrder;
    private RadioButton mModeCOD;
    private String address;
    private int total = 0;
    private int min_order = 0;
    private int delivery_charge = 0;
    private int discount = 0;
    private int grand_total = 0;
    private TextView payment_applyPromocode;

    public static String getDate() {
        String dateInMilliseconds = String.valueOf(new Date().getTime());
        String dateFormat = "dd/MM/yyyy hh:mm:ss aa";
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_page);

        setToolbar();

        mDeliveryAddress = (TextView) findViewById(R.id.deliveryAddress);
        mUpdateAddressBtn = (TextView) findViewById(R.id.updateAddress);
        mUpdateAddressBtn.setOnClickListener(this);
        mPlaceOrder = (Button) findViewById(R.id.placeOrderBtn);
        mPlaceOrder.setOnClickListener(this);
        mModeCOD = (RadioButton) findViewById(R.id.modeCOD);

        sharedPreferences = getSharedPreferences(getString(R.string.sharedPrefDeliveryAddress), MODE_PRIVATE);

        progressDialog = new ProgressDialog(this);

        parentLayoutView = findViewById(R.id.paymentActicityParentView);

        payment_applyPromocode = (TextView) findViewById(R.id.payment_applyPromocode);
        payment_applyPromocode.setOnClickListener(this);

        setCharges();
    }

    public void setToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Payment Page");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MyCartNew.class));
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        setAddress();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.updateAddress:
                startActivity(new Intent(getApplicationContext(), AddressActivity.class));
                setAddress();
                break;

            case R.id.placeOrderBtn:
                if (mModeCOD.isChecked()) {
                    progressDialog.setTitle("Please wait...");
                    progressDialog.setMessage("Placing your order,\nPlease do not close the application.");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    getAppLiveness("Cash on delivery");
                } else
                    Snackbar.make(parentLayoutView, "Please select mode of payment", Snackbar.LENGTH_SHORT).show();
                break;

            case R.id.payment_applyPromocode:
                applyPromocode();
                break;
        }
    }

    public String setAddress() {
        address = "";
        address = address + sharedPreferences.getString("Name", null);
        address = address + "\n" + sharedPreferences.getString("addressline1", null);
        address = address + "\n" + sharedPreferences.getString("addressline2", null);
        address = address + "\n" + sharedPreferences.getString("city", null);
        address = address + " - " + sharedPreferences.getString("pincode", null);
        address = address + "\n" + sharedPreferences.getString("state", null);
        address = address + "\n" + sharedPreferences.getString("contact", null);
        mDeliveryAddress.setText(address);
        return address;
    }

    public void getAppLiveness(final String mop){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Configs").child("appset");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String live = String.valueOf(dataSnapshot.child("liveness").getValue());
                if (live.equals("1")) {
                    placeOrder(mop);
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

    //Getting order number and updating
    public void placeOrder(final String paymentmode) {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.sharedPrefDeliveryAddress), MODE_PRIVATE);
        if (sharedPreferences.getString("Name", null) == null) {
            Toast.makeText(getApplicationContext(), "Please update your address to continue.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), AddressActivity.class));
            return;
        }

        if (haveNetworkConnection() == false) {
            Snackbar.make(parentLayoutView, "Please check your internet connection.", Snackbar.LENGTH_SHORT).show();
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

                placeOrderOnFirebase(String.valueOf(ordernumber[0]), paymentmode);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                orderPlaceFailed("Failed to place order. Try again!!!");
            }
        });
    }

    //Updataing order details on firebase
    public void placeOrderOnFirebase(final String ordernumber, String paymentmode) {

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Order").child(ordernumber);
        databaseReference.child("orderid").setValue(ordernumber);
        databaseReference.child("from").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());
        databaseReference.child("date").setValue(String.valueOf(getDate()));

        databaseReference.child("total").setValue("" + total);
        databaseReference.child("deliverycharge").setValue("" + delivery_charge);
        databaseReference.child("discount").setValue("" + discount);
        databaseReference.child("grandtotal").setValue("" + grand_total);

        databaseReference.child("paymentmode").setValue(paymentmode);

        databaseReference.child("status").setValue("Order placed");
        databaseReference.child("deliveryaddress").setValue(setAddress().replace("\n", ", "));
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
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(getString(R.string.database_path), null);
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from P_CART", null);

        if (cursor.moveToFirst() == false) {
            Toast.makeText(getApplicationContext(), "Your Cart is empty!", Toast.LENGTH_SHORT).show();
        } else {
            do {
                databaseReference = FirebaseDatabase.getInstance().getReference().child("OrderDetails")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString().replace(".", "*"))
                        .child(ordernumber)
                        .child("prod" + count);
                databaseReference.child("key").setValue(cursor.getString(cursor.getColumnIndex("key")));
                databaseReference.child("booktype").setValue(cursor.getString(cursor.getColumnIndex("booktype")));
                databaseReference.child("price").setValue(cursor.getString(cursor.getColumnIndex("price")));
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
    public void orderPlacedSuccessfully() {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(getString(R.string.database_path), null);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS P_CART");
        progressDialog.dismiss();
        startActivity(new Intent(getApplicationContext(), OrderPageActivity.class));
        finish();
    }

    public void orderPlaceFailed(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
    }

    private boolean setCharges() {
        TextView payment_total = (TextView) findViewById(R.id.payment_total);
        final TextView payment_delivery_charge = (TextView) findViewById(R.id.payment_delivery_charge);
        final TextView payment_discount = (TextView) findViewById(R.id.payment_discount);
        final TextView payment_grand_total = (TextView) findViewById(R.id.payment_grand_total);


        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.sharedPrefDeliveryAddress), MODE_PRIVATE);
        total = Integer.parseInt(sharedPreferences.getString("Total", null));
        payment_total.setText("\u20B9 " + String.valueOf(total));

        final boolean[] result = {false};
        databaseReference = FirebaseDatabase.getInstance().getReference().child("mybooks");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                delivery_charge = Integer.parseInt(String.valueOf(dataSnapshot.child("delivery_charge").getValue()));
                min_order = Integer.parseInt(String.valueOf(dataSnapshot.child("min_order").getValue()));

                if (total >= min_order) {
                    delivery_charge = 0;
                }
                grand_total = total + delivery_charge - discount;

                payment_delivery_charge.setText("\u20B9 " + String.valueOf(delivery_charge));
                payment_discount.setText("\u20B9 " + String.valueOf(discount));
                payment_grand_total.setText("\u20B9 " + String.valueOf(grand_total));

                result[0] = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result[0] = false;
            }
        });

        return result[0];
    }

    private void applyPromocode() {

        final String promocode = "NEW";

        databaseReference = FirebaseDatabase.getInstance().getReference().child("promocode");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(promocode) == null) {

                } else {
                    discount = Integer.parseInt(String.valueOf(dataSnapshot.child(promocode).getValue()));
                }

                setCharges();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
}
