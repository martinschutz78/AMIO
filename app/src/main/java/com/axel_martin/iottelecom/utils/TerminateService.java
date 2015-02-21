package com.axel_martin.iottelecom.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.axel_martin.iottelecom.DataService;

/**
 * @author Axel
 */
public class TerminateService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("KillService", "In TerminateService");
        context.stopService(new Intent(context, DataService.class));
    }
}
