package com.mybooks.mybooks;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by am361000 on 31/08/17.
 */

public class RecyclerAdapterProductView extends RecyclerView.Adapter<RecyclerAdapterProductView.MyHolder> {

    List<ModelProductList> listdata;
    Context ctx;
    TextView cart_item_count;
    Activity act;

    public RecyclerAdapterProductView(Context ctx, List<ModelProductList> listdata, TextView cart_item_count, Activity act) {
        this.listdata = listdata;
        this.ctx = ctx;
        this.cart_item_count = cart_item_count;
        this.act = act;
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

        setWishlistBtn(ctx, modelProductList.getF11(), holder.wishListBtnAdded);

        /* set price*/
        int mrp_p = Integer.parseInt(modelProductList.getF7());
        final int new_p = Integer.parseInt(modelProductList.getF8());
        final int old_p = Integer.parseInt(modelProductList.getF9());
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
            holder.loadimage_progress_bar.setVisibility(View.GONE);
        } else if (modelProductList.getF13().equals("na")) {
            holder.mBookImage.setImageResource(R.drawable.no_image_available);
            holder.loadimage_progress_bar.setVisibility(View.GONE);
        } else {
            //Picasso.with(ctx).load(modelProductList.getF13()).into(holder.mBookImage);
            //holder.loadimage_progress_bar.setVisibility(View.VISIBLE);
            Glide.with(ctx).load(modelProductList.getF13())
                    .error(R.drawable.no_image_available)
                    .thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            holder.loadimage_progress_bar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            holder.loadimage_progress_bar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(holder.mBookImage);
        }

        /*Set out of stock tag and Add to cart button visibility*/
        if (Integer.parseInt(modelProductList.getF10()) <= 0) {
            holder.outofstockimg.setVisibility(View.VISIBLE);
            holder.addToCartButton.setVisibility(View.GONE);
        } else {
            holder.outofstockimg.setVisibility(View.GONE);
            holder.addToCartButton.setVisibility(View.VISIBLE);

            SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(ctx.getString(R.string.database_path), null);
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS P_CART(key VARCHAR, booktype VARCHAR, price VARCHAR, qty VARCHAR);");
            Cursor cursor = sqLiteDatabase.rawQuery("Select * from P_CART WHERE key = '" + modelProductList.getF11() + "'", null);
            if (cursor.getCount() > 0) {
                holder.addToCartButton.setText("ADDED");
                holder.addToCartButton.setTextColor(Color.GREEN);
            }
        }

        /* ADD to cart button click handler*/
        holder.addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int new_price = Integer.parseInt(modelProductList.getF8());
                int old_price = Integer.parseInt(modelProductList.getF9());
                if (new_price == old_price) {
                    addProductToCart(ctx, holder.mBookImage, modelProductList.getF11(), "New", String.valueOf(new_price));
                } else {
                    addProductToCart(ctx, holder.mBookImage, modelProductList.getF11(), "Old", String.valueOf(old_price));
                }
                //addProductToCart(ctx, modelProductList.getF11(), holder.mBookImage);
                holder.addToCartButton.setText("ADDED");
                holder.addToCartButton.setTextColor(Color.GREEN);

            }
        });

        /* List view click listener*/
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadBookDetails(v.getContext(), modelProductList.getF11());
            }
        });

        /*Wish list button click*/
        holder.wishListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation zoomin = AnimationUtils.loadAnimation(ctx, R.anim.zoom_in);
                final Animation zoomout = AnimationUtils.loadAnimation(ctx, R.anim.zoom_out);
                holder.wishListBtn.setAnimation(zoomin);
                zoomin.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        holder.wishListBtn.setAnimation(zoomout);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                if (holder.wishListBtnAdded.getVisibility() == View.GONE) {
                    addProductToWishList(v.getContext(), modelProductList.getF11(), holder.wishListBtnAdded);
                } else {
                    removeProductToWishList(v.getContext(), modelProductList.getF11(), holder.wishListBtnAdded);
                }
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

    public void addProductToCart(Context ctx, View targetView, String key, String type, String price) {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(ctx.getString(R.string.database_path), null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS P_CART(key VARCHAR, booktype VARCHAR, price VARCHAR, qty VARCHAR);");
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from P_CART WHERE key = '" + key + "'", null);

        if (cursor.getCount() <= 0) {
            sqLiteDatabase.execSQL("INSERT INTO P_CART VALUES('" + key + "','" + type + "', '" + price + "', '1');");
            //Toast.makeText(ctx, "Product added to your Cart ", Toast.LENGTH_SHORT).show();
            makeFlyAnimation(targetView);
        } else {
            Toast.makeText(ctx, "Already added to your Cart", Toast.LENGTH_SHORT).show();
        }
    }

    private void addProductToWishList(Context ctx, String key, ImageView imageView) {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(ctx.getString(R.string.database_path), null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS WISHLIST(key VARCHAR);");
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from WISHLIST WHERE key = '" + key + "'", null);

        if (cursor.getCount() <= 0) {
            sqLiteDatabase.execSQL("INSERT INTO WISHLIST VALUES('" + key + "');");
            Toast.makeText(ctx, "Product added to Wishlist ", Toast.LENGTH_SHORT).show();
            imageView.setVisibility(View.VISIBLE);
        } else {
            //Toast.makeText(ctx, "Already added to Wishlist", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeProductToWishList(Context ctx, String key, ImageView imageView) {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(ctx.getString(R.string.database_path), null);
        sqLiteDatabase.execSQL("DELETE FROM WISHLIST WHERE key = '" + key + "'");
        imageView.setVisibility(View.GONE);
        //Toast.makeText(ctx, "Product removed from Wishlist ", Toast.LENGTH_SHORT).show();
    }

    public void setCartCount() {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(ctx.getString(R.string.database_path), null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS P_CART(key VARCHAR, booktype VARCHAR, price VARCHAR, qty VARCHAR);");
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from P_CART ", null);
        cart_item_count.setText("" + cursor.getCount());
    }

    private void makeFlyAnimation(final View targetView) {
        TextView destView = cart_item_count;

        new CircleAnimationUtil().attachActivity(act).setTargetView(targetView).setMoveDuration(500).setDestView(destView).setAnimationListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Toast.makeText(ctx, "Continue Shopping...", Toast.LENGTH_SHORT).show();
                Animation animation1 = AnimationUtils.loadAnimation(ctx, R.anim.fade_in);
                targetView.startAnimation(animation1);
                targetView.setVisibility(View.VISIBLE);
                setCartCount();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).startAnimation();


    }

    public void setWishlistBtn(Context ctx, String key, ImageView imageView) {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(ctx.getString(R.string.database_path), null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS WISHLIST(key VARCHAR);");
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from WISHLIST WHERE key = '" + key + "'", null);
        if (cursor.getCount() <= 0) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.VISIBLE);
        }
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
        TextView addToCartButton;

        ImageView wishListBtn;
        ImageView wishListBtnAdded;

        ProgressBar loadimage_progress_bar;

        public MyHolder(View mview) {
            super(mview);

            wishListBtn = (ImageView) mview.findViewById(R.id.wishListBtn);
            wishListBtnAdded = (ImageView) mview.findViewById(R.id.wishListBtnAdded);

            loadimage_progress_bar = (ProgressBar) mview.findViewById(R.id.loadimage_progress_bar);

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

            addToCartButton = (TextView) mview.findViewById(R.id.addToCartButton);
            addToCartButton.setVisibility(View.GONE);


            linearLayout = (LinearLayout) mview.findViewById(R.id.card_parent);

        }
    }
}
