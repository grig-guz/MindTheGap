package com.example.grigorii.mindthegap.model;



/**
 * Created by grigorii on 10/06/16.
 *
 * Class for keeping an information about each arrival
 */
public class Arrival implements Comparable<Arrival> {

    private int timeToStation;
    private String destinationName;

    public Arrival(String destinationName, int timeToStation) {

        this.destinationName = destinationName;
        this.timeToStation = timeToStation;
    }

    /**
     * Making an equality of two Arrival instances based
     * on timeToStation and destination name
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Arrival arrival = (Arrival) o;

        if (timeToStation != arrival.timeToStation) return false;
        return destinationName != null ? destinationName.equals(arrival.destinationName) : arrival.destinationName == null;

    }

    @Override
    public int hashCode() {
        int result = timeToStation;
        result = 31 * result + (destinationName != null ? destinationName.hashCode() : 0);
        return result;
    }

    /**
     * Compares two instances of Arrival based on how the
     * amount of time required for particular train to get
     * to the station
     * @param another an Arrival instance
     * @return difference between times for arrival
     */
    @Override
    public int compareTo(Arrival another) {
        return this.timeToStation - another.timeToStation;
    }

    public int getTimeToStation() {
        return timeToStation;
    }

    public void setTimeToStation(int timeToStation) {
        this.timeToStation = timeToStation;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }
}
