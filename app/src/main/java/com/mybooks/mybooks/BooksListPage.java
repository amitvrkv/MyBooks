package com.mybooks.mybooks;

import android.media.Image;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BooksListPage extends AppCompatActivity implements View.OnClickListener{

    private ImageView mbackButton;
    private LinearLayout mToolbar;

    private RecyclerView mBookList;

    private DatabaseReference mdatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_list_page);

        mdatabaseReference = FirebaseDatabase.getInstance().getReference().child("Books");


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
                mdatabaseReference
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

    public static class BookListHolder extends RecyclerView.ViewHolder{

        View mview;

        public BookListHolder(View itemView) {
            super(itemView);
            mview = itemView;
        }

        public void settitle(String title){
            TextView mtitle = (TextView) mview.findViewById(R.id.bookTitle);
            mtitle.setText(title);
        }

        public void setauthor(String author){
            TextView mauthor = (TextView) mview.findViewById(R.id.bookAuthor);
            mauthor.setText(author);
        }

        public void setCourse(String course) {
            TextView mcourse = (TextView) mview.findViewById(R.id.bookCourse);
            mcourse.setText(course);
        }

        public void setclass(String mclass){
            TextView mmclass = (TextView) mview.findViewById(R.id.bookClass);
            mmclass.setText(mclass);
        }

        public void setsellingprice(String msellprice){
            TextView msell = (TextView) mview.findViewById(R.id.bookSellingPrice);
            msell.setText(msellprice);
        }

        public void setmarketprice(String mmarprice){
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
        }
    }
}
