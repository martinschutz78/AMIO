package com.axel_martin.iottelecom;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.axel_martin.iottelecom.model.JsonLabels;
import com.axel_martin.iottelecom.model.Measure;
import com.axel_martin.iottelecom.utils.MyNotifier;
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
 * @author Martin, Axel
 */
public class DataService extends Service {

    private double lumixDelta = 75;

    private ArrayList<Measure> measures;
    private MyNotifier myNotifyer;
    private Measure lastMeasure;

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
    public static final String SCHEDULED_REF = "scheduledBoolean";
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
            isSchedulded = bundle.getBoolean(SCHEDULED_REF);
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


    private BroadcastReceiver terminateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("DATA SERVICE", "STOP COMMAND");
            timer.cancel();
            timer.purge();
            stopSelf();
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        Log.d("DATA SERVICE", "START COMMAND");
        interval = bundle.getInt(POLLING_REF)*MINUTE_IN_MILLIS;
        cache = bundle.getInt(CACHE_REF);
        isTemperature = bundle.getBoolean(IS_TEMPERATURE_ALERT_REF);
        minTemperatureTrigger = bundle.getInt(MIN_TEMPERATURE_REF);
        maxTemperatureTrigger = bundle.getInt(MAX_TEMPERATURE_REF);
        isLight = bundle.getBoolean(IS_LIGHT_ALERT_REF);
        lightTtrigger = bundle.getInt(LIGHT_REF);
        isSchedulded = bundle.getBoolean(SCHEDULED_REF);
        startTime = bundle.getString(START_TIME_REF);
        endTime = bundle.getString(END_TIME_REF);
        isMail = bundle.getBoolean(MAIL_REF);
        mailAddress = bundle.getString(MAIL_ADDRESS_REF);
        isSms = bundle.getBoolean(SMS_REF);
        smsAddress = bundle.getString(SMS_ADDRESS_REF);

        return super.onStartCommand(intent, flags, startId);
        //return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        measures = new ArrayList<>();
        registerReceiver(firstReceiver, new IntentFilter("com.axel_martin.iottelecom.MainActivity.FIRST"));
        registerReceiver(receiver, new IntentFilter("com.axel_martin.iottelecom.MainActivity.FLUSH"));
        registerReceiver(updateReceiver, new IntentFilter("com.axel_martin.iottelecom.MainActivity.UPDATE"));
        registerReceiver(terminateReceiver, new IntentFilter("com.axel_martin.iottelecom.MyNotifier.STOP"));
        myStartService();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(firstReceiver);
        unregisterReceiver(receiver);
        myNotifyer.cancelAll();
        unregisterReceiver(updateReceiver);
        unregisterReceiver(terminateReceiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("SERVICE", "ONBIND");
        return null;
    }

    public void myStartService() {
        Log.d("SERVICE", "Starting service...");
        timer = new Timer();
        myNotifyer = new MyNotifier(this);
        startForeground(1, myNotifyer.createPermanentNotify());
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

    public void updateData() throws ExecutionException, InterruptedException {

        AsyncTask<String, String, Measure> parser = new AsyncTask<String, String, Measure>() {

            @Override
            protected Measure doInBackground(String... params) {
                ObjectMapper mapper = new ObjectMapper();

                try {
                    Measure measure = mapper.readValue(params[0], Measure.class);
                    checkTriggers(measure);
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

    private void checkTriggers(Measure measure) {
        if (lastMeasure != null) {
            for (int i = 0; i < measure.getData().size(); i++) {
                for (int j = 0; j < lastMeasure.getData().size(); j++) {
                    if (measure.getData().get(i).getMote() == lastMeasure.getData().get(j).getMote()) {

                        //Lumix trigger
                        if (measure.getData().get(i).getLabel().equals(JsonLabels.LIGHT1) && lastMeasure.getData().get(j).getLabel().equals(JsonLabels.LIGHT1)) {

                            //Debug
                            myNotifyer.createLightNotify(2.5, true);
                            if (measure.getData().get(i).getValue() == lastMeasure.getData().get(j).getValue()) {
                                Log.d("same value", "true");
                            } else {
                                Log.d("same value", "false");
                            }

                            if (measure.getData().get(i).getValue() - lastMeasure.getData().get(j).getValue() >= lumixDelta) {
                                myNotifyer.createLightNotify(measure.getData().get(i).getMote(), true);
                            }
                        }

                        //Temperature trigger
                        if (measure.getData().get(i).getLabel().equals(JsonLabels.TEMPERATURE) && lastMeasure.getData().get(j).getLabel().equals(JsonLabels.TEMPERATURE)) {

                            //Debug
                            myNotifyer.createTemperatureNotify(2.5, true);
                            if (measure.getData().get(i).getValue() == lastMeasure.getData().get(j).getValue()) {
                                Log.d("same value", "true");
                            } else {
                                Log.d("same value", "false");
                            }

                            if (measure.getData().get(i).getValue() - lastMeasure.getData().get(j).getValue() >= lumixDelta) {
                                myNotifyer.createLightNotify(measure.getData().get(i).getMote(), true);
                            }
                        }
                    }
                }
            }
        }
        lastMeasure = measure;
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
        Log.d("SERVICE", "ADD MEASURE : "+String.valueOf(measures.size()));
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

//    @Override
//    public boolean stopService(Intent name) {
//        timer.cancel();
//        timer.purge();
//        Log.d("DATA SERVICE", "THE STOP SERVICE HAS BEEN CALLED");
//        return super.stopService(name);
//    }
}
