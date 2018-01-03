package com.mybooks.mybooks.activities;

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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mybooks.mybooks.R;
import com.mybooks.mybooks.app_pref.MyFormat;
import com.mybooks.mybooks.models.OrderCustomDetailsBookList;

public class OrderCustomDetailsActivity extends AppCompatActivity {

    FirebaseRecyclerAdapter<OrderCustomDetailsBookList, OrderCustomDetailsViewHolder> firebaseRecyclerAdapter;
    RecyclerView recyclerView;
    private String orderId;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_custom_details);

        Bundle bundle = getIntent().getExtras();
        orderId = bundle.getString("orderId");

        recyclerView = (RecyclerView) findViewById(R.id.orderDetailsRecycleView);
        recyclerView.setNestedScrollingEnabled(false);

        setToolbar();
        setBillDetailsAndShippingAddress();
        setProductDetails();
        setProductCounts();
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

    private void setBillDetailsAndShippingAddress() {
        final TextView textViewAddress = (TextView) findViewById(R.id.address);
        final TextView payment_total = (TextView) findViewById(R.id.payment_total);
        final TextView payment_delivery_charge = (TextView) findViewById(R.id.payment_delivery_charge);
        final TextView payment_discount = (TextView) findViewById(R.id.payment_discount);
        final TextView wallet_amount = (TextView) findViewById(R.id.wallet_amount);
        final TextView payment_payable_amt = (TextView) findViewById(R.id.payment_payable_amt);
        final TextView payment_mode = (TextView) findViewById(R.id.payment_mode);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("ORDER").child("CUSTOMORDER").child(orderId);
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setProductCounts() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("ORDER")
                .child("ORDERDETAILS")
                .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString().replace(".", "*"))
                .child("CUSTOMORDER")
                .child(orderId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                TextView product_count = (TextView) findViewById(R.id.product_count);
                product_count.setText("( " + count + " )");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setProductDetails() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("ORDER")
                .child("ORDERDETAILS")
                .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString().replace(".", "*"))
                .child("CUSTOMORDER")
                .child(orderId);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<OrderCustomDetailsBookList, OrderCustomDetailsViewHolder>(
                OrderCustomDetailsBookList.class,
                R.layout.order_book_list_view,
                OrderCustomDetailsViewHolder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(OrderCustomDetailsViewHolder viewHolder, OrderCustomDetailsBookList model, int position) {
                viewHolder.setFields(
                        model.getAuthor(),
                        model.getBookType(),
                        model.getCourse(),
                        model.getDescription(),
                        model.getEstPrice(),
                        model.getKey(),
                        model.getMrp(),
                        model.getPublisher(),
                        model.getQty(),
                        model.getTitle(),
                        model.getTotal()
                );
            }
        };

        mLayoutManager = new LinearLayoutManager(OrderCustomDetailsActivity.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class OrderCustomDetailsViewHolder extends RecyclerView.ViewHolder {
        View view;
        String key;

        public OrderCustomDetailsViewHolder(View itemView) {
            super(itemView);

            this.view = itemView;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadBookDetails(v.getContext(), key);
                }
            });
        }

        public void loadBookDetails(Context ctx, String key) {
            if (key.equals("") || key.equalsIgnoreCase("na")) {
                Toast.makeText(view.getContext(), "Book details not found.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(ctx, Individual_book_details.class);
            intent.putExtra("key", key);
            ctx.startActivity(intent);
        }

        public void setFields(String author, String bookType, String course, String description, String estPrice, String key, String mrp, String publisher, String qty, String title, String total) {
            this.key = key;

            int gtot = Integer.parseInt(qty) * Integer.parseInt(estPrice);

            TextView mTitle = (TextView) view.findViewById(R.id.obookTitle);
            TextView mPublisher = (TextView) view.findViewById(R.id.obookPublisher);
            TextView mAuthor = (TextView) view.findViewById(R.id.obookAuthor);
            TextView mType = (TextView) view.findViewById(R.id.obooktype);
            TextView mPricePerBook = (TextView) view.findViewById(R.id.obookPricePerBook);
            TextView mQty = (TextView) view.findViewById(R.id.oQuantity);
            TextView mbook_grand = (TextView) view.findViewById(R.id.obookGrandTotal);
            ImageView mBookImage = (ImageView) view.findViewById(R.id.obookImage);

            if (key.equals("") || key.equalsIgnoreCase("na")) {
                mBookImage.setVisibility(View.GONE);
                mTitle.setText(MyFormat.capitalizeEveryWord(title));
                mPublisher.setText(publisher);
                mAuthor.setText(author);
                mType.setText("Book Type: " + bookType);
                mQty.setText("Quantity: " + qty);
                mPricePerBook.setText("Estimated Price per Book: ₹ " + estPrice);
                mbook_grand.setText("Total Price: ₹ " + gtot);
            } else {
                setFieldsFromKey(key, bookType, qty);
            }
        }

        public void setFieldsFromKey(String key, final String type, final String qty) {
            this.key = key;
            final TextView mTitle = (TextView) view.findViewById(R.id.obookTitle);
            TextView mPublisher = (TextView) view.findViewById(R.id.obookPublisher);
            mPublisher.setVisibility(View.GONE);
            TextView mAuthor = (TextView) view.findViewById(R.id.obookAuthor);
            mAuthor.setVisibility(View.GONE);
            final TextView mType = (TextView) view.findViewById(R.id.obooktype);
            final TextView mPricePerBook = (TextView) view.findViewById(R.id.obookPricePerBook);
            final TextView mQty = (TextView) view.findViewById(R.id.oQuantity);
            final TextView mbook_grand = (TextView) view.findViewById(R.id.obookGrandTotal);
            final ImageView mBookImage = (ImageView) view.findViewById(R.id.obookImage);


            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("PRODUCT").child("PRODUCTS").child(key);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String price_per_book;

                    mTitle.setText(MyFormat.capitalizeEveryWord(dataSnapshot.child("f2").getValue().toString()));

                    mType.setText("Book Type: " + MyFormat.capitalizeEveryWord(type));

                    if (type.equalsIgnoreCase("new")) {
                        price_per_book = dataSnapshot.child("f8").getValue().toString();
                    } else {
                        price_per_book = dataSnapshot.child("f9").getValue().toString();
                    }

                    mPricePerBook.setText("Price per Book: \u20B9 " + price_per_book);
                    mQty.setText("Quantity: " + qty);
                    int grand = Integer.parseInt(price_per_book) * Integer.parseInt(qty);
                    mbook_grand.setText("Total Price: \u20B9 " + grand);

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
        }
    }
}
