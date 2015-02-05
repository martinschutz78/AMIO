package com.axel_martin.iottelecom.com.axel_martin.iottelecom.GUI;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.widget.TableRow;
import android.widget.TextView;

import java.sql.Date;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Martin on 04/02/2015.
 */
public class OverviewHumidityCardRow extends TableRow{

    private double value, mote;
    private long date;

    private TextView valueView, dateView, moteView;

    public OverviewHumidityCardRow(Context context, double value, long date, double mote) {
        super(context);

        this.value=value;
        this.date=date;
        this.mote=mote;

        valueView = new TextView(context);
        dateView = new TextView(context);
        moteView = new TextView(context);

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        params.weight = 1;

        valueView.setLayoutParams(params);
        dateView.setLayoutParams(params);
        moteView.setLayoutParams(params);

        valueView.setText(String.valueOf(value)+"%");
        dateView.setText(getDate(date));
        moteView.setText(String.valueOf(mote));


        valueView.setTextColor(Color.BLACK);
        dateView.setTextColor(Color.BLACK);
        moteView.setTextColor(Color.BLACK);

        this.addView(moteView);
        this.addView(dateView);
        this.addView(valueView);

    }


    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.FRANCE);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("HH:mm:ss dd-MM-yyyy", cal).toString();
        return date;
    }
}
