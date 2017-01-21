package com.example.grigorii.mindthegap.utility.fixedBonusPackClasses;

import android.content.Context;
import android.content.Intent;

import com.example.grigorii.mindthegap.model.Station;
import com.example.grigorii.mindthegap.view.PlatformsListActivity;
import com.google.gson.Gson;

import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.views.MapView;

/**
 * Created by grigorii on 18/06/16.
 *
 * OSM Bonus Pack marker that contains a Station
 * instance and onMarkerClickListener such that
 * PlatformListActivity is called when user tapped
 * on a marker
 */
public class StationMarker extends Marker {

    private Station station;

    // Deprecated since no onClickListener is
    // built there
    @Deprecated
    public StationMarker(MapView mapView) {

        super(mapView);
    }

    public StationMarker(MapView mapView, final Context mContext) {
        super(mapView);
        setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                String stationGson = (new Gson()).toJson(station);
                int index = station.getName().indexOf(" Underground Station");

                String stationName = station.getName().substring(0, index);
                Intent intent = new Intent(mContext, PlatformsListActivity.class)
                .putExtra(Intent.EXTRA_TEXT ,stationGson)
                        .putExtra("Station", stationName);
                mContext.startActivity(intent);
                return true;
            }
        });
    }

    // Deprecated since no onClickListener is
    // built there
    @Deprecated
    public StationMarker(MapView mapView, ResourceProxy resourceProxy) {
        super(mapView, resourceProxy);
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

}
