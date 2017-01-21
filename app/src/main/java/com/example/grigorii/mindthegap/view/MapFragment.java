package com.example.grigorii.mindthegap.view;



import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.grigorii.mindthegap.R;
import com.example.grigorii.mindthegap.utility.loaders.LineSequenceLoadingManager;


import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

/**
 * Created by grigorii on 18/06/16.
 */
public class MapFragment extends Fragment implements LocationListener {

    private static ArrayList<String> stationsToLoad = new ArrayList<>();
    private LocationManager locManager;
    private IMapController mapController;
    private ResourceProxy resourceProxy;
    private MapView map;
    private ItemizedOverlay<OverlayItem> locationOverlay;
    private ArrayList<OverlayItem> items;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        ImageView view = (ImageView) rootView.findViewById(R.id.tfl);
        view.setImageDrawable(getResources().getDrawable(R.mipmap.tflopen));

        resourceProxy = new DefaultResourceProxyImpl(getActivity().getApplicationContext());
        map = (MapView) rootView.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.setBuiltInZoomControls(false);
        mapController = map.getController();

        GeoPoint startPoint = new GeoPoint(51.506321, -0.127582);
        mapController.setCenter(startPoint);


        locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        try {
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000, 100, this);
            items = new ArrayList<>();

            items.add(new OverlayItem("Your location", null, startPoint));
            locationOverlay = new ItemizedIconOverlay<OverlayItem>(
                    items, new GestureListener(), resourceProxy);


            map.getOverlays().add(locationOverlay);

        } catch (SecurityException e) {
            e.printStackTrace();
        }



        mapController.setZoom(11);

        stationsToLoad.add("bakerloo");
        stationsToLoad.add("northern");
        stationsToLoad.add("central");
        stationsToLoad.add("jubilee");
        stationsToLoad.add("district");
        stationsToLoad.add("piccadilly");
        stationsToLoad.add("victoria");

        LineSequenceLoadingManager manager = LineSequenceLoadingManager
                .initializeLoadingManager(map, getActivity());

        for (String id : stationsToLoad) {
            manager.startDownload(id);
        }

        return rootView;
    }



    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();

        GeoPoint point = new GeoPoint(lat, lon);

        mapController.setCenter(point);

        items.clear();
        items.add(new OverlayItem("Your location", null, point));

        map.getOverlays().remove(locationOverlay);

        locationOverlay = new ItemizedIconOverlay<OverlayItem>(
                items,
                new GestureListener(),
                resourceProxy
        );
        map.getOverlays().add(locationOverlay);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    class GestureListener implements ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
        @Override
        public boolean onItemSingleTapUp(int index, OverlayItem item) {
            return true;
        }

        @Override
        public boolean onItemLongPress(int index, OverlayItem item) {
            return false;
        }
    }
}
