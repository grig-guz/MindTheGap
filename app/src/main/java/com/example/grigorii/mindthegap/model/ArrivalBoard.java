package com.example.grigorii.mindthegap.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by grigorii on 10/06/16.
 *
 * Class for keeping information about on arrivals for
 * station platforms.
 */
public class ArrivalBoard implements Iterable<Arrival> {

    private String platformName;
    private String lineId;
    private List<Arrival> arrivalsList;

    public ArrivalBoard() {
        arrivalsList = new ArrayList<>();
    }


    /**
     * Equality of two instances is based on platformName
     * and lineId
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArrivalBoard board = (ArrivalBoard) o;

        if (platformName != null ? !platformName.equals(board.platformName) : board.platformName != null)
            return false;
        return lineId != null ? lineId.equals(board.lineId) : board.lineId == null;

    }

    @Override
    public int hashCode() {
        int result = platformName != null ? platformName.hashCode() : 0;
        result = 31 * result + (lineId != null ? lineId.hashCode() : 0);
        return result;
    }

    @Override
    public Iterator<Arrival> iterator() {
        return arrivalsList.iterator();
    }

    public void addArrival(Arrival arrival) {
        arrivalsList.add(arrival);
    }

    public List<Arrival> getArrivalsList() {
        return arrivalsList;
    }

    public void setArrivalsList(List<Arrival> arrivalsList) {
        this.arrivalsList = arrivalsList;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }
}
