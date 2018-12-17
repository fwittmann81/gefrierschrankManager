package com.example.wittmanf.gefrierschrankmanager;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class BaseApplication extends Application {

    public static final String EXP_CHANNEL_ID = "expChannel";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    EXP_CHANNEL_ID,
                    "expChannel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Essen läuft ab");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
