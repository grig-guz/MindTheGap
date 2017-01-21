package com.example.grigorii.mindthegap.model;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by grigorii on 10/06/16.
 *
 * Class for keeping information about each branch. This is
 * done by aggregating it with LatLon, so Branch contains a
 * list of LatLon elements
 */
public class Branch implements Iterable<LatLon> {


    private ArrayList<LatLon> stationSeq;

    @Override
    public Iterator<LatLon> iterator() {
        return stationSeq.iterator();
    }

    public void setStationSeq(ArrayList<LatLon> stationSeq) {
        this.stationSeq = stationSeq;
    }

    /**
     * Equality of two Branches is based on equality of their
     * lists with LatLon elements
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Branch latLons = (Branch) o;

        return stationSeq != null ? stationSeq.equals(latLons.stationSeq) : latLons.stationSeq == null;

    }

    @Override
    public int hashCode() {
        return stationSeq != null ? stationSeq.hashCode() : 0;
    }

    public ArrayList<LatLon> getStationSeq() {
        return stationSeq;
    }
}
