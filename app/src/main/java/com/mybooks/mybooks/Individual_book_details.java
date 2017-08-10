package com.mybooks.mybooks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class Individual_book_details extends AppCompatActivity {

    private ImageView book_image;
    private TextView close_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_book_details);

        Bundle bundle = getIntent().getExtras();
        String src = bundle.getString("src");

        book_image = (ImageView) findViewById(R.id.book_image);
        Picasso.with(getApplicationContext()).load(src).into(book_image);

        close_btn = (TextView) findViewById(R.id.close_btn);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
