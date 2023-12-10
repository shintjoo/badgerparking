package com.cs407.badgerparking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getIntExtra("time", 0) == 5){
            //trigger 5 minute notification
            Log.d("Alarm", "5 Minute Alarm!!");
        }
        if(intent.getIntExtra("time", 0)== 10){
            //trigger 10 minute notification
        }
        if(intent.getIntExtra("time", 0) ==15){
            //trigger 15 minute notification
        }
        if(intent.getIntExtra("time", 0) == 20){
            //trigger 20 minutes notification
        }
    }
}
