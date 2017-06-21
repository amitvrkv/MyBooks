package com.mybooks.mybooks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderDetailsActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    FirebaseRecyclerAdapter<OrderDetailsBookList, OrderDetailsViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        recyclerView = (RecyclerView) findViewById(R.id.orderDetailsRecycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle bundle = getIntent().getExtras();
        String orderId = bundle.getString("orderId");

        TextView titleOrderId = (TextView) findViewById(R.id.orderDetialsId);
        titleOrderId.setText("Orders ID: " + orderId);

        ImageView backBtn = (ImageView) findViewById(R.id.oderDetailsBackBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("OrderDetails").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString().replace(".", "*")).child(orderId);


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<OrderDetailsBookList, OrderDetailsViewHolder>(
                OrderDetailsBookList.class,
                R.layout.order_book_list_view,
                OrderDetailsViewHolder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(OrderDetailsViewHolder viewHolder, OrderDetailsBookList model, int position) {
                viewHolder.setTitile(model.getTitle());
                viewHolder.setAuthor(model.getAuthor());
                viewHolder.setQty(model.getQuantity());
                viewHolder.setCourse(model.getCourse());
                viewHolder.setSem(model.getSem());
                viewHolder.setBookType(model.getBooktype());
                viewHolder.setSellingandTotalPrice(model.getPrice(), model.getQuantity());
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    public static class OrderDetailsViewHolder extends RecyclerView.ViewHolder{
        View view;

        public OrderDetailsViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
        }

        public void setTitile(String titile) {
            TextView mTitle = (TextView) view.findViewById(R.id.obookTitle);
            mTitle.setText(titile);
        }

        public void setAuthor(String author) {
            TextView mAuthor = (TextView) view.findViewById(R.id.obookAuthor);
            mAuthor.setText(author);
        }

        public void setQty(String qty) {
            TextView mQty = (TextView) view.findViewById(R.id.oQuantity);
            mQty.setText("Qty: " + qty);
        }

        public void setCourse(String course) {
            TextView mCourse = (TextView) view.findViewById(R.id.obookCourse);
            mCourse.setText(course);
        }

        public void setSem(String sem) {
            TextView mSem = (TextView) view.findViewById(R.id.obookClass);
            mSem.setText(sem);
        }

        public void setBookType(String type) {
            TextView mType = (TextView) view.findViewById(R.id.ocheckBoxNewBook);
            mType.setText("Book Type: " + type);
        }

        public void setSellingandTotalPrice(String sellingPrice, String quantity) {
            TextView mSell = (TextView) view.findViewById(R.id.obookSellingPrice);
            mSell.setText("\u20B9 " + sellingPrice);

            int sell = Integer.parseInt(sellingPrice);
            int qty = Integer.parseInt(quantity);
            int total = sell * qty;

            TextView mTotal = (TextView) view.findViewById(R.id.ototalIndividual);
            mTotal.setText("Total: \u20B9 " + total);
        }
    }


}
