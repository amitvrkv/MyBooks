package com.mybooks.mybooks;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by am361000 on 29/06/17.
 */

public class MyBooksService extends Service {

    DatabaseReference databaseReference;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Toast.makeText(getApplicationContext(), "Service started", Toast.LENGTH_SHORT).show();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Order").orderByChild("from").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                SharedPreferences sharedPreferences;
                sharedPreferences = getSharedPreferences(getString(R.string.sharedPrefOrderIdDetails), MODE_PRIVATE);

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String orderid = ds.getKey().toString();
                    String status = ds.child("status").getValue().toString();

                    if ( sharedPreferences.contains(ds.getKey()) ) {

                        if ( ! sharedPreferences.getString(orderid, null).equals(status) ) {
                            addNotification(orderid, sharedPreferences.getString(orderid, null), status);
                            addToLocalDatabase(orderid, status);
                        }

                    } else {
                        addToLocalDatabase(orderid, status);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    private void addToLocalDatabase(String key, String status) {
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences(getString(R.string.sharedPrefOrderIdDetails), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, status);
        editor.commit();
    }

    private void addNotification(String orderId, String oldStatus, String newStatus) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle("My Books ( Order No: " + orderId + ")")
                        .setContentText("Order status changed from " + oldStatus + " to " + newStatus);

        Intent notificationIntent = new Intent(this, HomeActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}
