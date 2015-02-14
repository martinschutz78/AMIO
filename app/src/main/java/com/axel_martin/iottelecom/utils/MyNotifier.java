package com.axel_martin.iottelecom.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.axel_martin.iottelecom.MainActivity;
import com.axel_martin.iottelecom.R;

/**
 * @author Axel
 */
public class MyNotifier {
    private NotificationManager notificationManager;
    private Context context;

    public MyNotifier(Context context){

        this.context = context;

        //Create the Notification manager
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        createPermanentNotify();
    }

    public void createPermanentNotify(){

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);
        Intent deleteIntent = new Intent(context, TerminateService.class);
        PendingIntent pendingIntentTerminate = PendingIntent.getBroadcast(context, 0, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        //Create Notification
        NotificationCompat.Builder started = new NotificationCompat.Builder(context)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(context.getResources().getString(R.string.RunningBackground))
                .setContentIntent(resultPendingIntent)
                .setSmallIcon(R.drawable.small_notification)
                        //.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.large_notification))
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setColor(context.getResources().getColor(R.color.colorPrimary_Light))
                .setShowWhen(false)
                .setOngoing(true)
                .addAction(R.drawable.abc_ic_clear_mtrl_alpha, context.getResources().getString(R.string.Terminate), pendingIntentTerminate);

        //Add notification and it's id to the manager
        notificationManager.notify(1, started.build());
    }

    public void createLightNotify(double mote, boolean important) {
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        //Create Notification
        NotificationCompat.Builder started = new NotificationCompat.Builder(context)
                .setContentTitle(context.getResources().getString(R.string.LightAlert) + " " + Double.toString(mote))
                .setContentText(context.getResources().getString(R.string.LightAlertContent))
                .setContentIntent(resultPendingIntent)
                .setSmallIcon(R.drawable.small_notification)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.large_light_notification))
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setColor(context.getResources().getColor(R.color.colorPrimary_Light))
                .setGroup(context.getResources().getString(R.string.app_name))
                .setDefaults(NotificationCompat.DEFAULT_ALL);   //Sound, vibration and LED

        if (important) {
            started.setPriority(NotificationCompat.PRIORITY_MAX);
        } else {
            started.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }

        //Add notification and it's id to the manager
        notificationManager.notify(2, started.build());
    }

    public void createTemperatureNotify(double mote, boolean important) {
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        //Create Notification
        NotificationCompat.Builder started = new NotificationCompat.Builder(context)
                .setContentTitle(context.getResources().getString(R.string.TemperatureAlert) + " " + Double.toString(mote))
                .setContentText(context.getResources().getString(R.string.TemperatureAlertContent))
                .setContentIntent(resultPendingIntent)
                .setSmallIcon(R.drawable.small_notification)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.large_light_notification))
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setColor(context.getResources().getColor(R.color.colorPrimary_Light))
                .setGroup(context.getResources().getString(R.string.app_name))
                .setDefaults(NotificationCompat.DEFAULT_ALL);   //Sound, vibration and LED

        if (important) {
            started.setPriority(NotificationCompat.PRIORITY_MAX);
        } else {
            started.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }

        //Add notification and it's id to the manager
        notificationManager.notify(3, started.build());
    }

    public void createHumidityNotify(double mote, boolean important) {
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        //Create Notification
        NotificationCompat.Builder started = new NotificationCompat.Builder(context)
                .setContentTitle(context.getResources().getString(R.string.HumidityAlert) + " " + Double.toString(mote))
                .setContentText(context.getResources().getString(R.string.HumidityAlertContent))
                .setContentIntent(resultPendingIntent)
                .setSmallIcon(R.drawable.small_notification)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.large_light_notification))
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setColor(context.getResources().getColor(R.color.colorPrimary_Light))
                .setGroup(context.getResources().getString(R.string.app_name))
                .setDefaults(NotificationCompat.DEFAULT_ALL);   //Sound, vibration and LED

        if (important) {
            started.setPriority(NotificationCompat.PRIORITY_MAX);
        } else {
            started.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }

        //Add notification and it's id to the manager
        notificationManager.notify(4, started.build());
    }

    public void createSummaryNotify(boolean important) {
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        //Create Notification
        NotificationCompat.Builder started = new NotificationCompat.Builder(context)
                .setContentTitle(context.getResources().getString(R.string.MultipleAlerts))
                .setContentText(context.getResources().getString(R.string.MultipleAlertsContent))
                .setContentIntent(resultPendingIntent)
                .setSmallIcon(R.drawable.small_notification)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.large_light_notification))
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setColor(context.getResources().getColor(R.color.colorPrimary_Light))
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine("First Line    Light")
                        .addLine("Second Line   Empty")
                        .setBigContentTitle(context.getResources().getString(R.string.MultipleAlerts))
                        .setSummaryText(context.getResources().getString(R.string.MultipleAlertsContent)))
                .setGroup(context.getResources().getString(R.string.app_name))
                .setGroupSummary(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);   //Sound, vibration and LED

        if (important) {
            started.setPriority(NotificationCompat.PRIORITY_MAX);
        } else {
            started.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }

        //Add notification and it's id to the manager
        notificationManager.notify(4, started.build());
    }

    public void cancelAll() {
        notificationManager.cancelAll();
    }
}
