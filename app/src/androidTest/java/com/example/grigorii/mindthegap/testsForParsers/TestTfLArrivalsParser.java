package com.example.grigorii.mindthegap.testsForParsers;

import android.test.AndroidTestCase;

import com.example.grigorii.mindthegap.R;
import com.example.grigorii.mindthegap.model.Arrival;
import com.example.grigorii.mindthegap.model.ArrivalBoard;
import com.example.grigorii.mindthegap.model.LatLon;
import com.example.grigorii.mindthegap.model.Station;
import com.example.grigorii.mindthegap.utility.parsers.TfLArrivalsParser;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by grigorii on 13/06/16.
 *
 */
public class TestTfLArrivalsParser extends AndroidTestCase {

    public void testParseArrivals() throws JSONException, IOException {
        StringBuilder builder = getDataFromFile(R.raw.arrivals);
        String queryResult = builder.toString();
        Station parsedStation = new Station(
                "940GZZLUHAW",
                "Harrow & Wealdstone Underground station",
                new LatLon(-0.335219, 51.592266));

        parsedStation = TfLArrivalsParser.parseArrivals(queryResult, parsedStation);

        Station correctStation = buildCorrectStation();

        assertEquals("Stations are different", parsedStation.getArrivalBoards(), correctStation.getArrivalBoards());

    }

    private StringBuilder getDataFromFile (int id) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                       mContext.getResources().openRawResource(id)));
        StringBuilder builder = new StringBuilder();
        String buffer;

        while ((buffer=reader.readLine()) != null) {
            builder.append(buffer);
        }
        return builder;
    }

    private Station buildCorrectStation() {
        Station parsedStation = new Station(
                "940GZZLUHAW",
                "Harrow & Wealdstone Underground station",
                new LatLon(-0.335219, 51.592266));
        List<ArrivalBoard> board = buildCorrectArrivalBoard();
        parsedStation.setArrivalBoards(board);
        return parsedStation;
    }

    private List<ArrivalBoard> buildCorrectArrivalBoard() {
        List<ArrivalBoard> arrivalBoards = new ArrayList<>();
        ArrivalBoard board1 = new ArrivalBoard();
        board1.setLineId("bakerloo");
        board1.setPlatformName("Southbound - Platform 2");
        board1.addArrival(new Arrival("Elephant & Castle", 2 / 60));

        arrivalBoards.add(board1);

        ArrivalBoard board2 = new ArrivalBoard();
        board2.setLineId("bakerloo");
        board2.setPlatformName("Northbound - Platform 1");
        board2.addArrival(new Arrival("Check Front of Train", 1352 / 60));
        board2.addArrival(new Arrival("Harrow & Wealdstone", 1231 / 60));
        board2.addArrival(new Arrival("Harrow & Wealdstone", 811 / 60));
        board2.addArrival(new Arrival("Harrow & Wealdstone", 181 / 60));
        arrivalBoards.add(board2);

        return arrivalBoards;
    }

//    public void testParseDestinationName() throws JSONException {
//        String name = "PleaseLetMeTakeThisCourse Underground station";
//        String nullName = "";
//
//        assertEquals("PleaseLetMeTakeThisCourse", TfLArrivalsParser.parseDestinationName(name));
//
//        try
//        {
//            TfLArrivalsParser.parseDestinationName(nullName);
//            Assert.fail();
//        } catch (JSONException e) {
//
//        }
//
//    }
}
