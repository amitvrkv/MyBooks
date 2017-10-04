package com.mybooks.mybooks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class BlogActivity extends AppCompatActivity {

    FirebaseRecyclerAdapter<ModelClassBLog, BlogActivity.BlogHolder> firebaseRecyclerAdapter;

    Query databaseReference;

    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    private LinearLayoutManager mLayoutManager;

    public static String getDate() {
        String dateInMilliseconds = String.valueOf(new Date().getTime());
        String dateFormat = "dd/MM/yyyy hh:mm:ss aa";
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);
        setToolbar();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

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
                viewHolder.setId(model.getId(), model.getEmail());
                viewHolder.setFrom(model.getBy());
                viewHolder.setMsg(model.getComment());
                viewHolder.setLikeCount(model.getLike());
                viewHolder.setdislikeCount(model.getDislike());
                viewHolder.setDate(model.getDate());
            }
        };

        mLayoutManager = new LinearLayoutManager(BlogActivity.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(firebaseRecyclerAdapter);

        FloatingActionButton sendBtn = (FloatingActionButton) findViewById(R.id.floatingActionSendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNewMsg();
            }
        });
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

    public void sendNewMsg() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BlogActivity.this);
        alertDialog.setTitle("Type a message");

        final EditText editTextMsg = new EditText(BlogActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        editTextMsg.setLayoutParams(lp);
        alertDialog.setView(editTextMsg);

        alertDialog.setPositiveButton("SEND",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (!TextUtils.isEmpty(editTextMsg.getText())) {
                            updateId(editTextMsg.getText().toString());
                        }
                    }
                });

        alertDialog.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    public void updateId(final String msg) {
        if (haveNetworkConnection() == false) {
            Toast.makeText(getApplicationContext(), "Please check your internet connection.", Toast.LENGTH_LONG).show();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.sharedPrefDeliveryAddress), MODE_PRIVATE);
        if (sharedPreferences.getString("Name", null) == null) {
            Toast.makeText(getApplicationContext(), "Please update your NAME.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), AddressActivity.class));
            return;
        }

        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Posting your message.");
        progressDialog.show();

        final int[] blogId = {0};
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("mybooks").child("blog");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                blogId[0] = Integer.parseInt(dataSnapshot.getValue().toString());
                blogId[0] = blogId[0] + 1;
                databaseReference.setValue(String.valueOf(blogId[0]));

                postMsg(String.valueOf(blogId[0]), msg);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to post your message. Try again.", Toast.LENGTH_LONG).show();
                progressDialog.cancel();
            }
        });
    }

    public void postMsg(final String id, String msg) {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.sharedPrefDeliveryAddress), MODE_PRIVATE);
        String name = sharedPreferences.getString("Name", null);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Blog").child(id);
        databaseReference.child("id").setValue(id);
        databaseReference.child("by").setValue(name);
        databaseReference.child("comment").setValue(msg);
        databaseReference.child("dislike").setValue("0");
        databaseReference.child("dislikeFrom").setValue("");
        databaseReference.child("email").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        databaseReference.child("like").setValue("0");
        databaseReference.child("likeFrom").setValue("");
        databaseReference.child("date").setValue(getDate()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Posted your message.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to post your message. Try again.", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static class BlogHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View mView;

        String id;
        ImageView likeIcon;
        ImageView dislikeIcon;

        public BlogHolder(View itemView) {
            super(itemView);
            mView = itemView;

            RelativeLayout likeBtn = (RelativeLayout) mView.findViewById(R.id.likeBtn);
            likeBtn.setOnClickListener(this);
            RelativeLayout dislikeBtn = (RelativeLayout) mView.findViewById(R.id.dislikeBtn);
            dislikeBtn.setOnClickListener(this);

            likeIcon = (ImageView) mView.findViewById(R.id.likeIcon);
            dislikeIcon = (ImageView) mView.findViewById(R.id.dislikeIcon);
        }

        public void setId(String id, String email) {
            this.id = id;

            TextView textViewId = (TextView) mView.findViewById(R.id.blogId);
            textViewId.setText("#" + id);

            if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(email)) {
                RelativeLayout blogHeader = (RelativeLayout) mView.findViewById(R.id.blogHeader);
                blogHeader.setBackgroundColor(mView.getResources().getColor(R.color.colorAccent));
            }
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

        public void setDate(String date) {
            TextView textViewDate = (TextView) mView.findViewById(R.id.date);
            textViewDate.setText(date);
        }

        @Override
        public void onClick(View v) {
            final Animation zoomin = AnimationUtils.loadAnimation(mView.getContext(), R.anim.zoom_in);
            final Animation zoomout = AnimationUtils.loadAnimation(mView.getContext(), R.anim.zoom_out);
            switch (v.getId()) {
                case R.id.likeBtn:
                    //final Animation zoomin = AnimationUtils.loadAnimation(mView.getContext(), R.anim.zoom_in);
                    //final Animation zoomout = AnimationUtils.loadAnimation(mView.getContext(), R.anim.zoom_out);
                    likeIcon.startAnimation(zoomin);
                    zoomin.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            likeIcon.startAnimation(zoomout);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    zoomout.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            incLike();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    break;

                case R.id.dislikeBtn:
                    //final Animation zoomin = AnimationUtils.loadAnimation(mView.getContext(), R.anim.zoom_in);
                    //final Animation zoomout = AnimationUtils.loadAnimation(mView.getContext(), R.anim.zoom_out);
                    dislikeIcon.startAnimation(zoomin);
                    zoomin.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            dislikeIcon.startAnimation(zoomout);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    zoomout.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            incDislike();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
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
                        //likeIcon.startAnimation(zoomin);
                    } else {
                        Toast.makeText(mView.getContext(), "You have already liked this post.", Toast.LENGTH_SHORT).show();
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
                        //dislikeIcon.startAnimation(zoomin);
                    } else {
                        Toast.makeText(mView.getContext(), "You have already disliked this post.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
