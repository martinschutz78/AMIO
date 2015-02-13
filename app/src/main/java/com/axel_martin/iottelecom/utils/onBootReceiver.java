package com.axel_martin.iottelecom.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.axel_martin.iottelecom.DataService;

/**
 * Created by Martin on 13/02/2015.
 */
public class onBootReceiver  extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, DataService.class);
        context.startService(serviceIntent);
    }
}
