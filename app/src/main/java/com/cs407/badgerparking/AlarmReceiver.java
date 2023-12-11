package com.cs407.badgerparking;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("alarm", "alarm gen triggered");
        Log.d("alarm", "get int extra: " + intent.getIntExtra("time", -1));

        if(intent.getIntExtra("time", 0) == 5){
            //trigger 5 minute notification
            Log.d("Alarm", "5 Minute Alarm!!");
            NotificationHelper.getInstance().setNotificationContent("BadgerParking", "5 minute alarm!");
            NotificationHelper.getInstance().showNotification(context, "channel_reminders");
        }

        if(intent.getIntExtra("time", 0)== 15){
            NotificationHelper.getInstance().setNotificationContent("BadgerParking", "15 minute alarm");
            NotificationHelper.getInstance().showNotification(context, "channel_reminders");

            //trigger 15 minute notification
        }

        if(intent.getIntExtra("time", 0) ==30){
            NotificationHelper.getInstance().setNotificationContent("BadgerParking", "30 minute alarm");
            NotificationHelper.getInstance().showNotification(context, "channel_reminders");

        }

        if(intent.getIntExtra("time", 0) == 60){
            NotificationHelper.getInstance().setNotificationContent("BadgerParking", "one hour alarm");
            NotificationHelper.getInstance().showNotification(context, "channel_reminders");

        }


    }
}
