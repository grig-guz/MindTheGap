package com.example.grigorii.mindthegap.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by grigorii on 10/06/16.
 *
 * Class for keeping information about every underground
 * station.
 */
public class Station {

    private String id;
    private String name;
    private LatLon location;
    private List<ArrivalBoard> arrivalBoards;
    private Set<String> lineIds;

    public Station(String id, String name, LatLon location) {
        this.id = id;
        this.name = name;
        this.location = location;
        lineIds = new HashSet<>();
    }

    /**
     * Equality of two instances is based on id only
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Station station = (Station) o;

        return id != null ? id.equals(station.id) : station.id == null;

    }

    public void setArrivalBoards(List<ArrivalBoard> arrivalBoards) {
        this.arrivalBoards = arrivalBoards;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public String getId() {
        return id;
    }

    public void addLine(String line) {
        lineIds.add(line);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLon getLocation() {
        return location;
    }

    public void setLocation(LatLon location) {
        this.location = location;
    }

    public List<ArrivalBoard> getArrivalBoards() {
        return arrivalBoards;
    }

    public Set<String> getLines() {
            return lineIds;
    }

    public void setLineIds(Set<String> lineIds) {
        this.lineIds = lineIds;
    }

}
