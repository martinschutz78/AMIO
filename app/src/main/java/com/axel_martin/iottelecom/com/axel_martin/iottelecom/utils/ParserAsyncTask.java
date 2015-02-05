package com.axel_martin.iottelecom.com.axel_martin.iottelecom.utils;

import android.os.AsyncTask;

import com.axel_martin.iottelecom.com.axel_martin.iottelecom.model.Measure;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringReader;


/**
 * Created by Martin on 23/01/2015.
 */
public class ParserAsyncTask extends AsyncTask<String, String, Measure> {


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
}
