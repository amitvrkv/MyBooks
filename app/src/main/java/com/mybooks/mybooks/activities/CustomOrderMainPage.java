package com.mybooks.mybooks.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.mybooks.mybooks.R;

public class CustomOrderMainPage extends AppCompatActivity implements View.OnClickListener {

    private Button addItemBtn;
    private Button placeOrderBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_order_main_page);
        setToolbar();

        addItemBtn = (Button) findViewById(R.id.addItemBtn);
        addItemBtn.setOnClickListener(this);
        placeOrderBtn = (Button) findViewById(R.id.placeOrderBtn);
        placeOrderBtn.setOnClickListener(this);
    }

    public void setToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Custom Order");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addItemBtn:

                    Intent intent = new Intent(this, CustomOrderActivity.class);
                    intent.putExtra("key", "null");
                    startActivity(intent);

                break;

            case R.id.placeOrderBtn:

                break;
        }
    }
}
