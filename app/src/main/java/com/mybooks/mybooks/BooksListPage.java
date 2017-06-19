package com.mybooks.mybooks;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BooksListPage extends AppCompatActivity implements View.OnClickListener {

    private ImageView mbackButton;
    private LinearLayout mToolbar;

    private Button mdoneFilter, mclearFilter;
    private TextView mfilter, mcheckout;
    private RelativeLayout filterView;
    private Spinner mcousrseSelecter, msemSelecter;

    private RecyclerView mBookList;

    //private DatabaseReference mdatabaseQuery;
    private Query mdatabaseQuery;
    private FirebaseRecyclerAdapter<BookList, BookListHolder> firebaseRecyclerAdapter;
    //private DatabaseReference mdatabaseReference;

    //SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_list_page);

        //sqLiteDatabase = openOrCreateDatabase("MYBOOKS", MODE_PRIVATE, null);

        ///////filter start
        mfilter = (TextView) findViewById(R.id.filter);
        mfilter.setOnClickListener(this);

        mcheckout = (TextView) findViewById(R.id.checkout);
        mcheckout.setOnClickListener(this);

        mdoneFilter = (Button) findViewById(R.id.doneFilter);
        mdoneFilter.setOnClickListener(this);

        filterView = (RelativeLayout) findViewById(R.id.filterView);
        //filterView.setVisibility(View.GONE);

        //Course selector
        mcousrseSelecter = (Spinner) findViewById(R.id.courseSelecter);
        List<String> courseList = new ArrayList<String>();
        courseList.add("BCA");
        courseList.add("BCOM");
        courseList.add("BBM");
        courseList.add("BBA");
        courseList.add("BHM");
        Collections.sort(courseList);
        courseList.add(0, "select Course");
        ArrayAdapter<String> courseDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, courseList);
        courseDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mcousrseSelecter.setAdapter(courseDataAdapter);

        //Semester selector
        msemSelecter = (Spinner) findViewById(R.id.semesterSelecter);
        List<String> semList = new ArrayList<String>();
        semList.add(0, "select Semester");
        semList.add(1, "All Semester");
        semList.add("1st sem");
        semList.add("2nd sem");
        semList.add("3rd sem");
        semList.add("4th sem");
        semList.add("5th sem");
        semList.add("6th sem");
        semList.add("7th sem");
        semList.add("8th sem");
        semList.add("9th sem");
        semList.add("10th sem");
        ArrayAdapter<String> semDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, semList);
        semDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        msemSelecter.setAdapter(semDataAdapter);
        /////filter end


        //Fire base
        mdatabaseQuery = FirebaseDatabase.getInstance().getReference().child("Books");
        //mdatabaseQuery.orderByChild("course").equalTo("BCA");
        mdatabaseQuery.keepSynced(true);

        mToolbar = (LinearLayout) findViewById(R.id.toolbar);

        mbackButton = (ImageView) findViewById(R.id.backButton);
        mbackButton.setOnClickListener(this);

        mBookList = (RecyclerView) findViewById(R.id.recyclerView);
        mBookList.setHasFixedSize(true);
        mBookList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(getApplicationContext(), "Please select your course", Toast.LENGTH_SHORT).show();
    }


    public static class BookListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View mview;
        Button buyButton;

        String title;
        String author;
        String course;
        String sem;
        String key;
        String priceMRP;
        String priceOld;
        String priceNew;

        public BookListHolder(View itemView) {
            super(itemView);
            mview = itemView;

            buyButton = (Button) itemView.findViewById(R.id.bookBuy);
            buyButton.setOnClickListener(this);
        }

        public void settitle(String title) {
            TextView mtitle = (TextView) mview.findViewById(R.id.bookTitle);
            mtitle.setText(title);
            this.title = title;
        }

        public void setauthor(String author) {
            TextView mauthor = (TextView) mview.findViewById(R.id.bookAuthor);
            mauthor.setText(author);
            this.author = author;
        }

        public void setCourse(String course) {
            TextView mcourse = (TextView) mview.findViewById(R.id.bookCourse);
            mcourse.setText(course);
            this.course = course;
        }

        public void setSem(String sem) {
            TextView msem = (TextView) mview.findViewById(R.id.bookClass);
            msem.setText(sem);
            this.sem = sem;
        }

        public void setpriceMRP(String priceMRP) {
            TextView mmarket = (TextView) mview.findViewById(R.id.bookMarketPrice);
            mmarket.setText(priceMRP);
            this.priceMRP = priceMRP;
        }

        public void setpriceOld(String priceOld) {
            TextView msell = (TextView) mview.findViewById(R.id.bookSellingPrice);
            msell.setText("\u20B9 " + priceOld);
            this.priceOld = priceOld;
        }

        public void setavlcopy(int mavlcopy) {
            ImageView outofstockimg = (ImageView) mview.findViewById(R.id.outofstock);
            Button mbookbuy = (Button) mview.findViewById(R.id.bookBuy);

            if (mavlcopy <= 0) {
                outofstockimg.setVisibility(View.VISIBLE);
                mbookbuy.setVisibility(View.GONE);
            } else {
                outofstockimg.setVisibility(View.GONE);
                mbookbuy.setVisibility(View.VISIBLE);
            }
        }

        public void setKey(String key) {
            this.key = key;
        }

        public void setPriceNew(String priceNew) {
            this.priceNew = priceNew;
        }

        //Adding books to cart
        @Override
        public void onClick(View v) {
            if (v.getId() == buyButton.getId()) {
                BooksListPage bk = new BooksListPage();
                bk.insertDataToCart(v.getContext(), key, title, author, course, sem, priceMRP, priceNew, priceOld);
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backButton:
                finish();
                break;

            case R.id.filter:
                if (filterView.getVisibility() == View.VISIBLE) {
                    if (mcousrseSelecter.getSelectedItem().toString().equals("select Course"))
                        Toast.makeText(getApplicationContext(), "Please select your course", Toast.LENGTH_SHORT).show();
                    else {
                        filterView.setVisibility(View.GONE);
                        doneFilter();
                    }
                } else {
                    filterView.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.doneFilter:
                doneFilter();
                break;


            case R.id.checkout:
                startActivity(new Intent(getApplicationContext(), MyCart.class));
                break;
        }
    }

    public void doneFilter() {
        String course = mcousrseSelecter.getSelectedItem().toString();
        final String sem = msemSelecter.getSelectedItem().toString();

        if (course.equals("select Course")) {
            Toast.makeText(getApplicationContext(), "Please select your course", Toast.LENGTH_SHORT).show();
            return;
        } else {
            filterView.setVisibility(View.GONE);
            if (sem.equals("select Semester") || sem.equals("All Semester"))
                mdatabaseQuery = FirebaseDatabase.getInstance().getReference().child("Books").child(course);
            else
                mdatabaseQuery = FirebaseDatabase.getInstance().getReference().child("Books").child(course).orderByChild("sem").equalTo(sem);
        }

        mdatabaseQuery.keepSynced(true);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<BookList, BookListHolder>(
                BookList.class,
                R.layout.books_list_view,
                BookListHolder.class,
                mdatabaseQuery
        ) {
            @Override
            protected void populateViewHolder(BookListHolder viewHolder, final BookList model, int position) {
                viewHolder.setauthor(model.getAuthor());
                viewHolder.settitle(model.getTitle());
                viewHolder.setCourse(model.getCourse());
                viewHolder.setSem(model.getSem());
                viewHolder.setpriceMRP(model.getPriceMRP());
                viewHolder.setpriceOld(model.getPriceOld());
                viewHolder.setPriceNew(model.getPriceNew());
                viewHolder.setavlcopy(model.getAvlcopy());
                viewHolder.setKey(model.getKey());

                // OnclickListener on recycler view redirect to book preview page
                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(getApplicationContext(), "Selected : " + model.getCourse() +"and "+ model.getKey(), Toast.LENGTH_SHORT).show();
                        //insertDataToCart(model.getKey(), model.getTitle(), model.getAuthor(), model.getCourse(), model.getSem(), model.getPriceMRP(), model.getPriceNew(), model.getPriceOld());
                    }
                });
            }
        };
        mBookList.setAdapter(firebaseRecyclerAdapter);
    }

    public void insertDataToCart(Context ctx, String key, String title, String author, String course, String sem, String priceMRP, String priceNew, String priceOld) {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(ctx.getString(R.string.database_path), null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS CART(key VARCHAR,title VARCHAR,author VARCHAR,course VARCHAR,sem VARCHAR,priceMRP VARCHAR,priceNew VARCHAR,priceOld VARCHAR, booktype VARCHAR, qty VARCHAR);");
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from CART WHERE key = '" + key + "'", null);

        if (cursor.getCount() <= 0) {
                sqLiteDatabase.execSQL("INSERT INTO CART VALUES('" + key + "','" + title + "','" + author + "','" + course + "','" + sem + "', '" + priceMRP + "' , '" + priceNew + "' , '" + priceOld + "', 'old', '1');");
                Toast.makeText(ctx, "Product added to your Cart", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(ctx, "Already added to your Cart", Toast.LENGTH_SHORT).show();
    }

}
