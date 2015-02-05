package com.axel_martin.iottelecom.com.axel_martin.iottelecom.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Martin on 04/02/2015.
 */
public class Model implements Serializable{

    private ArrayList<Measure> measureList;
    private Info info;

    public Model(){
        measureList = new ArrayList<>();
    }

    public ArrayList<Measure> getMeasureList() {
        return measureList;
    }

    public void setMeasureList(ArrayList<Measure> measureList) {
        this.measureList = measureList;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }
}
