package com.example.grigorii.mindthegap.model;

/**
 * Created by grigorii on 10/06/16.
 *
 * Class for keeping latitude and longitude values
 * for stations, which are used to build branches and
 * to put station markers on the map.
 */


public class LatLon {

    private double lat;
    private double lon;

    public LatLon(double lon, double lat) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    /**
     * Equality is based on latitude and longitude
     * values
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LatLon latLon = (LatLon) o;

        if (Double.compare(latLon.lat, lat) != 0) return false;
        return Double.compare(latLon.lon, lon) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(lat);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lon);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
