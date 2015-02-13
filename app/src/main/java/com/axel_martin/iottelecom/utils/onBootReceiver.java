package com.axel_martin.iottelecom.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.axel_martin.iottelecom.DataService;

/**
 * Created by Martin on 13/02/2015.
 */
public class onBootReceiver  extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = context.getSharedPreferences("com.axel_martin.iottelecom_preferences", 0);
        boolean isEnable = preferences.getBoolean("isServiceStartAtBoot", false);
        Log.d("ON BOOT RECEIVER", String.valueOf(isEnable));
        if(isEnable){
            Intent serviceIntent = new Intent(context, DataService.class);
            context.startService(serviceIntent);
        }

    }
}
