package com.mybooks.mybooks;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MyCart extends AppCompatActivity {

    private ListView cartList;
    SQLiteDatabase sqLiteDatabase;

    int total = 0;

    private TextView mGrandTotal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart);

        mGrandTotal = (TextView) findViewById(R.id.grandTotalPrice);

        cartList = (ListView) findViewById(R.id.cartList);

        ArrayList<String> title = new ArrayList<>();
        ArrayList<String> author = new ArrayList<>();
        ArrayList<String> course = new ArrayList<>();
        ArrayList<String> sem = new ArrayList<>();
        ArrayList<String> priceMRP = new ArrayList<>();
        ArrayList<String> priceOld = new ArrayList<>();
        ArrayList<String> priceNew = new ArrayList<>();
        ArrayList<String> key = new ArrayList<>();

        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(getString(R.string.database_path), null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS CART(key VARCHAR, title VARCHAR, author VARCHAR, course VARCHAR, sem VARCHAR, priceMRP VARCHAR, priceNew VARCHAR, priceOld VARCHAR);");
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from CART", null);

        //cursor.moveToFirst();

        if (cursor.moveToFirst() == false) {
            Toast.makeText(getApplicationContext(), "Your Cart is empty!", Toast.LENGTH_SHORT).show();
            RelativeLayout footer = (RelativeLayout) findViewById(R.id.footer);
            footer.setVisibility(View.GONE);
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

                //total = total + Integer.parseInt(cursor.getString(cursor.getColumnIndex("priceOld")));

            } while (cursor.moveToNext());

            cartList.setAdapter(new listViewCustomAdapter(this, key, title, author, course, sem, priceMRP, priceNew, priceOld));
            mGrandTotal.setText("Total: \u20B9 " + total);
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

        public LayoutInflater inflater = null;

        public listViewCustomAdapter(Context context, ArrayList<String> key, ArrayList<String> title, ArrayList<String> author, ArrayList<String> course, ArrayList<String> sem, ArrayList<String> priceMRP, ArrayList<String> priceNew, ArrayList<String> priceOld) {
            this.key = key;
            this.context = context;
            this.title = title;
            this.author = author;
            this.course = course;
            this.sem = sem;
            this.priceMRP = priceMRP;
            this.priceOld = priceOld;
            this.priceNew = priceNew;

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

            final TextView mpriceSell = (TextView) view.findViewById(R.id.cbookSellingPrice);
            mpriceSell.setText("\u20B9 " + priceOld.get(position));

            final TextView mtotalIndividual = (TextView) view.findViewById(R.id.totalIndividual);
            mtotalIndividual.setText("Total: \u20B9 " + priceOld.get(position));

            total.add(priceOld.get(position));

            final TextView mremoveBtn = (TextView) view.findViewById(R.id.cremoveBtn);
            CheckBox newBookCheck = (CheckBox) view.findViewById(R.id.checkBoxNewBook);
            final Spinner spinner = (Spinner) view.findViewById(R.id.quantity);

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

                    total.remove(position);

                    listViewCustomAdapter.this.notifyDataSetChanged();
                    setGrandTotal();
                }
            });


            newBookCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mpriceSell.setText("\u20B9 " + priceNew.get(position));
                        total.add(position, priceNew.get(position));
                    } else {
                        mpriceSell.setText("\u20B9 " + priceOld.get(position));
                        total.add(position, priceOld.get(position));
                    }

                    /*int t = 0;
                    int qty = Integer.parseInt(spinner.getSelectedItem().toString());
                    int price = Integer.parseInt(total.get(position));
                    t = qty * price;
                    total.add(position, "" + t);*/

                    mtotalIndividual.setText("Total: \u20B9 " + total.get(position));
                    //setGrandTotal();
                }
            });


            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    int t = 0;
                    int qty = Integer.parseInt(spinner.getSelectedItem().toString());
                    int price = Integer.parseInt(priceOld.get(position));
                    t = qty * price;
                    total.add(position, "" + t);

                    mtotalIndividual.setText("Total: \u20B9 " + total.get(position));
                    //setGrandTotal();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            return view;
        }

        public void setGrandTotal() {
            int gtotal = 0;

            for (int i = 0; i < total.size(); i++) {
                gtotal = gtotal + Integer.parseInt(total.get(i));
            }

            mGrandTotal.setText("Total: \u20B9 " + gtotal);
        }
    }

}
