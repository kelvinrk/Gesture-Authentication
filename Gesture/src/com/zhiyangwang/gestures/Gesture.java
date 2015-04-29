package com.zhiyangwang.gestures;

import com.zhiyangwang.geometry.Point;
import com.zhiyangwang.geometry.PointSet;

/**
 * Implements a single / multi-stroke gesture.
 */
public class Gesture {
	public Point[] OriginalPoints = null;   
    public Point[] Points = null;               // resampled points
    public String Name = "";
    public int samplingRate = 64;
    
    /**
     * Constructs a gesture from an array of points.
     */
    public Gesture(Point[] points, String name, int samplingRate)
    {
        this.Name = name;
        this.samplingRate = samplingRate;
        if (points == null || points.length == 0)
        {
            OriginalPoints = new Point[0];
            Points = new Point[0];
        }
        else
        {
            OriginalPoints = new Point[points.length];
            for (int i = 0; i < points.length; i++)
                OriginalPoints[i] = new Point(points[i]);
            preprocessGesture(samplingRate);
        }
    }

    protected void preprocessGesture(int samplingRate)
    {
        Points = PointSet.resample(OriginalPoints, samplingRate);
        Points = PointSet.translateOffset(Points, PointSet.centroid(Points));
    }

    public int samplingRate() {
    	return Points.length;
    }
}
