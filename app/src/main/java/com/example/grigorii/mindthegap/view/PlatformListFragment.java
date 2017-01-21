package com.example.grigorii.mindthegap.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.grigorii.mindthegap.R;
import com.example.grigorii.mindthegap.model.ArrivalBoard;
import com.example.grigorii.mindthegap.model.Station;
import com.example.grigorii.mindthegap.utility.adapters.PlatformsListAdapter;
import com.example.grigorii.mindthegap.utility.loaders.ArrivalsLoader;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by grigorii on 18/06/16.
 */
public class PlatformListFragment extends Fragment {

    private PlatformsListAdapter adapter;
    private List<ArrivalBoard> data;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_platform_list, container, false);
        String gsonStation = getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT);
        data = new ArrayList<>();
        final Gson gson = new Gson();

        Station station = gson.fromJson(gsonStation, Station.class);
        final String stationName = getActivity().getIntent().getStringExtra("Station");
        listView = (ListView) v.findViewById(R.id.listview_platform);

        adapter = new PlatformsListAdapter(getActivity(), R.id.fragment_platform_item, new ArrayList<ArrivalBoard>(), station);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrivalBoard board = adapter.getItem(position);
                Intent intent = new Intent(getActivity(), ArrivalsListActivity.class)
                        .putExtra(Intent.EXTRA_TEXT,gson.toJson(board))
                        .putExtra("Station", stationName);
                startActivity(intent);
            }
        });
        ArrivalsLoader loader = new ArrivalsLoader(station, adapter);
        loader.execute();

        return v;
    }
}
