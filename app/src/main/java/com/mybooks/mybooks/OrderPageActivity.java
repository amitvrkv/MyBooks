package com.mybooks.mybooks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
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
            protected void populateViewHolder(OrderBookListHolder viewHolder, OrderBookList model, int position) {
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


    public static class OrderBookListHolder extends RecyclerView.ViewHolder{

        View mView;

        public OrderBookListHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setOrderId(String id){
            TextView orderId = (TextView) mView.findViewById(R.id.osid);
            orderId.setText("Order ID: " + id);
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
        }

        public void setComment(String comment) {
            TextView mComment = (TextView) mView.findViewById(R.id.oComment);
            mComment.setText("Comment: " + comment);
        }
    }

}

