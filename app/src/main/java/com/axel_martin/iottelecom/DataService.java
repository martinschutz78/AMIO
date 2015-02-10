package com.axel_martin.iottelecom;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.axel_martin.iottelecom.com.axel_martin.iottelecom.model.Measure;
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

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            Log.d("CATCH", "CATCH BROADCAST");
            flushMeasures();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        measures = new ArrayList<>();
        registerReceiver(receiver, new IntentFilter("com.axel_martin.iottelecom.MainActivity"));
        myStartService();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void myStartService(){
        Timer timer = new Timer();
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
        60000);
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


        Intent intent = new Intent("com.axel_martin.iottelecom");
        intent.putExtra("RESULT", measures);
        sendBroadcast(intent);


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

    
}