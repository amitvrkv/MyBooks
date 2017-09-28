package com.mybooks.mybooks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CustomOrderActivity extends AppCompatActivity {

    private TextView mtitle;
    private TextView mAuthor;
    private TextView mPublisher;
    private TextView mCourse;
    private TextView mDesc;
    private TextView mPlaceOrderBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_order);
        //setToolbar();

        mtitle = (TextView) findViewById(R.id.customTitle);
        mAuthor = (TextView) findViewById(R.id.customAuthor);
        mPublisher = (TextView) findViewById(R.id.customPublisher);
        mCourse = (TextView) findViewById(R.id.customCourse);
        mDesc = (TextView) findViewById(R.id.customDesc);

        mPlaceOrderBtn = (TextView) findViewById(R.id.customPlaceOrder);
        mPlaceOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mtitle.setError(null);
                mAuthor.setError(null);
                mCourse.setError(null);
                mDesc.setError(null);

                if (TextUtils.isEmpty(mtitle.getText().toString())) {
                    mtitle.setError("This field is required.");
                    return;
                } else if (TextUtils.isEmpty(mAuthor.getText().toString())) {
                    mAuthor.setError("This field is required.");
                    return;
                } else if (TextUtils.isEmpty(mPublisher.getText().toString())) {
                    mPublisher.setError("This field is required.");
                    return;
                } else if (TextUtils.isEmpty(mCourse.getText().toString())) {
                    mCourse.setError("This field is required.");
                    return;
                } else if (TextUtils.isEmpty(mDesc.getText().toString())) {
                    mDesc.setError("This field is required.");
                    return;
                }
                placeCustomOrde(mtitle.getText().toString(), mAuthor.getText().toString(), mPublisher.getText().toString(), mCourse.getText().toString(), mDesc.getText().toString());
            }
        });
    }

    public void setToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Customise Order");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void placeCustomOrde(String title, String author, String publisher, String course, String desc) {
        Toast.makeText(getApplicationContext(), "Customise order feature coming soon", Toast.LENGTH_SHORT).show();
    }
}
