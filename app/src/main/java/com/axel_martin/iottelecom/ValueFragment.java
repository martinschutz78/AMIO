package com.axel_martin.iottelecom;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.axel_martin.iottelecom.com.axel_martin.iottelecom.model.JsonLabels;
import com.axel_martin.iottelecom.com.axel_martin.iottelecom.model.Model;
import com.axel_martin.iottelecom.com.axel_martin.iottelecom.observerPattern.MyObserver;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.XLabels;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class ValueFragment extends Fragment implements MyObserver {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_MODEL = "model_arg";

    private View rootView;
    private SwipeRefreshLayout rootLinear;
    private LayoutInflater inflater;
    private SwipeRefreshLayout refreshLayout;

    private int sectionNumber;

    private ArrayList<LineDataSet> lineDataSets;

    private LineDataSet dataSet;
    int counter = 0;
    int generalCounter = 0;
    private ArrayList<String> dateList;
    private String toTest = "";

    private Model model;

    private LineData data;

    private LineChart chart;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ValueFragment newInstance(int sectionNumber, Model model) {
        ValueFragment fragment = new ValueFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putSerializable(ARG_MODEL, model);
        fragment.setArguments(args);
        return fragment;
    }

    public ValueFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        rootView = inflater.inflate(R.layout.fragment_value, container, false);
        model = (Model) getArguments().getSerializable(ARG_MODEL);
        sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        this.setRetainInstance(true);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.setupGUI();
    }

    public void setupGUI() throws NullPointerException{


        switch (sectionNumber){
            case MainActivity.TEMPERATURE_FRAGMENT:
                toTest = JsonLabels.TEMPERATURE;
                break;
            case MainActivity.LIGHT_FRAGMENT:
                toTest = JsonLabels.LIGHT1;
                break;
            case MainActivity.HUMIDITY_FRAGMENT:
                toTest = JsonLabels.HUMIDITY;
                break;
        }

        chart = (LineChart) rootView.findViewById(R.id.chart);

        chart.animateX(500);

        lineDataSets = new ArrayList<>();
        dateList = new ArrayList<>();
        boolean isFirst = true;
        for(int j=0; j<model.getSenderList().size();j++) {

            ArrayList<Entry> entryList = new ArrayList<>();

            counter = 0;


            for (int i = 0; i < model.getSenderList().get(j).getDatalist().size(); i++) {
                if (model.getSenderList().get(j).getDatalist().get(i).getLabel().equals(toTest)) {
                    entryList.add(new Entry((float) model.getSenderList().get(j).getDatalist().get(i).getValue(),
                            counter));
                    /*if(model.getSenderList().get(j).getDatalist().get(i).getTimestamp()<model.getSenderList().get(j).getDatalist().get(0).getTimestamp()){
                        entryList.remove(0);
                        //dateList.remove(0);

                    }*/
                    if(isFirst){
                        dateList.add(this.getDate(model.getSenderList().get(j).getDatalist().get(i).getTimestamp()));
                        generalCounter++;
                    }
                    counter++;
                }
            }

            isFirst = false;
            dataSet = new LineDataSet(entryList, String.valueOf(model.getSenderList().get(j).getId()));
            dataSet.setCircleSize(10);
            dataSet.setLineWidth(5);
            int[] colors = getResources().getIntArray(R.array.color);


            lineDataSets.add(dataSet);
            for(int i=0;i<lineDataSets.size();i++){
                lineDataSets.get(i).setColor(colors[i%lineDataSets.size()]);
                lineDataSets.get(i).setCircleColor(colors[i % lineDataSets.size()]);
            }
        }
        data = new LineData(dateList, lineDataSets);
        chart.setData(data);
        chart.setTouchEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDragEnabled(true);
        switch (sectionNumber){
            case MainActivity.TEMPERATURE_FRAGMENT:
                chart.setDescription("Temperature");
                break;
            case MainActivity.LIGHT_FRAGMENT:
                chart.setDescription("Light");
                break;
            case MainActivity.HUMIDITY_FRAGMENT:
                chart.setDescription("Humidity");
                break;
        }
        XLabels xl = chart.getXLabels();
        xl.setTextSize(25);
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.FRANCE);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("HH:mm:ss", cal).toString();
        return date;
    }

    @Override
    public void update() {
        setupGUI();
    }
}
