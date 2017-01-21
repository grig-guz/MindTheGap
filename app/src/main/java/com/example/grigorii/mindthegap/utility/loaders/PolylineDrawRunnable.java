package com.example.grigorii.mindthegap.utility.loaders;


import com.example.grigorii.mindthegap.model.Branch;
import com.example.grigorii.mindthegap.model.LatLon;
import com.example.grigorii.mindthegap.model.Line;
import com.example.grigorii.mindthegap.utility.fixedBonusPackClasses.FixedRoadManager;

import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by grigorii on 17/06/16.
 *
 */
public class PolylineDrawRunnable implements Runnable {

    protected static final int POLYLINE_CREATE_STATE_FAILED = -1;
    protected static final int POLYLINE_CREATE_STATE_COMPLETED = 1;

    private LineTask mLineTask;

    public PolylineDrawRunnable(LineTask lineTask) {
        mLineTask = lineTask;
    }

    /**
     * Method for parcing Line object into a set of Polyline objects.
     * OSMDroidBonusPack is used to create GeoPoints, Roads and Polylines.
     */
    @Override
    public void run() {
        try {
            Line line = mLineTask.getLine();

            // Set of Polyline objects to be returned when filled
            Set<Polyline> polylines = new HashSet<>();

            // Set of all branches of a particular Line object
            Set<Branch> branches = line.getBranches();

            // Creating Polyline for every Branch
            for (Branch branch : branches) {

                // Sequence of GeoPoint objects used to build a single Polyline
                ArrayList<GeoPoint> wayPoints = new ArrayList<>();

                // Getting coordinates of GeoPoints from LatLon objects inside Branch
                for (LatLon latLon : branch) {

                    wayPoints.add(new GeoPoint(latLon.getLat(), latLon.getLon()));
                }

                // Road object built by sending query
                Road road = new Road(wayPoints);
                Polyline roadOverlay = FixedRoadManager.buildRoadOverlay(road, line.getColor(), 6, mLineTask.getContext());
                polylines.add(roadOverlay);
            }

            // Adding polylines to task and sending message for manager to draw them
            mLineTask.setPolylines(polylines);
            mLineTask.handlePolylineCreateState(POLYLINE_CREATE_STATE_COMPLETED);

        } catch (Exception e) {
            e.printStackTrace();
            mLineTask.handlePolylineCreateState(POLYLINE_CREATE_STATE_FAILED);
        } finally {
            mLineTask.setPolylineCreateThread(null);
        }
    }
}
