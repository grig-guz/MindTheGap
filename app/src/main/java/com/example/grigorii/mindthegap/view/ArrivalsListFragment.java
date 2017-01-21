package com.example.grigorii.mindthegap.view;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.grigorii.mindthegap.R;
import com.example.grigorii.mindthegap.model.Arrival;
import com.example.grigorii.mindthegap.model.ArrivalBoard;
import com.example.grigorii.mindthegap.utility.adapters.ArrivalsListAdapter;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ArrivalsListFragment extends Fragment {

    public ArrivalsListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_arrivals_list, container, false);

        // Get arrivalBoard instance from intent
        Gson gson = new Gson();
        String gsonArrivalBoard = getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT);
        ArrivalBoard board = gson.fromJson(gsonArrivalBoard, ArrivalBoard.class);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_arrival);
        List<Arrival> arrivalList = board.getArrivalsList();
        Collections.sort(arrivalList);
        ArrivalsListAdapter adapter = new ArrivalsListAdapter(
                getActivity(),
                R.layout.fragment_arrivals_list,
                arrivalList,
                board.getPlatformName());
        listView.setAdapter(adapter);

        return rootView;
    }
}
