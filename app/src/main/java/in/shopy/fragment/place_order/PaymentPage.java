package in.shopy.fragment.place_order;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.shopy.R;
import in.shopy.Utils.BillDetails;
import in.shopy.app_pref.AppPref;
import in.shopy.app_pref.MyFormat;
import in.shopy.models.ModelClassBLog;
import in.shopy.models.OrderBookList;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentPage extends Fragment implements View.OnClickListener {
    private static View view;
    private static FragmentManager fragmentManager;

    private static TextView payable_amount;

    private static RadioGroup radioGroup_mop;
    private static RadioButton onlinePayment;
    private static RadioButton cashOnDelivery;

    private static Button btn_continue;
    private static Button cancel;

    private static String mop = "NA";

    private static ProgressDialog progressDialog;

    private static DatabaseReference databaseReference;

    public PaymentPage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_payment_page, container, false);

        initViews();
        setListeners();
        setInitValue();

        setRadioGroup_mop();

        return view;
    }

    private void initViews() {
        fragmentManager = getActivity().getSupportFragmentManager();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Placing order");
        progressDialog.setMessage("Please do not close this window");
        progressDialog.setCancelable(false);

        payable_amount = (TextView) view.findViewById(R.id.payable_amount);

        radioGroup_mop = (RadioGroup) view.findViewById(R.id.radioGroup_mop);
        onlinePayment = (RadioButton) view.findViewById(R.id.onlinePayment);
        cashOnDelivery = (RadioButton) view.findViewById(R.id.cashOnDelivery);

        btn_continue = (Button) view.findViewById(R.id.btn_continue);
        cancel = (Button) view.findViewById(R.id.cancel);
    }

    private void setListeners() {
        btn_continue.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    private void setInitValue() {
        payable_amount.setText("Total Payable Amount: " + in.shopy.Utils.BillDetails.getPayable_amount());

        BillDetails.setDate(MyFormat.getDate());
        BillDetails.setFrom(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        BillDetails.setStatus("Order placed");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_continue:
                placeOrderNow();
                break;

            case R.id.cancel:
                getActivity().finish();
                break;
        }
    }

    private void setRadioGroup_mop() {
        radioGroup_mop.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.onlinePayment:
                        mop = "OP";
                        break;

                    case R.id.cashOnDelivery:
                        mop = "COD";
                        break;
                }
            }
        });
    }

    private void placeOrderNow() {
        if (mop.equalsIgnoreCase("na")) {
            Toast.makeText(getContext(), "Please select mode of payment", Toast.LENGTH_LONG).show();
            return;
        }
        switch (mop) {
            case "OP":
                payOnline();
                break;

            case "COD":
                payByCash();
                break;
        }
    }

    private void payOnline() {
        BillDetails.setPaymentmode("Pay Online");
    }

    private void payByCash() {
        BillDetails.setPaymentmode("Cash on Delivery");
    }

    public void getAppLiveness(final String mop) {
        progressDialog.show();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Configs");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String live = String.valueOf(dataSnapshot.child("app_liveness").getValue());
                String error_msg = String.valueOf(dataSnapshot.child("error_msg").getValue());

                if (live.equalsIgnoreCase("true")) {
                    getAndUpdateOrderId();
                } else {
                    Toast.makeText(getContext(), error_msg, Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Getting order number and updating
    public void getAndUpdateOrderId() {
        if (AppPref.checkNetworkConnection(getContext()) == false) {
            Toast.makeText(getContext(), "Please check your internet connection.", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.show();

        final int[] ordernumber = {0};
        databaseReference = FirebaseDatabase.getInstance().getReference().child("ORDER").child("ORDERCOUNT").child("MYORDER");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ordernumber[0] = Integer.parseInt(dataSnapshot.getValue().toString());
                ordernumber[0] = ordernumber[0] + 1;
                databaseReference.setValue(String.valueOf(ordernumber[0]));

                BillDetails.setOrderid("" + ordernumber[0]);

                placeOrderOnFirebase();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                //orderPlaceFailed("Failed to place order. Try again!!!");
            }
        });
    }

    //Updataing order details on firebase
    public void placeOrderOnFirebase() {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("");

    }
}
