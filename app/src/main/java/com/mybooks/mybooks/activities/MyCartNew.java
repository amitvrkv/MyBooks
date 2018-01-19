package com.mybooks.mybooks.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mybooks.mybooks.R;
import com.mybooks.mybooks.app_pref.MyFormat;
import com.mybooks.mybooks.models.ModelProductList;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyCartNew extends AppCompatActivity implements View.OnClickListener {

    RelativeLayout parentLayout;
    ListView my_cart_item_list;
    SQLiteDatabase sqLiteDatabase;

    TextView mGrandTotal;
    RelativeLayout TotalLayout;

    Button cart_product_continueBtn;

    ImageView app_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart_new);
        setToolbar();

        my_cart_item_list = (ListView) findViewById(R.id.my_cart_item_list);
        parentLayout = (RelativeLayout) findViewById(R.id.parentLayout);

        app_logo = (ImageView) findViewById(R.id.app_logo);

        mGrandTotal = (TextView) findViewById(R.id.grandTotal);
        TotalLayout = (RelativeLayout) findViewById(R.id.TotalLayout);

        cart_product_continueBtn = (Button) findViewById(R.id.cart_product_continueBtn);
        cart_product_continueBtn.setOnClickListener(this);

        /*
        cart_product_continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!haveNetworkConnection()) {
                    Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
                } else if (mGrandTotal.getText().toString().equals("0")) {
                    return;
                }

                getApplicationContext().startActivity(new Intent(getApplicationContext(), PaymentPageActivity.class));

                finish();
                //Toast.makeText(getApplicationContext(), "payment page to be implemented", Toast.LENGTH_SHORT).show();
            }
        });
        */

        setMy_cart_item_list();
    }

    public void setToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("My Cart");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setMy_cart_item_list() {
        List<String> key = new ArrayList<>();
        List<String> type = new ArrayList<>();
        List<String> price = new ArrayList<>();
        List<String> qty = new ArrayList<>();
        List<String> mtotal = new ArrayList<>();


        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(this.getString(R.string.database_path), null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS P_CART(key VARCHAR, booktype VARCHAR, price VARCHAR, qty VARCHAR);");
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from P_CART", null);

        if (cursor.moveToFirst() == false) {
            app_logo.setVisibility(View.VISIBLE);
            TotalLayout.setVisibility(View.GONE);
            Snackbar.make(parentLayout, "Your Cart is empty!", Snackbar.LENGTH_INDEFINITE).show();
        } else {
            app_logo.setVisibility(View.GONE);
            do {
                key.add(cursor.getString(cursor.getColumnIndex("key")));
                type.add(cursor.getString(cursor.getColumnIndex("booktype")));
                price.add(cursor.getString(cursor.getColumnIndex("price")));
                qty.add(cursor.getString(cursor.getColumnIndex("qty")));
                mtotal.add(String.valueOf(Integer.parseInt(cursor.getString(cursor.getColumnIndex("price"))) * Integer.parseInt(cursor.getString(cursor.getColumnIndex("qty")))));
            } while (cursor.moveToNext());
            my_cart_item_list.setAdapter(new listViewCustomAdapter(this, key, type, price, qty, mtotal));
        }
    }

    private boolean haveNetworkConnection() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cart_product_continueBtn) {
            if (!haveNetworkConnection()) {
                Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
            } else if (mGrandTotal.getText().toString().equals("0")) {
                return;
            }

            startActivity(new Intent(getApplicationContext(), PaymentPageActivity.class));

            finish();
        }
    }

    public class listViewCustomAdapter extends BaseAdapter {

        public LayoutInflater inflater = null;
        Context context;
        List<String> key = new ArrayList<>();
        List<String> type = new ArrayList<>();
        List<String> price = new ArrayList<>();
        List<String> qty = new ArrayList<>();
        List<String> mtotal = new ArrayList<>();

        public listViewCustomAdapter(Context context, List<String> key, List<String> type, List<String> price, List<String> qty, List<String> mtotal) {
            this.context = context;
            this.key = key;
            this.type = type;
            this.price = price;
            this.qty = qty;
            this.mtotal = mtotal;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return key.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final int final_pos = position;

            final View view = inflater.inflate(R.layout.cart_book_list_view_new, null);
            final TextView cart_product_title = (TextView) view.findViewById(R.id.cart_product_title);
            final TextView cart_product_description_1 = (TextView) view.findViewById(R.id.cart_product_description_1);
            final TextView cart_product_description_2 = (TextView) view.findViewById(R.id.cart_product_description_2);
            final TextView cart_product_mrp = (TextView) view.findViewById(R.id.cart_product_mrp);
            final TextView cart_product_sell_price = (TextView) view.findViewById(R.id.cart_product_sell_price);
            cart_product_mrp.setPaintFlags(cart_product_mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            final ImageView cart_product_image = (ImageView) view.findViewById(R.id.cart_product_image);

            final RadioGroup radio_group_type = (RadioGroup) view.findViewById(R.id.cart_product_radio_group_type);
            final RadioButton radio_button_old_type = (RadioButton) view.findViewById(R.id.cart_product_radio_button_old_type);
            final RadioButton radio_button_new_type = (RadioButton) view.findViewById(R.id.cart_product_radio_button_new_type);

            final Spinner cart_product_spinner_qty = (Spinner) view.findViewById(R.id.cart_product_spinner_qty);
            int q = Integer.parseInt(String.valueOf(qty.get(position)));
            cart_product_spinner_qty.setSelection(q - 1);

            final TextView cart_product_total = (TextView) view.findViewById(R.id.cart_product_total);
            cart_product_total.setText("\u20B9 " + mtotal.get(position));

            Button cart_product_remove_btn = (Button) view.findViewById(R.id.cart_product_remove_btn);
            cart_product_remove_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeProductFromCart(position);
                }
            });

            Button cart_product_move_to_wishlist_btn = (Button) view.findViewById(R.id.cart_product_move_to_wishlist_btn);
            cart_product_move_to_wishlist_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addProductToWishList(position);
                }
            });

            cart_product_sell_price.setText("\u20B9 " + price.get(position));

            if (type.get(position).equalsIgnoreCase("New")) {
                radio_button_new_type.setChecked(true);
            } else if (type.get(position).equalsIgnoreCase("Old")) {
                radio_button_old_type.setChecked(true);
            }

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("PRODUCT").child("PRODUCTS").child(key.get(position));
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final ModelProductList modelProductList = dataSnapshot.getValue(ModelProductList.class);
                    cart_product_title.setText(MyFormat.capitalizeEveryWord(modelProductList.getF2()));
                    cart_product_description_1.setText(MyFormat.capitalizeEveryWord(modelProductList.getF3()));
                    cart_product_description_2.setText(MyFormat.capitalizeEveryWord(modelProductList.getF4()));
                    cart_product_mrp.setText(modelProductList.getF7());

                    if (modelProductList.getF13() == null) {
                        cart_product_image.setImageResource(R.drawable.no_image_available);
                    } else if (modelProductList.getF13().equals("na")) {
                        cart_product_image.setImageResource(R.drawable.no_image_available);
                    } else {
                        //Picasso.with(context).load(modelProductList.getF13()).into(cart_product_image);
                        Glide.with(context).load(modelProductList.getF13())
                                .error(R.drawable.no_image_available)
                                .thumbnail(0.1f).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(cart_product_image);
                    }

                    /* hide show book type selection view*/
                    int new_price = Integer.parseInt(modelProductList.getF8());
                    int old_price = Integer.parseInt(modelProductList.getF9());
                    if (new_price == old_price) {
                        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.cart_product_booktype);
                        linearLayout.setVisibility(View.GONE);
                    } else {
                        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.cart_product_booktype);
                        linearLayout.setVisibility(View.VISIBLE);
                    }

                    /*Selecting the type of book and updating the total amount*/
                    radio_group_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                            if (checkedId == radio_button_old_type.getId()) {
                                updateDatabase(modelProductList.getF11(), "booktype", "Old");
                                updateDatabase(modelProductList.getF11(), "price", modelProductList.getF9());
                                cart_product_sell_price.setText("\u20B9 " + modelProductList.getF9());

                                mtotal.set(final_pos, String.valueOf(Integer.parseInt(qty.get(final_pos)) * Integer.parseInt(modelProductList.getF9())));

                            } else if (checkedId == radio_button_new_type.getId()) {
                                updateDatabase(modelProductList.getF11(), "booktype", "New");
                                updateDatabase(modelProductList.getF11(), "price", modelProductList.getF8());
                                cart_product_sell_price.setText("\u20B9 " + modelProductList.getF8());

                                mtotal.set(final_pos, String.valueOf(Integer.parseInt(qty.get(final_pos)) * Integer.parseInt(modelProductList.getF8())));

                            }
                            cart_product_total.setText("\u20B9 " + mtotal.get(position));
                            setGrandTotal();
                        }
                    });


                    if (type.get(position).equalsIgnoreCase("New")) {
                        updateDatabase(modelProductList.getF11(), "booktype", "New");
                        cart_product_sell_price.setText("\u20B9 " + modelProductList.getF8());
                    } else if (type.get(position).equalsIgnoreCase("Old")) {
                        cart_product_sell_price.setText("\u20B9 " + modelProductList.getF9());
                        updateDatabase(modelProductList.getF11(), "booktype", "Old");
                    }

                    /*Selecting the quantity and updating the total amount*/
                    cart_product_spinner_qty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            qty.set(final_pos, String.valueOf(position + 1));
                            updateDatabase(key.get(final_pos), "qty", String.valueOf(position + 1));

                            if (radio_button_new_type.isChecked()) {
                                mtotal.set(final_pos, String.valueOf(Integer.parseInt(qty.get(final_pos)) * Integer.parseInt(modelProductList.getF8())));
                            } else if (radio_button_old_type.isChecked()) {
                                mtotal.set(final_pos, String.valueOf(Integer.parseInt(qty.get(final_pos)) * Integer.parseInt(modelProductList.getF9())));
                            }
                            cart_product_total.setText("\u20B9 " + mtotal.get(final_pos));
                            setGrandTotal();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            setGrandTotal();

            return view;
        }

        public void setGrandTotal() {
            int gtotal = 0;
            for (int i = 0; i < mtotal.size(); i++) {
                gtotal = gtotal + Integer.parseInt(mtotal.get(i));
            }
            mGrandTotal.setText("" + gtotal);

            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.sharedPrefDeliveryAddress), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Total", String.valueOf(gtotal));
            editor.commit();
        }

        public void updateDatabase(String key, String column, String value) {
            if (!haveNetworkConnection()) {
                Toast.makeText(getApplicationContext(), "Check your internet connection \nChanges not updated", Toast.LENGTH_SHORT).show();
                return;
            }
            sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(getString(R.string.database_path), null);
            sqLiteDatabase.execSQL("UPDATE P_CART SET " + column + "='" + value + "' WHERE key='" + key + "'");
        }

        public void removeProductFromCart(final int position) {
            sqLiteDatabase.execSQL("DELETE FROM P_CART WHERE key = '" + key.get(position) + "'");

            key.remove(position);
            type.remove(position);
            qty.remove(position);
            price.remove(position);
            mtotal.remove(position);

            if (mtotal.size() == 0) {
                TotalLayout.setVisibility(View.GONE);
                Snackbar.make(parentLayout, "Your Cart is empty!", Snackbar.LENGTH_INDEFINITE).show();
            }

            setMy_cart_item_list();

        }

        public void addProductToWishList(int position) {
            SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(getApplicationContext().getString(R.string.database_path), null);
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS WISHLIST(key VARCHAR);");
            Cursor cursor = sqLiteDatabase.rawQuery("Select * from WISHLIST WHERE key = '" + key.get(position) + "'", null);

            if (cursor.getCount() <= 0) {
                sqLiteDatabase.execSQL("INSERT INTO WISHLIST VALUES('" + key.get(position) + "');");
                Toast.makeText(getApplicationContext(), "Product moved to Wishlist ", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Already present in Wishlist", Toast.LENGTH_SHORT).show();
            }
            removeProductFromCart(position);
        }
    }
}