package com.mybooks.mybooks;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by am361000 on 31/08/17.
 */

public class RecyclerAdapterProductView extends RecyclerView.Adapter<RecyclerAdapterProductView.MyHolder> {

    String filter;

    List<ModelProductList> listdata;
    Context ctx;
    DatabaseReference mDatabase;

    public RecyclerAdapterProductView(Context ctx, List<ModelProductList> listdata) {
        this.listdata = listdata;
        this.ctx = ctx;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.books_list_view, parent, false);
        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }


    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {
        final ModelProductList modelProductList = listdata.get(position);
        holder.mtitle.setText(capitalizeEveryWord(modelProductList.getF2()));
        holder.mpublisher.setText(capitalizeEveryWord(modelProductList.getF3()));
        holder.mauthor.setText(capitalizeEveryWord(modelProductList.getF4()));
        holder.mcourse.setText(modelProductList.getF5());
        holder.msem.setText(modelProductList.getF6());


        /* set price*/
        int mrp_p = Integer.parseInt(modelProductList.getF7());
        int new_p = Integer.parseInt(modelProductList.getF8());
        int old_p = Integer.parseInt(modelProductList.getF9());
        holder.mrp_price.setText(modelProductList.getF7());
        holder.new_price.setText(modelProductList.getF8());
        holder.old_price.setText("\u20B9" + modelProductList.getF9());

        /*if (old_p == 0) {
            holder.old_price.setText("\u20B9" + modelProductList.getF8());
            holder.new_price.setText(modelProductList.getF7());
            holder.mrp_price.setVisibility(View.GONE);
        }
        if (new_p == 0) {
            holder.new_price.setText(modelProductList.getF7());
            holder.mrp_price.setVisibility(View.GONE);
        }*/

        /* set book cover*/
        if (modelProductList.getF13() == null) {
            holder.mBookImage.setImageResource(R.drawable.no_image_available);
        } else if (modelProductList.getF13().equals("na")) {
            holder.mBookImage.setImageResource(R.drawable.no_image_available);
        } else {
            Picasso.with(ctx).load(modelProductList.getF13()).into(holder.mBookImage);
        }

        /*Set out of stock tag*/
        if (Integer.parseInt(modelProductList.getF10()) <= 0) {
            holder.outofstockimg.setVisibility(View.VISIBLE);
            holder.buyButtonTxt.setVisibility(View.GONE);
        } else {
            holder.outofstockimg.setVisibility(View.GONE);
            holder.buyButtonTxt.setVisibility(View.VISIBLE);
        }

                /* ADD to cart button click handler*/
        holder.buyButtonTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ctx, bookList.getSrc(), Toast.LENGTH_SHORT).show();
            }
        });

                /* List view click listener*/
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadBookDetails(v.getContext(), modelProductList.getF11());
            }
        });
    }

    public void loadBookDetails(Context ctx, String key) {
        Intent intent = new Intent(ctx, Individual_book_details.class);
        intent.putExtra("key", key);
        ctx.startActivity(intent);
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
    public int getItemCount() {
        return listdata.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        LinearLayout linearLayout;

        TextView mtitle;
        TextView mpublisher;
        ImageView mBookImage;
        TextView mauthor;
        TextView mcourse;
        TextView msem;

        TextView mrp_price;
        TextView new_price;
        TextView old_price;

        ImageView outofstockimg;
        TextView buyButtonTxt;

        public MyHolder(View mview) {
            super(mview);

            mtitle = (TextView) mview.findViewById(R.id.p_f2);
            mtitle.setText("");
            mpublisher = (TextView) mview.findViewById(R.id.p_f3);
            mpublisher.setText("");
            mBookImage = (ImageView) mview.findViewById(R.id.p_f13);
            mauthor = (TextView) mview.findViewById(R.id.p_f4);
            mauthor.setText("");
            mcourse = (TextView) mview.findViewById(R.id.p_f5);
            mcourse.setText("");
            msem = (TextView) mview.findViewById(R.id.p_f6);
            msem.setText("");
            mrp_price = (TextView) mview.findViewById(R.id.p_f7);
            mrp_price.setText("");
            mrp_price.setPaintFlags(mrp_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            old_price = (TextView) mview.findViewById(R.id.p_f9);
            old_price.setText("");
            new_price = (TextView) mview.findViewById(R.id.p_f8);
            new_price.setText("");
            new_price.setPaintFlags(new_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            outofstockimg = (ImageView) mview.findViewById(R.id.outofstock);

            buyButtonTxt = (TextView) mview.findViewById(R.id.bookBuyTxt);
            buyButtonTxt.setVisibility(View.GONE);


            linearLayout = (LinearLayout) mview.findViewById(R.id.card_parent);


        }
    }
}
