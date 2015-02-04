package com.axel_martin.iottelecom;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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

import java.util.Properties;
import java.util.concurrent.ExecutionException;

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
    private LinearLayout rootLinear;
    private LayoutInflater inflater;

    private Model model;

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
        rootLinear = (LinearLayout) rootView.findViewById(R.id.overview_fragment_rootLinear);
        rootLinear.setVisibility(View.GONE);
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

        HttpGetAsyncTask httpTask = new HttpGetAsyncTask();
        HttpGetAsyncTask httpInfoTask = new HttpGetAsyncTask();
        String result ="";
        String resultMote="";
        try {
            result = httpTask.execute("http://iotlab.telecomnancy.eu/rest/data/1/light1-temperature/last").get();
            resultMote = httpInfoTask.execute("http://iotlab.telecomnancy.eu/rest/info/motes").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        ParserAsyncTask parserTask = new ParserAsyncTask();
        ParserInfoAsyncTask parserInfoTask = new ParserInfoAsyncTask();
        parserTask.execute(result);
        parserInfoTask.execute(resultMote);
        try {
            model.getMeasureList().add(parserTask.get());
            model.setInfo(parserInfoTask.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        ProgressBar progress = (ProgressBar) rootView.findViewById(R.id.overview_fragment_progress);
        progress.setVisibility(View.GONE);
        rootLinear.setVisibility(View.VISIBLE);
        LinearLayout rootCard = (LinearLayout) inflater.inflate(R.layout.overview_card_layout, rootLinear);
        TextView motesTitle = (TextView) rootCard.findViewById(R.id.overview_motes_title);
        TextView motesNumber = (TextView) rootCard.findViewById(R.id.overview_motes_number);
        TableLayout motesTable = (TableLayout) rootCard.findViewById(R.id.overview_motes_table);
        TableLayout temperatureTable = (TableLayout) rootCard.findViewById(R.id.overview_temperature_table);
        TextView temperatureMean = (TextView) rootCard.findViewById(R.id.overview_temperature_mean);
        //motesTitle.setText("TEST");
        motesNumber.setText(getResources().getString(R.string.overview_motes_number)+" "+String.valueOf(model.getInfo().getMotesNb()));
        for(int i=0; i<model.getInfo().getSink().size();i++){
            motesTable.addView(new OverviewMotesCardRow(
                    getActivity().getApplicationContext(),
                    model.getInfo().getSink().get(i).getId(),
                    model.getInfo().getSink().get(i).getIpv6(),
                    model.getInfo().getSink().get(i).getLat(),
                    model.getInfo().getSink().get(i).getLon(),
                    "Sink"
            ));
        }
        for(int i=0; i<model.getInfo().getSender().size();i++){
            motesTable.addView(new OverviewMotesCardRow(
                    getActivity().getApplicationContext(),
                    model.getInfo().getSender().get(i).getId(),
                    model.getInfo().getSender().get(i).getIpv6(),
                    model.getInfo().getSender().get(i).getLat(),
                    model.getInfo().getSender().get(i).getLon(),
                    "Sender"
            ));
        }
        int lastIndex = model.getMeasureList().size()-1;
        int tempValueNumber = 0;
        double tempValueCumul = 0;
        for(int i=0; i<model.getMeasureList().get(lastIndex).getData().size();i++){
            Data data = model.getMeasureList().get(lastIndex).getData().get(i);
            if(data.getLabel().equals(JsonLabels.TEMPERATURE)){
                tempValueCumul += data.getValue();
                tempValueNumber ++;
                temperatureTable.addView(new OverviewTemperatureCardRow(
                    getActivity().getApplicationContext(),
                    data.getValue(),
                    data.getTimestamp(),
                    data.getMote()
                ));
            }
        }
        double mean = tempValueCumul/tempValueNumber;
        int meanTemp  = (int) (mean*100);
        mean = meanTemp/100;
        temperatureMean.setText(String.valueOf(mean)+"°C");



    }
}
