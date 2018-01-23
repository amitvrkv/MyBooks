package in.shopy.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import in.shopy.BuildConfig;

public class About extends AppCompatActivity {


    private Intent intent;

    Object createdTimestamp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(in.shopy.R.layout.activity_about);

        setToolbar();

        TextView textView = (TextView) findViewById(in.shopy.R.id.version);
        textView.setText("Version: " + BuildConfig.VERSION_NAME);
    }

    public void setToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(in.shopy.R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("About");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void startTransaction(View view){
        Toast.makeText(getApplicationContext(), "startTransaction", Toast.LENGTH_SHORT).show();
    }

}

