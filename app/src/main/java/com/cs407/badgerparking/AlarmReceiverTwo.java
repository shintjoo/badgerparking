package com.cs407.badgerparking;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class AlarmReceiverTwo extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("alarm", "alarm gen triggered");
        Log.d("alarm", "get int extra: " + intent.getIntExtra("time", -1));


        NotificationHelper.getInstance().setNotificationContent("BadgerParking", "20 second demo");
        NotificationHelper.getInstance().showNotification(context, "channel_reminders");



    }
}
