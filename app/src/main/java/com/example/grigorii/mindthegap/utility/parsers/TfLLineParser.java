package com.example.grigorii.mindthegap.utility.parsers;

import android.graphics.Color;

import com.example.grigorii.mindthegap.model.Branch;
import com.example.grigorii.mindthegap.model.LatLon;
import com.example.grigorii.mindthegap.model.Line;
import com.example.grigorii.mindthegap.model.Station;
import com.example.grigorii.mindthegap.model.StationManager;
import com.example.grigorii.mindthegap.utility.exceptions.MalformedLatLonSequenceException;
import com.example.grigorii.mindthegap.utility.exceptions.TfLLineDataMissingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashSet;
import java.util.Set;

/**
 * Created by grigorii on 11/06/16.
 *
 * Class that contains static methods for parsing line data from
 * query result
 */

public class TfLLineParser {

    // Instance of stationManager to prevent creating duplicate values
    private static StationManager stationManager = StationManager.getInstance();
    private static Set<Branch> createdBranches = stationManager.getListOfBranches();

    /*
     * Object to be used as monitor in a synchronized block
     * to make updating the values of the stationManager
     * object thread-safe
     */
    private static final Object lock = new Object();

    // Constants for parsing Line object from JSON
    private static final String TFL_BRANCH = "lineStrings";
    private static final String TFL_STOP_POINT_SEQUENCE = "stopPointSequences";
    private static final String TFL_STOP_POINT = "stopPoint";
    private static final String TFL_LINE_ID = "lineId";
    private static final String TFL_LINE_NAME = "lineName";
    private static final String TFL_STATION_ID = "stationId";
    private static final String TFL_STATION_NAME = "name";
    private static final String TFL_STATION_LAT = "lat";
    private static final String TFL_STATION_LON = "lon";

    /**
     * Main method for parsing raw line data into Line object
     * @param queryResult raw json line data
     * @return instance of Line created from json data
     * @throws JSONException
     * @throws MalformedLatLonSequenceException
     * @throws TfLLineDataMissingException
     */
    public static Line parseLine(String queryResult) throws JSONException, MalformedLatLonSequenceException, TfLLineDataMissingException {
        String lineId, lineName;
        JSONObject jsonObject;
        JSONArray branches, stopSequences;

        try {
            jsonObject = new JSONObject(queryResult);
            lineId = jsonObject.getString(TFL_LINE_ID);
            lineName = jsonObject.getString(TFL_LINE_NAME);
            branches = jsonObject.getJSONArray(TFL_BRANCH);
        } catch (JSONException e) {
            throw new TfLLineDataMissingException();
        }

        Line line = new Line(lineId, lineName);



        line.setBranches(parseBranches(branches));
        stopSequences = jsonObject.getJSONArray(TFL_STOP_POINT_SEQUENCE);
        line = parseStations(stopSequences, line);

        return line;
    }

    /**
     * Method for parsing branches in raw line data.
     * Uses BranchStringParser
     * @param branches jsonArray with raw branch data
     * @return set of branches in the line
     * @throws JSONException
     * @throws MalformedLatLonSequenceException
     */
    public static Set<Branch> parseBranches(JSONArray branches) throws JSONException, MalformedLatLonSequenceException {
        Set<Branch> parsedBranches = new HashSet<>();

        for (int i = 0; i < branches.length(); i++) {

            String rawBranch = branches.getString(i);
            Branch parsedBranch = new Branch();

            parsedBranch.setStationSeq(BranchStringParser
                    .getBranchData(rawBranch));

            /*
             * If branch was already created, don't add it
             * to the set of branches
             */
            if (createdBranches.contains(parsedBranch))
                continue;

            createdBranches.add(parsedBranch);
            parsedBranches.add(parsedBranch);

        }
        return parsedBranches;
    }

    /**
     * Method for parsing stations in stopSequenceArray from query into Line
     * that contains these stations
     * @param stopSequences jsonArray
     * @param line Line to put the data in
     * @return Line with stations
     * @throws JSONException
     * @throws TfLLineDataMissingException
     */
    public static Line parseStations(JSONArray stopSequences, Line line) throws JSONException, TfLLineDataMissingException {

        // Iterate over all stations
        for (int i = 0; i < stopSequences.length(); i++) {

            JSONObject stopPointSequenceEntry = stopSequences.getJSONObject(i);
            JSONArray stopPointSequenceArray;
            try {
                stopPointSequenceArray = stopPointSequenceEntry.getJSONArray(TFL_STOP_POINT);
            } catch (JSONException e) {
                throw new TfLLineDataMissingException();
            }

            outerLoop:
            for (int j = 0; j < stopPointSequenceArray.length(); j++) {

                JSONObject stopPoint = stopPointSequenceArray.getJSONObject(j);

                String stationId = stopPoint.getString(TFL_STATION_ID);

                /*
                 * Preventing multiple threads from accessing stationManager.
                 * This will help to avoid iteration over set that is being
                 * modified at this time
                 */
                synchronized (lock) {
                    /*
                     * If this station is already created, add lineId
                     * of this line to this station
                     */
                    for (Station station : stationManager) {
                        if (station.getId().equals(stationId)) {
                            station.addLine(line.getId());
                            line.getStations().add(station);
                            continue outerLoop;
                        }
                    }
                }

                String stationName = stopPoint.getString(TFL_STATION_NAME);
                double stationLat = stopPoint.getDouble(TFL_STATION_LAT);
                double stationLon = stopPoint.getDouble(TFL_STATION_LON);
                LatLon stationLocation = new LatLon(stationLon, stationLat);

                Station station = new Station(stationId,
                        stationName,
                        stationLocation);

                // Adding elements to a shared set is not thread-safe
                synchronized (lock) {

                    stationManager.addStation(station);
                }

                line.getStations().add(station);
                station.addLine(line.getId());
            }
        }

        setColor(line);

        return line;
    }

    /**
     * Method for setting color to a line
     * @param line instance of line
     * @throws JSONException
     */
    private static void setColor(Line line) throws JSONException {
        switch (line.getId()) {

            case "central":
                line.setColor(Color.RED);
                break;
            case "circle":
                line.setColor(Color.YELLOW);
            case "district":
                line.setColor(Color.parseColor("#1B5E20"));
                break;
            case "jubilee":
                line.setColor(Color.GRAY);
                break;
            case "northern":
                line.setColor(Color.BLACK);
                break;
            case "piccadilly":
                line.setColor(Color.BLUE);
                break;
            case "bakerloo":
                line.setColor(Color.parseColor("#795548"));
                break;
            case "victoria":
                line.setColor(Color.CYAN);
                break;
            case "hammersmith-cyty":
                line.setColor(Color.parseColor("#F48FB1"));
                break;
            case "metropolitan":
                line.setColor(Color.MAGENTA);
            default:
                throw new JSONException("Error assigning color" + line.getId());
        }
    }

}
