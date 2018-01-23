package in.shopy.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import in.shopy.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomOrderMainPage extends AppCompatActivity implements View.OnClickListener {

    SQLiteDatabase sqLiteDatabase;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    private Button addItemBtn;
    private Button placeOrderBtn;
    private ListView customOrderListView;

    public static String getDate() {
        String dateInMilliseconds = String.valueOf(new Date().getTime());
        String dateFormat = "dd/MM/yyyy hh:mm:ss aa";
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_order_main_page);
        setToolbar();

        addItemBtn = (Button) findViewById(R.id.addItemBtn);
        addItemBtn.setOnClickListener(this);
        placeOrderBtn = (Button) findViewById(R.id.placeOrderBtn);
        placeOrderBtn.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        customOrderListView = (ListView) findViewById(R.id.customOrderListView);
        set_Book_data();
    }

    public void setToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Custom Order");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addItemBtn:
                Intent intent = new Intent(this, CustomOrderAddProduct.class);
                intent.putExtra("key", "null");
                startActivity(intent);
                break;

            case R.id.placeOrderBtn:
                progressDialog.setTitle("Please wait...");
                progressDialog.setMessage("Placing your order,\nPlease do not close this window.");
                progressDialog.setCancelable(false);
                progressDialog.show();
                placeOrder();
                break;
        }
    }

    public void getAppLiveness(final String mop) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Configs");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String live = String.valueOf(dataSnapshot.child("app_liveness").getValue());
                if (live.equalsIgnoreCase("true")) {
                    placeOrder(mop);
                } else {
                    //Toast.makeText(getApplicationContext(), "Somthing went wrong.\nOrder can not be place at this movement", Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(),
                            "We've temporarily suspended our operations and are not taking any request until further notice.\nInconvenience is highly regretted.",
                            Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void placeOrder() {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(getString(R.string.database_path), null);
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from CUSTOM_BOOK", null);
        if (cursor.moveToFirst() == false) {
            Toast.makeText(getApplicationContext(), "Add books to continue", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }
        getAppLiveness("COD");
    }

    //Getting order number and updating
    public void placeOrder(final String paymentmode) {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.sharedPrefDeliveryAddress), MODE_PRIVATE);
        if (sharedPreferences.getString("Name", null) == null) {
            Toast.makeText(getApplicationContext(), "Please update your address to continue.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), AddressActivity.class));
            return;
        }

        if (haveNetworkConnection() == false) {
            //Snackbar.make(parentLayoutView, "Please check your internet connection.", Snackbar.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        final int[] ordernumber = {0};
        databaseReference = FirebaseDatabase.getInstance().getReference().child("ORDER").child("ORDERCOUNT").child("CUSTOMORDER");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ordernumber[0] = Integer.parseInt(dataSnapshot.getValue().toString());
                ordernumber[0] = ordernumber[0] + 1;
                databaseReference.setValue(String.valueOf(ordernumber[0]));

                placeOrderOnFirebase(getOrderId(String.valueOf(ordernumber[0])), paymentmode);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                orderPlaceFailed("Failed to place order. Try again!!!");
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

    public String getOrderId(String orderId) {
        int len = orderId.length();
        int setLen = 10 - len;
        String final_order_id = "0000000000";
        final_order_id = "CD" + final_order_id.substring(0, setLen) + orderId;
        return final_order_id;
    }

    public String setAddress() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.sharedPrefDeliveryAddress), MODE_PRIVATE);
        String address = "";
        address = address + sharedPreferences.getString("Name", null);
        address = address + "\n" + sharedPreferences.getString("addressline1", null);
        address = address + "\n" + sharedPreferences.getString("addressline2", null);
        address = address + "\n" + sharedPreferences.getString("city", null);
        address = address + " - " + sharedPreferences.getString("pincode", null);
        address = address + "\n" + sharedPreferences.getString("state", null);
        address = address + "\n" + sharedPreferences.getString("contact", null);
        //mDeliveryAddress.setText(address);
        return address;
    }

    //Updataing order details on firebase
    public void placeOrderOnFirebase(final String ordernumber, String paymentmode) {

        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("ORDER")
                .child("CUSTOMORDER")
                .child(ordernumber);
        databaseReference.child("orderid").setValue(ordernumber);
        databaseReference.child("from").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());
        databaseReference.child("date").setValue(String.valueOf(getDate()));

        databaseReference.child("total").setValue("");
        databaseReference.child("deliverycharge").setValue("");
        databaseReference.child("discount").setValue("");
        databaseReference.child("payable_amount").setValue("");

        databaseReference.child("paymentmode").setValue(paymentmode);

        databaseReference.child("status").setValue("Pending for My Books team approval");
        databaseReference.child("deliveryaddress").setValue(setAddress().replace("\n", ", "));
        databaseReference.child("comment").setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    placeOrderedProductOnFirebase(ordernumber);
                } else {
                    orderPlaceFailed("Failed to place order. Try again!!!");
                }
            }
        });
    }

    //Updating product details on firebase
    public void placeOrderedProductOnFirebase(String ordernumber) {
        int count = 1;
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(getString(R.string.database_path), null);
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from CUSTOM_BOOK", null);

        if (cursor.moveToFirst() == false) {
            Toast.makeText(getApplicationContext(), "Your Cart is empty!", Toast.LENGTH_SHORT).show();
            return;
        } else {
            do {
                databaseReference = FirebaseDatabase.getInstance().getReference()
                        .child("ORDER")
                        .child("ORDERDETAILS")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString().replace(".", "*"))
                        .child("CUSTOMORDER")
                        .child(ordernumber)
                        .child("prod" + count);
                databaseReference.child("key").setValue("na");
                databaseReference.child("title").setValue(cursor.getString(cursor.getColumnIndex("title")));
                databaseReference.child("author").setValue(cursor.getString(cursor.getColumnIndex("author")));
                databaseReference.child("publisher").setValue(cursor.getString(cursor.getColumnIndex("publisher")));

                databaseReference.child("course").setValue(cursor.getString(cursor.getColumnIndex("course")));
                databaseReference.child("mrp").setValue(cursor.getString(cursor.getColumnIndex("mrp")));
                databaseReference.child("bookType").setValue(cursor.getString(cursor.getColumnIndex("bookType")));
                databaseReference.child("estPrice").setValue(cursor.getString(cursor.getColumnIndex("estPrice")));
                databaseReference.child("description").setValue(cursor.getString(cursor.getColumnIndex("description")));
                databaseReference.child("qty").setValue(cursor.getString(cursor.getColumnIndex("qty")));

                databaseReference.child("total").setValue(cursor.getString(cursor.getColumnIndex("total"))).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        } else {
                            orderPlaceFailed("Failed to update your product to server. Try again!!!");
                        }
                    }
                });

                count++;

            } while (cursor.moveToNext());

            orderPlacedSuccessfully(ordernumber);
        }
    }

    //order placed successfully
    public void orderPlacedSuccessfully(String ordernumber) {
        Toast.makeText(getApplicationContext(), "Order Placed successfully", Toast.LENGTH_LONG).show();

        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(getString(R.string.database_path), null);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS CUSTOM_BOOK");
        progressDialog.dismiss();
        //startActivity(new Intent(getApplicationContext(), OrderPageActivity.class));
        Intent intent = new Intent(getApplicationContext(), OrderCustomDetailsActivity.class);
        intent.putExtra("orderId", ordernumber);
        startActivity(intent);

        finish();
    }

    //On failed order
    public void orderPlaceFailed(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
    }

    public void set_Book_data() {
        List<String> kkey = new ArrayList<>();
        List<String> title = new ArrayList<>();
        List<String> author = new ArrayList<>();
        List<String> publisher = new ArrayList<>();
        List<String> course = new ArrayList<>();
        List<String> mrp = new ArrayList<>();
        List<String> bookType = new ArrayList<>();
        List<String> estPrice = new ArrayList<>();
        List<String> description = new ArrayList<>();
        List<String> qty = new ArrayList<>();
        List<String> total = new ArrayList<>();

        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(this.getString(R.string.database_path), null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS CUSTOM_BOOK (kkey VARCHAR, title VARCHAR, author VARCHAR, publisher VARCHAR, course VARCHAR, mrp VARCHAR, bookType VARCHAR, estPrice VARCHAR, description VARCHAR, qty VARCAHR, total VARCHAR);");
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from CUSTOM_BOOK", null);

        if (cursor.moveToFirst() == false) {

        } else {
            do {
                kkey.add(cursor.getString(0));
                title.add(cursor.getString(1));
                author.add(cursor.getString(2));
                publisher.add(cursor.getString(3));
                course.add(cursor.getString(4));
                mrp.add(cursor.getString(5));
                bookType.add(cursor.getString(6));
                estPrice.add(cursor.getString(7));
                description.add(cursor.getString(8));
                qty.add(cursor.getString(9));
                total.add(cursor.getString(10));
            } while (cursor.moveToNext());
            customOrderListView.setAdapter(new listViewCustomAdapter(this, kkey, title, author, publisher, course, mrp, bookType, estPrice, description, qty, total));
        }
    }

    public class listViewCustomAdapter extends BaseAdapter {

        public LayoutInflater inflater = null;
        Context context;
        List<String> kkey = new ArrayList<>();
        List<String> title = new ArrayList<>();
        List<String> author = new ArrayList<>();
        List<String> publisher = new ArrayList<>();
        List<String> course = new ArrayList<>();
        List<String> mrp = new ArrayList<>();
        List<String> bookType = new ArrayList<>();
        List<String> estPrice = new ArrayList<>();
        List<String> description = new ArrayList<>();
        List<String> qty = new ArrayList<>();
        List<String> total = new ArrayList<>();

        public listViewCustomAdapter(Context context, List<String> kkey, List<String> title, List<String> author, List<String> publisher, List<String> course, List<String> mrp, List<String> bookType, List<String> estPrice, List<String> description, List<String> qty, List<String> total) {
            this.kkey = kkey;
            this.context = context;
            this.title = title;
            this.author = author;
            this.publisher = publisher;
            this.course = course;
            this.mrp = mrp;
            this.bookType = bookType;
            this.estPrice = estPrice;
            this.description = description;
            this.qty = qty;
            this.total = total;

            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return title.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = inflater.inflate(R.layout.view_custom_order, null);
            final int final_position = position;

            final CardView customCardView = (CardView) view.findViewById(R.id.customCardView);

            TextView custom_title = (TextView) view.findViewById(R.id.custom_title);
            TextView custom_author = (TextView) view.findViewById(R.id.custom_author);
            TextView custom_publisher = (TextView) view.findViewById(R.id.custom_publisher);
            TextView custom_course = (TextView) view.findViewById(R.id.custom_course);
            TextView custom_description = (TextView) view.findViewById(R.id.custom_description);
            TextView custom_mrp = (TextView) view.findViewById(R.id.custom_mrp);
            TextView custom_bookType = (TextView) view.findViewById(R.id.custom_bookType);
            TextView custom_estPrice = (TextView) view.findViewById(R.id.custom_estPrice);

            Spinner custom_spinner_qty = (Spinner) view.findViewById(R.id.custom_spinner_qty);
            final TextView custom_total = (TextView) view.findViewById(R.id.custom_total);
            Button custom_remove_btn = (Button) view.findViewById(R.id.custom_remove_btn);

            custom_title.setText("Title: " + title.get(position));
            custom_author.setText("Author: " + author.get(position));
            custom_publisher.setText("Publisher: " + publisher.get(position));
            custom_course.setText("Course/Sem/Year: " + course.get(position));
            custom_description.setText("Description: " + description.get(position));
            custom_mrp.setText("MRP: \u20B9" + mrp.get(position));
            custom_bookType.setText("Book Type: " + bookType.get(position));
            custom_estPrice.setText("Estimated Price: \u20B9" + estPrice.get(position));

            if (!mrp.equals("0")) {
                total.set(position, estPrice.get(position));
                custom_total.setText("\u20B9" + total);
            } else {
                total.set(position, "0");
                custom_total.setText("\u20B9 NA");
            }

            custom_remove_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteData(customCardView, position);
                }
            });

            custom_spinner_qty.setSelection(Integer.parseInt(qty.get(position)) - 1);

            custom_spinner_qty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (!mrp.equals("0")) {
                        int tot = Integer.parseInt(estPrice.get(final_position)) * (position + 1);
                        total.set(final_position, String.valueOf(tot));
                        custom_total.setText("\u20B9" + total.get(final_position));

                        updateDatabase(kkey.get(final_position), "total", total.get(final_position));
                        updateDatabase(kkey.get(final_position), "qty", String.valueOf(position + 1));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            return view;
        }


        public void updateDatabase(String key, String column, String value) {
            sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(getString(R.string.database_path), null);
            sqLiteDatabase.execSQL("UPDATE CUSTOM_BOOK SET " + column + "='" + value + "' WHERE kkey='" + key + "'");
        }


        public void deleteData(CardView customCardView, final int position) {
            Animation fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
            customCardView.startAnimation(fadeOut);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    sqLiteDatabase.execSQL("DELETE FROM CUSTOM_BOOK WHERE title = '" + title.get(position) + "'");
                    title.remove(position);
                    author.remove(position);
                    publisher.remove(position);
                    course.remove(position);
                    description.remove(position);
                    mrp.remove(position);
                    bookType.remove(position);
                    estPrice.remove(position);
                    CustomOrderMainPage.listViewCustomAdapter.this.notifyDataSetChanged();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }
}
