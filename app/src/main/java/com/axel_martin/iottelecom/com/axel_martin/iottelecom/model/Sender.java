package com.axel_martin.iottelecom.com.axel_martin.iottelecom.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Martin on 04/02/2015.
 */
public class Sender extends AbstractMote implements Serializable{

    private ArrayList<Data> datalist;

    public ArrayList<Data> getDatalist() {
        return datalist;
    }

    public void setDatalist(ArrayList<Data> datalist) {
        this.datalist = datalist;
    }
}
