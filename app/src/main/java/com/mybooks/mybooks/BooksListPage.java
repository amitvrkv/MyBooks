package com.mybooks.mybooks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BooksListPage extends AppCompatActivity implements View.OnClickListener {

    private ImageView mbackButton;
    private LinearLayout mToolbar;

    private Button mdoneFilter, mclearFilter;
    private TextView mfilter, mcheckout;
    private RelativeLayout filterView;
    private Spinner mcousrseSelecter, msemSelecter;

    private RecyclerView mBookList;

    private Query mdatabaseQuery;
    private FirebaseRecyclerAdapter<BookList, BookListHolder> firebaseRecyclerAdapter;

    private ProgressDialog progressDialog;

    ImageView mSearchToolbarBtn, mSearchBtn;
    RelativeLayout mSearchOptionLayout, mCheckoutAndFilterOptionLayout;
    EditText mSearchData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_list_page);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Loading books details...");
        progressDialog.setCancelable(false);

        /* Search option*/
        mSearchToolbarBtn = (ImageView) findViewById(R.id.seachToolbarMenu);
        mSearchToolbarBtn.setOnClickListener(this);
        mSearchToolbarBtn.setVisibility(View.GONE);
        mSearchBtn = (ImageView) findViewById(R.id.searchBtn);
        mSearchBtn.setOnClickListener(this);
        mSearchOptionLayout = (RelativeLayout) findViewById(R.id.searchOption);
        mSearchOptionLayout.setVisibility(View.GONE);
        mCheckoutAndFilterOptionLayout = (RelativeLayout) findViewById(R.id.checkoutAndFilterOption);
        mCheckoutAndFilterOptionLayout.setVisibility(View.VISIBLE);
        mSearchData = (EditText) findViewById(R.id.searchData);

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
        semList.add("1");
        semList.add("2");
        semList.add("3");
        semList.add("4");
        semList.add("5");
        semList.add("6");
        semList.add("7");
        semList.add("8");
        semList.add("9");
        semList.add("10");
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
        TextView buyButtonTxt;

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

            buyButtonTxt = (TextView) itemView.findViewById(R.id.bookBuyTxt);
            buyButtonTxt.setOnClickListener(this);
        }

        public void setImage(final String src) {
            final ImageView mBookImage = (ImageView) mview.findViewById(R.id.bookImage);
            if (!src.equals("na")) {
                Picasso.with(mview.getContext()).load(src).into(mBookImage);
                /*Picasso.with(mview.getContext()).load(src).networkPolicy(NetworkPolicy.OFFLINE).into(mBookImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(mview.getContext()).load(src).into(mBookImage);
                    }
                });*/
            } else {
                mBookImage.setImageResource(R.drawable.no_image_available);
            }
        }

        public void settitle(String title) {
            TextView mtitle = (TextView) mview.findViewById(R.id.bookTitle);
            mtitle.setText(capitalizeEveryWord(title));
            this.title = title;
        }

        public String capitalizeEveryWord(String str) {
            System.out.println(str);
            StringBuffer stringbf = new StringBuffer();
            Matcher m = Pattern.compile(
                    "([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(str);

            while (m.find()) {
                m.appendReplacement(
                        stringbf, m.group(1).toUpperCase() + m.group(2).toLowerCase());
            }
            return m.appendTail(stringbf).toString();
        }


        public void setauthor(String author) {
            TextView mauthor = (TextView) mview.findViewById(R.id.bookAuthor);
            mauthor.setText(capitalizeEveryWord(author));
            this.author = author;
        }

        public void setCourse(String course) {
            TextView mcourse = (TextView) mview.findViewById(R.id.bookCourse);
            mcourse.setText(course);
            this.course = course;
        }

        public void setSem(String sem) {
            TextView msem = (TextView) mview.findViewById(R.id.bookClass);
            msem.setText("Semester: " + sem);
            this.sem = sem;
        }

        public void setpriceMRP(String priceMRP) {
            TextView mmarket = (TextView) mview.findViewById(R.id.bookMarketPrice);
            mmarket.setText(priceMRP);
            mmarket.setPaintFlags(mmarket.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            this.priceMRP = priceMRP;
        }

        public void setpriceOld(String priceOld) {
            TextView msell = (TextView) mview.findViewById(R.id.bookSellingPrice);
            msell.setText("\u20B9 " + priceOld);
            this.priceOld = priceOld;
        }

        public void setavlcopy(int mavlcopy) {
            ImageView outofstockimg = (ImageView) mview.findViewById(R.id.outofstock);
            TextView mbookbuy = (TextView) mview.findViewById(R.id.bookBuyTxt);

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
            if (v.getId() == buyButtonTxt.getId()) {
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
                        ////
                        Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_right_filter);
                        filterView.startAnimation(slideUp);
                        ////

                        mSearchToolbarBtn.setVisibility(View.VISIBLE);
                        filterView.setVisibility(View.GONE);
                        doneFilter();
                    }
                } else {
                    ///
                    Animation slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left_filter);
                    filterView.startAnimation(slideDown);
                    ////

                    mSearchToolbarBtn.setVisibility(View.GONE);
                    filterView.setVisibility(View.VISIBLE);
                }
                //doneFilter();
                break;

            case R.id.doneFilter:
                doneFilter();
                break;


            case R.id.checkout:
                startActivity(new Intent(getApplicationContext(), MyCart.class));
                break;

            case R.id.seachToolbarMenu:
                if (mSearchOptionLayout.getVisibility() == View.VISIBLE) {
                    mSearchOptionLayout.setVisibility(View.GONE);
                    mCheckoutAndFilterOptionLayout.setVisibility(View.VISIBLE);
                } else {
                    mSearchOptionLayout.setVisibility(View.VISIBLE);
                    mCheckoutAndFilterOptionLayout.setVisibility(View.GONE);
                }
                break;

            case R.id.searchBtn:
                seacrhBooks();
                break;
        }
    }

    public void seacrhBooks() {
        progressDialog.show();
        String course = mcousrseSelecter.getSelectedItem().toString();

        mdatabaseQuery = FirebaseDatabase.getInstance().getReference().child("Books").child(course).orderByChild("title").startAt(mSearchData.getText().toString().toUpperCase());
        mdatabaseQuery.keepSynced(true);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<BookList, BookListHolder>(
                BookList.class,
                R.layout.books_list_view,
                BookListHolder.class,
                mdatabaseQuery
        ) {
            @Override
            protected void populateViewHolder(BookListHolder viewHolder, final BookList model, int position) {
                viewHolder.setImage(model.getSrc());
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

        mdatabaseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                if (dataSnapshot.getChildrenCount() <= 0) {
                    Toast.makeText(getApplicationContext(), "No books found", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), dataSnapshot.getChildrenCount() + " book(s) found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void doneFilter() {
        String course = mcousrseSelecter.getSelectedItem().toString();
        final String sem = msemSelecter.getSelectedItem().toString();

        if (course.equals("select Course")) {
            Toast.makeText(getApplicationContext(), "Please select your course", Toast.LENGTH_SHORT).show();
            return;
        } else {
            mSearchToolbarBtn.setVisibility(View.VISIBLE);
            Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_right_filter);
            filterView.startAnimation(slideUp);
            filterView.setVisibility(View.GONE);
            if (sem.equals("select Semester") || sem.equals("All Semester"))
                mdatabaseQuery = FirebaseDatabase.getInstance().getReference().child("Books").child(course);
            else
                mdatabaseQuery = FirebaseDatabase.getInstance().getReference().child("Books").child(course).orderByChild("sem").equalTo(sem);
        }

        progressDialog.show();

        mdatabaseQuery.keepSynced(true);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<BookList, BookListHolder>(
                BookList.class,
                R.layout.books_list_view,
                BookListHolder.class,
                mdatabaseQuery
        ) {
            @Override
            protected void populateViewHolder(BookListHolder viewHolder, final BookList model, int position) {
                viewHolder.setImage(model.getSrc());
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

        mdatabaseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                if (dataSnapshot.getChildrenCount() <= 0) {
                    Toast.makeText(getApplicationContext(), "No books found", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), dataSnapshot.getChildrenCount() + " book(s) found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void insertDataToCart(Context ctx, String key, String title, String author, String course, String sem, String priceMRP, String priceNew, String priceOld) {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(ctx.getString(R.string.database_path), null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS CART(key VARCHAR,title VARCHAR,author VARCHAR,course VARCHAR,sem VARCHAR,priceMRP VARCHAR,priceNew VARCHAR,priceOld VARCHAR, booktype VARCHAR, qty VARCHAR);");
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from CART WHERE key = '" + key + "'", null);

        if (cursor.getCount() <= 0) {
            sqLiteDatabase.execSQL("INSERT INTO CART VALUES('" + key + "','" + title + "','" + author + "','" + course + "','" + sem + "', '" + priceMRP + "' , '" + priceNew + "' , '" + priceOld + "', 'old', '1');");
            Toast.makeText(ctx, "Product added to your Cart", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ctx, "Already added to your Cart", Toast.LENGTH_SHORT).show();
        }

    }

}
