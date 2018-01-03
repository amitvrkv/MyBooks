package com.mybooks.mybooks.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mybooks.mybooks.R;

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

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Order").orderByChild("from").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                SharedPreferences sharedPreferences;
                sharedPreferences = getSharedPreferences(getString(R.string.sharedPrefOrderIdDetails), MODE_PRIVATE);

                String orderid = "";
                String status = "";

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if (ds.child("status").getValue() == null ) {
                        continue;
                    }

                    orderid = ds.getKey().toString();
                    status = (String) ds.child("status").getValue();

                    if (sharedPreferences.contains(ds.getKey())) {
                        if (!sharedPreferences.getString(orderid, null).equals(status)) {
                            showNotification(orderid, sharedPreferences.getString(orderid, null), status);
                            addToLocalDatabase(orderid, status);
                        }
                    } else {
                        showNotification(orderid, "new", status);
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

    private void showNotification(String orderId, String oldStatus, String newStatus) {
        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.app_icon);

        String title = "My Books ";
        String msg;

        if (oldStatus.equals("new")) {
            msg = "New order placed with Order ID: " + orderId;
        } else if (oldStatus.equals("comment")) {
            msg = newStatus;
        } else
            msg = "Order No: " + orderId + "\nYour order is " + newStatus;

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle(title).setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg)
                        .setAutoCancel(true);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        builder.setVibrate(new long[]{300, 300, 300, 300});

        //Intent notificationIntent = new Intent(this, OrderPageActivity.class);

        //PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
          //      PendingIntent.FLAG_UPDATE_CURRENT);
        //builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}
