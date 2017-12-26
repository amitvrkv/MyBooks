package com.mybooks.mybooks.activities;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mybooks.mybooks.R;

public class CustomOrderAddProduct extends AppCompatActivity implements View.OnClickListener {

    private EditText customTitle;
    private EditText customAuthor;
    private EditText customPublisher;
    private EditText customCourse;
    private EditText customMRP;
    private RadioGroup customBooktype;
    private RadioButton customOld;
    private RadioButton customNew;
    private TextView customEstimatedPrice;
    private EditText customDescription;
    private Button customAddBookBtn;

    private String title;
    private String author;
    private String publisher;
    private String course;
    private String bookMRP;
    private String bookType;
    private String estimatedPrice;
    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_order_add_product);

        customTitle = (EditText) findViewById(R.id.customTitle);
        customAuthor = (EditText) findViewById(R.id.customAuthor);
        customPublisher = (EditText) findViewById(R.id.customPublisher);
        customCourse = (EditText) findViewById(R.id.customCourse);
        customMRP = (EditText) findViewById(R.id.customMRP);
        customBooktype = (RadioGroup) findViewById(R.id.customBooktype);
        customOld = (RadioButton) findViewById(R.id.customOld);
        customNew = (RadioButton) findViewById(R.id.customNew);
        final TextView customEstimatedPrice = (TextView) findViewById(R.id.customEstimatedPrice);
        customDescription = (EditText) findViewById(R.id.customDescription);
        customAddBookBtn = (Button) findViewById(R.id.customAddBookBtn);
        customAddBookBtn.setOnClickListener(this);

        customEstimatedPrice.setSelected(true);

        customMRP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(customMRP.getText())) {
                    customEstimatedPrice.setText("Enter MRP printed on book to know estimated price");
                    bookMRP = "0";
                    estimatedPrice = "0";
                    return;
                }

                bookMRP = customMRP.getText().toString().trim();

                int mrp = Integer.parseInt(customMRP.getText().toString());
                int estPrice = 0;

                if (customOld.isChecked()) {
                    estPrice = mrp - mrp * 30 / 100;
                    customEstimatedPrice.setText("\u20B9 " + estPrice);
                }
                if (customNew.isChecked()) {
                    estPrice = mrp - mrp * 15 / 100;
                    customEstimatedPrice.setText("\u20B9 " + estPrice);
                }

                estimatedPrice = String.valueOf(estPrice);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        customBooktype.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (TextUtils.isEmpty(customMRP.getText())) {
                    customEstimatedPrice.setText("Enter MRP printed on book to know estimated price");
                    bookMRP = "0";
                    estimatedPrice = "0";
                    return;
                }

                int mrp = Integer.parseInt(customMRP.getText().toString());

                if (checkedId == customOld.getId()) {
                    customEstimatedPrice.setText("" + (mrp - mrp * 30 / 100));
                }
                if (checkedId == customNew.getId()) {
                    customEstimatedPrice.setText("" + (mrp - mrp * 15 / 100));
                }
            }
        });

    }

    public void setToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Add Product");
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
        if (v == customAddBookBtn) {
            verifyData();
        }
    }

    public void verifyData() {
        customTitle.setError(null);
        customAuthor.setError(null);

        if (TextUtils.isEmpty(customTitle.getText())) {
            customTitle.setError("Enter book title");
            return;
        } else {
            title = customTitle.getText().toString().trim();
        }

        if (TextUtils.isEmpty(customAuthor.getText())) {
            customAuthor.setError("Enter book author");
            return;
        } else {
            author = customAuthor.getText().toString().trim();
        }

        if (TextUtils.isEmpty(customPublisher.getText())) {
            customPublisher.setError("Enter book publisher");
            return;
        } else {
            publisher = customPublisher.getText().toString().trim();
        }

        if (TextUtils.isEmpty(customCourse.getText())) {
            customCourse.setError("Enter book course");
            return;
        } else {
            course = customCourse.getText().toString().trim();
        }

        if (!(customOld.isChecked() || customNew.isChecked())) {
            Toast.makeText(getApplicationContext(), "Please select BOOK TYPE", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (customOld.isChecked())
                bookType = "Old";
            else if (customNew.isChecked())
                bookType = "New";
        }

        if (TextUtils.isEmpty(customDescription.getText())) {
            description = "na";
        } else {
            description = customDescription.getText().toString().trim();
        }

        addProductToDatabase();
    }

    public void addProductToDatabase() {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(this.getString(R.string.database_path), null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS CUSTOM_BOOK (kkey VARCHAR, title VARCHAR, author VARCHAR, publisher VARCHAR, course VARCHAR, mrp VARCHAR, bookType VARCHAR, estPrice VARCHAR, description VARCHAR, qty VARCAHR, total VARCHAR);");
        sqLiteDatabase.execSQL("INSERT INTO CUSTOM_BOOK VALUES('" + title + author + "','" + title + "','" + author + "','" + publisher + "','" + course + "','" + bookMRP + "','" + bookType + "','" + estimatedPrice + "','" + description + "','" + "1" + "','" + estimatedPrice + "');");

        Toast.makeText(getApplicationContext(), "Book added successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
