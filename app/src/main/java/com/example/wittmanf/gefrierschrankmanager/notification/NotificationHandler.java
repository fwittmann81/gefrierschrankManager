package com.example.wittmanf.gefrierschrankmanager.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.core.app.NotificationCompat;

import com.example.wittmanf.gefrierschrankmanager.BaseApplication;
import com.example.wittmanf.gefrierschrankmanager.Constants;
import com.example.wittmanf.gefrierschrankmanager.Item;
import com.example.wittmanf.gefrierschrankmanager.R;
import com.example.wittmanf.gefrierschrankmanager.activity.ShowDetailsActivity;

import java.util.Date;

public class NotificationHandler {

    public static void setNextNotification(Context context, Item item) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.example.wittmanf.gefrierschrankmanager", Context.MODE_PRIVATE);

        long notifyBefore = Constants.ALERTIME_DAYS_MAPPING.get(sharedPreferences.getString(Constants.SP_ALERT_TIME, Constants.ALERT_ONE_WEEK));
        long notificationTime = item.getMaxFreezeDate().getTime() - notifyBefore * 86400000;        //86400000ms = 24h

        String message;
        if (item.getMaxFreezeDate().before(new Date())) {
            message = item.getName() + " ist am " + Constants.SDF.format(item.getMaxFreezeDate()) + " abgelaufen!";
        } else {
            message = item.getName() + " läuft am " + Constants.SDF.format(item.getMaxFreezeDate()) + " ab!";
        }

        int notificationId = calculateNotificationId(item.getCreationDate().getTime());

        scheduleNotification(context, notificationTime, notificationId, message, item);
    }

    public static void deleteNotification(Context context, Item item) {
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        int notificationId = calculateNotificationId(item.getMaxFreezeDate().getTime());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private static void scheduleNotification(Context context, long notificationTime, int notificationId, String message, Item item) {
        Intent intent = new Intent(context, ShowDetailsActivity.class);
        intent.putExtra("selectedItem", item);
        PendingIntent pendingActivity = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context, BaseApplication.EXP_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_freeze)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pendingActivity)
                .build();

        Intent notificationIntent = new Intent(context, NotificationPublisher.class);

        notificationIntent.putExtra("notification_id", notificationId);
        notificationIntent.putExtra("notification", notification);
        notificationIntent.putExtra("itemKey", item.getDatabaseKey());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
    }

    private static int calculateNotificationId(long id) {
        return (int) (id - (long) (Math.floor(id / 1000000000) * 1000000000));
    }
}
