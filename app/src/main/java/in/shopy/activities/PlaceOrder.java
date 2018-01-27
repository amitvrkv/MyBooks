package in.shopy.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import in.shopy.R;
import in.shopy.Utils.Utils;
import in.shopy.fragment.login_2.Login_Fragment;
import in.shopy.fragment.place_order.BillDetails;
import in.shopy.fragment.place_order.DeliveryAddressPage;

public class PlaceOrder extends AppCompatActivity {

    private static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        fragmentManager = getSupportFragmentManager();


        // If savedinstnacestate is null then replace splash fragment
        if (savedInstanceState == null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.frameContainer, new DeliveryAddressPage(),
                            Utils.DeliveryAddress).commit();
        }
    }

    @Override
    public void onBackPressed() {
        // Find the tag of signup and forgot password fragment

        Fragment DeliveryAddress = fragmentManager
                .findFragmentByTag(Utils.DeliveryAddress);
        Fragment BiilDetails = fragmentManager
                .findFragmentByTag(Utils.BiilDetails);
        Fragment Payment = fragmentManager
                .findFragmentByTag(Utils.Payment);


        // Check if both are null or not
        // If both are not null then replace login fragment else do backpressed
        // task

        if (Payment != null) {
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                    .replace(R.id.frameContainer, new BillDetails(),
                            Utils.BiilDetails).commit();
        } else if (BiilDetails != null) {
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                    .replace(R.id.frameContainer, new DeliveryAddressPage(),
                            Utils.DeliveryAddress).commit();
        } else
            super.onBackPressed();
    }
}
