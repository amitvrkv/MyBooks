package com.mybooks.mybooks.app_pref;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by am361000 on 13/01/18.
 */

public class MyFirebase {
    public static void updateOnMobileVerification(String number){
        FirebaseDatabase.getInstance().getReference()
                .child("User").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "*"))
                .child("address").child("isVerified").setValue("true");
        FirebaseDatabase.getInstance().getReference()
                .child("MOBILE_VERIFICATION")
                .child("PENDING_VERIFICATION")
                .child(number).setValue(null);
    }
}
