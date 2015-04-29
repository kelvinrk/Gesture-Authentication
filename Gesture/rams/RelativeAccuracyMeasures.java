package com.zhiyangwang.rams;

import com.zhiyangwang.geometry.Measurements;
import com.zhiyangwang.geometry.Point;
import com.zhiyangwang.geometry.PointSet;
import com.zhiyangwang.geometry.Vector;
import com.zhiyangwang.gestures.CentroidGesture;
import com.zhiyangwang.gestures.Gesture;
import com.zhiyangwang.gestures.PointAlignmentType;

public class RelativeAccuracyMeasures {
	private static boolean computeAsPercentage = false;
//	private boolean computeAsPercentage = false;
	
	/***************************** 6 geometric relative accuracy measures: ShE, ShV, LE, SzE, BE, BV ***************************/
	
	/**
	 * Shape Error (ShE) is defined as the average of the absolute Euclidean distances
	 * computed between gesture points and task axis points.
	 */
	
    public static double shapeError(Gesture gesture, CentroidGesture taskAxis)
    {
        double[] errors = computeLocalShapeErrors(gesture, taskAxis);
        return computeAverage(errors);
    }

    /**
	 * Shape Variability (ShV) is defined as the standard deviation of all point distances to the gesture task axis.
	 */
    public static double shapeVariability(Gesture gesture, CentroidGesture taskAxis)
    {
        double[] errors = computeLocalShapeErrors(gesture, taskAxis);
        return computeStdev(errors);
    }

    private static double[] computeLocalShapeErrors(Gesture gesture, CentroidGesture taskAxis)
    {
        // align gesture points with the task axis
        Point[] taskAxisPts = taskAxis.getPoints();
        Point[] gesturePts = taskAxis.alignGesture(gesture);

        // compute errors
        double[] errors = new double[gesturePts.length];
        for (int i = 0; i < gesturePts.length; i++)
            errors[i] = Measurements.euclideanDistance(taskAxisPts[i], gesturePts[i]);
        return errors;
    }

    /**
	 * length Error (LE) is defined as the absolute difference in path length 
	 * between the gesture and the task axis.
	 */
    public static double lengthError(Gesture gesture, CentroidGesture taskAxis, boolean computeAsPercentage)
    {
        double gesturelength = PointSet.pathLength(gesture.Points);
        double taskAxislength = PointSet.pathLength(taskAxis.Points);
        double error = Math.abs(gesturelength - taskAxislength);
//        RelativeAccuracyMeasures.computeAsPercentage = computeAsPercentage;
        if (computeAsPercentage)
            error /= taskAxislength;
        return error;
    }
    
    /**
	 * Size Error (SzE) is defined 
	 * as the absolute difference in area size from the task axis.
	 */
    public static double sizeError(Gesture gesture, CentroidGesture taskAxis, boolean computeAsPercentage)
    {
        double gestureArea = PointSet.boundingBox(gesture.Points).getArea();
        double taskAxisArea = PointSet.boundingBox(taskAxis.Points).getArea();
        double error = Math.abs(gestureArea - taskAxisArea);
//        RelativeAccuracyMeasures.computeAsPercentage = computeAsPercentage;
        if (computeAsPercentage)
            error /= taskAxisArea;
        return error;
    }

    /**
	 * Bending Error (BE) is defined as the average of absolute differences in
	 * turning angle of each gesture point to the task axis.
	 */
    public static double bendingError(Gesture gesture, CentroidGesture taskAxis)
    {
        double[] errors = computeLocalBendingErrors(gesture, taskAxis);
        return computeAverage(errors);
    }

    /**
	 * Bending Variability (BV) is defined
	 * as the standard deviation of differences in turning angle of each point to the task axis.
	 */
    public static double bendingVariability(Gesture gesture, CentroidGesture taskAxis)
    {
        double[] errors = computeLocalBendingErrors(gesture, taskAxis);
        return computeStdev(errors);
    }

    private static double[] computeLocalBendingErrors(Gesture gesture, CentroidGesture taskAxis)
    {
        // align gesture points with the task axis
        Point[] taskAxisPts = taskAxis.getPoints();
        Point[] gesturePts = taskAxis.alignGesture(gesture);

        // compute errors
        double[] kTaskAxis = computeTurningAngleArray(taskAxisPts);
        double[] kGesture = computeTurningAngleArray(gesturePts);
        double[] errors = new double[gesturePts.length];
        for (int i = 0; i < gesturePts.length; i++)
            errors[i] = Math.abs(kGesture[i] - kTaskAxis[i]);
        return errors;
    }

    /**
	 * Returns an array with turning angle values for each point.
	 */
    private static double[] computeTurningAngleArray(Point[] points)
    {
        double[] k = new double[points.length];
        int n = k.length;
        int r = 1;
        for (int i = 0; i < n; i++)
        {
            if (i - r >= 0 && i + r < n)
            {
                double angle = Measurements.angle(new Vector(points[i], points[i + r]), new Vector(points[i - r], points[i]));
                if (angle > Math.PI)
                    angle = angle - 2.0 * Math.PI;
                k[i] = angle;
            }
            else k[i] = 0;
        }
        return k;
    }

    /*******************kinematic relative accuracy measures: TE, TV, VE, VV**************************************/

    /**
	 *  Time Error (TE) is defined as the absolute difference in articulation time.
	 */
    public static double timeError(Gesture gesture, CentroidGesture taskAxis, boolean computeAsPercentage)
    {
        double taskAxisTime = productionTime(taskAxis);
        double gestureTime = productionTime(gesture);
        double error = Math.abs(gestureTime - taskAxisTime);
//        RelativeAccuracyMeasures.computeAsPercentage = computeAsPercentage;
        if (computeAsPercentage)
            error /= taskAxisTime;
        return error;
    }

    /**
	 * Time Variability (TV) is defined as the standard deviation of local times.
	 */
    public static double timeVariability(Gesture gesture, CentroidGesture taskAxis)
    {
        // align gesture points with the task axis
        Point[] taskAxisPts = taskAxis.getPoints();
        Point[] gesturePts = taskAxis.alignGesture(gesture);

        // compute errors
        double[] errors = new double[gesturePts.length];
        for (int i = 0; i < gesturePts.length; i++)
            errors[i] = Math.abs(gesturePts[i].T - taskAxisPts[i].T);
        return computeStdev(errors);
    }

    /**
	 * Speed Error (VE) is defined as the absolute difference in speed.
	 */
    public static double velocityError(Gesture gesture, CentroidGesture taskAxis)
    {
        double[] errors = computeLocalSpeedErrors(gesture, taskAxis);
        return computeAverage(errors);
    }

    /**
	 * Velocity Variability is defined as the standard deviation of local speed values.
	 */
    public static double velocityVariability(Gesture gesture, CentroidGesture taskAxis)
    {
        double[] errors = computeLocalSpeedErrors(gesture, taskAxis);
        return computeStdev(errors);
    }

    private static double[] computeLocalSpeedErrors(Gesture gesture, CentroidGesture taskAxis)
    {
        // align gesture points with the task axis
        Point[] taskAxisPts = taskAxis.getPoints();
        Point[] gesturePts = taskAxis.alignGesture(gesture);

        // compute measure
        double[] vTaskAxis = computeSpeedArray(taskAxisPts);
        double[] vGesture = computeSpeedArray(gesturePts);
        double[] errors = new double[gesturePts.length];
        for (int i = 0; i < gesturePts.length; i++)
            errors[i] = Math.abs(vGesture[i] - vTaskAxis[i]);
        return errors;
    }

    /**
	 * Gets the production time of the gesture articulation.
	 */
    private static double productionTime(Gesture gesture)
    {
        Point[] points = gesture.Points;
        if (points.length <= 1) return 0;
        return points[points.length - 1].T - points[0].T;
    }

    /**
	 * Returns an array with speed values for each point.
	 */
    private static double[] computeSpeedArray(Point[] points)
    {
        int n = points.length;
        double[] v = new double[n];
        int r = 1;
        for (int i = 0; i < n; i++)
        {
            int index1 = Math.max(0, i - r);
            int index2 = Math.min(i + r, n - 1);
            double distance = PointSet.computePathLength(points, index1, index2);
            double time = points[index2].T - points[index1].T;
            if (Math.abs(time) < 1e-5)
                v[i] = 0;
            else v[i] = distance / time;
        }
        return v;
    }

    /************************** 2 articulation relative accuracy measures: SkE, SkOE *****************************/

    /**
	 * Stroke Error (SkE) is defined as the absolute difference in stroke count.
	 */
    public static double strokeError(Gesture gesture, CentroidGesture taskAxis)
    {
        return Math.abs(numberOfStrokes(gesture) - numberOfStrokes(taskAxis));
    }

    /**
	 * Returns the number of strokes of a gesture.
	 */
    private static int numberOfStrokes(Gesture gesture)
    {
        Point[] points = gesture.Points;
        int numStrokes = points.length > 0 ? 1 : 0;
        for (int i = 1; i < points.length; i++)
            if (points[i].ID != points[i - 1].ID)
                numStrokes++;
        return numStrokes;
    }

    /**
	 * Stroke Order Error (SkOE) is defined as the absolute difference between the $1 and $P distance costs.
	 */
    public static double strokeOrderError(Gesture gesture, CentroidGesture taskAxis)
    {
        // align gesture points with the task axis
        Point[] taskAxisPts = taskAxis.getPoints();

        // compute $1 cost measure
        Point[] gesturePts = taskAxis.alignGesture(gesture, PointAlignmentType.Chronological);
        double _1DollarCost = 0;
        for (int i = 0; i < gesturePts.length; i++)
            _1DollarCost += Measurements.euclideanDistance(gesturePts[i], taskAxisPts[i]);

        // compute $P cost measure
        gesturePts = taskAxis.alignGesture(gesture, PointAlignmentType.PointCloudBestMatch);
        double _PDollarCost = 0;
        for (int i = 0; i < gesturePts.length; i++)
            _PDollarCost += Measurements.euclideanDistance(gesturePts[i], taskAxisPts[i]);

        return Math.abs(_1DollarCost - _PDollarCost);
    }


    /***************************Utility functions: average, stdev********************************/

    /**
	 * Computes the average of a set of real values.
	 */
    private static double computeAverage(double[] values)
    {
        double avg = 0;
        for(double v : values)
            avg += v;
        avg /= values.length;
        return avg;
    }

    /**
	 * Computes the standard deviation for a set of real values.
	 */
    private static double computeStdev(double[] values)
    {
        double avg = computeAverage(values);
        double stdev = 0;
        for(double v : values)
            stdev += (v - avg) * (v - avg);
        stdev = Math.sqrt(stdev / (values.length - 1));
        return stdev;
    }
}
