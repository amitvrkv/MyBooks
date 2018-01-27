package in.shopy.fragment.place_order;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import in.shopy.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentPage extends Fragment implements View.OnClickListener {
    private static View view;
    private static FragmentManager fragmentManager;

    private static Button cancel;

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
        return view;
    }

    private void initViews() {
        fragmentManager = getActivity().getSupportFragmentManager();

        cancel = (Button) view.findViewById(R.id.cancel);
    }

    private void setListeners() {
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                getActivity().finish();
        }
    }
}
