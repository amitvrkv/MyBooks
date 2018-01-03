package com.mybooks.mybooks.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mybooks.mybooks.R;
import com.mybooks.mybooks.models.OrderDetailsBookList;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrderDetailsActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    FirebaseRecyclerAdapter<OrderDetailsBookList, OrderDetailsViewHolder> firebaseRecyclerAdapter;

    String orderId;

    List<OrderDetailsActivity> orderDetailsActivityList;

    private LinearLayoutManager mLayoutManager;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        recyclerView = (RecyclerView) findViewById(R.id.orderDetailsRecycleView);
        recyclerView.setNestedScrollingEnabled(false);
        //recyclerView.setHasFixedSize(true);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle bundle = getIntent().getExtras();
        orderId = bundle.getString("orderId");

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Loading data");
        //progressDialog.show();

        setToolbar();

        setBillDetailsAndShippingAddress();

        setProductDetails();

        setProductCounts();
    }

    private void setProductCounts() {
        progressDialog.show();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("ORDER")
                .child("ORDERDETAILS")
                .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString().replace(".", "*"))
                .child("MYORDER")
                .child(orderId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                TextView product_count = (TextView) findViewById(R.id.product_count);
                product_count.setText("( " + count + " )");

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setBillDetailsAndShippingAddress() {
        progressDialog.show();

        final TextView textViewAddress = (TextView) findViewById(R.id.address);
        final TextView payment_total = (TextView) findViewById(R.id.payment_total);
        final TextView payment_delivery_charge = (TextView) findViewById(R.id.payment_delivery_charge);
        final TextView payment_discount = (TextView) findViewById(R.id.payment_discount);
        final TextView wallet_amount = (TextView) findViewById(R.id.wallet_amount);
        final TextView payment_payable_amt = (TextView) findViewById(R.id.payment_payable_amt);
        final TextView payment_mode = (TextView) findViewById(R.id.payment_mode);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("ORDER").child("MYORDER").child(orderId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String total = (String) dataSnapshot.child("total").getValue();
                String delivey_charge = (String) dataSnapshot.child("deliverycharge").getValue();
                String address = (String) dataSnapshot.child("deliveryaddress").getValue();
                String discount = (String) dataSnapshot.child("discount").getValue();
                //String wallet = (String) dataSnapshot.child("").getValue();
                String payable_amt = (String) dataSnapshot.child("payable_amount").getValue();
                String payMode = (String) dataSnapshot.child("paymentmode").getValue();

                payment_total.setText("\u20B9 " + total);
                payment_delivery_charge.setText("\u20B9 " + delivey_charge);
                payment_discount.setText("\u20B9 " + discount);
                //wallet_amount.setText("");
                payment_payable_amt.setText("\u20B9 " + payable_amt);

                payment_mode.setText("Payment mode: " + payMode);
                textViewAddress.setText(address.replaceFirst(", ", "\n"));

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Order ID: " + orderId);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setProductDetails() {
        progressDialog.show();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("ORDER")
                .child("ORDERDETAILS").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString().replace(".", "*"))
                .child("MYORDER")
                .child(orderId);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<OrderDetailsBookList, OrderDetailsViewHolder>(
                OrderDetailsBookList.class,
                R.layout.order_book_list_view,
                OrderDetailsViewHolder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(OrderDetailsViewHolder viewHolder, OrderDetailsBookList model, int position) {
                viewHolder.setFields(model.getKey(), model.getBooktype(), model.getQuantity(), model.getPrice());
                progressDialog.dismiss();
            }
        };

        mLayoutManager = new LinearLayoutManager(OrderDetailsActivity.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class OrderDetailsViewHolder extends RecyclerView.ViewHolder {
        View view;
        String key;

        public OrderDetailsViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadBookDetails(v.getContext(), key);
                }
            });
        }

        public void setFields(String key, String type, String qty, String price_per_book) {
            this.key = key;
            final TextView mTitle = (TextView) view.findViewById(R.id.obookTitle);
            TextView mPublisher = (TextView) view.findViewById(R.id.obookPublisher);
            mPublisher.setVisibility(View.GONE);
            TextView mAuthor = (TextView) view.findViewById(R.id.obookAuthor);
            mAuthor.setVisibility(View.GONE);
            TextView mType = (TextView) view.findViewById(R.id.obooktype);
            TextView mPricePerBook = (TextView) view.findViewById(R.id.obookPricePerBook);
            TextView mQty = (TextView) view.findViewById(R.id.oQuantity);
            TextView mbook_grand = (TextView) view.findViewById(R.id.obookGrandTotal);
            final ImageView mBookImage = (ImageView) view.findViewById(R.id.obookImage);


            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("PRODUCT").child("PRODUCTS").child(key);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mTitle.setText(capitalizeEveryWord(dataSnapshot.child("f2").getValue().toString()));

                    if (!dataSnapshot.child("f13").getValue().toString().equalsIgnoreCase("na")) {
                        Glide.with(view.getContext())
                                .load(dataSnapshot.child("f13").getValue().toString())
                                .into(mBookImage);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mType.setText("Book Type: " + capitalizeEveryWord(type));
            mPricePerBook.setText("Price per Book: \u20B9 " + price_per_book);
            mQty.setText("Quantity: " + qty);
            int grand = Integer.parseInt(price_per_book) * Integer.parseInt(qty);
            mbook_grand.setText("Total Price: \u20B9 " + grand);
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

        public void loadBookDetails(Context ctx, String key) {
            Intent intent = new Intent(ctx, Individual_book_details.class);
            intent.putExtra("key", key);
            ctx.startActivity(intent);
        }
    }
}



