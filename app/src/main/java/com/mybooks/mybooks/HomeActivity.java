package com.mybooks.mybooks;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    View parentLayoutView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        startService(new Intent(this, MyBooksService.class));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        //drawer.openDrawer(GravityCompat.START);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        parentLayoutView = findViewById(R.id.drawer_layout);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.helpMenu) {
            getHelp("Help: ");
            return true;
        }

        if (id == R.id.blogMenu) {
            startActivity(new Intent(getApplicationContext(), BooksListPageNew.class));
            //Toast.makeText(getApplicationContext(),"Coming soon.", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.feedbackMenu) {
            getHelp("Feedback: ");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        if ( ! haveNetworkConnection()) {
            Snackbar.make(parentLayoutView, "Please check your internet connection", Snackbar.LENGTH_LONG).show();
            return false;
        }

        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.orderBookMenu :
                startActivity(new Intent(getApplicationContext(), BooksListPageNew.class));
                break;

            /*case R.id.customiseOrderMenu:
                startActivity(new Intent(getApplicationContext(), CustomOrderActivity.class));
                break;*/

            case R.id.myCartMenu :
                startActivity(new Intent(getApplicationContext(), MyCartNew.class));
                break;

            case R.id.logoutMenu :
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
                break;
            case R.id.myAccMenu:
                startActivity(new Intent(getApplicationContext(), MyAccountActivity.class));
                break;

            case R.id.myOrder:
                startActivity(new Intent(getApplicationContext(), OrderPageActivity.class));
                break;
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    private void getHelp(final String subject){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Please send an email to : orderonlinemybook@gmail.com");
        alertDialogBuilder.setPositiveButton("Send mail",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                        emailIntent.setType("text/plain");
                        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{  "orderonlinemybooks@gmail.com"});
                        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
                        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");


                        emailIntent.setType("message/rfc822");

                        try {
                            startActivity(Intent.createChooser(emailIntent,
                                    "Send email using..."));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(getApplicationContext(),
                                    "No email clients installed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });


        alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
