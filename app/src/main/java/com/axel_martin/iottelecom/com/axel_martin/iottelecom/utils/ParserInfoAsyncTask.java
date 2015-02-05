package com.axel_martin.iottelecom.com.axel_martin.iottelecom.utils;

import android.os.AsyncTask;

import com.axel_martin.iottelecom.MainFragment;
import com.axel_martin.iottelecom.com.axel_martin.iottelecom.model.Info;
import com.axel_martin.iottelecom.com.axel_martin.iottelecom.model.Measure;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringReader;


/**
 * Created by Martin on 23/01/2015.
 */
public class ParserInfoAsyncTask extends AsyncTask<String, String, Info> {

    private MainFragment mainFragment;

    public ParserInfoAsyncTask(MainFragment mainFragment){
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
    }
}
