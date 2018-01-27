package in.shopy.fragment.place_order;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import in.shopy.R;
import in.shopy.Utils.MySharedPreference;
import in.shopy.Utils.Utils;
import in.shopy.activities.AddressActivity;

import static android.content.Context.MODE_PRIVATE;

public class DeliveryAddressPage extends Fragment implements View.OnClickListener {
    private static View view;
    private static FragmentManager fragmentManager;

    private static TextView name;
    private static TextView address;
    private static TextView mobileNumber;
    private static Button updateAddress;
    private static Button cancel;
    private static Button btn_continue;

    SharedPreferences sharedPreferences;

    public DeliveryAddressPage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_delivery_address_page, container, false);

        initViews();
        setListeners();
        setAddress();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setAddress();
    }

    private void initViews() {
        fragmentManager = getActivity().getSupportFragmentManager();

        name = (TextView) view.findViewById(R.id.name);
        address = (TextView) view.findViewById(R.id.address);
        mobileNumber = (TextView) view.findViewById(R.id.mobileNumber);

        updateAddress = (Button) view.findViewById(R.id.updateAddress);

        cancel = (Button) view.findViewById(R.id.cancel);
        btn_continue = (Button) view.findViewById(R.id.btn_continue);
    }

    private void setListeners() {
        btn_continue.setOnClickListener(this);
        cancel.setOnClickListener(this);
        updateAddress.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                getActivity().finish();
                break;

            case R.id.btn_continue:
                if (MySharedPreference.isDeliveryAddressCorrect(getContext())) {
                    loadBillDetailsPage();
                } else {
                    Toast.makeText(getContext(), "Please update delivery address", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.updateAddress:
                getContext().startActivity(
                        new Intent(getContext(), AddressActivity.class));
                break;
        }
    }

    private void loadBillDetailsPage() {
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(in.shopy.R.anim.right_enter, in.shopy.R.anim.left_out)
                .replace(in.shopy.R.id.frameContainer,
                        new BillDetails(),
                        Utils.BiilDetails).commit();
    }

    public void setAddress() {

        sharedPreferences = getContext().getSharedPreferences(getString(in.shopy.R.string.sharedPrefDeliveryAddress), MODE_PRIVATE);

        String nameStr = "";
        String addressStr = "";
        String mobileStr = "";

        if (MySharedPreference.isDeliveryAddressCorrect(getContext())) {

            name.setVisibility(View.VISIBLE);
            mobileNumber.setVisibility(View.VISIBLE);
            nameStr = MySharedPreference.getDataFromAddress(getContext(), "Name");
            mobileStr = MySharedPreference.getDataFromAddress(getContext(), "contact");

            addressStr = MySharedPreference.getAddress(getContext());
            addressStr = addressStr.replace(nameStr + "\n","");
            addressStr = addressStr.replace(mobileStr + "\n","");
        } else {

            name.setVisibility(View.GONE);
            addressStr = "Please update your delivery address";
            mobileNumber.setVisibility(View.GONE);
        }

        name.setText(nameStr);
        address.setText(addressStr);
        mobileNumber.setText(mobileStr);
    }
}
