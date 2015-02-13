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
            try {
                updateData();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            timer.cancel();
            timer.purge();
            startTimer(interval);
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
        myNotifyer.cancelAll();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void myStartService() {
        timer = new Timer();
        myNotifyer = new MyNotifier(this);
        startTimer(interval);
    }

    public void startTimer(int myInterval){
        timer.scheduleAtFixedRate(new TimerTask() {
                                      @Override
                                      public void run() {
                                          try {
                                              updateData();
                                          } catch (ExecutionException e) {
                                              e.printStackTrace();
                                          } catch (InterruptedException e) {
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
                    checkLuminosity(measure);
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


        Intent intent = new Intent("com.axel_martin.iottelecom");
        intent.putExtra("RESULT", measures);
        sendBroadcast(intent);


    }

    private void checkLuminosity(Measure measure) {
        if (lastMeasure != null) {
            for (int i = 0; i < measure.getData().size(); i++) {
                for (int j = 0; j < lastMeasure.getData().size(); j++) {
                    if (measure.getData().get(i).getMote() == lastMeasure.getData().get(j).getMote()) {

                        //Lumix trigger
                        if (measure.getData().get(i).getLabel().equals(JsonLabels.LIGHT1) && lastMeasure.getData().get(j).getLabel().equals(JsonLabels.LIGHT1)) {

                            //Debug
                            myNotifyer.createLightNotify(2.5);
                            if (measure.getData().get(i).getValue() == lastMeasure.getData().get(j).getValue()) {
                                Log.d("same value", "true");
                            } else {
                                Log.d("same value", "false");
                            }

                            if (measure.getData().get(i).getLabel().equals(JsonLabels.LIGHT1) && measure.getData().get(i).getValue() - lastMeasure.getData().get(j).getValue() >= lumixDelta) {
                                myNotifyer.createLightNotify(measure.getData().get(i).getMote());
                            }
                        }
                    }
                }
            }
        }
        lastMeasure = measure;
    }

    public void addMeasure(Measure measure){
        if(measures.size()>10){
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
