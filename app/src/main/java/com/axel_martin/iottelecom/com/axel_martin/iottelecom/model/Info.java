package com.axel_martin.iottelecom.com.axel_martin.iottelecom.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Martin on 04/02/2015.
 */
public class Info implements Serializable{

    private int motesNb;
    private ArrayList<Sender> sender;
    private ArrayList<Sink> sink;

    public int getMotesNb() {
        return motesNb;
    }

    public void setMotesNb(int motesNb) {
        this.motesNb = motesNb;
    }

    public ArrayList<Sender> getSender() {
        return sender;
    }

    public void setSender(ArrayList<Sender> sender) {
        this.sender = sender;
    }

    public ArrayList<Sink> getSink() {
        return sink;
    }

    public void setSink(ArrayList<Sink> sink) {
        this.sink = sink;
    }
}
