package com.example.grigorii.mindthegap.utility.loaders;

import android.content.Context;


import com.example.grigorii.mindthegap.model.Line;
import com.example.grigorii.mindthegap.utility.fixedBonusPackClasses.StationMarker;

import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.views.MapView;

import java.util.Set;

/**
 * Created by grigorii on 16/06/16.
 * Task instance serving as bridge between LineSequenceLoadingManager and
 * Threads where all the data processing occurs.
 */
public class LineTask {

    private Set<Polyline> mPolylines;

    private Line mLine;

    private Set<StationMarker> mMarkers;

    private Runnable mLineLoadRunnable;

    private Runnable mPolylineDrawRunnable;

    private Runnable mMarkerCreateRunnable;

    private String mLineID;

    private Context mContext;

    private MapView mMapView;
    /*
     * An object containing the ThreadPool singleton
     */
    private static LineSequenceLoadingManager mLoadingManager;


    /**
     * Creates a LineTask containing download and parse object.
     */
    LineTask() {
        // Create runnable
        mLineLoadRunnable = new LineSequenceRunnable(this);
        mPolylineDrawRunnable = new PolylineDrawRunnable(this);
        mMarkerCreateRunnable = new MarkerCreateRunnable(this);
        mLoadingManager = LineSequenceLoadingManager.getInstance();
        mContext = mLoadingManager.getContext();
        mMapView = mLoadingManager.getMapView();
    }

    /**
     * Method that adds line id to the task in order to
     * form query for downloading line in LineSequenceRunnable
     * @param lineId id of the line to download the data for
     */
    void initializeLineLoadTask(String lineId) {
        mLineID = lineId;
    }

    public void setPolylines(Set<Polyline> polylines) {
        mPolylines = polylines;
    }

    /**
     * Method for notifying LineSequenceLoadingManager that
     * LineSequence runnable finished its work
     * @param state response id from LineSequenceRunnable
     */
    public void handleDownloadState(int state) {
        int outState;

        switch (state) {

            case LineSequenceRunnable.DOWNLOAD_STATE_COMPLETED:

                outState = LineSequenceLoadingManager.LINE_DOWNLOADING_COMPLETE;
                break;

            default:

                outState = LineSequenceLoadingManager.LINE_DOWNLOADING_FAILED;
        }

        handleState(outState);
    }

    /**
     * Method for notifying LineSequenceLoadingManager that
     * PolylineDrawRunnable finished its work
     * @param state response id from PolylineDrawRunnable
     */
    public void handlePolylineCreateState(int state) {
        int outState;

        switch (state) {
            case PolylineDrawRunnable.POLYLINE_CREATE_STATE_COMPLETED:

                outState = LineSequenceLoadingManager.POLYLINE_DRAWING_COMPLETE;
                break;

            default:

                outState = LineSequenceLoadingManager.POLYLINE_DRAWING_FAILED;
        }

        handleState(outState);
    }

    /**
     * Method for notifying LineSequenceLoadingManager that
     * MarkerCreateRunnable finished its work
     * @param state response id from MarkerCreateRunnable
     */
    public void handleMarkerCreateState(int state) {
        int outState;

        switch (state) {

            case MarkerCreateRunnable.MARKER_CREATE_STATE_COMPLETED:

                outState = LineSequenceLoadingManager.MARKER_CREATING_COMPLETE;
                break;

            default:

                outState = LineSequenceLoadingManager.MARKER_DRAWING_FAILED;
        }

        handleState(outState);
    }

    /**
     * Recycles LineTask object before it's put back into the pool. Helps
     * to avoid memory leaks.
     */
    public void recycle() {

        mLineID = null;
        mLine = null;
        mMarkers = null;
        mPolylines = null;
    }

    void handleState(int state) {
        mLoadingManager.handleState(this, state);
    }


    public Set<Polyline> getPolylines() {
        return mPolylines;
    }

    public void setLineDownloadingThread(Thread lineLoadRunnable) {
        mLineLoadRunnable = lineLoadRunnable;
    }

    public void setPolylineCreateThread(Thread polylineDrawingRunnable) {
        mPolylineDrawRunnable = polylineDrawingRunnable;
    }

    public String getLineID() {
        return mLineID;
    }

    public Context getContext() {
        return mContext;
    }

    public Line getLine() {
        return mLine;
    }

    public void setLine(Line line) {
        mLine = line;
    }

    public Runnable getPolylineCreateRunnable() {
        return mPolylineDrawRunnable;
    }

    public Runnable getMarkerCreateRunnable() {
        return mMarkerCreateRunnable;
    }

    public void setMarkerCreateThread(Runnable markerCreateRunnable) {
        mMarkerCreateRunnable = markerCreateRunnable;
    }

    public Runnable getLineLoadRunnable() {
        return mLineLoadRunnable;
    }

    public Set<StationMarker> getMarkers() {
        return mMarkers;
    }

    public void setMarkers(Set<StationMarker> markers) {
        mMarkers = markers;
    }

    public MapView getMapView() {
        return mMapView;
    }
}
