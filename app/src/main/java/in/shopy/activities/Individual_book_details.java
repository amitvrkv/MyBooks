package in.shopy.activities;

import android.content.Intent;
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
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.shopy.R;
import in.shopy.app_pref.MyFormat;
import in.shopy.models.ModelProductList;
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

    ImageView outofstock;

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
        setContentView(in.shopy.R.layout.activity_individual_book_details);

        setToolbar();

        Bundle bundle = getIntent().getExtras();
        String key = bundle.getString("key");

        book_title_bold = (TextView) findViewById(in.shopy.R.id.book_title_bold);
        book_old_bold = (TextView) findViewById(in.shopy.R.id.book_old_bold);
        book_new_bold = (TextView) findViewById(in.shopy.R.id.book_new_bold);
        //book_new_bold.setPaintFlags(book_new_bold.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        book_mrp_bold = (TextView) findViewById(in.shopy.R.id.book_mrp_bold);
        book_mrp_bold.setPaintFlags(book_mrp_bold.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        old_per_off = (TextView) findViewById(in.shopy.R.id.old_per_off);
        new_per_off = (TextView) findViewById(in.shopy.R.id.new_per_off);

        outofstock = (ImageView) findViewById(in.shopy.R.id.outofstock);

        wishListBtn = (ImageView) findViewById(in.shopy.R.id.btn_wishList);
        wishListBtn.setOnClickListener(this);
        wishListBtnAdded = (ImageView) findViewById(in.shopy.R.id.btn_wishListAdded);
        wishListBtnAdded.setOnClickListener(this);

        mBookTitle = (TextView) findViewById(in.shopy.R.id.BookTitle);
        mBookPublisher = (TextView) findViewById(in.shopy.R.id.BookPublisher);
        mBookAuthor = (TextView) findViewById(in.shopy.R.id.BookAuthor);
        mBookCourse = (TextView) findViewById(in.shopy.R.id.BookCourse);
        mBookSem = (TextView) findViewById(in.shopy.R.id.BookSem);
        mBookMRP = (TextView) findViewById(in.shopy.R.id.BookMRP);
        mBookNewPrice = (TextView) findViewById(in.shopy.R.id.BookNewPrice);
        mBookOldPrice = (TextView) findViewById(in.shopy.R.id.BookOldPrice);
        mBook_image = (ImageView) findViewById(in.shopy.R.id.book_image);
        mBook_image.setOnClickListener(this);

        btn_add_to_cart = (Button) findViewById(in.shopy.R.id.btn_add_to_cart);
        btn_add_to_cart.setOnClickListener(this);
        btn_buy = (Button) findViewById(in.shopy.R.id.btn_buy);
        btn_buy.setOnClickListener(this);

        relativeLayout_book_image_large = (RelativeLayout) findViewById(in.shopy.R.id.relativeLayout_book_image_large);

        book_image_large = (ImageView) findViewById(in.shopy.R.id.book_image_large);

        scrollView = (ScrollView) findViewById(in.shopy.R.id.scrollView);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("PRODUCT").child("PRODUCTS").child(key);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                modelProductList = dataSnapshot.getValue(ModelProductList.class);

                mBookTitle.setText(MyFormat.capitalizeEveryWord(modelProductList.getF2()));
                book_title_bold.setText(mBookTitle.getText().toString());

                mBookPublisher.setText(MyFormat.capitalizeEveryWord(modelProductList.getF3()));
                mBookAuthor.setText(MyFormat.capitalizeEveryWord(modelProductList.getF4()));
                mBookCourse.setText(modelProductList.getF5());
                mBookSem.setText(modelProductList.getF6());


                mBookMRP.setText(modelProductList.getF7());
                mBookNewPrice.setText(modelProductList.getF8());
                mBookOldPrice.setText(modelProductList.getF9());

                book_old_bold.setText("\u20B9" + mBookOldPrice.getText().toString());
                book_new_bold.setText(mBookNewPrice.getText().toString());
                book_mrp_bold.setText(mBookMRP.getText().toString());

                if (modelProductList.getF9().equals("0")) {
                    book_old_bold.setVisibility(View.GONE);
                    old_per_off.setVisibility(View.GONE);

                    mBookOldPrice.setText("NA");
                    book_new_bold.setText("\u20B9 " + mBookNewPrice.getText().toString());

                } else {
                    book_old_bold.setVisibility(View.VISIBLE);
                    old_per_off.setVisibility(View.VISIBLE);
                    book_new_bold.setPaintFlags(book_new_bold.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }

                /*
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
                */


                //Set Image
                if (modelProductList.getF13().equalsIgnoreCase("na")) {

                    mBook_image.setImageDrawable(getResources().getDrawable(in.shopy.R.drawable.no_image_available));
                } else {
                    /*
                    Glide.with(getApplicationContext()).load(modelProductList.getF13())
                            .error(in.shopy.R.drawable.no_image_available)
                            .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(mBook_image);
                            */
                    Glide.with(getApplicationContext())
                            .load(modelProductList.getF13())
                            .apply(new RequestOptions()
                                    .error(R.drawable.no_image_available)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                            )
                            .into(mBook_image);
                }
                //Set OFF
                try {
                    int mrp = Integer.parseInt(modelProductList.getF7());
                    int newp = Integer.parseInt(modelProductList.getF8());
                    int old = Integer.parseInt(modelProductList.getF9());
                    int old_per = mrp - old;
                    int new_per = mrp - newp;

                    old_per_off.setText("₹ " + old_per + " off on old book");
                    new_per_off.setText("₹ " + new_per + " off on new book");

                } catch (Exception e) {
                    //old_per_off.setVisibility(View.GONE);
                    //new_per_off.setVisibility(View.GONE);
                }


                //Out of stock
                if (modelProductList.getF10().equals("0")) {
                    outofstock.setVisibility(View.VISIBLE);
                    btn_buy.setEnabled(false);
                    btn_add_to_cart.setEnabled(false);
                } else {
                    outofstock.setVisibility(View.GONE);
                    btn_buy.setEnabled(true);
                    btn_add_to_cart.setEnabled(true);
                }

                setWishList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void setToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(in.shopy.R.id.toolbar);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case in.shopy.R.id.book_image:
                showLargeImage();
                break;
            case in.shopy.R.id.btn_wishList:
                addToWishList();
                break;
            case in.shopy.R.id.btn_wishListAdded:
                addToWishList();
                break;
            case in.shopy.R.id.btn_add_to_cart:
                addToCart();
                break;
            case in.shopy.R.id.btn_buy:
                buy();
                break;
        }
    }

    private void buy() {
        addToCart();
        startActivity(new Intent(this, MyCartNew.class));
        finish();
    }

    private void addToCart() {
        int price;
        String type = null;
        int new_price = Integer.parseInt(modelProductList.getF8());
        int old_price = Integer.parseInt(modelProductList.getF9());

        if (new_price == old_price || old_price == 0) {
            price = new_price;
            type = "New";
        } else {
            price = old_price;
            type = "OLd";
        }

        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(getString(in.shopy.R.string.database_path), null);
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
        Animation zoomin = AnimationUtils.loadAnimation(getApplicationContext(), in.shopy.R.anim.zoom_in);
        final Animation zoomout = AnimationUtils.loadAnimation(getApplicationContext(), in.shopy.R.anim.zoom_out);
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


        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(getString(in.shopy.R.string.database_path), null);
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

            /*
            Glide.with(getApplicationContext()).load(modelProductList.getF13())
                    .error(in.shopy.R.drawable.no_image_available)
                    .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(book_image_large);
                    */
            Glide.with(getApplicationContext())
                    .load(modelProductList.getF13())
                    .apply(new RequestOptions()
                            .error(R.drawable.no_image_available)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                    )
                    .into(book_image_large);

            PhotoViewAttacher photoAttacher;
            photoAttacher = new PhotoViewAttacher(book_image_large);
            photoAttacher.update();
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
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(getString(in.shopy.R.string.database_path), null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS WISHLIST(key VARCHAR);");
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from WISHLIST WHERE key = '" + modelProductList.getF11() + "'", null);
        if (cursor.getCount() <= 0) {
            wishListBtnAdded.setVisibility(View.GONE);
        } else {
            wishListBtnAdded.setVisibility(View.VISIBLE);
        }
    }

}
