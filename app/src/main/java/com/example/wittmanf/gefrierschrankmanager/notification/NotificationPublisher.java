package com.example.wittmanf.gefrierschrankmanager.notification;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationManagerCompat;

import com.example.wittmanf.gefrierschrankmanager.Constants;
import com.example.wittmanf.gefrierschrankmanager.activity.MainActivity;
import com.google.firebase.database.FirebaseDatabase;


/**
 * Publish the notification if a food will be expire
 */
public class NotificationPublisher extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        String itemKey = intent.getStringExtra("itemKey");
        Notification notification = intent.getParcelableExtra("notification");
        int notificationId = intent.getIntExtra("notification_id", 0);

        //Send notification
        notificationManager.notify(notificationId, notification);

        //Update the database to get the notfication only once
        FirebaseDatabase.getInstance().getReference()
                .child(MainActivity.FREEZER_ID)
                .child(Constants.DB_CHILD_ITEMS)
                .child(itemKey)
                .child(Constants.DB_CHILD_EXP_DATE_SHOWN)
                .setValue(true);
    }
}
