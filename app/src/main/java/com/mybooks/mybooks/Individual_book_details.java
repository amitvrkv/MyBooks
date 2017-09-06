package com.mybooks.mybooks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Individual_book_details extends AppCompatActivity {

    private ImageView mBook_image, mBackButton;
    private TextView mBookTitle, mBookPublisher, mBookAuthor, mBookCourse, mBookSem, mBookMRP, mBookNewPrice, mBookOldPrice;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_book_details);

        Bundle bundle = getIntent().getExtras();
        String course = bundle.getString("course");
        String key = bundle.getString("key");

        mBookTitle = (TextView) findViewById(R.id.BookTitle);
        mBookPublisher = (TextView) findViewById(R.id.BookPublisher);
        mBookAuthor = (TextView) findViewById(R.id.BookAuthor);
        mBookCourse = (TextView) findViewById(R.id.BookCourse);
        mBookSem = (TextView) findViewById(R.id.BookSem);
        mBookMRP = (TextView) findViewById(R.id.BookMRP);
        mBookNewPrice = (TextView) findViewById(R.id.BookNewPrice);
        mBookOldPrice = (TextView) findViewById(R.id.BookOldPrice);
        mBook_image = (ImageView) findViewById(R.id.book_image);

        mBackButton = (ImageView) findViewById(R.id.BackButton);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Products").child(key);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*
                BookList bookList = dataSnapshot.getValue(BookList.class);
                mBookTitle.setText("Book Title: " + bookList.getTitle());
                mBookPublisher.setText("Publisher: " + bookList.getPublisher());
                mBookAuthor.setText("Author: " + bookList.getAuthor());
                mBookCourse.setText("Course: " + bookList.getCourse());
                mBookSem.setText("Semester: " + bookList.getSem());
                mBookMRP.setText("Book MRP: \u20B9 " + bookList.getPriceMRP());
                mBookNewPrice.setText("New Book Price: \u20B9 " + bookList.getPriceNew());
                mBookOldPrice.setText("Old Book Price: \u20B9 " + bookList.getPriceOld());

                if (bookList.getSrc().equals("na"))
                    mBook_image.setVisibility(View.GONE);
                else
                    Picasso.with(getApplicationContext()).load(bookList.getSrc()).into(mBook_image);
                    */
                ModelProductList modelProductList = dataSnapshot.getValue(ModelProductList.class);
                mBookTitle.setText("Book Title: " + modelProductList.getF2());
                mBookPublisher.setText("Publisher: " + modelProductList.getF3());
                mBookAuthor.setText("Author: " + modelProductList.getF4());
                mBookCourse.setText("Course: " + modelProductList.getF5());
                mBookSem.setText("Semester: " + modelProductList.getF6());
                mBookMRP.setText("Book MRP: \u20B9 " + modelProductList.getF7());
                mBookNewPrice.setText("New Book Price: \u20B9 " + modelProductList.getF8());
                mBookOldPrice.setText("Old Book Price: \u20B9 " + modelProductList.getF9());

                if (modelProductList.getF13().equals("na"))
                    mBook_image.setVisibility(View.GONE);
                else
                    Picasso.with(getApplicationContext()).load(modelProductList.getF13()).into(mBook_image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
