package com.mybooks.mybooks.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mybooks.mybooks.models.ModelProductList;
import com.mybooks.mybooks.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.co.senab.photoview.PhotoViewAttacher;

public class Individual_book_details extends AppCompatActivity implements View.OnClickListener {

    ModelProductList modelProductList;
    RelativeLayout relativeLayout_book_image_large;
    ImageView book_image_large;

    TextView book_title_bold;
    TextView book_old_bold;
    TextView book_new_bold;
    TextView book_mrp_bold;
    TextView old_per_off;
    TextView new_per_off;

    ImageView wishListBtn;
    ImageView wishListBtnAdded;
    Button btn_add_to_cart;
    Button btn_buy;

    ScrollView scrollView;

    private ImageView mBook_image;
    private TextView mBookTitle, mBookPublisher, mBookAuthor, mBookCourse, mBookSem, mBookMRP, mBookNewPrice, mBookOldPrice;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_book_details);

        setToolbar();

        Bundle bundle = getIntent().getExtras();
        String key = bundle.getString("key");

        book_title_bold = (TextView) findViewById(R.id.book_title_bold);
        book_old_bold = (TextView) findViewById(R.id.book_old_bold);
        book_new_bold = (TextView) findViewById(R.id.book_new_bold);
        book_new_bold.setPaintFlags(book_new_bold.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        book_mrp_bold = (TextView) findViewById(R.id.book_mrp_bold);
        book_mrp_bold.setPaintFlags(book_mrp_bold.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        old_per_off = (TextView) findViewById(R.id.old_per_off);
        new_per_off = (TextView) findViewById(R.id.new_per_off);

        wishListBtn = (ImageView) findViewById(R.id.btn_wishList);
        wishListBtn.setOnClickListener(this);
        wishListBtnAdded = (ImageView) findViewById(R.id.btn_wishListAdded);
        wishListBtnAdded.setOnClickListener(this);

        mBookTitle = (TextView) findViewById(R.id.BookTitle);
        mBookPublisher = (TextView) findViewById(R.id.BookPublisher);
        mBookAuthor = (TextView) findViewById(R.id.BookAuthor);
        mBookCourse = (TextView) findViewById(R.id.BookCourse);
        mBookSem = (TextView) findViewById(R.id.BookSem);
        mBookMRP = (TextView) findViewById(R.id.BookMRP);
        mBookNewPrice = (TextView) findViewById(R.id.BookNewPrice);
        mBookOldPrice = (TextView) findViewById(R.id.BookOldPrice);
        mBook_image = (ImageView) findViewById(R.id.book_image);
        mBook_image.setOnClickListener(this);

        btn_add_to_cart = (Button) findViewById(R.id.btn_add_to_cart);
        btn_add_to_cart.setOnClickListener(this);
        btn_buy = (Button) findViewById(R.id.btn_buy);
        btn_buy.setOnClickListener(this);

        relativeLayout_book_image_large = (RelativeLayout) findViewById(R.id.relativeLayout_book_image_large);

        book_image_large = (ImageView) findViewById(R.id.book_image_large);

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Products").child(key);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                modelProductList = dataSnapshot.getValue(ModelProductList.class);

                mBookTitle.setText(capitalizeEveryWord(modelProductList.getF2()));
                book_title_bold.setText(mBookTitle.getText().toString());

                mBookPublisher.setText(capitalizeEveryWord(modelProductList.getF3()));
                mBookAuthor.setText(capitalizeEveryWord(modelProductList.getF4()));
                mBookCourse.setText(modelProductList.getF5());
                mBookSem.setText(modelProductList.getF6());
                if (modelProductList.getF7().equals("0") || modelProductList.getF7().equals("0") || modelProductList.getF7().equals("0")) {
                    mBookMRP.setText("not available");
                    mBookNewPrice.setText("not available");
                    mBookOldPrice.setText("not available");

                    book_mrp_bold.setVisibility(View.GONE);
                    book_new_bold.setVisibility(View.GONE);
                    book_old_bold.setVisibility(View.GONE);
                } else {
                    mBookMRP.setText(modelProductList.getF7());
                    mBookNewPrice.setText(modelProductList.getF8());
                    mBookOldPrice.setText(modelProductList.getF9());

                    book_old_bold.setText("\u20B9" + mBookOldPrice.getText().toString());
                    book_new_bold.setText(mBookNewPrice.getText().toString());
                    book_mrp_bold.setText(mBookMRP.getText().toString());
                }

                if (modelProductList.getF13().equalsIgnoreCase("na"))
                    mBook_image.setVisibility(View.GONE);
                else
                    //Picasso.with(getApplicationContext()).load(modelProductList.getF13()).into(mBook_image);
                    Glide.with(getApplicationContext()).load(modelProductList.getF13())
                            .error(R.drawable.no_image_available)
                            .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(mBook_image);

                try {
                    int mrp = Integer.parseInt(mBookMRP.getText().toString());
                    int newp = Integer.parseInt(mBookNewPrice.getText().toString());
                    int old = Integer.parseInt(mBookOldPrice.getText().toString());
                    int old_per = mrp - old;
                    int new_per = mrp - newp;
                    old_per_off.setText("₹ " + old_per + " off on old book");
                    new_per_off.setText("₹ " + new_per + " off on new book");
                } catch (Exception e) {
                    old_per_off.setVisibility(View.GONE);
                    new_per_off.setVisibility(View.GONE);
                }
                setWishList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void setToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public String capitalizeEveryWord(String str) {

        if (str == null)
            return "";

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.book_image:
                showLargeImage();
                break;
            case R.id.btn_wishList:
                addToWishList();
                break;
            case R.id.btn_wishListAdded:
                addToWishList();
                break;
            case R.id.btn_add_to_cart:
                addToCart();
                break;
            case R.id.btn_buy:
                buy();
                break;
        }
    }

    private void buy() {

    }

    private void addToCart() {
        int price;
        String type = null;
        int new_price = Integer.parseInt(modelProductList.getF8());
        int old_price = Integer.parseInt(modelProductList.getF9());
        if (new_price == old_price) {
            price = new_price;
            type = "New";
        } else {
            price = old_price;
            type = "OLd";
        }

        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(getString(R.string.database_path), null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS P_CART(key VARCHAR, booktype VARCHAR, price VARCHAR, qty VARCHAR);");

        Cursor cursor = sqLiteDatabase.rawQuery("Select * from P_CART WHERE key = '" + modelProductList.getF11() + "'", null);

        if (cursor.getCount() <= 0) {
            sqLiteDatabase.execSQL("INSERT INTO P_CART VALUES('" + modelProductList.getF11() + "','" + type + "', '" + String.valueOf(price) + "', '1');");
            Toast.makeText(getApplicationContext(), "Product added to your Cart ", Toast.LENGTH_SHORT).show();
            //makeFlyAnimation(targetView);
        } else {
            Toast.makeText(getApplicationContext(), "Already added to your Cart", Toast.LENGTH_SHORT).show();
        }
    }

    private void addToWishList() {
        Animation zoomin = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
        final Animation zoomout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);
        wishListBtn.setAnimation(zoomin);
        zoomin.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                wishListBtn.setAnimation(zoomout);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(getString(R.string.database_path), null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS WISHLIST(key VARCHAR);");
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from WISHLIST WHERE key = '" + modelProductList.getF11() + "'", null);

        if (cursor.getCount() <= 0) {
            sqLiteDatabase.execSQL("INSERT INTO WISHLIST VALUES('" + modelProductList.getF11() + "');");
            //Toast.makeText(getApplicationContext(), "Product added to Wishlist ", Toast.LENGTH_SHORT).show();
            wishListBtnAdded.setVisibility(View.VISIBLE);
        } else {
            //Toast.makeText(getApplicationContext(), "Already added to Wishlist", Toast.LENGTH_SHORT).show();
            sqLiteDatabase.execSQL("DELETE FROM WISHLIST WHERE key = '" + modelProductList.getF11() + "'");
            wishListBtnAdded.setVisibility(View.GONE);
        }
    }


    private void showLargeImage() {
        if (modelProductList.getF13().equalsIgnoreCase("na")) {
            //book_image_large.setVisibility(View.GONE);
            return;
        } else {
            relativeLayout_book_image_large.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
            Glide.with(getApplicationContext()).load(modelProductList.getF13())
                    .error(R.drawable.no_image_available)
                    .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(book_image_large);

            PhotoViewAttacher photoAttacher;
            photoAttacher = new PhotoViewAttacher(book_image_large);
            photoAttacher.update();

            photoAttacher.setDisplayMatrix(photoAttacher.getDisplayMatrix());
        }
    }

    @Override
    public void onBackPressed() {
        if (relativeLayout_book_image_large.getVisibility() == View.VISIBLE) {
            relativeLayout_book_image_large.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    public void setWishList() {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(getString(R.string.database_path), null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS WISHLIST(key VARCHAR);");
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from WISHLIST WHERE key = '" + modelProductList.getF11() + "'", null);
        if (cursor.getCount() <= 0) {
            wishListBtnAdded.setVisibility(View.GONE);
        } else {
            wishListBtnAdded.setVisibility(View.VISIBLE);
        }
    }

}
