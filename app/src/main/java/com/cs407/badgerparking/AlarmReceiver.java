package com.cs407.badgerparking;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class AlarmReceiver extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("alarm", "alarm gen triggered");
        Log.d("alarm", "get int extra: " + intent.getIntExtra("time", -1));

        //todo used for demo, remove before release
        if(intent.getIntExtra("time", 0) ==1){
            NotificationHelper.getInstance().setNotificationContent("BadgerParking", "10 second demo");
            NotificationHelper.getInstance().showNotification(context, "channel_reminders");
        }

        //todo used for demo, remove before release
        if(intent.getIntExtra("time", 0) == 2){
            NotificationHelper.getInstance().setNotificationContent("BadgerParking", "20 second demo");
            NotificationHelper.getInstance().showNotification(context, "channel_reminders");
        }

        if(intent.getIntExtra("time", 0) == 5){
            //trigger 5 minute notification
            Log.d("Alarm", "5 Minute Alarm!!");
            NotificationHelper.getInstance().setNotificationContent("BadgerParking", "5 minutes until overparked!");
            NotificationHelper.getInstance().showNotification(context, "channel_reminders");
        }

        if(intent.getIntExtra("time", 0)== 15){
            Log.d("Alarm", "15 Minute Alarm!!");
            NotificationHelper.getInstance().setNotificationContent("BadgerParking", "15 minutes until overparked!");
            NotificationHelper.getInstance().showNotification(context, "channel_reminders");
            //trigger 15 minute notification
        }

        if(intent.getIntExtra("time", 0) ==30){
            Log.d("Alarm", "30 Minute Alarm!!");
            NotificationHelper.getInstance().setNotificationContent("BadgerParking", "30 minutes until overparked!");
            NotificationHelper.getInstance().showNotification(context, "channel_reminders");

        }

        if(intent.getIntExtra("time", 0) == 60){
            Log.d("Alarm", "60 Minute Alarm!!");
            NotificationHelper.getInstance().setNotificationContent("BadgerParking", "60 minutes until overparked!");
            NotificationHelper.getInstance().showNotification(context, "channel_reminders");

        }


    }
}
