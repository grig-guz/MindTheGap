package com.example.grigorii.mindthegap.utility.fixedBonusPackClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.GeometryMath;
import org.osmdroid.util.TileSystem;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.util.constants.MathConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by grigorii on 18/06/16.
 *
 * This class is almost the same as original Polyline from
 * OSMBonusPack, except it contains fixed draw() method, which allows Polylines
 * to be seen when its endpoints are out of the screen.
 *
 * PLEASE NOTE! The code for this fixed method is not mine. It is provided by
 * stellardeveloper at
 * https://github.com/MKergall/osmbonuspack/issues/171
 *
 */
public class FixedPolyline extends Polyline {
    /** original GeoPoints */
    private int mOriginalPoints[][]; //as an array, to reduce object creation
    protected boolean mGeodesic;
    private final Path mPath = new Path();
    protected Paint mPaint = new Paint();
    /** points, converted to the map projection */
    private ArrayList<Point> mPoints;
    /** Number of points that have precomputed values */
    private int mPointsPrecomputed;
    public boolean mRepeatPath = false; /** if true: at low zoom level showing multiple maps, path will be drawn on all maps */

    /** bounding rectangle for the current line segment */
    private final Rect mLineBounds = new Rect();
    private final Point mTempPoint1 = new Point();
    private final Point mTempPoint2 = new Point();

    protected OnClickListener mOnClickListener;

    public FixedPolyline(Context ctx){
        this(new DefaultResourceProxyImpl(ctx));
    }

    public FixedPolyline(final ResourceProxy resourceProxy){
        super(resourceProxy);
        //default as defined in Google API:
        this.mPaint.setColor(Color.BLACK);
        this.mPaint.setStrokeWidth(10.0f);
        this.mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        this.clearPath();
        mOriginalPoints = new int[0][2];
        mGeodesic = false;
    }

    protected void clearPath() {
        this.mPoints = new ArrayList<Point>();
        this.mPointsPrecomputed = 0;
    }

    protected void addPoint(final GeoPoint aPoint) {
        addPoint(aPoint.getLatitudeE6(), aPoint.getLongitudeE6());
    }

    protected void addPoint(final int aLatitudeE6, final int aLongitudeE6) {
        mPoints.add(new Point(aLatitudeE6, aLongitudeE6));
    }

    /** @return a copy of the points. */
    public List<GeoPoint> getPoints(){
        List<GeoPoint> result = new ArrayList<GeoPoint>(mOriginalPoints.length);
        for (int i=0; i<mOriginalPoints.length; i++){
            GeoPoint gp = new GeoPoint(mOriginalPoints[i][0], mOriginalPoints[i][1]);
            result.add(gp);
        }
        return result;
    }

    public int getNumberOfPoints(){
        return mOriginalPoints.length;
    }

    public int getColor(){
        return mPaint.getColor();
    }

    public float getWidth(){
        return mPaint.getStrokeWidth();
    }

    /** @return the Paint used. This allows to set advanced Paint settings. */
    public Paint getPaint(){
        return mPaint;
    }

    public boolean isVisible(){
        return isEnabled();
    }

    public boolean isGeodesic(){
        return mGeodesic;
    }

    public void setColor(int color){
        mPaint.setColor(color);
    }

    public void setWidth(float width){
        mPaint.setStrokeWidth(width);
    }

    public void setVisible(boolean visible){
        setEnabled(visible);
    }

    public void setOnClickListener(OnClickListener listener){
        mOnClickListener = listener;
    }

    protected void addGreatCircle(final GeoPoint startPoint, final GeoPoint endPoint, final int numberOfPoints) {
        //	adapted from page http://compastic.blogspot.co.uk/2011/07/how-to-draw-great-circle-on-map-in.html
        //	which was adapted from page http://maps.forum.nu/gm_flight_path.html

        // convert to radians
        final double lat1 = startPoint.getLatitude() * MathConstants.DEG2RAD;
        final double lon1 = startPoint.getLongitude() * MathConstants.DEG2RAD;
        final double lat2 = endPoint.getLatitude() * MathConstants.DEG2RAD;
        final double lon2 = endPoint.getLongitude() * MathConstants.DEG2RAD;

        final double d = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin((lat1 - lat2) / 2), 2) + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin((lon1 - lon2) / 2), 2)));
        double bearing = Math.atan2(Math.sin(lon1 - lon2) * Math.cos(lat2),
                Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2))
                / -MathConstants.DEG2RAD;
        bearing = bearing < 0 ? 360 + bearing : bearing;

        for (int i = 1; i <= numberOfPoints; i++) {
            final double f = 1.0 * i / (numberOfPoints+1);
            final double A = Math.sin((1 - f) * d) / Math.sin(d);
            final double B = Math.sin(f * d) / Math.sin(d);
            final double x = A * Math.cos(lat1) * Math.cos(lon1) + B * Math.cos(lat2) * Math.cos(lon2);
            final double y = A * Math.cos(lat1) * Math.sin(lon1) + B * Math.cos(lat2) * Math.sin(lon2);
            final double z = A * Math.sin(lat1) + B * Math.sin(lat2);

            final double latN = Math.atan2(z, Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)));
            final double lonN = Math.atan2(y, x);
            addPoint((int) (latN * MathConstants.RAD2DEG * 1E6), (int) (lonN * MathConstants.RAD2DEG * 1E6));
        }
    }

    /** Set the points.
     * Note that a later change in the original points List will have no effect.
     * To add/remove/change points, you must call setPoints again.
     * If geodesic mode has been set, the long segments will follow the earth "great circle". */
    public void setPoints(List<GeoPoint> points){
        clearPath();
        int size = points.size();
        mOriginalPoints = new int[size][2];
        for (int i=0; i<size; i++){
            GeoPoint p = points.get(i);
            mOriginalPoints[i][0] = p.getLatitudeE6();
            mOriginalPoints[i][1] = p.getLongitudeE6();
            if (!mGeodesic){
                addPoint(p);
            } else {
                if (i>0){
                    //add potential intermediate points:
                    GeoPoint prev = points.get(i-1);
                    final int greatCircleLength = prev.distanceTo(p);
                    //add one point for every 100kms of the great circle path
                    final int numberOfPoints = greatCircleLength/100000;
                    addGreatCircle(prev, p, numberOfPoints);
                }
                addPoint(p);
            }
        }
    }

    /** Sets whether to draw each segment of the line as a geodesic or not.
     * Warning: it takes effect only if set before setting the points in the Polyline. */
    public void setGeodesic(boolean geodesic){
        mGeodesic = geodesic;
    }

    protected void precomputePoints(Projection pj){
        final int size = this.mPoints.size();
        while (this.mPointsPrecomputed < size) {
            final Point pt = this.mPoints.get(this.mPointsPrecomputed);
            pj.toProjectedPixels(pt.x, pt.y, pt);
            this.mPointsPrecomputed++;
        }
    }

    protected void drawOld(final Canvas canvas, final MapView mapView, final boolean shadow) {

        if (shadow) {
            return;
        }

        final int size = this.mPoints.size();
        if (size < 2) {
            // nothing to paint
            return;
        }

        final Projection pj = mapView.getProjection();

        // precompute new points to the intermediate projection.
        precomputePoints(pj);

        Point screenPoint0 = null; // points on screen
        Point screenPoint1;
        Point projectedPoint0; // points from the points list
        Point projectedPoint1;

        // clipping rectangle in the intermediate projection, to avoid performing projection.
        BoundingBoxE6 boundingBox = pj.getBoundingBox();
        Point topLeft = pj.toProjectedPixels(boundingBox.getLatNorthE6(),
                boundingBox.getLonWestE6(), null);
        Point bottomRight = pj.toProjectedPixels(boundingBox.getLatSouthE6(),
                boundingBox.getLonEastE6(), null);
        final Rect clipBounds = new Rect(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y);
        // take into account map orientation:
        if (mapView.getMapOrientation() != 0.0f)
            GeometryMath.getBoundingBoxForRotatatedRectangle(clipBounds, mapView.getMapOrientation(), clipBounds);

        mPath.rewind();
        projectedPoint0 = this.mPoints.get(size - 1);
        mLineBounds.set(projectedPoint0.x, projectedPoint0.y, projectedPoint0.x, projectedPoint0.y);

        for (int i = size - 2; i >= 0; i--) {
            // compute next points
            projectedPoint1 = this.mPoints.get(i);
            mLineBounds.union(projectedPoint1.x, projectedPoint1.y);

            if (!Rect.intersects(clipBounds, mLineBounds)) {
                // skip this line, move to next point
                projectedPoint0 = projectedPoint1;
                screenPoint0 = null;
                continue;
            }

            // the starting point may be not calculated, because previous segment was out of clip
            // bounds
            if (screenPoint0 == null) {
                screenPoint0 = pj.toPixelsFromProjected(projectedPoint0, this.mTempPoint1);
                mPath.moveTo(screenPoint0.x, screenPoint0.y);
            }

            screenPoint1 = pj.toPixelsFromProjected(projectedPoint1, this.mTempPoint2);

            // skip this point, too close to previous point
            if (Math.abs(screenPoint1.x - screenPoint0.x) + Math.abs(screenPoint1.y - screenPoint0.y) <= 1) {
                continue;
            }

            mPath.lineTo(screenPoint1.x, screenPoint1.y);

            // update starting point to next position
            projectedPoint0 = projectedPoint1;
            screenPoint0.x = screenPoint1.x;
            screenPoint0.y = screenPoint1.y;
            mLineBounds.set(projectedPoint0.x, projectedPoint0.y, projectedPoint0.x, projectedPoint0.y);
        }

        canvas.drawPath(mPath, mPaint);
    }

    @Override
    protected void draw (final Canvas canvas, final MapView mapView, final boolean shadow) {

        if (shadow) {
            return;
        }

        final int size = mPoints.size();
        if (size < 2) {
            // nothing to paint
            return;
        }

        final Projection pj = mapView.getProjection();
        // 180° in longitude in pixels, for avoiding lines > 180° in length
        final int halfMapSize = TileSystem.MapSize(mapView.getProjection().getZoomLevel()) / 2;
        // southern Limit of the map in Pixels, for detecting map crossing lines
        final int southLimit = pj.toPixelsFromMercator(0, halfMapSize * 2, null).y - 1;
        // calculate clipBounds in screen coordinates for removing "invisible" line segments
        final Rect clipBounds = new Rect(0, 0, mapView.getWidth() - 1, mapView.getHeight() - 1);
        // take into account map orientation:
        if (mapView.getMapOrientation() != 0.0f)
            GeometryMath.getBoundingBoxForRotatatedRectangle(clipBounds, mapView.getMapOrientation(), clipBounds);

        // area of visible line segments
        Rect pathExtension = new Rect(clipBounds); // used for decision: canvas.draw (fast) or bitmap draw (slow, avoiding OpenGLRenderer problem)
        boolean lineVisible = false;

        // precompute new points to the intermediate projection.
        precomputePoints(pj);

        final Point projectedPoint0 = mPoints.get(0); // points from the points list

        final Point screenPoint0 = pj.toPixelsFromProjected(projectedPoint0, mTempPoint1); // points on screen
        Point projectedPoint1;
        Point screenPoint1;

        mPath.rewind();

        for (int i = 1; i < size; i++) {

            // compute next points
            projectedPoint1 = mPoints.get(i);
            screenPoint1 = pj.toPixelsFromProjected(projectedPoint1, this.mTempPoint2);
            if (Math.abs(screenPoint1.x - screenPoint0.x) + Math.abs(screenPoint1.y - screenPoint0.y) <= 1
                    && screenPoint0.y != southLimit && screenPoint1.y != southLimit) { // do not skip points  crossing maps!
                // skip this point, too close to previous point
                continue;
            }


            // check for lines exceeding 180° in longitude, cut line into two segments
            if ((Math.abs(screenPoint1.x - screenPoint0.x) > halfMapSize)
                    // check for lines crossing the southern limit
                    || (screenPoint1.y > southLimit) != (screenPoint0.y > southLimit)) {
                // handle x and y coordinates separately
                int x0 = screenPoint0.x;
                int y0 = screenPoint0.y;
                int x1 = screenPoint1.x;
                int y1 = screenPoint1.y;

                // first check x
                if (Math.abs(screenPoint1.x - screenPoint0.x) > halfMapSize) {// x has to be adjusted
                    if (screenPoint1.x < mapView.getWidth() / 2) {
                        // screenPoint1 is left of screenPoint0
                        x1 += halfMapSize * 2; // set x1 360° east of screenPoint1
                        x0 -= halfMapSize * 2; // set x0 360° west of screenPoint0
                    } else {
                        x1 -= halfMapSize * 2;
                        x0 += halfMapSize * 2;
                    }
                }

                // now check y
                if ((screenPoint1.y > southLimit) != (screenPoint0.y > southLimit)) {
                    // line is crossing from one map to the other
                    if (screenPoint1.y > southLimit) {
                        // screenPoint1 was switched to map below
                        y1 -= halfMapSize * 2;  // set y1 into northern map
                        y0 += halfMapSize * 2;  // set y0 into map below
                    } else {
                        y1 += halfMapSize * 2;
                        y0 -= halfMapSize * 2;
                    }
                }
                // create rectangle of line segment, ensure top < bottom and right < left  to obtain valid rectangle!
                mLineBounds.set(Math.min(screenPoint0.x, x1), Math.min(screenPoint0.y, y1),
                        Math.max(screenPoint0.x, x1), Math.max(screenPoint0.y, y1));
                // check whether this line segment is visible
                if (Rect.intersects(clipBounds, mLineBounds)) {
                    mPath.moveTo(screenPoint0.x, screenPoint0.y);
                    mPath.lineTo(x1, y1);
                    pathExtension.union(mLineBounds); // caution! buggy for invalid rectangles (top > bottom or right > left) !
                    lineVisible = true;
                }
                screenPoint0.x = x0;
                screenPoint0.y = y0;
            } // end of line break check

            // check, whether this line segment is visible, ensure top < bottom and right < left  to obtain valid rectangle!
            mLineBounds.set(Math.min(screenPoint0.x, screenPoint1.x), Math.min(screenPoint0.y, screenPoint1.y),
                    Math.max(screenPoint0.x, screenPoint1.x), Math.max(screenPoint0.y, screenPoint1.y));
            if (Rect.intersects(clipBounds, mLineBounds)) {
                mPath.moveTo(screenPoint0.x, screenPoint0.y);
                mPath.lineTo(screenPoint1.x, screenPoint1.y);
                pathExtension.union(mLineBounds);
                lineVisible = true;
            }

            // update starting point to next position
            screenPoint0.x = screenPoint1.x;
            screenPoint0.y = screenPoint1.y;
        }


        // send only visible lines to canvas, not segments outside screen area
        if (lineVisible) {

            //  check, whether visible line segments cover less than 2048 pixels rectangle.
            if (Math.max(pathExtension.width(), pathExtension.height()) <= 2048) {
                //  Use fast canvas.drawPath method.
                canvas.drawPath(mPath, mPaint);
            } else {

                // fixing W/OpenGLRenderer(29239): Shape path too large to be rendered into a texture.
                // This will occur, if memory exceeds 2040 pixels => Path will disappear.
                // Draw path into temporary bitmap in screen size, then send bitmap to screen canvas.
                // <application android:hardwareAccelerated="false" > does not fix the OpenGLRenderer problem!
                // Line (80, 80) to (-80, 80) will disappear at zoomLevel 4, Line (90, 90) to (-90, 90) at zoomLevel 3
                final Rect dest = new Rect(0, 0, mapView.getWidth() - 1, mapView.getHeight() - 1);
                //          dest.set(0, 0, mapView.getWidth()-1,  mapView.getHeight()-1);
                //          final Bitmap.Config conf = Bitmap.Config.ARGB_8888; // 8 bit Aplpha, RGB
                final Bitmap mBmp = Bitmap.createBitmap(mapView.getWidth(), mapView.getHeight(), Bitmap.Config.ARGB_8888); // this creates a MUTABLE bitmap
                final Canvas bmpcanvas = new Canvas(mBmp);
                // Draw path to bitmap according to actual screen rotation
                if (mapView.getMapOrientation() != 0.0f)
                    bmpcanvas.rotate(mapView.getMapOrientation(), mapView.getWidth() / 2, mapView.getHeight() / 2);
                bmpcanvas.drawPath(mPath, mPaint);

                // Draw bitmap according to actual screen rotation, derotate canvas if necessary
                if (mapView.getMapOrientation() != 0.0f)
                    canvas.rotate(-mapView.getMapOrientation(), mapView.getWidth() / 2, mapView.getHeight() / 2);
                canvas.drawBitmap(mBmp, dest, dest, null);
                //restore canvas rotation if necessary
                if (mapView.getMapOrientation() != 0.0f)
                    canvas.rotate(mapView.getMapOrientation(), mapView.getWidth() / 2, mapView.getHeight() / 2);
            }
        }
    }
}

