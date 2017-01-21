package com.example.grigorii.mindthegap.utility.loaders;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.grigorii.mindthegap.BuildConfig;
import com.example.grigorii.mindthegap.model.Station;
import com.example.grigorii.mindthegap.utility.adapters.PlatformsListAdapter;
import com.example.grigorii.mindthegap.utility.parsers.TfLArrivalsParser;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

/**
 * Created by grigorii on 19/06/16.
 *
 * Asynchronous loader for loading arrival information
 * for a station when the user tapped on the marker with
 * this station
 */
public class ArrivalsLoader extends AsyncTask<Void, Void, Station> {

    private static final String LOG_TAG = ArrivalsLoader.class.getSimpleName();

    /*
     * Station marker with this instance of station
     * was called
     */
    private Station mStation;

    // Adapter to put the loaded data in
    private PlatformsListAdapter mAdapter;

    // Constants for building URL
    private static final String APPID_PARAM = "app_id";
    private static final String APPKEY_PARAM = "app_key";
    private static final String STOP_POINT_ID = "stopPointId";


    public ArrivalsLoader(Station station, PlatformsListAdapter adapter) {
        mStation = station;
        mAdapter = adapter;
    }


    @Override
    protected Station doInBackground(Void... params) {

        // Object for creating connection to the TfL server
        HttpURLConnection urlConnection = null;

        // Used to read bytes from a server's response
        BufferedReader reader = null;

        // Object for storing the server's decoded response
        String queryResult;

        try {

            // Base URI for building query URL
            final String BASE_URI = "https://api.tfl.gov.uk/line/" +
                    buildLines(mStation) + "/arrivals";

            // Building final URI
            Uri uri = Uri.parse(BASE_URI).buildUpon()
                    .appendQueryParameter(STOP_POINT_ID, mStation.getId())
                    .appendQueryParameter(APPID_PARAM, BuildConfig.TFL_APP_ID)
                    .appendQueryParameter(APPKEY_PARAM, BuildConfig.TFL_APP_KEY)
                    .build();

            // Final URL
            URL url = new URL(uri.toString());

            // Connecting to the TfL server
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Fetching data from TfL server
            InputStream inputStream = urlConnection.getInputStream();

            StringBuilder builder = new StringBuilder();

            // Request returned null
            if (inputStream == null) {
                Log.e(LOG_TAG, "Input stream is null, request failed");
                return null;
            }

            // Wrapping input stream
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            // Interrupt this task if input stream was empty
            if (builder.length() == 0) {
                Log.e(LOG_TAG, "Request returned empty input stream");
                return null;
            }

            // Final query result in JSON format
            queryResult = builder.toString();

            // Parsing arrivals
            mStation = TfLArrivalsParser.parseArrivals(queryResult, mStation);

            return mStation;

        } catch (IOException e) {
            Log.e(LOG_TAG, "IOEXCEPTION");
        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSONEXCEPTION");
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error while closing reader");
                }
            }
        }

        // Something happened wrong if it got there
        return null;
    }

    /**
     * Method for building an element of uri containing
     * all the lines that cross the station
     * @param station
     * @return String with names of lines separated by
     * commas
     */
    private String buildLines(Station station) {

        StringBuilder builder = new StringBuilder();
        Set<String> lineIds = station.getLines();

        for (String lineId : lineIds) {
            builder.append(lineId);
            builder.append(",");
        }

        return builder.toString();
    }

    /**
     * Called when doInBackground method finished its work
     * @param station station with loaded arrival data
     */
    @Override
    protected void onPostExecute(Station station) {

        //Setting new data to the adapter
        mAdapter.setData(station.getArrivalBoards());
    }
}
