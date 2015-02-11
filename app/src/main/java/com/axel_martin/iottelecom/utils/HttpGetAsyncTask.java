package com.axel_martin.iottelecom.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.axel_martin.iottelecom.MainFragment;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Martin on 20/01/2015.
 */
public class HttpGetAsyncTask extends AsyncTask<String, String, String>{

    public static final int DATA = 1;
    public static final int INFO = 2;

    private MainFragment mainFragment;
    private int type;

    public HttpGetAsyncTask(MainFragment mainfrag, int type){
        this.mainFragment=mainfrag;
        this.type=type;
    }

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

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(type == this.DATA){
            //mainFragment.updateDataToParse(s);
        } else {
            mainFragment.updateInfoToParse(s);
        }

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
