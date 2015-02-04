package com.axel_martin.iottelecom.com.axel_martin.iottelecom.model;

import java.io.Serializable;

/**
 * Created by Martin on 23/01/2015.
 */
public class Data implements Serializable{
    private long timestamp;
    private String label;
    private double value;
    private double mote;

    public double getMote() {
        return mote;
    }

    public void setMote(double mote) {
        this.mote = mote;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
