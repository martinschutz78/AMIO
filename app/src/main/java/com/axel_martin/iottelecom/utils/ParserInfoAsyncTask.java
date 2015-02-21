package com.axel_martin.iottelecom.utils;

import android.os.AsyncTask;

import com.axel_martin.iottelecom.MainActivity;
import com.axel_martin.iottelecom.MainFragment;
import com.axel_martin.iottelecom.model.Info;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;


/**
 * Created by Martin on 23/01/2015.
 */
public class ParserInfoAsyncTask extends AsyncTask<String, String, Info> {

    private MainActivity mainFragment;

    public ParserInfoAsyncTask(MainActivity mainFragment){
        this.mainFragment=mainFragment;
    }


    @Override
    protected Info doInBackground(String... params) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            Info info = mapper.readValue(params[0], Info.class);
            return info;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Info info) {
        super.onPostExecute(info);
        mainFragment.updateInfoFinishParsing(info);
        mainFragment.sendFirstFlush();
    }
}
