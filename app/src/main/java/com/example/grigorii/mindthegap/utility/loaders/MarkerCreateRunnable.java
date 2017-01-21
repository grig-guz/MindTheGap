package com.example.grigorii.mindthegap.utility.loaders;


import com.example.grigorii.mindthegap.R;
import com.example.grigorii.mindthegap.model.Line;
import com.example.grigorii.mindthegap.model.Station;
import com.example.grigorii.mindthegap.utility.fixedBonusPackClasses.StationMarker;

import org.osmdroid.util.GeoPoint;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by grigorii on 17/06/16.
 *
 * Class for creating markers
 */
public class MarkerCreateRunnable implements Runnable {

    protected static final int MARKER_CREATE_STATE_FAILED = -1;
    protected static final int MARKER_CREATE_STATE_COMPLETED = 1;

    private LineTask mLineTask;

    public MarkerCreateRunnable(LineTask lineTask) {
        mLineTask = lineTask;
    }

    /**
     * Draws markers from data received in LineSequenceRunnable
     */
    @Override
    public void run() {
        Line line = mLineTask.getLine();
        Set<Station> stations = line.getStations();
        Set<StationMarker> markers = new HashSet<>();

        try {

            // Building marker for each station
            for (Station station : stations) {
                StationMarker marker = new StationMarker(mLineTask.getMapView(), mLineTask.getContext());

                double lat = station.getLocation().getLat();
                double lon = station.getLocation().getLon();

                marker.setPosition(new GeoPoint(lat, lon));

                marker.setIcon(mLineTask.getContext().getResources().getDrawable(R.mipmap.station));

                marker.setTitle(station.getName());

                marker.setStation(station);

                markers.add(marker);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mLineTask.handleMarkerCreateState(MARKER_CREATE_STATE_FAILED);
        }

        mLineTask.setMarkers(markers);

        mLineTask.handleMarkerCreateState(MARKER_CREATE_STATE_COMPLETED);

        mLineTask.setMarkerCreateThread(null);

    }
}
