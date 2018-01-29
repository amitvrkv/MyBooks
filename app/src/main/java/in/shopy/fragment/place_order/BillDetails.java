package in.shopy.fragment.place_order;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.shopy.R;
import in.shopy.Utils.Utils;
import in.shopy.app_pref.AppPref;

public class BillDetails extends Fragment implements View.OnClickListener {

    private static View view;
    private static FragmentManager fragmentManager;

    private static TextView total;
    private static TextView delivery_charge;
    private static TextView discount;
    private static TextView grand_total;
    private static TextView walletAmt;
    private static TextView payable_amount;

    private static TextView wallet_amount;
    private static CheckBox checkboxWallet;

    private static EditText promo_code;
    private static Button btn_apply;

    private static Button cancel;
    private static Button btn_continue;

    private static ProgressDialog progressDialog;

    public BillDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_bill_details, container, false);
        initViews();
        setListeners();

        //in.shopy.Utils.BillDetails.setDiscount("0");
        //in.shopy.Utils.BillDetails.setWalletamt("0");
        //in.shopy.Utils.BillDetails.setAppliedPromo("NA");

        walletCheckBox();
        setWalletAmount();

        setBillDetails();

        return view;
    }

    private void initViews() {
        fragmentManager = getActivity().getSupportFragmentManager();

        total = (TextView) view.findViewById(R.id.total);
        delivery_charge = (TextView) view.findViewById(R.id.delivery_charge);
        discount = (TextView) view.findViewById(R.id.discount);
        grand_total = (TextView) view.findViewById(R.id.grand_total);
        walletAmt = (TextView) view.findViewById(R.id.walletAmt);
        payable_amount = (TextView) view.findViewById(R.id.payable_amount);

        wallet_amount = (TextView) view.findViewById(R.id.wallet_amount);
        checkboxWallet = (CheckBox) view.findViewById(R.id.checkboxWallet);

        promo_code = (EditText) view.findViewById(R.id.promo_code);
        btn_apply = (Button) view.findViewById(R.id.btn_apply);

        cancel = (Button) view.findViewById(R.id.cancel);
        btn_continue = (Button) view.findViewById(R.id.btn_continue);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Please wait...");
        progressDialog.setCancelable(false);
    }

    private void setListeners() {
        btn_apply.setOnClickListener(this);
        cancel.setOnClickListener(this);
        btn_continue.setOnClickListener(this);

        promo_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promo_code.setCursorVisible(true);
                promo_code.setError(null);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_apply:
                applyPromoCode();
                break;

            case R.id.cancel:
                getActivity().finish();
                break;

            case R.id.btn_continue:
                loadPaymentPage();
                break;
        }
    }

    private void loadPaymentPage() {
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(in.shopy.R.anim.right_enter, in.shopy.R.anim.left_out)
                .replace(in.shopy.R.id.frameContainer,
                        new PaymentPage(),
                        Utils.Payment).commit();
    }

    private void walletCheckBox() {
        in.shopy.Utils.BillDetails.setWalletamt("0");

        checkboxWallet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    progressDialog.setMessage("Including wallet amount");
                    progressDialog.show();

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                            .child("User")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "*"));
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String wlt_amt = String.valueOf(dataSnapshot.child("wallet").getValue());
                            in.shopy.Utils.BillDetails.setWalletamt(wlt_amt);
                            setBillField();
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                            progressDialog.dismiss();
                        }
                    });
                } else {

                    setBillField();
                }
            }
        });
    }

    private void setWalletAmount() {
        progressDialog.setMessage("Loading bill details...");
        progressDialog.show();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("User")
                .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "*"));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String wlt_amt = String.valueOf(dataSnapshot.child("wallet").getValue());
                wallet_amount.setText(wlt_amt);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    private void setBillDetails() {
        progressDialog.setMessage("Loading bill details...");
        progressDialog.show();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("ORDER")
                .child("ORDERCHARGES");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int min_order = Integer.parseInt(String.valueOf(dataSnapshot.child("MIN_ORDER").getValue()));
                int dil_charge = Integer.parseInt(String.valueOf(dataSnapshot.child("DELIVERY_CHARGE").getValue()));

                int tot = Integer.parseInt(in.shopy.Utils.BillDetails.getTotal());

                if (tot < min_order) {

                    in.shopy.Utils.BillDetails.deliverycharge = String.valueOf(dil_charge);
                } else {

                    in.shopy.Utils.BillDetails.deliverycharge = "0";
                }

                setBillField();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    private void setBillField() {
        promo_code.setCursorVisible(false);

        total.setText(in.shopy.Utils.BillDetails.getTotal());
        delivery_charge.setText(in.shopy.Utils.BillDetails.getDeliverycharge());

        if (in.shopy.Utils.BillDetails.getDiscount() == null) {
            in.shopy.Utils.BillDetails.setDiscount("0");
        }
        discount.setText(in.shopy.Utils.BillDetails.getDiscount());

        int grd_tot = Integer.parseInt(in.shopy.Utils.BillDetails.getTotal()) + Integer.parseInt(in.shopy.Utils.BillDetails.getDeliverycharge()) - Integer.parseInt(in.shopy.Utils.BillDetails.getDiscount());
        grand_total.setText("" + grd_tot);

        if (in.shopy.Utils.BillDetails.getWalletamt() == null) {
            in.shopy.Utils.BillDetails.setWalletamt("0");
        }
        walletAmt.setText(in.shopy.Utils.BillDetails.getWalletamt());

        int pay_amt = grd_tot - Integer.parseInt(in.shopy.Utils.BillDetails.walletamt);
        in.shopy.Utils.BillDetails.setPayable_amount("" + pay_amt);
        payable_amount.setText("" + pay_amt);

        progressDialog.dismiss();
    }

    private void applyPromoCode() {

        in.shopy.Utils.BillDetails.setAppliedPromo("NA");
        in.shopy.Utils.BillDetails.setDiscount("0");

        Animation shakeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        CardView promo_code_layout = (CardView) view.findViewById(R.id.promo_code_layout);

        promo_code.setError(null);

        if (TextUtils.isEmpty(promo_code.getText())) {

            promo_code_layout.setAnimation(shakeAnimation);

            promo_code.setError("Apply PROMO CODE to avail discount");
            setBillField();
            return;
        }

        progressDialog.setMessage("Applying PROMO CODE");
        progressDialog.show();

        final String prm_code = promo_code.getText().toString();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("PROMOCODE");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(prm_code)) {

                    int dis_value = Integer.parseInt(String.valueOf(dataSnapshot.child(prm_code).child("dis_value").getValue()));
                    int min_order = Integer.parseInt(String.valueOf(dataSnapshot.child(prm_code).child("min_order").getValue()));
                    int count = Integer.parseInt(String.valueOf(dataSnapshot.child(prm_code).child("count").getValue()));

                    if (Integer.parseInt(in.shopy.Utils.BillDetails.getTotal()) < min_order) {

                        progressDialog.dismiss();
                        setBillField();
                        AppPref.showAlertDialog(getContext(), "Error",
                                "Minimum order of " + min_order + " required.");

                    } else {

                        progressDialog.dismiss();
                        in.shopy.Utils.BillDetails.setDiscount("" + dis_value);
                        in.shopy.Utils.BillDetails.setAppliedPromo("" + dis_value);
                        setBillField();
                    }

                } else {

                    progressDialog.dismiss();
                    setBillField();
                    AppPref.showAlertDialog(getContext(), "Error", "Invalid PROMO CODE");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                setBillField();
            }
        });
    }

}
