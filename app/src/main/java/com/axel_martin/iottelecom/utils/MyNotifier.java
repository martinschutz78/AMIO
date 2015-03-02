package com.axel_martin.iottelecom.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.SmsManager;
import android.util.Log;

import com.axel_martin.iottelecom.MainActivity;
import com.axel_martin.iottelecom.R;

import java.util.Calendar;

/**
 * @author Axel
 */
public class MyNotifier {
    private final String startTime;
    private final String endTime;
    private final boolean isMail;
    private final String mailAddress;
    private final boolean isSms;
    private final String smsAddress;
    private final boolean isScheduled;
    private boolean important;
    private NotificationManager notificationManager;
    private Context context;
    private SendMail sendMail;
    private SmsManager smsManager;

    public MyNotifier(Context context, boolean isScheduled, String startTime, String endTime, boolean isMail, String mailAddress, boolean isSms, String smsAddress){

        this.context = context;
        this.isScheduled = isScheduled;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isMail = isMail;
        this.mailAddress = mailAddress;
        this.isSms = isSms;
        this.smsAddress = smsAddress;
        this.important = checkImportant();
        Log.d("TimeStart", startTime);

        //Get SMSManager
        smsManager = SmsManager.getDefault();

        //Create the Notification manager
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        createPermanentNotify();
    }

    public Notification createPermanentNotify(){

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);
        Intent stopIntent = new Intent("com.axel_martin.iottelecom.MyNotifier.STOP");
        PendingIntent pendingIntentTerminate = PendingIntent.getBroadcast(context, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

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

        return started.build();
    }

    public void createLightNotify(double mote, double value) {
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);

        important = checkImportant();

        //Create Mail and send it if in non important period
        if (!important) {
            if (isMail) {
                sendMail = new SendMail(context, mailAddress,context.getResources().getString(R.string.LightAlert) + " " + Double.toString(mote), context.getResources().getString(R.string.LightAlertContent) + " " + Double.toString(value) + "lx");
                try {
                    sendMail.send();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (isSms) {
                smsManager.sendTextMessage(smsAddress, null, context.getResources().getString(R.string.LightAlert) + " " + Double.toString(mote) + "\n" + context.getResources().getString(R.string.LightAlertContent) + " " + Double.toString(value) + "lx", null, null);
            }
        }

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
                .setContentText(context.getResources().getString(R.string.LightAlertContent) + "\n" + Double.toString(value) + "lx")
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

    public void createTemperatureNotify(double mote, double value) {
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);

        important = checkImportant();

        //Create Mail and send it if in non important period
        if (!important) {
            if (isMail) {
                sendMail = new SendMail(context, mailAddress,context.getResources().getString(R.string.TemperatureAlert) + " " + Double.toString(mote), context.getResources().getString(R.string.TemperatureAlertContent) + " " + Double.toString(value) + "°C");
                try {
                    sendMail.send();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (isSms) {
                smsManager.sendTextMessage(smsAddress, null, context.getResources().getString(R.string.TemperatureAlert) + " " + Double.toString(mote) + "\n" + context.getResources().getString(R.string.TemperatureAlertContent) + " " + Double.toString(value) + "°C", null, null);
            }
        }

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
                .setContentText(context.getResources().getString(R.string.TemperatureAlertContent) + "\n" + Double.toString(value) + "°C")
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

//    public void createHumidityNotify(double mote, boolean important) {
//        // Creates an explicit intent for an Activity in your app
//        Intent resultIntent = new Intent(context, MainActivity.class);
//
//        // The stack builder object will contain an artificial back stack for the
//        // started Activity.
//        // This ensures that navigating backward from the Activity leads out of
//        // your application to the Home screen.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//        // Adds the back stack for the Intent (but not the Intent itself)
//        stackBuilder.addParentStack(MainActivity.class);
//        // Adds the Intent that starts the Activity to the top of the stack
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent =
//                stackBuilder.getPendingIntent(
//                        0,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//
//        //Create Notification
//        NotificationCompat.Builder started = new NotificationCompat.Builder(context)
//                .setContentTitle(context.getResources().getString(R.string.HumidityAlert) + " " + Double.toString(mote))
//                .setContentText(context.getResources().getString(R.string.HumidityAlertContent))
//                .setContentIntent(resultPendingIntent)
//                .setSmallIcon(R.drawable.small_notification)
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.large_light_notification))
//                .setCategory(NotificationCompat.CATEGORY_ALARM)
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                .setColor(context.getResources().getColor(R.color.colorPrimary_Light))
//                .setGroup(context.getResources().getString(R.string.app_name))
//                .setDefaults(NotificationCompat.DEFAULT_ALL);   //Sound, vibration and LED
//
//        if (important) {
//            started.setPriority(NotificationCompat.PRIORITY_MAX);
//        } else {
//            started.setPriority(NotificationCompat.PRIORITY_DEFAULT);
//        }
//
//        //Add notification and it's id to the manager
//        notificationManager.notify(4, started.build());
//    }
//
//    public void createSummaryNotify(boolean important) {
//        // Creates an explicit intent for an Activity in your app
//        Intent resultIntent = new Intent(context, MainActivity.class);
//
//        // The stack builder object will contain an artificial back stack for the
//        // started Activity.
//        // This ensures that navigating backward from the Activity leads out of
//        // your application to the Home screen.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//        // Adds the back stack for the Intent (but not the Intent itself)
//        stackBuilder.addParentStack(MainActivity.class);
//        // Adds the Intent that starts the Activity to the top of the stack
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent =
//                stackBuilder.getPendingIntent(
//                        0,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//
//        //Create Notification
//        NotificationCompat.Builder started = new NotificationCompat.Builder(context)
//                .setContentTitle(context.getResources().getString(R.string.MultipleAlerts))
//                .setContentText(context.getResources().getString(R.string.MultipleAlertsContent))
//                .setContentIntent(resultPendingIntent)
//                .setSmallIcon(R.drawable.small_notification)
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.large_light_notification))
//                .setCategory(NotificationCompat.CATEGORY_ALARM)
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                .setColor(context.getResources().getColor(R.color.colorPrimary_Light))
//                .setStyle(new NotificationCompat.InboxStyle()
//                        .addLine("First Line    Light")
//                        .addLine("Second Line   Empty")
//                        .setBigContentTitle(context.getResources().getString(R.string.MultipleAlerts))
//                        .setSummaryText(context.getResources().getString(R.string.MultipleAlertsContent)))
//                .setGroup(context.getResources().getString(R.string.app_name))
//                .setGroupSummary(true)
//                .setDefaults(NotificationCompat.DEFAULT_ALL);   //Sound, vibration and LED
//
//        if (important) {
//            started.setPriority(NotificationCompat.PRIORITY_MAX);
//        } else {
//            started.setPriority(NotificationCompat.PRIORITY_DEFAULT);
//        }
//
//        //Add notification and it's id to the manager
//        notificationManager.notify(4, started.build());
//    }

    public void cancelAll() {
        notificationManager.cancelAll();
    }

    public boolean checkImportant(){
        //Check day and time
        if(isScheduled){
            Log.d("TIME", startTime);

            String sTime[] = startTime.split(":");
            String eTime[] = endTime.split(":");

            Calendar start = Calendar.getInstance();
            start.setTimeInMillis(System.currentTimeMillis());
            start.set(Calendar.HOUR, Integer.parseInt(sTime[0]));
            start.set(Calendar.MINUTE, Integer.parseInt(sTime[1]));

            Calendar end = Calendar.getInstance();
            start.setTimeInMillis(System.currentTimeMillis());
            end.set(Calendar.HOUR, Integer.parseInt(eTime[0]));
            end.set(Calendar.MINUTE, Integer.parseInt(eTime[1]));

            Calendar now = Calendar.getInstance();
            start.setTimeInMillis(System.currentTimeMillis());

            Log.d("After", Boolean.toString(start.before(now)));
            if (start.before(now) && end.after(now)) {
                return true;
            }
        }
        return false;
    }
}
