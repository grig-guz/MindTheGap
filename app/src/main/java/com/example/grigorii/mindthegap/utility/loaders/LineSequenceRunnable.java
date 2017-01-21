package com.example.grigorii.mindthegap.utility.loaders;

import android.net.Uri;
import android.util.Log;

import com.example.grigorii.mindthegap.BuildConfig;
import com.example.grigorii.mindthegap.model.Line;
import com.example.grigorii.mindthegap.utility.exceptions.MalformedLatLonSequenceException;
import com.example.grigorii.mindthegap.utility.exceptions.TfLLineDataMissingException;
import com.example.grigorii.mindthegap.utility.parsers.TfLLineParser;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Created by grigorii on 10/06/16.
 *
 * This task downloads Line data from a resource addressed by a URL and
 * uses this data to construct a set of Polyline objects used to draw
 * the route of each Branch of every Line.
 *
 * Instances of this class are created and managed by LineTask's objects.
 */

public class LineSequenceRunnable implements Runnable {

    protected static final int DOWNLOAD_STATE_FAILED = -1;
    protected static final int DOWNLOAD_STATE_COMPLETED = 1;

    // Constants for building URL
    private static final String APPID_PARAM = "app_id";
    private static final String APPKEY_PARAM = "app_key";

    // Field that contains the calling object
    private LineTask mLineTask;

    // Tag for this class
    private final String LOG_TAG = LineSequenceRunnable.class.getSimpleName();

    // List with lines to be drawn on MapView
    private static final List<String> linesToDraw = Arrays.asList(
            "central", "district", "jubilee", "northern",
            "piccadilly", "bakerloo", "victoria");

    /**
     * Constructor that creates an instance of LineSequenceRunnable
     * @param lineTask The LineTask object that called this task
     */
    public LineSequenceRunnable(LineTask lineTask) {
        mLineTask = lineTask;
    }

    /*
     * Defines this object's task, which is a set of instructions designed
     * to run on a Thread
     */
    @Override
    public void run() {
        // Moves the current Thread into the background
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        // Object for creating connection to the TfL server
        HttpURLConnection urlConnection = null;

        // Used to read bytes from a server's response
        BufferedReader reader = null;

        // Object for storing the server's decoded response
        String queryResult;

        // Fetch Line data and create a set of Polyline objects
        try {

            // Base URI for building query URL
            final String BASE_URI = "https://api.tfl.gov.uk/line/" +
                    mLineTask.getLineID() + "/route/sequence/outbound";

            // Building final URI
            Uri uri = Uri.parse(BASE_URI).buildUpon()
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

            // Object to store lines read from the query by BufferedReader
            StringBuilder builder = new StringBuilder();

            // Request returned null
            if (inputStream == null) {
                Log.e(LOG_TAG, "Input stream is null, request failed");
                return;
            }

            /*
             * Object to read and decode query result from InputStream line
             * by line
             */
            reader = new BufferedReader(new InputStreamReader(inputStream));

            // Reading data from query result line by line
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            // Interrupt this task if input stream was empty
            if (builder.length() == 0) {
                Log.e(LOG_TAG, "Request returned empty input stream");
                return;
            }

            // Final query result in JSON format
            queryResult = builder.toString();

            /*
             * Calling TfLLineParser method to parse JSON query result
             * into line object
             */
            Line parsedLine = TfLLineParser.parseLine(queryResult);

            // Pass polyline data to the
            if (linesToDraw.contains(parsedLine.getId())) {

                mLineTask.setLine(parsedLine);

                /*
                 * Sets the status message in the LineTask instance.
                 * This indicates that data for Polylines is successfully downloaded
                 */

                mLineTask.handleDownloadState(DOWNLOAD_STATE_COMPLETED);
            }

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG,"URL is malformed");
            mLineTask.handleDownloadState(DOWNLOAD_STATE_FAILED);

        } catch(IOException e) {
            Log.e(LOG_TAG, "Error while getting input stream");
            mLineTask.handleDownloadState(DOWNLOAD_STATE_FAILED);

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
            mLineTask.handleDownloadState(DOWNLOAD_STATE_FAILED);

        } catch (MalformedLatLonSequenceException e) {
            Log.e(LOG_TAG, "LatLon sequence in query result is corrupt or missing");
            mLineTask.handleDownloadState(DOWNLOAD_STATE_FAILED);

        } catch (TfLLineDataMissingException e ) {
            Log.e(LOG_TAG, "Line data is missing in query result");
            mLineTask.handleDownloadState(DOWNLOAD_STATE_FAILED);
          /*
           * Closing reader and urlConnection
           */
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

            mLineTask.setLineDownloadingThread(null);
        }
    }
}
