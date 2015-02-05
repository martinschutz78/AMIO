package com.axel_martin.iottelecom;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.axel_martin.iottelecom.com.axel_martin.iottelecom.GUI.OverviewHumidityCardRow;
import com.axel_martin.iottelecom.com.axel_martin.iottelecom.GUI.OverviewLightCardRow;
import com.axel_martin.iottelecom.com.axel_martin.iottelecom.GUI.OverviewMotesCardRow;
import com.axel_martin.iottelecom.com.axel_martin.iottelecom.GUI.OverviewTemperatureCardRow;
import com.axel_martin.iottelecom.com.axel_martin.iottelecom.model.Data;
import com.axel_martin.iottelecom.com.axel_martin.iottelecom.model.Info;
import com.axel_martin.iottelecom.com.axel_martin.iottelecom.model.JsonLabels;
import com.axel_martin.iottelecom.com.axel_martin.iottelecom.model.Measure;
import com.axel_martin.iottelecom.com.axel_martin.iottelecom.model.Model;
import com.axel_martin.iottelecom.com.axel_martin.iottelecom.utils.HttpGetAsyncTask;
import com.axel_martin.iottelecom.com.axel_martin.iottelecom.utils.ParserAsyncTask;
import com.axel_martin.iottelecom.com.axel_martin.iottelecom.utils.ParserInfoAsyncTask;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * A placeholder fragment containing a simple view.
 */
public class ValueFragment extends Fragment {
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

    private Model model;

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

    public void setupGUI(){
        LineChart chart = (LineChart) rootView.findViewById(R.id.chart);

        /*ArrayList<Entry> entryList = new ArrayList<>();
        Entry entry1 = new Entry(10, 1);
        Entry entry2 = new Entry(10, 2);
        Entry entry3 = new Entry(20, 2);

        entryList.add(entry1);
        entryList.add(entry2);
        entryList.add(entry3);

        LineDataSet dataSet = new LineDataSet(entryList, "entryOne");

        ArrayList<String> exemple = new ArrayList<>();
        exemple.add("un");
        exemple.add("deux");
        exemple.add("trois");

        ArrayList<LineDataSet> lineDataSets = new ArrayList<>();
        lineDataSets.add(dataSet);

        LineData data = new LineData(exemple, lineDataSets);*/

        ArrayList<Entry> entryList = new ArrayList<>();
        ArrayList<String> dateList = new ArrayList<>();
        int counter = 0;
        //Log.d("count", String.valueOf(model.getInfo().getSender().get(0).getDatalist().size()));
        for(int i=0; i<model.getSenderList().get(0).getDatalist().size();i++){
            if(model.getSenderList().get(0).getDatalist().get(i).getLabel().equals(JsonLabels.TEMPERATURE)){
                entryList.add(new Entry((float) model.getSenderList().get(0).getDatalist().get(i).getValue(),counter));
                dateList.add(this.getDate(model.getSenderList().get(0).getDatalist().get(i).getTimestamp()));
                counter++;
            }
        }
        LineDataSet dataSet = new LineDataSet(entryList, String.valueOf(model.getSenderList().get(0).getId()));

        ArrayList<LineDataSet> lineDataSets = new ArrayList<>();
        lineDataSets.add(dataSet);
        LineData data = new LineData(dateList, lineDataSets);
        chart.setData(data);
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.FRANCE);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("HH:mm:ss", cal).toString();
        return date;
    }
}
