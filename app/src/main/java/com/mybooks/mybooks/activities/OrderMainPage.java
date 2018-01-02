package com.mybooks.mybooks.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mybooks.mybooks.R;
import com.mybooks.mybooks.models.ModelClassCustomOrder;
import com.mybooks.mybooks.models.ModelClassOrderMainPage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderMainPage extends AppCompatActivity {

    FirebaseRecyclerAdapter<ModelClassOrderMainPage, OrderMainPage.OrderBookListHolder> firebaseRecyclerAdapter;
    FirebaseRecyclerAdapter<ModelClassCustomOrder, OrderMainPage.CustomOrderBookListHolder> firebaseRecyclerAdapterCustomOrder;

    private RecyclerView recyclerView;
    private Query databaseReference;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_main_page);
        setToolbar();

        recyclerView = (RecyclerView) findViewById(R.id.orderMainPageRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setDataForMyOrder();


        TextView myOrderTab = (TextView) findViewById(R.id.orderMainPageMyOrderTab);
        final View myOrderActiveLine = findViewById(R.id.myOrderMainPageActiveLine);
        TextView customiseOrderTab = (TextView) findViewById(R.id.orderMainPageCustomiseOrderTab);
        final View customiseOrderActiveLine = findViewById(R.id.customiseOrderMainPageActiveLine);
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
        getSupportActionBar().setTitle("Orders");
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
        databaseReference = FirebaseDatabase.getInstance().getReference().child("ORDER").child("MYORDER").orderByChild("from").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ModelClassOrderMainPage, OrderBookListHolder>(
                ModelClassOrderMainPage.class,
                R.layout.order_status_list_view_2,
                OrderBookListHolder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(OrderBookListHolder viewHolder, ModelClassOrderMainPage model, int position) {
                viewHolder.setOrderId(model.getOrderid());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setGrandTotal(model.getTotal());
                viewHolder.setDate(model.getDate());
            }
        };

        mLayoutManager = new LinearLayoutManager(OrderMainPage.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    public void setDataForCustomOrder() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("ORDER").child("CUSTOMORDER").orderByChild("from").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());

        firebaseRecyclerAdapterCustomOrder = new FirebaseRecyclerAdapter<ModelClassCustomOrder, OrderMainPage.CustomOrderBookListHolder>(
                ModelClassCustomOrder.class,
                R.layout.order_status_list_view_2,
                OrderMainPage.CustomOrderBookListHolder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(OrderMainPage.CustomOrderBookListHolder viewHolder, ModelClassCustomOrder model, int position) {
                viewHolder.setOrderId(model.getOrderid());
                viewHolder.setStatus(model.getStatus());
                //viewHolder.setGrandTotal(model.getTotal());
                viewHolder.setDate(model.getDate());
            }
        };

        mLayoutManager = new LinearLayoutManager(OrderMainPage.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(firebaseRecyclerAdapterCustomOrder);
    }

    public static class OrderBookListHolder extends RecyclerView.ViewHolder {

        View mView;
        String orderId = null;

        public OrderBookListHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(mView.getContext(), "Clicked", Toast.LENGTH_SHORT).show();
                    loadBookDetails();
                }
            });
        }


        public void setOrderId(String od_id) {
            /*
            int len = od_id.length();
            int setLen = 10 - len;
            String final_order_id = "0000000000";
            final_order_id = "OD" + final_order_id.substring(0, setLen) + od_id;
            */

            TextView order_id = (TextView) mView.findViewById(R.id.order_id);
            order_id.setText(od_id);

            this.orderId = od_id;
        }

        public void setStatus(String status) {
            TextView mStatus = (TextView) mView.findViewById(R.id.order_status);
            mStatus.setText(status);
            //ImageView status_color = (ImageView) mView.findViewById(R.id.status_color);
        }

        public void setGrandTotal(String grand_total) {
            TextView mGrand_total = (TextView) mView.findViewById(R.id.order_grant_total);
            mGrand_total.setText("₹ " + grand_total);
        }

        public void setDate(String mDate) {
            SimpleDateFormat form = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            Date date = null;
            try {
                date = form.parse(mDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat postFormater = new SimpleDateFormat("dd");
            String newDateStr = postFormater.format(date);
            TextView day = (TextView) mView.findViewById(R.id.order_date);
            day.setText(newDateStr);

            postFormater = new SimpleDateFormat("MMM");
            newDateStr = postFormater.format(date);
            TextView month = (TextView) mView.findViewById(R.id.order_month);
            month.setText(newDateStr);
        }

        public void loadBookDetails() {
            Intent intent = new Intent(mView.getContext(), OrderDetailsActivity.class);
            intent.putExtra("orderType", "OD");
            intent.putExtra("orderId", orderId);
            mView.getContext().startActivity(intent);
        }
    }

    public static class CustomOrderBookListHolder extends RecyclerView.ViewHolder {

        View mView;
        String orderId = null;

        public CustomOrderBookListHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(mView.getContext(), "Clicked", Toast.LENGTH_SHORT).show();
                    loadBookDetails();
                }
            });
        }

        public void setOrderId(String od_id) {
            TextView order_id = (TextView) mView.findViewById(R.id.order_id);
            order_id.setText(od_id);
            this.orderId = od_id;
        }

        public void setStatus(String status) {
            TextView mStatus = (TextView) mView.findViewById(R.id.order_status);
            mStatus.setText(status);
            //ImageView status_color = (ImageView) mView.findViewById(R.id.status_color);
        }

        public void setGrandTotal(String grand_total) {
            TextView mGrand_total = (TextView) mView.findViewById(R.id.order_grant_total);
            mGrand_total.setText("₹ " + grand_total);
        }

        public void setDate(String mDate) {
            SimpleDateFormat form = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            Date date = null;
            try {
                date = form.parse(mDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat postFormater = new SimpleDateFormat("dd");
            String newDateStr = postFormater.format(date);
            TextView day = (TextView) mView.findViewById(R.id.order_date);
            day.setText(newDateStr);

            postFormater = new SimpleDateFormat("MMM");
            newDateStr = postFormater.format(date);
            TextView month = (TextView) mView.findViewById(R.id.order_month);
            month.setText(newDateStr);
        }

        public void loadBookDetails() {
            Intent intent = new Intent(mView.getContext(), OrderCustomDetailsActivity.class);
            intent.putExtra("orderType", "CD");
            intent.putExtra("orderId", orderId);
            mView.getContext().startActivity(intent);
        }
    }
}
