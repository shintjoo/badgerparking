package com.cs407.badgerparking;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {

    public static final NotificationHelper INSTANCE = new NotificationHelper();

    private NotificationHelper(){}

    public static NotificationHelper getInstance(){
        return INSTANCE;
    }


    /*
    * NotificationHelper is built using discrete notification channels just in case we want to send
    * more than one kind of notification in the future
    *
    * If we need to add more, create a new string for createXNotificationChannel
    * and make a new channel ID
    * */

    public void createRemindNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Reminders";
            String description = "All Time Reminders";

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel remindChannel = new NotificationChannel(context.getString(R.string.remind_noti_channel_id), name, importance);
            remindChannel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(remindChannel);
        }
    }

    private int notificationID = 0;
    private String sender = null;
    private String message = null;

    public void setNotificationContent(String sender, String message) {
        this.sender = sender;
        this.message = message;
        this.notificationID++;
    }

    public void showNotification(Context context, String channel_id) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED){
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channel_id)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentText(sender)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationID, builder.build());
    }


}
