package com.mybooks.mybooks;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BooksListPage extends AppCompatActivity implements View.OnClickListener {

    private ImageView mbackButton;
    private LinearLayout mToolbar;

    private Button mdoneFilter, mclearFilter;
    private TextView mfilter, mcheckout;
    private RelativeLayout filterView;
    private Spinner mcousrseSelecter, msemSelecter;

    private RecyclerView mBookList;

    //private DatabaseReference mdatabaseQuery;
    private Query mdatabaseQuery;
    //private DatabaseReference mdatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_list_page);

        ///////filter start
        mfilter = (TextView) findViewById(R.id.filter);
        mfilter.setOnClickListener(this);

        mcheckout = (TextView) findViewById(R.id.checkout);
        mcheckout.setOnClickListener(this);

        mdoneFilter = (Button) findViewById(R.id.doneFilter);
        mdoneFilter.setOnClickListener(this);

        mclearFilter = (Button) findViewById(R.id.clearFilter);
        mclearFilter.setOnClickListener(this);

        filterView = (RelativeLayout) findViewById(R.id.filterView);
        filterView.setVisibility(View.GONE);

        mcousrseSelecter = (Spinner) findViewById(R.id.courseSelecter);
        List<String> courseList = new ArrayList<String>();
        courseList.add("BCA");
        courseList.add("BCOM");
        courseList.add("BBM");
        courseList.add("BBA");
        courseList.add("BHM");
        Collections.sort(courseList);
        courseList.add(0, "select Course");
        ArrayAdapter<String> courseDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, courseList);
        courseDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mcousrseSelecter.setAdapter(courseDataAdapter);

        msemSelecter = (Spinner) findViewById(R.id.semesterSelecter);
        List<String> semList = new ArrayList<String>();
        semList.add(0, "select Semester");
        semList.add("Semester 1");
        semList.add("Semester 2");
        semList.add("Semester 3");
        semList.add("Semester 4");
        semList.add("Semester 5");
        semList.add("Semester 6");
        semList.add("Semester 7");
        semList.add("Semester 8");
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

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<BookList, BookListHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<BookList, BookListHolder>(
                BookList.class,
                R.layout.books_list_view,
                BookListHolder.class,
                mdatabaseQuery
        ) {
            @Override
            protected void populateViewHolder(BookListHolder viewHolder, BookList model, int position) {
                viewHolder.setauthor(model.getAuthor());
                viewHolder.settitle(model.getTitle());
                viewHolder.setCourse(model.getCourse());
                viewHolder.setclass(model.getCclass());
                viewHolder.setsellingprice(model.getSellingprice());
                viewHolder.setmarketprice(model.getMarketprice());
            }
        };

        mBookList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class BookListHolder extends RecyclerView.ViewHolder {

        View mview;

        public BookListHolder(View itemView) {
            super(itemView);
            mview = itemView;
        }

        public void settitle(String title) {
            TextView mtitle = (TextView) mview.findViewById(R.id.bookTitle);
            mtitle.setText(title);
        }

        public void setauthor(String author) {
            TextView mauthor = (TextView) mview.findViewById(R.id.bookAuthor);
            mauthor.setText(author);
        }

        public void setCourse(String course) {
            TextView mcourse = (TextView) mview.findViewById(R.id.bookCourse);
            mcourse.setText(course);
        }

        public void setclass(String mclass) {
            TextView mmclass = (TextView) mview.findViewById(R.id.bookClass);
            mmclass.setText(mclass);
        }

        public void setsellingprice(String msellprice) {
            TextView msell = (TextView) mview.findViewById(R.id.bookSellingPrice);
            msell.setText("\u20B9 " + msellprice);
        }

        public void setmarketprice(String mmarprice) {
            TextView mmarket = (TextView) mview.findViewById(R.id.bookMarketPrice);
            mmarket.setText(mmarprice);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backButton:
                finish();
                break;

            case R.id.filter:
                if (filterView.getVisibility() == View.VISIBLE)
                    filterView.setVisibility(View.GONE);
                else
                    filterView.setVisibility(View.VISIBLE);
                break;

            case R.id.doneFilter:
                filterView.setVisibility(View.GONE);
                break;

            case R.id.clearFilter:
                break;

            case R.id.checkout:
                break;
        }
    }


}
