package com.cs407.badgerparking;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class fiveMinReceiver extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {


        Log.d("Alarm", "5 Minute Alarm!!");
        NotificationHelper.getInstance().setNotificationContent("BadgerParking", "5 minutes until overparked!");
        NotificationHelper.getInstance().showNotification(context, "channel_reminders");
    }
}
