package com.axel_martin.iottelecom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.axel_martin.iottelecom.GUI.OverviewHeaderRow;
import com.axel_martin.iottelecom.GUI.OverviewHumidityCardRow;
import com.axel_martin.iottelecom.GUI.OverviewLightCardRow;
import com.axel_martin.iottelecom.GUI.OverviewMotesCardRow;
import com.axel_martin.iottelecom.GUI.OverviewTemperatureCardRow;
import com.axel_martin.iottelecom.model.Data;
import com.axel_martin.iottelecom.model.Info;
import com.axel_martin.iottelecom.model.JsonLabels;
import com.axel_martin.iottelecom.model.Measure;
import com.axel_martin.iottelecom.model.Model;
import com.axel_martin.iottelecom.utils.HttpGetAsyncTask;
import com.axel_martin.iottelecom.utils.ParserInfoAsyncTask;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_MODEL = "model_arg";

    private View rootView;
    private ProgressBar progress;
    private SwipeRefreshLayout rootLinear;
    private LayoutInflater inflater;
    private SwipeRefreshLayout refreshLayout;

    private Model model;

    private Measure measure;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MainFragment newInstance(int sectionNumber, Model model) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putSerializable(ARG_MODEL, model);
        fragment.setArguments(args);
        return fragment;
    }

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        rootLinear = (SwipeRefreshLayout) rootView.findViewById(R.id.overview_fragment_rootLinear);
        progress = (ProgressBar) rootView.findViewById(R.id.overview_fragment_progress);
        rootLinear.setVisibility(View.GONE);
        rootLinear.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent intentFirstFlush = new Intent("com.axel_martin.iottelecom.MainActivity.FIRST");
                getActivity().sendBroadcast(intentFirstFlush);
            }
        });
        model = (Model) getArguments().getSerializable(ARG_MODEL);
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
        Log.d("MainFragment", "Activity created");
        progress.setVisibility(View.VISIBLE);
        rootLinear.setVisibility(View.GONE);
        if(model.getInfo() != null){
            update();
        }

    }

    public void update(){
        SwipeRefreshLayout rootCard = (SwipeRefreshLayout) inflater.inflate(R.layout.overview_card_layout, rootLinear);
        TextView motesTitle = (TextView) rootCard.findViewById(R.id.overview_motes_title);
        TextView motesNumber = (TextView) rootCard.findViewById(R.id.overview_motes_number);
        TableLayout motesTable = (TableLayout) rootCard.findViewById(R.id.overview_motes_table);
        TableLayout temperatureTable = (TableLayout) rootCard.findViewById(R.id.overview_temperature_table);
        TableLayout humidityTable = (TableLayout) rootCard.findViewById(R.id.overview_humidity_table);
        TableLayout lightTable = (TableLayout) rootCard.findViewById(R.id.overview_light_table);
        TextView temperatureMean = (TextView) rootCard.findViewById(R.id.overview_temperature_mean);
        TextView humidityMean = (TextView) rootCard.findViewById(R.id.overview_humidity_mean);
        TextView lightMean = (TextView) rootCard.findViewById(R.id.overview_light_mean);

        motesTable.removeAllViews();
        temperatureTable.removeAllViews();
        humidityTable.removeAllViews();
        lightTable.removeAllViews();

        motesNumber.setText(getResources().getString(R.string.NumberMotes) + " " + String.valueOf(model.getInfo().getMotesNb()));
        for (int i = 0; i < model.getInfo().getSink().size(); i++) {
            motesTable.addView(new OverviewMotesCardRow(
                    getActivity().getApplicationContext(),
                    model.getInfo().getSink().get(i).getId(),
                    model.getInfo().getSink().get(i).getIpv6(),
                    model.getInfo().getSink().get(i).getLat(),
                    model.getInfo().getSink().get(i).getLon(),
                    "Sink"
            ));
        }
        for (int i = 0; i < model.getInfo().getSender().size(); i++) {
            motesTable.addView(new OverviewMotesCardRow(
                    getActivity().getApplicationContext(),
                    model.getInfo().getSender().get(i).getId(),
                    model.getInfo().getSender().get(i).getIpv6(),
                    model.getInfo().getSender().get(i).getLat(),
                    model.getInfo().getSender().get(i).getLon(),
                    "Sender"
            ));
        }
        try {
            int tempValueNumber = 0;
            double tempValueCumul = 0;
            int last = model.getMeasureList().size() - 1;
            temperatureTable.addView(new OverviewHeaderRow(getActivity().getApplicationContext()));
            for (int i = 0; i < model.getMeasureList().get(last).getData().size(); i++) {
                Data data = model.getMeasureList().get(last).getData().get(i);
                if (data.getLabel().equals(JsonLabels.TEMPERATURE)) {
                    tempValueCumul += data.getValue();
                    tempValueNumber++;
                    temperatureTable.addView(new OverviewTemperatureCardRow(
                            getActivity().getApplicationContext(),
                            data.getValue(),
                            data.getTimestamp(),
                            data.getMote()
                    ));
                }
            }
            double mean = tempValueCumul / tempValueNumber;
            double meanTemp = Math.round(mean * 100);
            mean = meanTemp / 100;
            temperatureMean.setText(String.valueOf(mean) + "°C");

            tempValueNumber = 0;
            tempValueCumul = 0;
            humidityTable.addView(new OverviewHeaderRow(getActivity().getApplicationContext()));
            for (int i = 0; i < model.getMeasureList().get(last).getData().size(); i++) {
                Data data = model.getMeasureList().get(last).getData().get(i);
                if (data.getLabel().equals(JsonLabels.HUMIDITY)) {
                    tempValueCumul += data.getValue();
                    tempValueNumber++;
                    humidityTable.addView(new OverviewHumidityCardRow(
                            getActivity().getApplicationContext(),
                            data.getValue(),
                            data.getTimestamp(),
                            data.getMote()
                    ));
                }
            }
            mean = tempValueCumul / tempValueNumber;
            meanTemp = Math.round(mean * 100);
            mean = meanTemp / 100;
            humidityMean.setText(String.valueOf(mean) + "%");

            tempValueNumber = 0;
            tempValueCumul = 0;
            lightTable.addView(new OverviewHeaderRow(getActivity().getApplicationContext()));
            for (int i = 0; i < model.getMeasureList().get(last).getData().size(); i++) {
                Data data = model.getMeasureList().get(last).getData().get(i);
                if (data.getLabel().equals(JsonLabels.LIGHT1)) {
                    tempValueCumul += data.getValue();
                    tempValueNumber++;
                    lightTable.addView(new OverviewLightCardRow(
                            getActivity().getApplicationContext(),
                            data.getValue(),
                            data.getTimestamp(),
                            data.getMote()
                    ));
                }
            }
            mean = tempValueCumul / tempValueNumber;
            meanTemp = Math.round(mean * 100);
            mean = meanTemp / 100;
            lightMean.setText(String.valueOf(mean) + "lx");
        } catch (Exception e){
            e.printStackTrace();
        }

        progress.setVisibility(View.GONE);
        rootLinear.setVisibility(View.VISIBLE);
        rootLinear.setRefreshing(false);
        rootLinear.invalidate();
    }

}
