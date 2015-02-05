package com.axel_martin.iottelecom;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
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

    }
}
