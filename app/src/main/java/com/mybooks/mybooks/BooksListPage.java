package com.mybooks.mybooks;

import android.media.Image;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class BooksListPage extends AppCompatActivity implements View.OnClickListener{

    private ImageView mbackButton;
    private ListView mbookList;
    private LinearLayout mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_list_page);

        mbookList = (ListView) findViewById(R.id.booksList);
        mToolbar = (LinearLayout) findViewById(R.id.toolbar);

        mbackButton = (ImageView) findViewById(R.id.backButton);
        mbackButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backButton:
                finish();
                break;
        }
    }
}
