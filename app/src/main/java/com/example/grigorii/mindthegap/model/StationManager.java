package com.example.grigorii.mindthegap.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by grigorii on 11/06/16.
 *
 * Singleton class for keeping and branches,
 * which is helpful while parsing.
 */
public class StationManager implements Iterable<Station> {

    private static StationManager ourInstance = new StationManager();

    private Set<Branch> setOfBranches = new HashSet<>();

    private Set<Station> setOfStations = new HashSet<>();

    public static StationManager getInstance() {
        return ourInstance;
    }


    private StationManager() {

    }

    @Override
    public Iterator<Station> iterator() {
        return setOfStations.iterator();
    }

    public void addStation(Station station) {
        setOfStations.add(station);
    }

    public Set<Branch> getListOfBranches() {
        return setOfBranches;
    }

    public void setListOfBranches(Set<Branch> setOfBranches) {
        this.setOfBranches = setOfBranches;
    }

    public void setListOfStations(Set<Station> setOfStations) {
        this.setOfStations = setOfStations;
    }


    public Set<Station> getListOfStations() {
        return setOfStations;
    }
}
