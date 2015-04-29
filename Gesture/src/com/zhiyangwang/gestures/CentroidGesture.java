package com.zhiyangwang.gestures;

import com.zhiyangwang.geometry.Measurements;
import com.zhiyangwang.geometry.Point;
import com.zhiyangwang.geometry.PointSet;


/**
 * Implements the centroid gesture shape of a set of gestures.
 */
public class CentroidGesture extends Gesture {
	public PointAlignmentType pointAlignmentType = PointAlignmentType.Chronological;
	
	public CentroidGesture(Gesture[] gestures, PointAlignmentType alignmentType, boolean useNearestNeighbor){
		super(gestures[0].Points, gestures[0].Name, gestures[0].samplingRate());
		this.pointAlignmentType = alignmentType;
        
        // compute the centroid shape of the gesture set
        Point[] centroid = new Point[this.samplingRate()];
        for (int i = 0; i < centroid.length; i++)
            centroid[i] = new Point();
        
//        for(Gesture gesture : gestures)
//        {
//            Point[] points = alignGesture(gesture);
//            for (int i = 0; i < points.length; i++)
//                centroid[i] = Point.plus(centroid[i], points[i]);
//        }
        
        for(Gesture gesture : gestures)
        {
            Point[] points = alignGesture(gesture);
            for (int i = 0; i < points.length; i++) {
                centroid[i] = Point.plus(points[i], centroid[i]);
            }
        }
        for (int i = 0; i < centroid.length; i++)
            centroid[i] = Point.divide(centroid[i], gestures.length);
        
        // create the average gesture
        this.OriginalPoints = centroid;
        this.preprocessGesture(this.samplingRate());
        
        // if in Nearest-Neighbor mode, the gesture sample that is closest to the centroid becomes the average gesture
        if (useNearestNeighbor)
        {
            centroid = getPointsForAlignment(this);
            
            int indexNN = -1;
            double min = Double.MAX_VALUE;
            for (int indexGesture = 0; indexGesture < gestures.length; indexGesture++)
            {
                Point[] points = alignGesture(gestures[indexGesture]);
                double distance = 0;
                for (int i = 0; i < points.length; i++)
                    distance += Measurements.sqrEuclideanDistance(centroid[i], points[i]);
                if (distance < min)
                {
                    min = distance;
                    indexNN = indexGesture;
                }
            }
            this.OriginalPoints = gestures[indexNN].OriginalPoints;
            this.preprocessGesture(gestures[indexNN].samplingRate());
        }
    }

	/**
     * Aligns a gesture to the centroid.
     */
    public Point[] alignGesture(Gesture gesture)
    {
        return alignGesture(gesture, pointAlignmentType);
    }

    public Point[] alignGesture(Gesture gesture, PointAlignmentType type)
    {
        Point[] points = getPointsForAlignment(gesture);
        if (type == PointAlignmentType.Chronological)
            return points;
        else
        {
        	int[] alignment = null;
            // Hungarian is employed below, but the $P point cloud distance can also be used
        	alignment = PDollarAlternatives.hungarian(getPointsForAlignment(this), points, alignment);
            Point[] newPoints = new Point[alignment.length];
            for (int i = 0; i < alignment.length; i++)
                newPoints[i] = points[alignment[i]];
            return newPoints;
        }
    }

    /**
     * Returns the points that will be used for the alignment process.
     */
    private Point[] getPointsForAlignment(Gesture gesture)
    {
        Point[] points = gesture.Points;
        return PointSet.translateOffset(points, PointSet.centroid(points));
    }

    /**
     * Returns the points of the average gesture
     */
    public Point[] getPoints()
    {
        return getPointsForAlignment(this);
    }
}
