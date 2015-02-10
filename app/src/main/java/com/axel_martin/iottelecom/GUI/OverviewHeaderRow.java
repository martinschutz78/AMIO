package com.axel_martin.iottelecom.GUI;

import android.content.Context;
import android.graphics.Color;
import android.widget.TableRow;
import android.widget.TextView;

import com.axel_martin.iottelecom.R;

/**
 * Created by Martin on 04/02/2015.
 */
public class OverviewHeaderRow extends TableRow{

    private String value;
    private String mote;
    private String date;

    private TextView valueView, dateView, moteView;

    public OverviewHeaderRow(Context context) {
        super(context);



        valueView = new TextView(context);
        dateView = new TextView(context);
        moteView = new TextView(context);

        value = getResources().getString(R.string.overview_humidity_value);
        mote = getResources().getString(R.string.overview_humidity_mote);
        date = getResources().getString(R.string.overview_humidity_date);

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        params.weight = 1;

        valueView.setLayoutParams(params);
        dateView.setLayoutParams(params);
        moteView.setLayoutParams(params);

        valueView.setText(value);
        dateView.setText(date);
        moteView.setText(mote);


        valueView.setTextColor(Color.BLACK);
        dateView.setTextColor(Color.BLACK);
        moteView.setTextColor(Color.BLACK);

        this.addView(moteView);
        this.addView(dateView);
        this.addView(valueView);

    }
}
