package com.mybooks.mybooks;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class OrderPageActivity extends AppCompatActivity {

    FirebaseRecyclerAdapter<OrderBookList, OrderBookListHolder> firebaseRecyclerAdapter;

    FirebaseRecyclerAdapter<ModelClassCustomOrder, CustomOrderBookListHolder> firebaseRecyclerAdapterCustomOrder;

    Query databaseReference;

    RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_page);
        setToolbar();

        recyclerView = (RecyclerView) findViewById(R.id.orderRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setDataForMyOrder();

        TextView myOrderTab = (TextView) findViewById(R.id.myOrderTab);
        final View myOrderActiveLine = findViewById(R.id.myOrderActiveLine);
        TextView customiseOrderTab = (TextView) findViewById(R.id.customiseOrderTab);
        final View customiseOrderActiveLine = findViewById(R.id.customiseOrderActiveLine);
        myOrderTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myOrderActiveLine.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                customiseOrderActiveLine.setBackgroundColor(getResources().getColor(R.color.Light_Grey));
                setDataForMyOrder();
            }
        });

        customiseOrderTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myOrderActiveLine.setBackgroundColor(getResources().getColor(R.color.Light_Grey));
                customiseOrderActiveLine.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                setDataForCustomOrder();
            }
        });

    }

    public void setToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("My Orders");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setDataForMyOrder() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Order").orderByChild("from").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<OrderBookList, OrderBookListHolder>(
                OrderBookList.class,
                R.layout.order_status_list_view,
                OrderBookListHolder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(OrderBookListHolder viewHolder, final OrderBookList model, int position) {
                viewHolder.setOrderId(model.getOrderid());
                viewHolder.setDate(model.getDate());
                viewHolder.setTotal(model.getTotal());
                viewHolder.setDeliveryCharge(model.getDeliverycharge());
                viewHolder.setDiscount(model.getDiscount());
                viewHolder.setGrandTotal(model.getGrandtotal());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setComment(model.getComment());
                viewHolder.setPaymentMode(model.getPaymentmode());
            }
        };

        mLayoutManager = new LinearLayoutManager(OrderPageActivity.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public void setDataForCustomOrder() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("OrderCustom").orderByChild("from").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());

        firebaseRecyclerAdapterCustomOrder = new FirebaseRecyclerAdapter<ModelClassCustomOrder, CustomOrderBookListHolder>(
                ModelClassCustomOrder.class,
                R.layout.ordercustom_status_list_view,
                CustomOrderBookListHolder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(CustomOrderBookListHolder viewHolder, ModelClassCustomOrder model, int position) {
                viewHolder.setOrderId(model.getOrderid());
                viewHolder.setDate(model.getDate());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setTitle(model.getTitle());
                viewHolder.setAuthor(model.getAuthor());
                viewHolder.setComment(model.getComment());
                viewHolder.setGetDetails(model.getdetails);
            }
        };

        mLayoutManager = new LinearLayoutManager(OrderPageActivity.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(firebaseRecyclerAdapterCustomOrder);
    }

    public static class OrderBookListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View mView;

        String ordId;
        String comments;

        TextView mcancelOrderBtn;

        public OrderBookListHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mcancelOrderBtn = (TextView) itemView.findViewById(R.id.cancelOrder);
            mcancelOrderBtn.setOnClickListener(this);

            TextView mGetOrderDetailsBtn = (TextView) itemView.findViewById(R.id.getOrderDetails);
            mGetOrderDetailsBtn.setOnClickListener(this);

            TextView mAddComments = (TextView) itemView.findViewById(R.id.oAddCommentsBtn);
            mAddComments.setOnClickListener(this);
        }

        public void setOrderId(String id) {
            TextView orderId = (TextView) mView.findViewById(R.id.osid);
            orderId.setText("Order ID: " + id);
            this.ordId = id;
        }

        public void setDate(String date) {
            TextView mDate = (TextView) mView.findViewById(R.id.odate);
            mDate.setText("Data and Time: " + date);
        }

        public void setTotal(String amt) {
            TextView total = (TextView) mView.findViewById(R.id.ototal);
            total.setText("Total Amount: \u20B9 " + amt);
        }

        public void setDeliveryCharge(String amt) {
            TextView odelivery = (TextView) mView.findViewById(R.id.odelivery);
            odelivery.setText("Delivery Charge: \u20B9 " + amt);
        }

        public void setDiscount(String amt) {
            TextView odiscount = (TextView) mView.findViewById(R.id.odiscount);
            odiscount.setText("Discount: \u20B9 " + amt);
        }

        public void setGrandTotal(String amt) {
            TextView grand_total = (TextView) mView.findViewById(R.id.ograndtotal);
            grand_total.setText("Grand Amount: \u20B9 " + amt);
        }

        public void setPaymentMode(String pm) {
            TextView mPaymentMode = (TextView) mView.findViewById(R.id.oPaymentMode);
            mPaymentMode.setText("Payment mode: " + pm);
        }

        public void setStatus(String status) {
            TextView mStatus = (TextView) mView.findViewById(R.id.oStatus);
            mStatus.setText("Order Status: " + status);

            TextView mcancelOrderBtn = (TextView) itemView.findViewById(R.id.cancelOrder);
            if (status.contains("cancelled") || status.contains("Delivered")) {
                mcancelOrderBtn.setText("CANCELLED");
                mcancelOrderBtn.setEnabled(false);
            } else {
                mcancelOrderBtn.setText("CANCEL ORDER");
            }
        }

        public void setComment(String comment) {
            TextView mComment = (TextView) mView.findViewById(R.id.oComment);
            if (comment.equals(""))
                mComment.setText(comment);
            else
                mComment.setText(comment.replace("EEEE", "ME"));
            this.comments = comment;
        }

        @Override
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.cancelOrder:

                    if (mcancelOrderBtn.getText().toString().equals("CANCEL ORDER")) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(mView.getContext());
                        final EditText edittext = new EditText(mView.getContext());
                        alert.setTitle("Enter reason");
                        alert.setView(edittext);

                        alert.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String reason = edittext.getText().toString();
                                if (TextUtils.isEmpty(reason)) {
                                    Toast.makeText(mView.getContext(), "Please provide reason for cancellation", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Order").child(ordId);
                                databaseReference.child("comment").setValue(comments + "Customer: " + "Order cancelled" + " (" + reason + ")\n");
                                databaseReference.child("status").setValue(v.getContext().getString(R.string.order_cancelled)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(mView.getContext(), "Order cancelled", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(mView.getContext(), "Failed to update status", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        });

                        alert.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        });

                        alert.show();

                    }
                    /*else if (mcancelOrderBtn.getText().toString().equals("DELETE ORDER")) {
                        DatabaseReference orderDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Order").child(ordId);
                        orderDatabaseReference.removeValue();

                        DatabaseReference detailsDatabaseReference = FirebaseDatabase.getInstance().getReference()
                                .child("OrderDetails")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".","*"))
                                .child(ordId);
                        detailsDatabaseReference.removeValue();

                        Toast.makeText(v.getContext(), "Order can not be deleted", Toast.LENGTH_SHORT).show();

                    }*/
                    break;

                case R.id.getOrderDetails:
                    Intent intent = new Intent(mView.getContext(), OrderDetailsActivity.class);
                    intent.putExtra("orderId", ordId);
                    mView.getContext().startActivity(intent);
                    break;

                case R.id.oAddCommentsBtn:
                    AlertDialog.Builder alert = new AlertDialog.Builder(mView.getContext());
                    final EditText edittext = new EditText(mView.getContext());
                    alert.setTitle("Enter comment");
                    alert.setView(edittext);
                    alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String reason = edittext.getText().toString();
                            if (TextUtils.isEmpty(reason))
                                return;

                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Order").child(ordId);
                            databaseReference.child("comment").setValue(comments + "EEEE: " + reason + "\n")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(mView.getContext(), "Comment updated", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(mView.getContext(), "Failed to update comment", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                    });

                    alert.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });
                    alert.show();
                    break;
            }
        }
    }

    public static class CustomOrderBookListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View mView;

        String ordId;
        String comments;
        String getdetails;

        TextView mcancelOrderBtn;

        public CustomOrderBookListHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mcancelOrderBtn = (TextView) itemView.findViewById(R.id.cancelOrder);
            mcancelOrderBtn.setOnClickListener(this);

            TextView mGetOrderDetailsBtn = (TextView) itemView.findViewById(R.id.getOrderDetails);
            mGetOrderDetailsBtn.setOnClickListener(this);

            TextView mAddComments = (TextView) itemView.findViewById(R.id.oAddCommentsBtn);
            mAddComments.setOnClickListener(this);
        }

        public void setOrderId(String id) {
            TextView orderId = (TextView) mView.findViewById(R.id.osid);
            orderId.setText("Order ID:  " + id);

            this.ordId = id;
        }

        public void setDate(String date) {
            TextView mDate = (TextView) mView.findViewById(R.id.odate);
            mDate.setText("Data and Time:  " + date);
        }

        public void setStatus(String status) {
            TextView oStatus = (TextView) mView.findViewById(R.id.oStatus);
            oStatus.setText("Status:  " + status);

            TextView mcancelOrderBtn = (TextView) itemView.findViewById(R.id.cancelOrder);
            if (status.contains("cancelled") || status.contains("Delivered")) {
                mcancelOrderBtn.setText("CANCELLED");
                mcancelOrderBtn.setEnabled(false);
            } else {
                mcancelOrderBtn.setText("CANCEL ORDER");
            }
        }

        public void setTitle(String title) {
            TextView otitle = (TextView) mView.findViewById(R.id.otitle);
            otitle.setText("Title:  " + title);
        }

        public void setAuthor(String author) {
            TextView oAuthor = (TextView) mView.findViewById(R.id.oAuthor);
            oAuthor.setText("Author:  " + author);
        }

        public void setComment(String comment) {
            TextView mComment = (TextView) mView.findViewById(R.id.oComment);
            if (comment.equals(""))
                mComment.setText(comment);
            else
                mComment.setText(comment.replace("EEEE", "ME"));
            this.comments = comment;
        }

        public void setGetDetails(String getdetails) {
            TextView getOrderDetails = (TextView) mView.findViewById(R.id.getOrderDetails);
            if (getdetails.equalsIgnoreCase("na")) {
                getOrderDetails.setEnabled(false);
            } else {
                getOrderDetails.setEnabled(true);
                getOrderDetails.setOnClickListener(this);
                this.getdetails = getdetails;
            }
        }

        @Override
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.cancelOrder:
                    if (mcancelOrderBtn.getText().toString().equals("CANCEL ORDER")) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(mView.getContext());
                        final EditText edittext = new EditText(mView.getContext());
                        alert.setTitle("Enter reason");
                        alert.setView(edittext);

                        alert.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String reason = edittext.getText().toString();
                                if (TextUtils.isEmpty(reason)) {
                                    Toast.makeText(mView.getContext(), "Please provide reason for cancellation", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("OrderCustom").child(ordId);
                                databaseReference.child("comment").setValue(comments + "Customer: " + "Order cancelled" + " (" + reason + ")\n");
                                databaseReference.child("status").setValue(v.getContext().getString(R.string.order_cancelled)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(mView.getContext(), "Order cancelled", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(mView.getContext(), "Failed to update status", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        });

                        alert.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        });

                        alert.show();
                    }
                case R.id.oAddCommentsBtn:
                    AlertDialog.Builder alert = new AlertDialog.Builder(mView.getContext());
                    final EditText edittext = new EditText(mView.getContext());
                    alert.setTitle("Enter comment");
                    alert.setView(edittext);
                    alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String reason = edittext.getText().toString();
                            if (TextUtils.isEmpty(reason))
                                return;

                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("OrderCustom").child(ordId);
                            databaseReference.child("comment").setValue(comments + "EEEE: " + reason + "\n")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(mView.getContext(), "Comment updated", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(mView.getContext(), "Failed to update comment", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                    });

                    alert.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });
                    alert.show();
                    break;

                case R.id.getOrderDetails:
                    Intent intent = new Intent(v.getContext(), Individual_book_details.class);
                    intent.putExtra("key", getdetails);
                    v.getContext().startActivity(intent);
                    break;
            }
        }


    }

}

