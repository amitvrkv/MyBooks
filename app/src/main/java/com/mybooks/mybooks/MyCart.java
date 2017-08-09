package com.mybooks.mybooks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MyCart extends AppCompatActivity implements View.OnClickListener{

    private ListView cartList;
    SQLiteDatabase sqLiteDatabase;

    int total = 0;

    private TextView mGrandTotal;
    private RelativeLayout footer;
    private Button mContinueBtn;
    private ImageView mbackBtn;

    View parentView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart);

        parentView = findViewById(R.id.mycartParentView);

        mGrandTotal = (TextView) findViewById(R.id.grandTotalPrice);
        footer = (RelativeLayout) findViewById(R.id.footer);

        mContinueBtn = (Button) findViewById(R.id.continueBtn);
        mContinueBtn.setOnClickListener(this);

        mbackBtn = (ImageView) findViewById(R.id.cartBackButton);
        mbackBtn.setOnClickListener(this);


        cartList = (ListView) findViewById(R.id.cartList);

        ArrayList<String> title = new ArrayList<>();
        ArrayList<String> author = new ArrayList<>();
        ArrayList<String> course = new ArrayList<>();
        ArrayList<String> sem = new ArrayList<>();
        ArrayList<String> priceMRP = new ArrayList<>();
        ArrayList<String> priceOld = new ArrayList<>();
        ArrayList<String> priceNew = new ArrayList<>();
        ArrayList<String> key = new ArrayList<>();
        ArrayList<String> booktype = new ArrayList<>();
        ArrayList<String> quantity = new ArrayList<>();

        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(getString(R.string.database_path), null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS CART(key VARCHAR, title VARCHAR, author VARCHAR, course VARCHAR, sem VARCHAR, priceMRP VARCHAR, priceNew VARCHAR, priceOld VARCHAR, booktype VARCHAR, qty VARCHAR);");
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from CART", null);

        if (cursor.moveToFirst() == false) {
            //Toast.makeText(getApplicationContext(), "Your Cart is empty!", Toast.LENGTH_SHORT).show();
            Snackbar.make(parentView, "Your Cart is empty!", Snackbar.LENGTH_INDEFINITE).show();

        } else {
            do {
                key.add(cursor.getString(cursor.getColumnIndex("key")));
                title.add(cursor.getString(cursor.getColumnIndex("title")));
                author.add(cursor.getString(cursor.getColumnIndex("author")));
                course.add(cursor.getString(cursor.getColumnIndex("course")));
                sem.add(cursor.getString(cursor.getColumnIndex("sem")));
                priceMRP.add(cursor.getString(cursor.getColumnIndex("priceMRP")));
                priceNew.add(cursor.getString(cursor.getColumnIndex("priceNew")));
                priceOld.add(cursor.getString(cursor.getColumnIndex("priceOld")));
                booktype.add(cursor.getString(cursor.getColumnIndex("booktype")));
                quantity.add(cursor.getString(cursor.getColumnIndex("qty")));

            } while (cursor.moveToNext());

            cartList.setAdapter(new listViewCustomAdapter(this, key, title, author, course, sem, priceMRP, priceNew, priceOld, booktype, quantity));
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cartBackButton:
                finish();
                break;

            case R.id.continueBtn:
                if( mGrandTotal.getText().toString().equals("Total: \u20B9 0")) {
                    Toast.makeText(getApplicationContext(), "Your cart is empty.", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(getApplicationContext(), PaymentPageActivity.class));
                    finish();
                }
                break;
        }
    }

    public class listViewCustomAdapter extends BaseAdapter {

        ArrayList<String> total = new ArrayList<>();

        Context context;
        ArrayList<String> title = new ArrayList<>();
        ArrayList<String> author = new ArrayList<>();
        ArrayList<String> course = new ArrayList<>();
        ArrayList<String> sem = new ArrayList<>();
        ArrayList<String> priceMRP = new ArrayList<>();
        ArrayList<String> priceOld = new ArrayList<>();
        ArrayList<String> priceNew = new ArrayList<>();
        ArrayList<String> key = new ArrayList<>();
        ArrayList<String> booktype = new ArrayList<>();
        ArrayList<String> quantity = new ArrayList<>();

        public LayoutInflater inflater = null;

        public listViewCustomAdapter(Context context, ArrayList<String> key, ArrayList<String> title, ArrayList<String> author, ArrayList<String> course, ArrayList<String> sem, ArrayList<String> priceMRP, ArrayList<String> priceNew, ArrayList<String> priceOld, ArrayList<String> booktype, ArrayList<String> quantity) {
            this.key = key;
            this.context = context;
            this.title = title;
            this.author = author;
            this.course = course;
            this.sem = sem;
            this.priceMRP = priceMRP;
            this.priceOld = priceOld;
            this.priceNew = priceNew;
            this.booktype = booktype;
            this.quantity = quantity;

            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return title.size();
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

            final View view = inflater.inflate(R.layout.cart_book_list_view, null);

            TextView mtitle = (TextView) view.findViewById(R.id.cbookTitle);
            mtitle.setText(title.get(position));

            TextView mauthor = (TextView) view.findViewById(R.id.cbookAuthor);
            mauthor.setText(author.get(position));

            TextView mcourse = (TextView) view.findViewById(R.id.cbookCourse);
            mcourse.setText(course.get(position));

            TextView msem = (TextView) view.findViewById(R.id.cbookClass);
            msem.setText(sem.get(position));

            TextView mpriceMrp = (TextView) view.findViewById(R.id.cbookMarketPrice);
            mpriceMrp.setText(priceMRP.get(position));
            mpriceMrp.setPaintFlags(mpriceMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            final TextView mpriceSell = (TextView) view.findViewById(R.id.cbookSellingPrice);
            mpriceSell.setText("\u20B9 " + priceOld.get(position));

            final TextView mtotalIndividual = (TextView) view.findViewById(R.id.totalIndividual);
            mtotalIndividual.setText("Total: \u20B9 " + priceOld.get(position));

            total.add("0");

            final TextView mremoveBtn = (TextView) view.findViewById(R.id.cremoveBtn);

            final CheckBox newBookCheck = (CheckBox) view.findViewById(R.id.checkBoxNewBook);
            if(booktype.get(position).equals("new"))
                newBookCheck.setChecked(true);
            else
                newBookCheck.setChecked(false);

            final Spinner spinner = (Spinner) view.findViewById(R.id.quantity);
            int q = Integer.parseInt(String.valueOf(quantity.get(position)));
            spinner.setSelection(q - 1);

            mremoveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Product removed from your cart", Toast.LENGTH_SHORT).show();
                    SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(getString(R.string.database_path), null);
                    sqLiteDatabase.execSQL("DELETE FROM CART WHERE key = '" + key.get(position) + "'");

                    key.remove(position);
                    title.remove(position);
                    author.remove(position);
                    course.remove(position);
                    sem.remove(position);
                    priceMRP.remove(position);
                    priceOld.remove(position);
                    priceNew.remove(position);
                    booktype.remove(position);
                    quantity.remove(position);

                    total.remove(position);

                    listViewCustomAdapter.this.notifyDataSetChanged();

                    setGrandTotal();
                }
            });


            newBookCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    int bookPrice = 0;
                    int qty = 1;
                    int totalPrice = 0;

                    if (isChecked) {
                        mpriceSell.setText("\u20B9 " + priceNew.get(position));
                        mpriceSell.setTextColor(getResources().getColor(R.color.Yellow));
                        bookPrice = Integer.parseInt(priceNew.get(position));
                        updateDatabase("booktype", "new", key.get(position));
                    } else {
                        mpriceSell.setText("\u20B9 " + priceOld.get(position));
                        mpriceSell.setTextColor(getResources().getColor(R.color.Green));
                        bookPrice = Integer.parseInt(priceOld.get(position));
                        updateDatabase("booktype", "old", key.get(position));
                    }

                    qty = Integer.parseInt(spinner.getSelectedItem().toString());

                    totalPrice = bookPrice * qty;

                    total.set(final_pos, String.valueOf(totalPrice));

                    mtotalIndividual.setText("Total: \u20B9 " + totalPrice);

                    setGrandTotal();
                }
            });


            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    int bookPrice = 0;
                    int totalPrice = 0;

                    int qty = Integer.parseInt(spinner.getSelectedItem().toString());

                    if(newBookCheck.isChecked()) {
                        bookPrice = Integer.parseInt(priceNew.get(final_pos));
                    } else {
                        bookPrice = Integer.parseInt(priceOld.get(final_pos));
                    }

                    totalPrice = bookPrice * qty;

                    total.set(final_pos, String.valueOf(totalPrice));

                    mtotalIndividual.setText("Total: \u20B9 " + totalPrice);

                    setGrandTotal();
                    updateDatabase("qty", spinner.getSelectedItem().toString() , key.get(final_pos));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            return view;
        }

        public void setGrandTotal() {
            int gtotal = 0;

            String list = null;

            for (int i = 0; i < total.size(); i++) {
                gtotal = gtotal + Integer.parseInt(total.get(i));
                list = list + " " + total.get(i);
            }

            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.sharedPrefDeliveryAddress), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("GrandTotal", String.valueOf(gtotal));
            editor.commit();

            mGrandTotal.setText("Total: \u20B9 " + gtotal);
        }

        public void updateDatabase(String column, String value, String key) {
            sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(getString(R.string.database_path), null);
            sqLiteDatabase.execSQL("UPDATE CART SET " + column + "='" + value + "' WHERE key='" + key + "'");
        }
    }

}
