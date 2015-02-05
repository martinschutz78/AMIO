package com.axel_martin.iottelecom.com.axel_martin.iottelecom.GUI;

import android.content.Context;
import android.graphics.Color;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by Martin on 04/02/2015.
 */
public class OverviewMotesCardRow extends TableRow{

    private int id;
    private double ipv6, lat, lon;
    private String type;

    private TextView idView, ipv6View, latView, lonView, typeView;

    public OverviewMotesCardRow(Context context, int id, double ipv6, double lat, double lon, String type) {
        super(context);

        this.id=id;
        this.ipv6=ipv6;
        this.lat=lat;
        this.lon=lon;
        this.type=type;

        idView = new TextView(context);
        typeView = new TextView(context);
        ipv6View = new TextView(context);
        latView = new TextView(context);
        lonView = new TextView(context);

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        params.weight = 1;

        idView.setLayoutParams(params);
        typeView.setLayoutParams(params);
        ipv6View.setLayoutParams(params);
        latView.setLayoutParams(params);
        lonView.setLayoutParams(params);

        idView.setText(String.valueOf(id));
        typeView.setText(type);
        ipv6View.setText(String.valueOf(ipv6));
        latView.setText(String.valueOf(lat));
        lonView.setText(String.valueOf(lon));

        idView.setTextColor(Color.BLACK);
        typeView.setTextColor(Color.BLACK);
        ipv6View.setTextColor(Color.BLACK);
        latView.setTextColor(Color.BLACK);
        lonView.setTextColor(Color.BLACK);

        this.addView(idView);
        this.addView(typeView);
        this.addView(ipv6View);
        this.addView(latView);
        this.addView(lonView);

    }


    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public double getIpv6() {
        return ipv6;
    }

    public void setIpv6(double ipv6) {
        this.ipv6 = ipv6;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TextView getIdView() {
        return idView;
    }

    public void setIdView(TextView idView) {
        this.idView = idView;
    }

    public TextView getIpv6View() {
        return ipv6View;
    }

    public void setIpv6View(TextView ipv6View) {
        this.ipv6View = ipv6View;
    }

    public TextView getLatView() {
        return latView;
    }

    public void setLatView(TextView latView) {
        this.latView = latView;
    }

    public TextView getLonView() {
        return lonView;
    }

    public void setLonView(TextView lonView) {
        this.lonView = lonView;
    }

    public TextView getTypeView() {
        return typeView;
    }

    public void setTypeView(TextView typeView) {
        this.typeView = typeView;
    }
}
