package com.axel_martin.iottelecom.com.axel_martin.iottelecom.model;

import java.io.Serializable;

/**
 * Created by Martin on 04/02/2015.
 */
public class AbstractMote implements Serializable{

    private int id;
    private double ipv6;
    private double mac;
    private double lat;
    private double lon;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getIpv6() {
        return ipv6;
    }

    public void setIpv6(double ipv6) {
        this.ipv6 = ipv6;
    }

    public double getMac() {
        return mac;
    }

    public void setMac(double mac) {
        this.mac = mac;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
