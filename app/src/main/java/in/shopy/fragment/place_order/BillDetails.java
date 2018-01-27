package in.shopy.fragment.place_order;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import in.shopy.R;
import in.shopy.Utils.Utils;

public class BillDetails extends Fragment implements View.OnClickListener {

    private static View view;
    private static FragmentManager fragmentManager;

    private static Button cancel;
    private static Button btn_continue;

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
        return view;
    }

    private void initViews() {
        fragmentManager = getActivity().getSupportFragmentManager();

        cancel = (Button) view.findViewById(R.id.cancel);
        btn_continue = (Button) view.findViewById(R.id.btn_continue);
    }

    private void setListeners() {
        cancel.setOnClickListener(this);
        btn_continue.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                getActivity().finish();
                break;

            case R.id.btn_continue:
                loadBillDetailsPage();
                break;
        }
    }

    private void loadBillDetailsPage(){
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(in.shopy.R.anim.right_enter, in.shopy.R.anim.left_out)
                .replace(in.shopy.R.id.frameContainer,
                        new PaymentPage(),
                        Utils.Payment).commit();
    }
}
