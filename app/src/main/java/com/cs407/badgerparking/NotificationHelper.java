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

    public static final String REMIND_CHANNEL_ID = "channel_reminders";

    /*
    * NotificationHelper is built using discrete notification channels just in case we want to send
    * more than one kind of notification in the future
    *
    * If we need to add more, create a new method for createXNotificationChannel
    * and make a new channel ID
    * */

    public void createRemindNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Reminders";
            String description = "All Time Reminders";

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel remindChannel = new NotificationChannel(REMIND_CHANNEL_ID, name, importance);
            remindChannel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(remindChannel);
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
