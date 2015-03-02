package com.axel_martin.iottelecom.GUI;

import android.content.Context;
import android.graphics.Color;
import android.widget.TableRow;
import android.widget.TextView;

import com.axel_martin.iottelecom.R;

/**
 * Created by Martin on 04/02/2015.
 */
public class OverviewMotesHeader extends TableRow{

    private String id;
    private String type;
    private String address;
    private String longitude;
    private String latitude;

    private TextView idView, typeView, addressView, longitudeView, latitudeView;

    public OverviewMotesHeader(Context context) {
        super(context);

        idView = new TextView(context);
        typeView = new TextView(context);
        addressView = new TextView(context);
        longitudeView = new TextView(context);
        latitudeView = new TextView(context);

        id = getResources().getString(R.string.Value);
        type = getResources().getString(R.string.Mote);
        address = getResources().getString(R.string.Date);
        longitude = getResources().getString(R.string.longitude);
        latitude = getResources().getString(R.string.latitude);

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        params.weight = 1;

        idView.setLayoutParams(params);
        typeView.setLayoutParams(params);
        addressView.setLayoutParams(params);
        longitudeView.setLayoutParams(params);
        latitudeView.setLayoutParams(params);


        idView.setText(id);
        typeView.setText(address);
        addressView.setText(type);
        longitudeView.setText(longitude);
        latitudeView.setText(latitude);

        idView.setTextColor(Color.BLACK);
        typeView.setTextColor(Color.BLACK);
        addressView.setTextColor(Color.BLACK);
        longitudeView.setTextColor(Color.BLACK);
        latitudeView.setTextColor(Color.BLACK);

        this.addView(addressView);
        this.addView(typeView);
        this.addView(idView);
        this.addView(latitudeView);
        this.addView(longitudeView);

    }
}
