package com.axel_martin.iottelecom.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.axel_martin.iottelecom.DataService;

/**
 * @author Axel
 */
public class TerminateService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.stopService(new Intent(context, DataService.class));
    }
}
