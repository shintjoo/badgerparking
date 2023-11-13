package com.cs407.badgerparking;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class NotificationHelper {

    public static final NotificationHelper INSTANCE = new NotificationHelper();

    private NotificationHelper(){}

    private static NotificationHelper getInstance(){
        return INSTANCE;
    }

    public static final String CHANNEL_ID = "channel_reminders";

    public void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Reminders";
            String description = "All Time Reminders";

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private int notificationID = 0;
    private String sender = null;
    private String message = null;

    public void SetNotificationContent(String sender, String message) {
        this.sender = sender;
        this.message = message;
        this.notificationID++;
    }


}
