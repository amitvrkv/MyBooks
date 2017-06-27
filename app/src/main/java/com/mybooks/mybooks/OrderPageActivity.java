package com.mybooks.mybooks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class OrderPageActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mOderBackBtn;

    FirebaseRecyclerAdapter<OrderBookList, OrderBookListHolder> firebaseRecyclerAdapter;

    Query databaseReference;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_page);

        mOderBackBtn = (ImageView) findViewById(R.id.oderBackBtn);
        mOderBackBtn.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.orderRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
                viewHolder.setAmount(model.getGrandtotal());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setComment(model.getComments());
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.oderBackBtn:
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                break;
        }
    }


    public static class OrderBookListHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        View mView;

        String ordId;

        TextView mcancelOrderBtn;

        public OrderBookListHolder(View itemView) {
            super(itemView);
            mView = itemView;

             mcancelOrderBtn = (TextView) itemView.findViewById(R.id.cancelOrder);
            mcancelOrderBtn.setOnClickListener(this);

            TextView mGetOrderDetailsBtn = (TextView) itemView.findViewById(R.id.getOrderDetails);
            mGetOrderDetailsBtn.setOnClickListener(this);
        }

        public void setOrderId(String id){
            TextView orderId = (TextView) mView.findViewById(R.id.osid);
            orderId.setText("Order ID: " + id);
            this.ordId = id;
        }

        public void setDate(String date) {
            TextView mDate = (TextView) mView.findViewById(R.id.odate);
            mDate.setText("Data and Time: " + date);
        }

        public void setAmount(String amt) {
            TextView mAmt = (TextView) mView.findViewById(R.id.oamount);
            mAmt.setText("Total Amount: " + amt);
        }

        public void setStatus(String status) {
            TextView mStatus = (TextView) mView.findViewById(R.id.oStatus);
            mStatus.setText("Order Status: " + status);

            TextView mcancelOrderBtn = (TextView) itemView.findViewById(R.id.cancelOrder);
            if (status.contains("cancelled") || status.contains("delivered")) {
                mcancelOrderBtn.setText("DELETE ORDER");
            } else {
                mcancelOrderBtn.setText("CANCEL ORDER");
            }
        }

        public void setComment(String comment) {
            TextView mComment = (TextView) mView.findViewById(R.id.oComment);
            mComment.setText("Comment: " + comment);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cancelOrder:

                    if (mcancelOrderBtn.getText().toString().equals("CANCEL ORDER")) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Order").child(ordId).child("status");
                        databaseReference.setValue("Order cancelled by user.");
                        Toast.makeText(v.getContext(), "Order cancelled", Toast.LENGTH_SHORT).show();
                    } else if (mcancelOrderBtn.getText().toString().equals("DELETE ORDER")){
                        DatabaseReference orderDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Order").child(ordId);
                        orderDatabaseReference.removeValue();

                        DatabaseReference detailsDatabaseReference = FirebaseDatabase.getInstance().getReference()
                                .child("OrderDetails")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".","*"))
                                .child(ordId);
                        detailsDatabaseReference.removeValue();

                        Toast.makeText(v.getContext(), "Order deleted", Toast.LENGTH_SHORT).show();

                    }
                    break;

                case R.id.getOrderDetails:
                    Intent intent = new Intent(mView.getContext(), OrderDetailsActivity.class);
                    intent.putExtra("orderId", ordId);
                    mView.getContext().startActivity(intent);
                    break;
            }
        }
    }

}

