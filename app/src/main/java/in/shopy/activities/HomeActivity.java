package in.shopy.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.shopy.R;
import in.shopy.Utils.MySharedPreference;
import in.shopy.adapters.MyPagerAdapter;
import in.shopy.app_pref.AppPref;
import in.shopy.services.MyBooksService;
import me.relex.circleindicator.CircleIndicator;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Pager
    private static ViewPager mPager;
    private static int currentPage = 0;
    View parentLayoutView;
    NavigationView navigationView;
    boolean doubleBackToExitPressedOnce = false;
    //private static final Integer[] XMEN= {R.drawable.ic_menu_share,R.drawable.ic_menu_manage,R.drawable.ic_menu_camera,R.drawable.ic_menu_send,R.drawable.ic_menu_gallery};
    //private ArrayList<Integer> XMENArray = new ArrayList<Integer>();
    private ArrayList<String> arrayListImageSrc = new ArrayList<>();
    private ArrayList<String> arrayListGoto = new ArrayList<>();

    //Status bar height
    public static int getStatusBarHeight(final Context context) {
        final Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            return resources.getDimensionPixelSize(resourceId);
        else
            return (int) Math.ceil((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? 24 : 25) * resources.getDisplayMetrics().density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        startService(new Intent(this, MyBooksService.class));

        initViews();
        setListeners();

        setAdvertisements();
        setAddress();
        //checkPermission();
        checkUserIfExist();
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        parentLayoutView = findViewById(R.id.drawer_layout);
    }

    private void setListeners() {
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setPadding(0, getStatusBarHeight(this), 0, 0);
    }

    private void setAddress() {
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences(getString(R.string.sharedPrefDeliveryAddress), MODE_PRIVATE);
        if (sharedPreferences.getString("Name", null) != null) {
            return;
        }
        startActivity(new Intent(getApplicationContext(), AddressActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();

        setWelcomeMsg();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            doubleBackToExitPressedOnce = true;

            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
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
            startActivity(new Intent(this, BlogActivity.class));
            return true;
        }

        if (id == R.id.feedbackMenu) {
            getHelp("Feedback: ");
            return true;
        }

        if (id == R.id.aboutMenu) {
            //startActivity(new Intent(this, About.class));
            startActivity(new Intent(this, PlaceOrder.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        if (!haveNetworkConnection()) {
            Snackbar.make(parentLayoutView, "Please check your internet connection", Snackbar.LENGTH_LONG).show();
            return false;
        }

        Intent intent = new Intent(this, BooksListPageNew.class);
        Bundle bundle = new Bundle();

        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.orderBookMenu:
                bundle.putString("f", "all");
                intent.putExtras(bundle);
                startActivity(intent);
                //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                break;

            case R.id.customiseOrderMenu:
                //Intent intent1 = new Intent(getApplicationContext(), CustomOrderActivity.class);
                Intent intent1 = new Intent(getApplicationContext(), CustomOrderMainPage.class);
                intent1.putExtra("key", "null");
                startActivity(intent1);
                break;

            case R.id.myCartMenu:
                startActivity(new Intent(getApplicationContext(), MyCartNew.class));
                break;


            case R.id.myAccMenu:
                startActivity(new Intent(getApplicationContext(), MyAccountActivity.class));
                break;

            case R.id.myOrder:
                startActivity(new Intent(getApplicationContext(), OrderMainPage.class));
                break;

            case R.id.myWishListMenu:
                bundle.putString("f", "wishlist");
                intent.putExtras(bundle);
                startActivity(intent);
                break;

            case R.id.logoutMenu:
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                MySharedPreference.clearSharedPreference(getApplicationContext());
                startActivity(new Intent(getApplicationContext(), Login_2.class));
                finish();
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

    private void getHelp(final String subject) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Please send an email to : orderonlinemybook@gmail.com");
        alertDialogBuilder.setPositiveButton("Send mail",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                        emailIntent.setType("text/plain");
                        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"orderonlinemybooks@gmail.com"});
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


        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void setWelcomeMsg() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.sharedPrefDeliveryAddress), MODE_PRIVATE);

        View mHeaderView = navigationView.getHeaderView(0);
        TextView welcomeMsg = (TextView) mHeaderView.findViewById(R.id.welcomeMsg);
        welcomeMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddressActivity.class));
            }
        });

        if (sharedPreferences.getString("Name", null) == null
                || sharedPreferences.getString("Name", null).equalsIgnoreCase("null")) {
            welcomeMsg.setText("There");
        } else {
            welcomeMsg.setText(sharedPreferences.getString("Name", null));
        }
    }

    private void getAppLiveness() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Configs").child("appset");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String live = String.valueOf(dataSnapshot.child("liveness").getValue());
                if (live.equals("1")) {

                } else {
                    Toast.makeText(getApplicationContext(), "Somthing went wrong.\nOrder can not be place at this movement", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setAdvertisements() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("advertisements");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayListImageSrc.clear();
                arrayListGoto.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String img_src = (String) dataSnapshot1.child("img_src").getValue();
                    String go_to = (String) dataSnapshot1.child("go_to").getValue();
                    arrayListImageSrc.add(img_src);
                    arrayListGoto.add(go_to);
                }
                initPager();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initPager() {

        mPager = (ViewPager) findViewById(R.id.pager);

        mPager.setAdapter(new MyPagerAdapter(HomeActivity.this, arrayListImageSrc, arrayListGoto));
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);


        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (currentPage == arrayListImageSrc.size()) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(runnable, 3000);

    }

    private void checkUserIfExist() {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("User");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "*"))) {

                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "*"));
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String live = String.valueOf(dataSnapshot.child("liveness").getValue());
                            if (live.equalsIgnoreCase("true")) {
                                databaseReference.removeEventListener(this);
                            } else {
                                //Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG).show();
                                AppPref.showAlertDialog(HomeActivity.this, "Account Locked", "Your account is locked. Please contact our helpline.");
                                databaseReference.removeEventListener(this);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
                    DatabaseReference mRef = mdatabase.getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "*"));
                    mRef.child("wallet").setValue("0");
                    mRef.child("liveness").setValue("true");

                    String name_mobile = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    String name = "null";
                    String mobile = "null";
                    Pattern p = Pattern.compile("([0-9])");
                    Matcher m = p.matcher(name_mobile);

                    if (name_mobile.length() > 10
                            && m.find() ) {
                        name = name_mobile.substring(0, name_mobile.length() - 11);
                        mobile = name_mobile.substring(name_mobile.length() - 10);
                    } else {
                        name = name_mobile;
                    }

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name).build();
                    FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates);

                    mRef.child("address").child("name").setValue("" + name);
                    mRef.child("address").child("email").setValue("" + FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    mRef.child("address").child("contact").setValue("" + mobile);
                    mRef.child("address").child("addressline1").setValue("null");
                    mRef.child("address").child("addressline2").setValue("null");
                    mRef.child("address").child("city").setValue("Bengaluru");
                    mRef.child("address").child("state").setValue("Karnataka");
                    mRef.child("address").child("pincode").setValue("null");
                    mRef.child("address").child("isVerified").setValue("false");


                    SharedPreferences sharedPreferences;
                    sharedPreferences = getSharedPreferences(getString(in.shopy.R.string.sharedPrefDeliveryAddress), MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("Name", "" + name);
                    editor.putString("contact", "" + mobile);
                    editor.putString("email", "" + FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    editor.putString("addressline1", "null");
                    editor.putString("addressline2", "null");
                    editor.putString("city", "Bengaluru");
                    editor.putString("state", "Karnataka");
                    editor.putString("pincode", "null");
                    editor.putString("isVerified", "false");
                    editor.commit();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
