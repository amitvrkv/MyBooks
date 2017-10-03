package com.mybooks.mybooks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class BlogActivity extends AppCompatActivity {

    FirebaseRecyclerAdapter<ModelClassBLog, BlogActivity.BlogHolder> firebaseRecyclerAdapter;

    Query databaseReference;

    RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);
        setToolbar();

        recyclerView = (RecyclerView) findViewById(R.id.blogRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Blog");

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ModelClassBLog, BlogActivity.BlogHolder>(
                ModelClassBLog.class,
                R.layout.blog_view,
                BlogActivity.BlogHolder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(BlogActivity.BlogHolder viewHolder, ModelClassBLog model, int position) {
                viewHolder.setNonField(model.getId());
                viewHolder.setFrom(model.getBy());
                viewHolder.setMsg(model.getComment());
                viewHolder.setLikeCount(model.getLike());
                viewHolder.setdislikeCount(model.getDislike());
            }
        };

        mLayoutManager = new LinearLayoutManager(BlogActivity.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public void setToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Blog");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public static class BlogHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View mView;

        String id;

        public BlogHolder(View itemView) {
            super(itemView);
            mView = itemView;

            RelativeLayout likeBtn = (RelativeLayout) mView.findViewById(R.id.likeBtn);
            likeBtn.setOnClickListener(this);
            RelativeLayout dislikeBtn = (RelativeLayout) mView.findViewById(R.id.dislikeBtn);
            dislikeBtn.setOnClickListener(this);
        }

        public void setNonField(String id) {
            this.id = id;
        }

        public void setFrom(String name) {
            TextView from = (TextView) mView.findViewById(R.id.from);
            from.setText(name);
        }

        public void setMsg(String message) {
            TextView msg = (TextView) mView.findViewById(R.id.msg);
            msg.setText(message);
        }

        public void setLikeCount(String num) {
            TextView likeCount = (TextView) mView.findViewById(R.id.likeCount);
            likeCount.setText(num);
        }

        public void setdislikeCount(String num) {
            TextView dislikeCount = (TextView) mView.findViewById(R.id.dislikeCount);
            dislikeCount.setText(num);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.likeBtn:
                    incLike();
                    break;

                case R.id.dislikeBtn:
                    incDislike();
                    break;
            }
        }

        public void incLike() {
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Blog").child(id);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String count = String.valueOf(dataSnapshot.child("like").getValue());
                    String likeFrom = String.valueOf(dataSnapshot.child("likeFrom").getValue());

                    count = String.valueOf(Integer.parseInt(count) + 1);

                    if (likeFrom.contains(FirebaseAuth.getInstance().getCurrentUser().getEmail()) == false) {
                        FirebaseDatabase.getInstance().getReference().child("Blog").child(id).child("like").setValue(count);
                        FirebaseDatabase.getInstance().getReference().child("Blog").child(id).child("likeFrom").setValue(likeFrom + " \n " + FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void incDislike() {
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Blog").child(id);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String count = String.valueOf(dataSnapshot.child("dislike").getValue());
                    String dislikeFrom = String.valueOf(dataSnapshot.child("dislikeFrom").getValue());

                    int setCount = Integer.parseInt(count) + 1;

                    if (dislikeFrom.contains(FirebaseAuth.getInstance().getCurrentUser().getEmail()) == false) {
                        FirebaseDatabase.getInstance().getReference().child("Blog").child(id).child("dislike").setValue("" + setCount);
                        FirebaseDatabase.getInstance().getReference().child("Blog").child(id).child("dislikeFrom").setValue(dislikeFrom + " \n " + FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
