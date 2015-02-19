package com.axel_martin.iottelecom;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.axel_martin.iottelecom.model.Measure;
import com.axel_martin.iottelecom.utils.TerminateService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

/**
 * Created by Martin on 09/02/2015.
 */
public class DataService extends Service {

    private ArrayList<Measure> measures;
    private NotificationManager notificationManager;

    private Timer timer;
    private int interval = 60000;
    private int cache = 10;
    private boolean isTemperature = false;
    private int minTemperatureTrigger = -1;
    private int maxTemperatureTrigger = -1;
    private boolean isLight = false;
    private int lightTtrigger = -1;
    private boolean isSchedulded = false;
    private String startTime = "";
    private String endTime ="";
    private boolean isMail = false;
    private String mailAddress = "";
    private boolean isSms = false;
    private String smsAddress = "";


    private static final int MINUTE_IN_MILLIS = 60000;
    public static final String POLLING_REF = "pollingTime";
    public static final String CACHE_REF = "cacheValue";
    public static final String IS_TEMPERATURE_ALERT_REF = "temperatureBoolean";
    public static final String MAX_TEMPERATURE_REF = "maxTemperature";
    public static final String MIN_TEMPERATURE_REF = "minTemperature";
    public static final String IS_LIGHT_ALERT_REF = "lightBoolean";
    public static final String LIGHT_REF = "light";
    public static final String SCHEDULDED_REF = "scheduldedBoolean";
    public static final String START_TIME_REF = "startTime";
    public static final String END_TIME_REF = "endTime";
    public static final String MAIL_REF = "mailBoolean";
    public static final String MAIL_ADDRESS_REF = "mailAddress";
    public static final String SMS_REF = "smsBoolean";
    public static final String SMS_ADDRESS_REF = "smsAddress";

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            Log.d("CATCH", "CATCH BROADCAST");
            flushMeasures();
        }
    };

    private BroadcastReceiver firstReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            Log.d("CATCH", "CATCH FIRST BROADCAST");
            if(measures.size()>0){
                sendMeasures();
                flushMeasures();
            } else {
                try {
                    updateData();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            Log.d("DATA SERVICE", "UPDATE SERVICE");
            interval = bundle.getInt(POLLING_REF)*MINUTE_IN_MILLIS;
            cache = bundle.getInt(CACHE_REF);
            isTemperature = bundle.getBoolean(IS_TEMPERATURE_ALERT_REF);
            minTemperatureTrigger = bundle.getInt(MIN_TEMPERATURE_REF);
            maxTemperatureTrigger = bundle.getInt(MAX_TEMPERATURE_REF);
            isLight = bundle.getBoolean(IS_LIGHT_ALERT_REF);
            lightTtrigger = bundle.getInt(LIGHT_REF);
            isSchedulded = bundle.getBoolean(SCHEDULDED_REF);
            startTime = bundle.getString(START_TIME_REF);
            endTime = bundle.getString(END_TIME_REF);
            isMail = bundle.getBoolean(MAIL_REF);
            mailAddress = bundle.getString(MAIL_ADDRESS_REF);
            isSms = bundle.getBoolean(SMS_REF);
            smsAddress = bundle.getString(SMS_ADDRESS_REF);

            timer.cancel();
            timer.purge();
            myStartService();
            //startTimer(interval);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        measures = new ArrayList<>();
        registerReceiver(firstReceiver, new IntentFilter("com.axel_martin.iottelecom.MainActivity.FIRST"));
        registerReceiver(receiver, new IntentFilter("com.axel_martin.iottelecom.MainActivity.FLUSH"));
        registerReceiver(updateReceiver, new IntentFilter("com.axel_martin.iottelecom.MainActivity.UPDATE"));
        myStartService();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(firstReceiver);
        unregisterReceiver(receiver);
        unregisterReceiver(updateReceiver);
        notificationManager.cancelAll();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void myStartService(){
        timer = new Timer();
        createNotify();
        startTimer(interval);
    }

    public void startTimer(int myInterval){
        timer.scheduleAtFixedRate(new TimerTask() {
                                      @Override
                                      public void run() {
                                          try {
                                              updateData();
                                          } catch (Exception e) {
                                              e.printStackTrace();
                                          }
                                      }
                                  },
                10000,
                myInterval);
    }

    private void createNotify(){
        //On créer un "gestionnaire de notification"
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);
        Intent deleteIntent = new Intent(this, TerminateService.class);
        PendingIntent pendingIntentTerminate = PendingIntent.getBroadcast(this, 0, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        //On créer la notification
        //Avec son icône et son texte défilant (optionel si l'on veut pas de texte défilant on met cet argument à null)
        NotificationCompat.Builder started = new NotificationCompat.Builder(this)
                .setContentTitle("IoT TELECOM Nancy")
                .setContentText("Running background...")
                .setContentIntent(resultPendingIntent)
                .setSmallIcon(R.drawable.small_notification)
                //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.large_notification))
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setColor(getResources().getColor(R.color.colorPrimary_Light))
                .setShowWhen(false)
                .setOngoing(true)
                .addAction(R.drawable.abc_ic_clear_mtrl_alpha, "Terminer", pendingIntentTerminate);

        //Enfin on ajoute notre notification et son ID à notre gestionnaire de notification
        notificationManager.notify(1, started.build());
    }

    public void updateData() throws ExecutionException, InterruptedException {

        AsyncTask<String, String, Measure> parser = new AsyncTask<String, String, Measure>() {

            @Override
            protected Measure doInBackground(String... params) {
                ObjectMapper mapper = new ObjectMapper();

                try {
                    Measure measure = mapper.readValue(params[0], Measure.class);
                    return measure;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };


        AsyncTask<String, String, String> httpTask = new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                HttpClient client = new DefaultHttpClient();
                HttpGet getRequest = new HttpGet(params[0]);
                HttpResponse response;
                String result = "";
                try {
                    response = client.execute(getRequest);

                    HttpEntity entity = response.getEntity();
                    // If the response does not enclose an entity, there is no need
                    // to worry about connection release

                    if (entity != null) {

                        // A Simple JSON Response Read
                        InputStream instream = entity.getContent();
                        result = convertStreamToString(instream);
                        // now you have the string representation of the HTML request
                        instream.close();
                    }

                } catch (IOException e) {
                    result = "connection failed";
                }
                Log.d("HTTPresult", result);
                return result;
            }
        };
        httpTask.execute("http://iotlab.telecomnancy.eu/rest/data/1/light1-temperature-humidity/last");
        Log.d("Service", "Finish task");
        String result = httpTask.get();
        parser.execute(result);

        addMeasure(parser.get());
        sendMeasures();
    }

    public void sendMeasures(){
        Intent intent = new Intent("com.axel_martin.iottelecom");
        intent.putExtra("RESULT", measures);
        sendBroadcast(intent);
    }

    public void addMeasure(Measure measure){
        if(measures.size()>cache){
            measures.remove(0);
            for(int i=0; i<measures.size()-1;i++){
                measures.set(i, measures.get(i+1));
            }
            measures.remove(measures.size()-1);
        }
        measures.add(measure);
    }

    public void flushMeasures(){
        Log.d("FLUSH", "FLUSH MEASURES");
        measures.clear();
    }

    private static String convertStreamToString(InputStream is) {
    /*
     * To convert the InputStream to String we use the BufferedReader.readLine()
     * method. We iterate until the BufferedReader return null which means
     * there's no more data to read. Each line will appended to a StringBuilder
     * and returned as String.
     */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    @Override
    public boolean stopService(Intent name) {
        timer.cancel();
        timer.purge();
        Log.d("DATA SERVICE", "THE STOP SERVICE HAS BEEN CALLED");
        return super.stopService(name);
    }
}
