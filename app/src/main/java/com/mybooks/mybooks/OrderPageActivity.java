package com.mybooks.mybooks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OrderPageActivity extends AppCompatActivity implements View.OnClickListener{

    FirebaseDatabase firebaseDatabase;

    private ImageView mOderBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_page);

        mOderBackBtn = (ImageView) findViewById(R.id.oderBackBtn);
        mOderBackBtn.setOnClickListener(this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("Order").orderByKey().equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.oderBackBtn:
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                break;
        }
    }
}
