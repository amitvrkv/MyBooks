package in.shopy.adapters;

/**
 * Created by am361000 on 18/11/17.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import in.shopy.activities.BooksListPageNew;

import java.util.ArrayList;

public class MyPagerAdapter extends PagerAdapter {

    //    private ArrayList<Integer> images;
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<String> arrayListImages;
    private ArrayList<String> arrayListGoto;

    public MyPagerAdapter(Context context, ArrayList<String> arrayListImages, ArrayList<String> arrayListGoto) {
        this.context = context;
        //this.images=images;
        inflater = LayoutInflater.from(context);
        this.arrayListImages = arrayListImages;
        this.arrayListGoto = arrayListGoto;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        //return images.size();
        return arrayListImages.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        View myImageLayout = inflater.inflate(in.shopy.R.layout.slide, view, false);
        ImageView myImage = (ImageView) myImageLayout
                .findViewById(in.shopy.R.id.image);

        final ProgressBar progressBar = (ProgressBar) myImageLayout.findViewById(in.shopy.R.id.progressBar);

        //myImage.setImageResource(images.get(position));

        Glide.with(context).load(arrayListImages.get(position))
                .error(in.shopy.R.drawable.no_image_available)
                .thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(myImage);


        myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "" + arrayListGoto.get(position), Toast.LENGTH_SHORT).show();
                if (arrayListGoto.get(position).equalsIgnoreCase("buy")) {
                    Intent intent = new Intent(context, BooksListPageNew.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("f", "all");
                    intent.putExtras(bundle);
                    //context.startActivity(intent);
                } else if (arrayListGoto.get(position).equalsIgnoreCase("custom")) {
                    //Intent intent1 = new Intent(context, CustomOrderActivity.class);
                    //intent1.putExtra("key", "null");
                    //context.startActivity(intent1);
                } else if (arrayListGoto.get(position).equalsIgnoreCase("order")) {
                    {
                        //context.startActivity(new Intent(context, OrderPageActivity.class));
                    }
                } else if (arrayListGoto.get(position).equalsIgnoreCase("individual")) {

                } else if (arrayListGoto.get(position).equalsIgnoreCase("orderdetails")) {

                } else if (arrayListGoto.get(position).equalsIgnoreCase("ordercustomdetails")) {

                }
            }
        });

        view.addView(myImageLayout, 0);
        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}