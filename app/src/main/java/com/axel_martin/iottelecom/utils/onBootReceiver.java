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
            serviceIntent.putExtra(DataService.POLLING_REF, preferences.getInt("interval", 60000));
            serviceIntent.putExtra(DataService.CACHE_REF, preferences.getInt("cache", 10));
            serviceIntent.putExtra(DataService.IS_TEMPERATURE_ALERT_REF, preferences.getBoolean("isTemperatureEnable", false));
            serviceIntent.putExtra(DataService.MAX_TEMPERATURE_REF, preferences.getInt("maximumTemperature", -1));
            serviceIntent.putExtra(DataService.MIN_TEMPERATURE_REF, preferences.getInt("minimumTemperature", -1));
            serviceIntent.putExtra(DataService.IS_LIGHT_ALERT_REF, preferences.getBoolean("isLightEnable", false));
            serviceIntent.putExtra(DataService.LIGHT_REF, preferences.getInt("light", -1));
            serviceIntent.putExtra(DataService.SCHEDULDED_REF, preferences.getBoolean("isTimeEnable", false));
            serviceIntent.putExtra(DataService.START_TIME_REF, preferences.getString("startTime", ""));
            serviceIntent.putExtra(DataService.END_TIME_REF, preferences.getString("endTime", ""));
            serviceIntent.putExtra(DataService.MAIL_REF, preferences.getBoolean("isMailEnable", false));
            serviceIntent.putExtra(DataService.MAIL_ADDRESS_REF, preferences.getString("mailAddress", ""));
            serviceIntent.putExtra(DataService.SMS_REF, preferences.getBoolean("isSmsEnable", false));
            serviceIntent.putExtra(DataService.SMS_ADDRESS_REF, preferences.getString("telNumber", ""));
            context.startService(serviceIntent);
        }

    }
}
