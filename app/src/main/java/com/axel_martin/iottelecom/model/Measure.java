package com.axel_martin.iottelecom.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Martin on 23/01/2015.
 */
public class Measure implements Serializable{
    private ArrayList<Data> data;

    public ArrayList<Data> getData() {
        return data;
    }

    public void setData(ArrayList<Data> data) {
        this.data = data;
    }
}

