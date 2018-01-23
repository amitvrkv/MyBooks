package in.shopy.app_pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by am361000 on 13/01/18.
 */

public class MyFirebase {
    public static void updateOnMobileVerification(final Context context, String number){
        FirebaseDatabase.getInstance().getReference()
                .child("User").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "*"))
                .child("address").child("isVerified").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                SharedPreferences sharedPreferences;
                sharedPreferences = context.getSharedPreferences(context.getString(in.shopy.R.string.sharedPrefDeliveryAddress), MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("isVerified","true");
                editor.commit();
            }
        });

        FirebaseDatabase.getInstance().getReference()
                .child("MOBILE_VERIFICATION")
                .child("PENDING_VERIFICATION")
                .child(number).setValue(null);
    }
}
