// **********************************************************************
// 
// <copyright>
// 
//  BBN Technologies, a Verizon Company
//  10 Moulton Street
//  Cambridge, MA 02138
//  (617) 873-8000
// 
//  Copyright (C) BBNT Solutions LLC. All rights reserved.
// 
// </copyright>
// **********************************************************************
// 
// $Source: /cvs/distapps/openmap/src/openmap/com/bbn/openmap/proj/Cartesian.java,v $
// $RCSfile: Cartesian.java,v $
// $Revision: 1.2 $
// $Date: 2006/02/16 16:22:46 $
// $Author: dietrick $
// 
// **********************************************************************

package com.bbn.openmap.proj;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * The Cartesian projection is a non-wrapping, straight-forward scaling
 * projection drawn in 2D. The simplest projection ever, it can be used for
 * regular plotting.
 */
public class Cartesian extends Proj implements Projection, java.io.Serializable {
    /**
     * The Cartesian name.
     */
    public final static transient String CartesianName = "Cartesian";

    protected Point2D ulLimit;
    protected Point2D lrLimit;
    protected double scaleFactor;

    protected transient double hWidth;
    protected transient double hHeight;
    protected transient double SFScale;

    protected transient AffineTransform transform1;
    protected transient AffineTransform transform2;
    // protected transient AffineTransform transform3;
    protected transient AffineTransform transform4;

    /**
     * Create a Cartesian projection that does straight scaling, no wrapping.
     * 
     * @param center the coordinates of the center of the map.
     * @param scale the scale to use for the map, referring to the difference of
     *        the ration between pixels versus coordinate values.
     * @param width the pixel width of the map.
     * @param height the pixel height of the map.
     */
    public Cartesian(Point2D center, float scale, int width, int height) {
        super(center, scale, width, height);
    }

    public void init() {
        scaleFactor = 100000000;
    }

    protected void computeParameters() {
        hWidth = width / 2.0;
        hHeight = height / 2.0;
        SFScale = scaleFactor / scale;

        transform1 = AffineTransform.getTranslateInstance(-centerX, -centerY);
        transform2 = AffineTransform.getScaleInstance(SFScale, -SFScale);
        // transform3 = AffineTransform.getScaleInstance(1 / SFScale,
        // -1 / SFScale);
        transform4 = AffineTransform.getTranslateInstance(hWidth, hHeight);
    }

    /**
     * Get the world coordinate of the upper left corner of the map.
     */
    public Point2D getUpperLeft() {
        return inverse(0, 0, new Point2D.Double());
    }

    /**
     * Get the world coordinate of the lower right corner of the map.
     */
    public Point2D getLowerRight() {
        return inverse(width, height, new Point2D.Double());
    }

    /**
     * Forward project a world coordinate into screen space.
     * 
     * @param wy vertical coordinate component in world units.
     * @param wx horizontal coordinate component in world units.
     * @param mapPoint screen point to load result into. OK if null, a new one
     *        will be created and returned.
     * @return Point2D provided or new one created containing map coordinate.
     */
    public Point2D forward(double wy, double wx, Point2D mapPoint) {

        double x = ((wx - centerX) * SFScale) + hWidth;
        double y = hHeight - ((wy - centerY) * SFScale);

        if (mapPoint == null) {
            mapPoint = new Point2D.Double(x, y);
        } else {
            mapPoint.setLocation(x, y);
        }
        /*
         * fPoint1.setLocation(wx, wy); Point2D tmp =
         * transform1.transform(fPoint1, fPoint2); tmp =
         * transform2.transform(tmp, fPoint1); tmp = transform4.transform(tmp,
         * fPoint2); mapPoint.setLocation(tmp.getX(), tmp.getY());
         */
        return mapPoint;
    }

    // Used for AffineTransform forward and inverse methods, to save
    // allocation expense.
    // Point2D fPoint1 = new Point2D.Double();
    // Point2D fPoint2 = new Point2D.Double();
    // Point2D iPoint1 = new Point2D.Double();
    // Point2D iPoint2 = new Point2D.Double();

    /**
     * Inverse projection a map coordinate into world space.
     * 
     * @param x horizontal map coordinate from left side of map.
     * @param y vertical map coordinate from top of map.
     * @param worldPoint a Point2D object to load result into. OK if null, a new
     *        one will be created if necessary.
     * @return Point2D provided or new one if created, containing the result.
     */
    public Point2D inverse(double x, double y, Point2D worldPoint) {
        double worldPointX = (x - hWidth) / SFScale + centerX;
        double worldPointY = (hHeight - y) / SFScale + centerY;

        if (worldPoint == null) {
            worldPoint = new Point2D.Double(worldPointX, worldPointY);
        } else {
            worldPoint.setLocation(worldPointX, worldPointY);
        }
        /*
         * try { iPoint1.setLocation(x, y); Point2D tmp =
         * transform4.inverseTransform(iPoint1, iPoint2); tmp =
         * transform3.transform(tmp, iPoint1); transform1.inverseTransform(tmp,
         * worldPoint); } catch (NoninvertibleTransformException e) {
         * e.printStackTrace(); } catch (Exception e) { e.printStackTrace(); }
         * 
         */
        return worldPoint;
    }

    /**
     * @param Az direction, 0 is north, positive is clockwise.
     * @param c number of world coordinates to pan.
     */
    public void pan(float Az, float c) {
        double currentX = centerX;
        double currentY = centerY;

        currentX -= c * Math.sin(Math.toRadians(Az) + Math.PI);
        currentY -= c * Math.cos(Math.toRadians(Az) + Math.PI);

        setCenter(new Point2D.Double(currentX, currentY));
    }

    /**
     * Pan half a view.
     */
    public void pan(float Az) {
        pan(Az, (float) (getUpperLeft().distance(getLowerRight()) / 4.0));
    }

    /**
     * Takes a java.awt.Shape object and re-projects it for a the current view.
     * Returns a GeneralPath.
     */
    public Shape forwardShape(Shape shape) {

        return transform4.createTransformedShape(transform2.createTransformedShape(transform1.createTransformedShape(shape)));

        // Set Proj.java for the iterator way of doing this.
    }

    /**
     */
    public String getName() {
        return CartesianName;
    }

    /**
     */
    public float getScale(Point2D ulWorldPoint, Point2D lrWorldPoint,
                          Point2D point1, Point2D point2) {
        try {

            double worldCoords;
            double deltaPix;
            double dx = Math.abs(lrWorldPoint.getX() - ulWorldPoint.getX());
            double dy = Math.abs(lrWorldPoint.getY() - ulWorldPoint.getY());

            if (dx <= dy) {
                worldCoords = dx;
                deltaPix = getWidth();
            } else {
                worldCoords = dy;
                deltaPix = getHeight();
            }

            // The new scale...
            return (float) (worldCoords / deltaPix * scaleFactor);
        } catch (NullPointerException npe) {
            com.bbn.openmap.util.Debug.error("CartesianProjection.getScale(): caught null pointer exception.");
            return Float.MAX_VALUE;
        }
    }

    public boolean isPlotable(double lat, double lon) {
        return true;
    }

}