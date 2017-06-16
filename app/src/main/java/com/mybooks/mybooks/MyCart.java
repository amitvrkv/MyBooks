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
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MyCart extends AppCompatActivity {

    private ListView cartList;
    SQLiteDatabase sqLiteDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart);

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
            } while (cursor.moveToNext());
            cartList.setAdapter(new listViewCustomAdapter(this, key, title, author, course, sem, priceMRP, priceNew, priceOld));
        }

    }


    public class listViewCustomAdapter extends BaseAdapter {

        int total = 0;

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

        public listViewCustomAdapter(Context context,  ArrayList<String> key, ArrayList<String> title, ArrayList<String> author, ArrayList<String> course, ArrayList<String> sem, ArrayList<String> priceMRP, ArrayList<String> priceNew, ArrayList<String> priceOld) {
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

            TextView mpriceOld = (TextView) view.findViewById(R.id.cbookSellingPrice);
            mpriceOld.setText("\u20B9 " + priceOld.get(position));

            final TextView mremoveBtn = (TextView) view.findViewById(R.id.cremoveBtn);
            mremoveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Product removed from your cart", Toast.LENGTH_SHORT).show();
                    SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(getString(R.string.database_path), null);
                    sqLiteDatabase.execSQL("DELETE FROM CART WHERE key = '" + key.get(position) + "'");
                    listViewCustomAdapter.this.notifyDataSetChanged();
                    startActivity(new Intent(view.getContext(), MyCart.class));
                    finish();
                }
            });

            return view;
        }
    }

}
