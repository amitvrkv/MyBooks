package com.mybooks.mybooks;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BooksListPageNew extends AppCompatActivity implements View.OnClickListener {

    List<String> listKeys;
    List<ModelProductList> listObjects;

    DatabaseReference databaseReference;
    RecyclerView recyclerView;

    //Tool bar
    //TextView filter;
    //TextView checkout;

    //Filter views
    String cat1, cat2, cat3, cat4;
    Button doneFilter;
    View filterLayout;
    Spinner spinnerCat1, spinnerCat2, spinnerCat3, spinnerCat4;
    List<String> list1;
    List<String> list2;
    List<String> list3;
    List<String> list4;

    FloatingActionButton floatingActionButtonFilter;

    //Search
    EditText editTextSearchData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_list_page_new);
        setToolbar();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        floatingActionButtonFilter = (FloatingActionButton) findViewById(R.id.floatingActionButtonFilter);
        floatingActionButtonFilter.setOnClickListener(this);

        editTextSearchData = (EditText) findViewById(R.id.search_data);
        editTextSearchData.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    editTextSearchData.setCursorVisible(false);
                    searchProduct();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    return true;
                }
                return false;
            }
        });
        editTextSearchData.setOnClickListener(this);

        /*Filter initial settings settings*/
        spinnerCat1 = (Spinner) findViewById(R.id.cat1);
        spinnerCat2 = (Spinner) findViewById(R.id.cat2);
        spinnerCat2.setVisibility(View.GONE);
        spinnerCat3 = (Spinner) findViewById(R.id.cat3);
        spinnerCat3.setVisibility(View.GONE);
        spinnerCat4 = (Spinner) findViewById(R.id.cat4);
        spinnerCat4.setVisibility(View.GONE);
        filterLayout = findViewById(R.id.filterLayout);
        filterLayout.setVisibility(View.INVISIBLE);
        doneFilter = (Button) findViewById(R.id.doneFilter);
        doneFilter.setOnClickListener(this);
        setFilter();

        setDataOnPageStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_order_book, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_data:
                editTextSearchData.setCursorVisible(true);
                break;
            case R.id.floatingActionButtonFilter:
                setFloatingActionButtonFilter();
                break;

            case R.id.doneFilter:
                setDoneFilter();
                break;
        }
    }

    public void setDoneFilter() {
        editTextSearchData.setText("");

        Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_right_filter);
        filterLayout.startAnimation(slideUp);
        filterLayout.setVisibility(View.GONE);
        slideUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        //setDataOnPageStart();
        Animation sd = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        floatingActionButtonFilter.startAnimation(sd);
        floatingActionButtonFilter.setVisibility(View.VISIBLE);


        if (spinnerCat1.getSelectedItem().equals("Category")) {
            setDataOnPageStart();
        } else if (spinnerCat2.getSelectedItem().equals("select")) {
            //Toast.makeText(getApplicationContext(),"> 1" , Toast.LENGTH_SHORT).show();
            productByCat1();
        } else if (spinnerCat3.getSelectedItem().equals("select")) {
            //Toast.makeText(getApplicationContext(),"> 2" , Toast.LENGTH_SHORT).show();
            productByCat2();
        } else if (spinnerCat4.getSelectedItem().equals("select")) {
            //Toast.makeText(getApplicationContext(),"> 3\n" +  modelProductList.getF1() + "\n" +  modelProductList.getF5() , Toast.LENGTH_SHORT).show();
            productByCat3();
        } else {
            //Toast.makeText(getApplicationContext(),"> 4" , Toast.LENGTH_SHORT).show();
            productByCat4();
        }
    }

    public void setFloatingActionButtonFilter() {
        recyclerView.setVisibility(View.GONE);
        Animation slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left_filter);
        filterLayout.setAnimation(slideDown);
        filterLayout.setVisibility(View.VISIBLE);

        Animation sd = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        floatingActionButtonFilter.startAnimation(sd);
        floatingActionButtonFilter.setVisibility(View.GONE);
    }

    public void setToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Order Books");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setDataOnPageStart() {
        listObjects = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");
        databaseReference.keepSynced(true);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listObjects.clear();
                //Toast.makeText(getApplicationContext(),"> <" , Toast.LENGTH_SHORT).show();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ModelProductList modelProductList = dataSnapshot1.getValue(ModelProductList.class);
                    listObjects.add(modelProductList);

                    /*
                    if (spinnerCat1.getSelectedItem().equals("Category")) {
                        listObjects.add(modelProductList);
                    } else if (spinnerCat1.getVisibility() == View.VISIBLE && spinnerCat2.getVisibility() == View.GONE || spinnerCat2.getSelectedItem().equals("select")) {
                        //Toast.makeText(getApplicationContext(),"> 1" , Toast.LENGTH_SHORT).show();
                        if (modelProductList.getF1().equals(spinnerCat1.getSelectedItem().toString())) {
                            listObjects.add(modelProductList);
                        }

                    } else if (spinnerCat2.getVisibility() == View.VISIBLE && spinnerCat3.getVisibility() == View.GONE || spinnerCat3.getSelectedItem().equals("select")) {
                        //Toast.makeText(getApplicationContext(),"> 2" , Toast.LENGTH_SHORT).show();
                        if (list3.contains(modelProductList.getF5())) {
                            listObjects.add(modelProductList);
                        }
                    } else if ((spinnerCat3.getVisibility() == View.VISIBLE && spinnerCat4.getVisibility() == View.GONE) || spinnerCat4.getSelectedItem().equals("select")) {
                        //Toast.makeText(getApplicationContext(),"> 3\n" +  modelProductList.getF1() + "\n" +  modelProductList.getF5() , Toast.LENGTH_SHORT).show();
                        if (modelProductList.getF1().equals(spinnerCat1.getSelectedItem().toString()) && modelProductList.getF5().equals(spinnerCat3.getSelectedItem().toString())) {
                            listObjects.add(modelProductList);
                        }

                    } else {
                        //Toast.makeText(getApplicationContext(),"> 4" , Toast.LENGTH_SHORT).show();
                        if (modelProductList.getF1().equals(spinnerCat1.getSelectedItem().toString()) && modelProductList.getF5().equals(spinnerCat3.getSelectedItem().toString()) && modelProductList.getF6().equals(spinnerCat4.getSelectedItem().toString())) {
                            listObjects.add(modelProductList);
                        } else {
                            listObjects.add(modelProductList);
                        }
                    }*/

                }
                RecyclerAdapterProductView recyclerAdapterProductView = new RecyclerAdapterProductView(getApplicationContext(), listObjects);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(BooksListPageNew.this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(recyclerAdapterProductView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void productByCat1() {
        listObjects = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listObjects.clear();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ModelProductList modelProductList = dataSnapshot1.getValue(ModelProductList.class);
                    if (modelProductList.getF1().equals(spinnerCat1.getSelectedItem().toString())) {
                        listObjects.add(modelProductList);
                    }
                }
                RecyclerAdapterProductView recyclerAdapterProductView = new RecyclerAdapterProductView(getApplicationContext(), listObjects);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(BooksListPageNew.this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(recyclerAdapterProductView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void productByCat2() {
        listObjects = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listObjects.clear();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ModelProductList modelProductList = dataSnapshot1.getValue(ModelProductList.class);
                    if (list3.contains(modelProductList.getF5())) {
                        listObjects.add(modelProductList);
                    }
                }
                RecyclerAdapterProductView recyclerAdapterProductView = new RecyclerAdapterProductView(getApplicationContext(), listObjects);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(BooksListPageNew.this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(recyclerAdapterProductView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void productByCat3() {
        listObjects = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listObjects.clear();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ModelProductList modelProductList = dataSnapshot1.getValue(ModelProductList.class);
                    if (modelProductList.getF1().equals(spinnerCat1.getSelectedItem().toString()) && modelProductList.getF5().equals(spinnerCat3.getSelectedItem().toString())) {
                        listObjects.add(modelProductList);
                    }
                }
                RecyclerAdapterProductView recyclerAdapterProductView = new RecyclerAdapterProductView(getApplicationContext(), listObjects);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(BooksListPageNew.this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(recyclerAdapterProductView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void productByCat4() {
        listObjects = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("ProductbyCourse")
                .child(spinnerCat1.getSelectedItem().toString()).child(spinnerCat2.getSelectedItem().toString()).child(spinnerCat3.getSelectedItem().toString()).child(spinnerCat4.getSelectedItem().toString());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listObjects.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String key = dataSnapshot1.getKey();
                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Products").child(key);
                    databaseReference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ModelProductList modelProductList = dataSnapshot.getValue(ModelProductList.class);
                            listObjects.add(modelProductList);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                RecyclerAdapterProductView recyclerAdapterProductView = new RecyclerAdapterProductView(getApplicationContext(), listObjects);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(BooksListPageNew.this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(recyclerAdapterProductView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setFilter() {
        list1 = new ArrayList<>();
        list2 = new ArrayList<>();
        list3 = new ArrayList<>();
        list4 = new ArrayList<>();

        /*Spinner 1 data collection*/
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Category");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list1.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    list1.add(dataSnapshot1.getKey());
                }

                list1.add(0, "Category");
                ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, list1);
                arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCat1.setAdapter(arrayAdapter1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*Spinner 2 data collection*/
        spinnerCat1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerCat1.getSelectedItem().equals("Category")) {
                    spinnerCat2.setVisibility(View.GONE);
                    spinnerCat3.setVisibility(View.GONE);
                    spinnerCat4.setVisibility(View.GONE);
                } else {
                    databaseReference.child(spinnerCat1.getSelectedItem().toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            list2.clear();
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                list2.add(dataSnapshot1.getKey());
                            }

                            spinnerCat2.setVisibility(View.VISIBLE);
                            list2.add(0, "select");
                            ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, list2);
                            arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerCat2.setAdapter(arrayAdapter2);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /*Spinner 3 data collection*/
        spinnerCat2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerCat2.getSelectedItem().equals("select")) {
                    spinnerCat3.setVisibility(View.GONE);
                } else {
                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Category").child(spinnerCat1.getSelectedItem().toString()).child(spinnerCat2.getSelectedItem().toString());
                    databaseReference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            list3.clear();
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                list3.add(dataSnapshot1.getKey());
                            }
                            spinnerCat3.setVisibility(View.VISIBLE);
                            list3.add(0, "select");
                            ArrayAdapter<String> arrayAdapter3 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, list3);
                            arrayAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerCat3.setAdapter(arrayAdapter3);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /*Spinner 4 data collection*/
        spinnerCat3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerCat3.getSelectedItem().equals("select")) {
                    spinnerCat4.setVisibility(View.GONE);
                } else {
                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Category").child(spinnerCat1.getSelectedItem().toString()).child(spinnerCat2.getSelectedItem().toString()).child(spinnerCat3.getSelectedItem().toString());
                    databaseReference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String sem = String.valueOf(dataSnapshot.getValue());

                            list4.clear();
                            for (int i = 1; i <= Integer.parseInt(sem); i++) {
                                list4.add("" + i);
                            }
                            spinnerCat4.setVisibility(View.VISIBLE);
                            list4.add(0, "select");
                            ArrayAdapter<String> arrayAdapter4 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, list4);
                            arrayAdapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerCat4.setAdapter(arrayAdapter4);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void searchProduct() {
        if (TextUtils.isEmpty(editTextSearchData.getText())) {
            setDataOnPageStart();
            return;
        }

        listObjects = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");
        databaseReference.orderByChild("f2").startAt(editTextSearchData.getText().toString().toUpperCase()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ModelProductList modelProductList = dataSnapshot1.getValue(ModelProductList.class);
                    listObjects.add(modelProductList);
                }
                RecyclerAdapterProductView recyclerAdapterProductView = new RecyclerAdapterProductView(getApplicationContext(), listObjects);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(BooksListPageNew.this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(recyclerAdapterProductView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}