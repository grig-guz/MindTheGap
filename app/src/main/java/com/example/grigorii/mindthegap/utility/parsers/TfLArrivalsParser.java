package com.example.grigorii.mindthegap.utility.parsers;

import android.util.Log;

import com.example.grigorii.mindthegap.model.Arrival;
import com.example.grigorii.mindthegap.model.ArrivalBoard;
import com.example.grigorii.mindthegap.model.Station;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by grigorii on 13/06/16.
 *
 * Class containing static methods for parsing arrivals from
 * arrivals query
 */
public class TfLArrivalsParser {

    // Constants for parsing
    private static final String TFL_TIME_TO_STATION = "timeToStation";
    private static final String TFL_DESTINATON_NAME = "destinationName";
    private static final String TFL_TOWARDS = "towards";
    private static final String TFL_PLATFORM_NAME = "platformName";
    private static final String TFL_LINE_ID = "lineId";

    /**
     * Main method for parsing arrivals
     * @param queryResult result of query
     * @param targetStation station to put the data in
     * @return station with data from arrivals
     * @throws JSONException
     */
    public static Station parseArrivals(String queryResult, Station targetStation) throws JSONException {

        JSONArray rawArrivals = new JSONArray(queryResult);

        List<ArrivalBoard> arrivalBoards = getArrivalBoards(rawArrivals);
        targetStation.setArrivalBoards(arrivalBoards);

        return targetStation;
    }

    /**
     * Method for removing " Underground Station" substring from station name
     * @param destinationName station name to work with
     * @return string without station name
     * @throws JSONException
     */
    private static String parseDestinationName(String destinationName) throws JSONException {

        if (destinationName.equals("")) {

            throw new JSONException("destinationName is null");
        }

        int index = destinationName.indexOf(" Underground Station");

        try {

            return destinationName.substring(0, index);
        } catch (StringIndexOutOfBoundsException e) {

            e.printStackTrace();
            Log.e("ERROR!","Something is wrong with" + destinationName + " " + index);
            throw new JSONException("error");
        }
    }

    /**
     * Method for parsing arrival boards from json array
     * @param rawArrivals jsonArray
     * @return list of arrival boards
     * @throws JSONException
     */
    public static List<ArrivalBoard> getArrivalBoards(JSONArray rawArrivals) throws JSONException {
        List<ArrivalBoard> arrivalBoards = new ArrayList<>();

        // Iterate over all elements of jsonArray
        for (int i = 0; i < rawArrivals.length(); i++) {
            String destination;
            String lineId;
            String platformName;
            int timeToStation;

            // Get destination name value
            JSONObject rawArrival = rawArrivals.getJSONObject(i);

            try {

                destination = parseDestinationName(rawArrival.getString(TFL_DESTINATON_NAME));
            } catch (JSONException e) {

                // when destinationName field is missing
                destination = rawArrival.getString(TFL_TOWARDS);
            }

            lineId = rawArrival.getString(TFL_LINE_ID);
            platformName = rawArrival.getString(TFL_PLATFORM_NAME);
            timeToStation = rawArrival.getInt(TFL_TIME_TO_STATION) / 60;

            // Defining new arrival board
            ArrivalBoard board = new ArrivalBoard();
            board.setLineId(lineId);
            board.setPlatformName(platformName);

            /*
             * if specified arrival board is already created,
             * add arrival value to previously created arrival
             * board. If not, create new arrival board
             */
            if (arrivalBoards.contains(board)) {

                for (ArrivalBoard existingBoard : arrivalBoards) {

                    if (existingBoard.equals(board)) {

                        Arrival arrival = new Arrival(destination, timeToStation);
                        existingBoard.addArrival(arrival);
                    }
                }
            } else {
                Arrival arrival = new Arrival(destination,
                        timeToStation);

                board.addArrival(arrival);
                arrivalBoards.add(board);
            }
        }

        return arrivalBoards;
    }
}
