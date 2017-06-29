package com.mybooks.mybooks;

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
import android.os.Vibrator;
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

                    if (status.equals(getString(R.string.order_cancelled)) || status.equals(getString(R.string.delivered))) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove(orderid);
                        editor.apply();
                    } else if (sharedPreferences.contains(ds.getKey())) {
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

        String title = "My Books ( Order No: " + orderId + ")";
        String msg;

        if (oldStatus.equals("new"))
            msg = "New order placed with Order ID: " + oldStatus;
        else
            msg = "Order status changed from " + oldStatus + " to " + newStatus;

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        //.setLargeIcon(bitmap)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle(title).setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg)
                        .setAutoCancel(true);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        builder.setVibrate(new long[]{500, 500, 500, 500});

        Intent notificationIntent = new Intent(this, OrderPageActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}
