package com.axel_martin.iottelecom.model;

import java.io.Serializable;

/**
 * Created by Martin on 04/02/2015.
 */
public class Sink extends AbstractMote implements Serializable{

    private int dodagVersionNumber;

    public int getDodagVersionNumber() {
        return dodagVersionNumber;
    }

    public void setDodagVersionNumber(int dodagVersionNumber) {
        this.dodagVersionNumber = dodagVersionNumber;
    }
}
