package com.example.grigorii.mindthegap.model;


import org.osmdroid.bonuspack.overlays.Polyline;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by grigorii on 10/06/16.
 *
 * Class for keeping information about underground lines.
 *
 */
public class Line {

    private String id;
    private String name;
    private Set<Branch> branches;
    private int color;
    private Set<Station> stations;

    public Line(String id, String name) {
        this.id = id;
        this.name = name;
        stations = new HashSet<>();
    }

    /**
     * Equality of two lines is based on the value of
     * their ids only.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Line line = (Line) o;

        return id != null ? id.equals(line.id) : line.id == null;
    }



    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public Set<Station> getStations() {
        return stations;
    }

    public void setStations(Set<Station> stations) {
        this.stations = stations;
    }

    public void setBranches(Set<Branch> branches) {
        this.branches = branches;
    }

    public String getId() {
        return id;
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

    public Set<Branch> getBranches() {
        return branches;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
