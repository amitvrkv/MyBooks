package com.mybooks.mybooks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
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
    private Spinner mcousrseSelecter, msemSelecter, mClassSelecter;

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
        mSearchData.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searhBooks();
                    return true;
                }
                return false;
            }
        });

        ///////filter start
        mfilter = (TextView) findViewById(R.id.filter);
        mfilter.setOnClickListener(this);
        mcheckout = (TextView) findViewById(R.id.checkout);
        mcheckout.setOnClickListener(this);
        mdoneFilter = (Button) findViewById(R.id.doneFilter);
        mdoneFilter.setOnClickListener(this);
        filterView = (RelativeLayout) findViewById(R.id.filterView);
        //filterView.setVisibility(View.GONE);

        //Class selector
        mClassSelecter = (Spinner) findViewById(R.id.classSelecter);
        mcousrseSelecter = (Spinner) findViewById(R.id.courseSelecter);
        msemSelecter = (Spinner) findViewById(R.id.semesterSelecter);

        final List<String> classList = new ArrayList<String>();
        classList.add("select Class");
        //classList.add("School");
        //classList.add("Pre University College");
        classList.add("Under Graduate");
        classList.add("Under Graduate");
        ArrayAdapter<String> classDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, classList);
        classDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mClassSelecter.setAdapter(classDataAdapter);

        //Course selector
        final List<String> courseList = new ArrayList<String>();
        mClassSelecter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.college);
                linearLayout.setVisibility(View.VISIBLE);
                msemSelecter.setVisibility(View.VISIBLE);
                courseList.clear();

                if (classList.get(position).toString().equals("select Class")) {
                    linearLayout.setVisibility(View.GONE);
                } else if (classList.get(position).toString().equals("School")) {
                    courseList.add("10th STD (SSLC)");
                    linearLayout.setVisibility(View.GONE);
                } else if (classList.get(position).toString().equals("Pre University College")) {
                    courseList.add("I PUC");
                    courseList.add("II PUC");
                    Collections.sort(courseList);
                    courseList.add(0, "select year");
                    msemSelecter.setVisibility(View.GONE);
                } else if (classList.get(position).toString().equals("Under Graduate")) {
                    courseList.add("BCA");
                    courseList.add("BCOM");
                    courseList.add("BBM");
                    courseList.add("BBA");
                    courseList.add("BHM");
                    courseList.add("BA");
                    courseList.add("BSC");
                    courseList.add("BE");
                    Collections.sort(courseList);
                    courseList.add(0, "select Course");
                } else if (classList.get(position).toString().equals("Under Graduate")) {
                    courseList.add("MA");
                    courseList.add("MCA");
                    courseList.add("MSC");
                    courseList.add("MCOM");
                    courseList.add("MBA");
                    Collections.sort(courseList);
                    courseList.add(0, "select Course");
                }

                ArrayAdapter<String> courseDataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, courseList);
                courseDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mcousrseSelecter.setAdapter(courseDataAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Semester selector

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
        String publisher;

        ImageView mBookImage;
        String src;

        public BookListHolder(View itemView) {
            super(itemView);
            this.mview = itemView;

            buyButtonTxt = (TextView) itemView.findViewById(R.id.addToCartButton);
            buyButtonTxt.setOnClickListener(this);
            mBookImage = (ImageView) mview.findViewById(R.id.p_f13);
            mBookImage.setOnClickListener(this);
        }

        public void setPublisher(String publisher) {
            this.publisher = publisher;
            TextView mpublisher = (TextView) mview.findViewById(R.id.p_f3);
            mpublisher.setText(publisher);
        }

        public void setImage(final String src) {
            mBookImage = (ImageView) mview.findViewById(R.id.p_f13);
            if (!src.equals("na")) {
                Picasso.with(mview.getContext()).load(src).into(mBookImage);
            } else {
                mBookImage.setImageResource(R.drawable.no_image_available);
            }
            this.src = src;
        }

        public void settitle(String title) {
            TextView mtitle = (TextView) mview.findViewById(R.id.p_f2);
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
            TextView mauthor = (TextView) mview.findViewById(R.id.p_f4);
            mauthor.setText(capitalizeEveryWord(author));
            this.author = author;
        }

        public void setCourse(String course) {
            TextView mcourse = (TextView) mview.findViewById(R.id.p_f5);
            mcourse.setText(course);
            this.course = course;
        }

        public void setSem(String sem) {
            TextView msem = (TextView) mview.findViewById(R.id.p_f6);
            msem.setText("Semester: " + sem);
            this.sem = sem;
        }

        public void setpriceMRP(String priceMRP) {
            TextView mmarket = (TextView) mview.findViewById(R.id.p_f7);
            mmarket.setText(priceMRP);
            mmarket.setPaintFlags(mmarket.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            this.priceMRP = priceMRP;
        }

        public void setpriceOld(String priceOld) {
            TextView msell = (TextView) mview.findViewById(R.id.p_f9);
            msell.setText("\u20B9 " + priceOld);
            this.priceOld = priceOld;
        }

        public void setPriceNew(String priceNew) {
            TextView bookSellingNewPrice = (TextView) mview.findViewById(R.id.p_f8);
            bookSellingNewPrice.setText(priceNew);
            bookSellingNewPrice.setPaintFlags(bookSellingNewPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            this.priceNew = priceNew;
        }

        public void setavlcopy(int mavlcopy) {
            ImageView outofstockimg = (ImageView) mview.findViewById(R.id.outofstock);
            TextView mbookbuy = (TextView) mview.findViewById(R.id.addToCartButton);

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

        //Adding books to cart
        @Override
        public void onClick(View v) {
            if (v.getId() == buyButtonTxt.getId()) {
                BooksListPage bk = new BooksListPage();
                bk.insertDataToCart(v.getContext(), key, title, publisher, author, course, sem, priceMRP, priceNew, priceOld);
            }
            if (v.getId() == mBookImage.getId()) {
                loadBookDetails(v.getContext(), course, key);
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
                Animation slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left_filter);

                slideDown.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mSearchToolbarBtn.setVisibility(View.GONE);
                        RelativeLayout relativeLayout_statusbar = (RelativeLayout) findViewById(R.id.statusbar);
                        relativeLayout_statusbar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                filterView.startAnimation(slideDown);
                filterView.setVisibility(View.VISIBLE);

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
                searhBooks();
                break;
        }
    }

    public void searhBooks() {
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
                viewHolder.setPublisher(model.getPublisher());

                // OnclickListener on recycler view redirect to book preview page
                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadBookDetails(v.getContext(), model.getCourse(), model.getKey());
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
        String classSel = mClassSelecter.getSelectedItem().toString();

        String course;
        final String sem;

        if (classSel.equals("select Class")) {
            Toast.makeText(getApplicationContext(), "Please select your class", Toast.LENGTH_SHORT).show();
            return;
        } else if (classSel.equals("SSLC")) {
            Toast.makeText(getApplicationContext(), "SSLC books will be available soon", Toast.LENGTH_SHORT).show();
            return;
        } else if (classSel.equals("PUC")) {

            Toast.makeText(getApplicationContext(), "PUC books will be available soon", Toast.LENGTH_SHORT).show();
            return;

        } else {

            course = mcousrseSelecter.getSelectedItem().toString();

            if (course.equals("select Course")) {
                Toast.makeText(getApplicationContext(), "Please select your course", Toast.LENGTH_SHORT).show();
                return;
            } else {

                sem = msemSelecter.getSelectedItem().toString();

                mSearchToolbarBtn.setVisibility(View.VISIBLE);
                Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_right_filter);
                filterView.startAnimation(slideUp);
                filterView.setVisibility(View.GONE);

                RelativeLayout relativeLayout_statusbar = (RelativeLayout) findViewById(R.id.statusbar);
                relativeLayout_statusbar.setVisibility(View.VISIBLE);

                if (sem.equals("select Semester") || sem.equals("All Semester"))
                    mdatabaseQuery = FirebaseDatabase.getInstance().getReference().child("Books").child(course);
                else
                    mdatabaseQuery = FirebaseDatabase.getInstance().getReference().child("Books").child(course).orderByChild("sem").equalTo(sem);
            }
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
                viewHolder.setPublisher(model.getPublisher());

                // OnclickListener on recycler view redirect to book preview page
                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(getApplicationContext(), "Selected :", Toast.LENGTH_SHORT).show();
                        loadBookDetails(v.getContext(), model.getCourse(), model.getKey());
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
                    //Toast.makeText(getApplicationContext(), "No books found", Toast.LENGTH_LONG).show();
                    showToastMessage(getApplicationContext(), "No book found", 1500);
                } else {
                    //Toast.makeText(getApplicationContext(), dataSnapshot.getChildrenCount() + " book(s) found.", Toast.LENGTH_SHORT).show();
                    showToastMessage(getApplicationContext(), dataSnapshot.getChildrenCount() + " book(s) found.", 1000);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void insertDataToCart(Context ctx, String key, String title, String publisher, String author, String course, String sem, String priceMRP, String priceNew, String priceOld) {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(ctx.getString(R.string.database_path), null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS CART(key VARCHAR,title VARCHAR, publisher VARCHAR, author VARCHAR,course VARCHAR,sem VARCHAR,priceMRP VARCHAR,priceNew VARCHAR,priceOld VARCHAR, booktype VARCHAR, qty VARCHAR);");
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from CART WHERE key = '" + key + "'", null);

        if (cursor.getCount() <= 0) {
            sqLiteDatabase.execSQL("INSERT INTO CART VALUES('" + key + "','" + title + "','" + publisher + "','" + author + "','" + course + "','" + sem + "', '" + priceMRP + "' , '" + priceNew + "' , '" + priceOld + "', 'old', '1');");
            Toast.makeText(ctx, "Product added to your Cart", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ctx, "Already added to your Cart", Toast.LENGTH_SHORT).show();
        }

    }

    public static void showToastMessage(Context context, String text, int duration) {
        final Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, duration);
    }

    public static void loadBookDetails(Context ctx, String course, String key) {
        Intent intent = new Intent(ctx, Individual_book_details.class);
        intent.putExtra("course", course);
        intent.putExtra("key", key);
        ctx.startActivity(intent);
    }

}