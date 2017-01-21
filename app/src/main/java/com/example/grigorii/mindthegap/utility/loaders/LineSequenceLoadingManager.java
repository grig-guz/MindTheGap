package com.example.grigorii.mindthegap.utility.loaders;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.example.grigorii.mindthegap.utility.fixedBonusPackClasses.StationMarker;

import org.osmdroid.bonuspack.clustering.MarkerClusterer;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Created by grigorii on 16/06/16.
 */
public class LineSequenceLoadingManager {
    /*
     * Status indicators
     */
    protected static final int LINE_DOWNLOADING_COMPLETE = 1;
    protected static final int POLYLINE_DRAWING_COMPLETE = 2;
    protected static final int LINE_DOWNLOADING_FAILED = -1;
    protected static final int MARKER_CREATING_COMPLETE = 3;
    protected static final int POLYLINE_DRAWING_FAILED = -2;
    protected static final int MARKER_DRAWING_FAILED = -3;

    // Sets the initial thread pool size
    private static final int CORE_POOL_SIZE = 4;

    // Sets the maximum thread pool size
    private static final int MAXIMUM_POOL_SIZE = 4;

    // In current Android implementations, this will return less than actual number of cores
    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    // Sets the amount of the an idle thread will wait before termination.
    private static final int KEEP_ALIVE_TIME = 1;

    //  Sets the time unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    // Single instance of LineSequenceLoadingManager used to implement singleton pattern
    private static LineSequenceLoadingManager mLoadingManager = new LineSequenceLoadingManager();;

    // Object for managing Messages in Thread
    private Handler mHandler;

    // Manager pool of background download of Line threads
    private ThreadPoolExecutor mDownloadThreadPool;

    // Manager pool of background creation of Polyline threads
    private ThreadPoolExecutor mPolylineDrawThreadPool;

    // Manager pool of background creation of Markers threads
    private ThreadPoolExecutor mMarkerCreateThreadPool;

    // Queue of Runnables for downloading Lines
    private BlockingQueue<Runnable> mLineDownloadWorkQueue;

    // Queue of Runnables for creating Polylines from Line objects
    private BlockingQueue<Runnable> mPolylineCreateWorkQueue;

    // Queue of Runnables for creating Markers from station objects inside Line objects
    private BlockingQueue<Runnable> mMarkerCreateWorkQueue;

    // Queue of LineSequenceLoadingManager tasks that are handed to a ThreadPool.
    private final Queue<LineTask> mLineTaskWorkQueue;

    // Context value for building polylines and markers
    private static Context mContext;

    // MapView to put polylines and markers in
    private static MapView mMapView;

    /**
     * Constructs the work queues and thread pools used to download and parse Lines and Arrivals.
     */
    private LineSequenceLoadingManager () {
        /*
         * Creates a work queue for the pool of Thread objects used for downloading Line objects
         * using a list queue that blocks when the mLineDownloadWorkQueue is empty.
         */
        mLineDownloadWorkQueue = new LinkedBlockingQueue<>();

        /*
         * Creates a work queue for the pool of Thread objects used for creating Polyline objects,
         * using a list queue that blocks when the mPolylineDrawWorkQueue is empty.
         */
        mPolylineCreateWorkQueue = new LinkedBlockingQueue<>();

        /*
         * Creates a work queue for the pool of Thread objects used for creating Marker objects,
         * using a list queue that blocks when the mMarkerCreateWorkQueue is empty.
         */
        mMarkerCreateWorkQueue = new LinkedBlockingQueue<>();

        /*
         * Creates a work queue for the set of task objects that control downloading and parsing,
         * using a list queue that blocks when the queue is empty.
         */
        mLineTaskWorkQueue = new LinkedBlockingQueue<>();

        /*
         * Creates a new pool of Thread objects for the download Line work queue
         */
        mDownloadThreadPool = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                mLineDownloadWorkQueue
        );

        /*
         * Creates a new pool of Thread objects for the draw Polyline work queue
         */
        mPolylineDrawThreadPool = new ThreadPoolExecutor(
                NUMBER_OF_CORES,
                NUMBER_OF_CORES,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                mPolylineCreateWorkQueue
        );

        /*
         * Creates a new pool of Thread objects for the create Markers work queue
         */
        mMarkerCreateThreadPool = new ThreadPoolExecutor(
                NUMBER_OF_CORES,
                NUMBER_OF_CORES,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                mMarkerCreateWorkQueue
        );

        /*
         * Instantiates an anonymous Handler object and defines its handleMessage() method.
         * The Handler has to run on the UI thread, because it moves Polyline objects from
         * LineSequenceDownloadManager to the MapView object.
         * The Handler is forced to run on UI thread because it is implemented in
         * LineSequenceLoadingManager constructor. Constructor is called when the class is
         * first referenced.
         */
        mHandler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {

                LineTask task = (LineTask) msg.obj;

                switch (msg.what) {

                    case POLYLINE_DRAWING_COMPLETE:

                        // Draw polylines on the map
                        Set<Polyline> polylines = task.getPolylines();
                        mMapView.getOverlays().addAll(polylines);
                        mMapView.invalidate();
                        break;

                    case MARKER_CREATING_COMPLETE:

                        // Add new markers to the map
                        Set<StationMarker> markers = task.getMarkers();
                        List<Overlay> overlays = mMapView.getOverlays();

                        for (StationMarker marker : markers) {

                            if (! overlays.contains(marker) ) {

                                overlays.add(marker);
                            }
                        }
                        mMapView.invalidate();
                        break;

                    default:
                        super.handleMessage(msg);
                }
            }
        };
    }

    public static LineSequenceLoadingManager getInstance() {
        return mLoadingManager;
    }

    /**
     * Handles state messages for a particular task object
     * @param task a task object
     * @param state The state if the task
     */
    public void handleState(LineTask task, int state) {
        switch (state) {

            // Start drawing polylines when downloading is complete
            case LINE_DOWNLOADING_COMPLETE:

                mPolylineDrawThreadPool.execute(task.getPolylineCreateRunnable());
                break;

            /*
             * Send message for handler to put polylines on the map
             * and start making markers when polylines drawing is
             * complete
             */
            case POLYLINE_DRAWING_COMPLETE:

                Message polylineMessage =
                        mHandler.obtainMessage(state, task);
                polylineMessage.sendToTarget();
                mMarkerCreateThreadPool.execute(task.getMarkerCreateRunnable());
                break;

            // Put markers on the map
            case MARKER_CREATING_COMPLETE:

                Message markerMessage =
                        mHandler.obtainMessage(state, task);
                markerMessage.sendToTarget();
                break;

            default:
                mHandler.obtainMessage(state, task).sendToTarget();
                break;
        }
    }

    public static LineSequenceLoadingManager initializeLoadingManager(MapView mapView, Context context) {

        mMapView = mapView;
        mContext = context;


        return mLoadingManager;
    }

    /**
     * Starts a line download and decode
     * @param lineId ID of the line to be downloaded and parsed
     */

    public static void startDownload(String lineId) {

        /*
         * Gets a task from the queue of tasks, returning null if the pool is empty
         */
        LineTask lineTask = mLoadingManager.mLineTaskWorkQueue.poll();

        // Create a new task if queue is null
        if (lineTask == null) {
            lineTask = new LineTask();
        }

        // Initializes the task
        lineTask.initializeLineLoadTask(lineId);

        /*
         * Executes the tasks' download and parse line runnable. If no Threads are
         * available in the thread pool, Runnable waits in queue
         */
        mLoadingManager.mDownloadThreadPool.execute(lineTask.getLineLoadRunnable());
    }

    /**
     * Free lineTask resources when the task is finished
     * @param lineTask task to clean
     */
    void recycleLineTask(LineTask lineTask) {

        // Frees memory in the task
        lineTask.recycle();

        // Puts the task object back into the queue for re-use.
        mLineTaskWorkQueue.offer(lineTask);
    }

    public Context getContext() {
        return mContext;
    }

    public MapView getMapView() {
        return mMapView;
    }
}
