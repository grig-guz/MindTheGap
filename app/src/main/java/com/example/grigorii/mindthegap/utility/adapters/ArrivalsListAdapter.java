package com.example.grigorii.mindthegap.utility.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.grigorii.mindthegap.R;
import com.example.grigorii.mindthegap.model.Arrival;

import java.util.List;

/**
 * Created by grigorii on 19/06/16.
 *
 * Adapter class for the listView in ArrivalsListFragment. Helps
 * to connect data to the listView
 */
public class ArrivalsListAdapter extends ArrayAdapter<Arrival> {

    private String mPlatformName;

    public ArrivalsListAdapter(Context context, int resource, List<Arrival> arrivals, String platformName) {

        super(context, resource, arrivals);
        mPlatformName = platformName;
    }

    /**
     * Method for adding elements of data array to the listView
     * @param position position of an element in the listView
     * @param convertView view where data should be added
     * @param parent parent viewGroup
     * @return convertView filled with data
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        // Inflate view if it is null.
        if (v == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            v = layoutInflater.inflate(R.layout.fragment_arrivals_item, null);
        }

        Arrival arrival = getItem(position);
        if (arrival != null) {

            TextView destinationName = (TextView) v.findViewById(R.id.arrival_destination);
            TextView platformName = (TextView) v.findViewById(R.id.arrival_platform);
            TextView timeToStation = (TextView) v.findViewById(R.id.arrival_time);

            destinationName.setText(arrival.getDestinationName());
            platformName.setText(mPlatformName);
            String time = arrival.getTimeToStation() + " mins";
            timeToStation.setText(time);
        }
        return v;
    }
}
