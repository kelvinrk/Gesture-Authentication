package com.zhiyangwang.protractor;

import java.util.List;

import android.graphics.PointF;

import com.zhiyangwang.geometry.Box;
import com.zhiyangwang.geometry.Point;
import com.zhiyangwang.geometry.PointSet;
import com.zhiyangwang.gestures.Gesture;

public class BasicUnistrokeRecognizer
{
    protected List<Gesture> _knonwGestures;
    protected Gesture _gestureToRecognize;

    protected int _sizeOfResample = 64;
    protected double _angle = 45;
    protected double _anglePrecision = 2;
    protected double _goldenRation = 0.5 * (-1 + Math.sqrt(5.0));

    public BasicUnistrokeRecognizer()
    {

    }
    public BasicUnistrokeRecognizer(List<Point> pointsToRecognize, List<Gesture> knownGestures)
    {
//        _gestureToRecognize = new Gestures() { Points = pointsToRecognize };
        _gestureToRecognize = new Gesture(pointsToRecognize.toArray(new Point[pointsToRecognize.size()]), "", this._sizeOfResample);
        _knonwGestures = knownGestures;

        start();
    }

    /// <summary>
    /// Starting algorithm
    /// </summary>
    protected void start()
    {
    	Point[] resampledPoints = ResamplePoints(_gestureToRecognize.OriginalPoints, _sizeOfResample);
        Point centroidPoint = MathHelper.CalculateCentroid(resampledPoints);
//        double theta = GeotrigEx.Angle(new PointF((float)centroidPoint.X, (float)centroidPoint.Y),
//                                    new PointF((float)resampledPoints[0].X,(float)resampledPoints[0].Y), false); 
        double theta = MathHelper.Angle(centroidPoint, resampledPoints[0], false);

        Point[] rotatedPoints = RotateBy(resampledPoints, -theta);
        //DrawPoints(rotatedPoints);
        Point[] scaledPoints = ScaleTo(rotatedPoints, 250);
        Point[] translatedPoints = TranslateTo(scaledPoints, new Point(0, 0, 0, 0));
        //DrawPoints(scaledPoints);
        double finalScore = RecognizeT(translatedPoints, 250);
//        _gestureToRecognize.Name = "Score :: " + finalScore;
    }

    /// <summary>
    /// Resample point to N - sampleSize
    /// </summary>
    /// <param name="points"></param>
    /// <param name="sampleSize"></param>
    /// <returns></returns>
    protected Point[] ResamplePoints(Point[] points, int sampleSize)
    {
        return PointSet.resample(points, sampleSize);
    }
    
    /// <summary>
    /// Find and save the indicative angle  ω from the  points’ 
    /// centroid to first point. Then rotate by –ω to set this angle to 0°
    /// </summary>
    /// <param name="pointsAfterStep_One"></param>
    /// <returns></returns>
    protected double RotateToZero(Point[] pointsAfterStep_One)
    {
        Point centroidPoint = MathHelper.CalculateCentroid(pointsAfterStep_One);
        double theta = Math.atan2(centroidPoint.Y - pointsAfterStep_One[0].Y, centroidPoint.X - pointsAfterStep_One[0].X);
        return theta;
    }
    /// <summary>
    /// Rotate Points accroding to Theta i centroid point
    /// </summary>
    /// <param name="pointsAfterStep_One"></param>
    /// <param name="theta"></param>
    /// <returns></returns>
    protected Point[] RotateBy(Point[] pointsAfterStep_One, double theta)
    {
        // optimaze
    	Point centroid = MathHelper.CalculateCentroid(pointsAfterStep_One);
        int pointsCount = pointsAfterStep_One.length;
        double cos = Math.cos(theta);
        double sin = Math.sin(theta);

        Point[] newPoints = new Point[pointsCount];
        Point newPoint = null;
        for (int i = 0; i < pointsCount; i++)
        {
            newPoint = new Point();
            newPoint.X = (pointsAfterStep_One[i].X - centroid.X) * cos - (pointsAfterStep_One[i].Y - centroid.Y) * sin + centroid.X;
            newPoint.Y = (pointsAfterStep_One[i].X - centroid.X) * sin + (pointsAfterStep_One[i].Y - centroid.Y) * cos + centroid.Y;
            newPoints[i] = newPoint;
        }
        return newPoints;
    }
    protected Point[] ScaleTo(Point[] rotatedPoints, int boundingSize)
    {
        Box boundingBox = MathHelper.CalculateBoundingBox(rotatedPoints);
        int pointsCount = rotatedPoints.length;
        Point[] newPoints = new Point[pointsCount];
        Point newPoint = null;
        
        for (int i = 0; i < pointsCount; i++)
        {
            newPoint = new Point();
            newPoint.X = rotatedPoints[i].X * boundingSize / boundingBox.getWidth();
            newPoint.Y = rotatedPoints[i].Y * boundingSize / boundingBox.getHeight();
            newPoints[i] = newPoint;
        }
        return newPoints;
    }
    /// <summary>
    /// Translate  points to the origin [Points (0,0)]
    /// </summary>
    /// <param name="scaledPoints"></param>
    /// <param name="originPoint"></param>
    /// <returns></returns>
    protected Point[] TranslateTo(Point[] scaledPoints, Point originPoint)
    {
        Point centroidPoint = MathHelper.CalculateCentroid(scaledPoints);
        int pointsCount = scaledPoints.length;
        Point[] newPoints = new Point[pointsCount];
        Point newPoint = null;

        for (int i = 0; i < pointsCount; i++)
        {
            newPoint = new Point();
            newPoint.X = scaledPoints[i].X + originPoint.X - centroidPoint.X;
            newPoint.Y = scaledPoints[i].Y + originPoint.Y - centroidPoint.Y;
            newPoints[i] = newPoint;
        }
        return newPoints;
    }
    /// <summary>
    /// f RECOGNIZE refers to the size passed to SCALE-TO in 
    ///    Step 3. The symbol  ϕ  equals  ½(-1  +  √5). We use  θ=±45° and 
    ///    θ∆=2° on line 3 of RECOGNIZE. Due to using RESAMPLE, we can 
    ///    assume that A and B in PATH-DISTANCE contain the same number 
    ///    of points, i.e., |A|=|B|.
    /// </summary>
    /// <param name="translatedPoints"></param>
    /// <param name="boundingSize"></param>
    /// <returns></returns>
    protected double RecognizeT(Point[] translatedPoints, double boundingSize)
    {
        double accB = Double.MAX_VALUE;
        int loopCounter = 0;
        int index = 0;
        // Stored Geture
        for(Gesture gesture : _knonwGestures)
        {
            double distance = DistanceAtBestAngle(translatedPoints,  gesture.Points, -_angle, _angle, _anglePrecision);

            if (distance < accB)
            {
                accB = distance;
                index = loopCounter;
            }
            loopCounter++;
        }
        double score = 1 - (accB / Math.sqrt(boundingSize * boundingSize + boundingSize * boundingSize));
        return score;
    }

    private double DistanceAtBestAngle(Point[] translatedPoints, Point[] gesturePoints, double minAngle, double maxAngle, double anglePrecision)
    {
        double x1 = _goldenRation * minAngle + (1 - _goldenRation) * maxAngle;
        double f1 = DistanceAtAngle(translatedPoints, gesturePoints, x1);
        double x2 = (1 - _goldenRation) * minAngle + _goldenRation * maxAngle;
        double f2 = DistanceAtAngle(translatedPoints, gesturePoints, x2);

        do
        {
            if (f1 < f2)
            {
                maxAngle = x2;
                x2 = x1;
                f2 = f1;
                x1 = _goldenRation * minAngle + (1 - _goldenRation) * maxAngle;
                // translatedPoints = RotateBy(translatedPoints, x1);
                double addModf = 1;
                f1 = DistanceAtAngle(translatedPoints, gesturePoints, x1) * addModf;
            }
            else
            {
                minAngle = x1;
                x1 = x2;
                f1 = f2;
                x2 = (1 - _goldenRation) * minAngle + _goldenRation * maxAngle;
                //translatedPoints = RotateBy(translatedPoints, x1);
                double addModf = 1;
                f2 = DistanceAtAngle(translatedPoints, gesturePoints, x2) * addModf;
            }

        } while (maxAngle - minAngle > anglePrecision);

        return Math.min(f1, f2);
    }
    private double DistanceAtAngle(Point[] translatedPoints, Point[] gesturePoints, double x1)
    {
//        var newPoints = new List<Points>();
//        var rotatedPoints = RotateBy(translatedPoints, x1);
        double pathDistance = CalculatePathDistance(translatedPoints, gesturePoints);
        return pathDistance;
    }
    private double CalculatePathDistance(Point[] translatedPoints, Point[] gesturePoints)
    {
        double d = 0;
        // assume newPoints.Count <= gesturePoints.Count
        for (int i = 0; i < translatedPoints.length; i++)
        {
            if (i < gesturePoints.length)
            {
                d += MathHelper.CalculatePointsDistance(translatedPoints[i], gesturePoints[i]);
            }
        }
        return d / translatedPoints.length;
    }



    private void DrawPoints(Point[] points)
    {
//        var testingForm = new AlgorithmsStepForm();
//        testingForm.Show();
//        testingForm.DrawPoints(points);

    }
    public Gesture Result()
    {
        return _gestureToRecognize;
    }
    
    public Point[] TransformInputGestures(Point[] points)
    {
    	Point[] resampledPoints = ResamplePoints(points, _sizeOfResample);
    	Point centroidPoint = MathHelper.CalculateCentroid(resampledPoints);
//        double theta = MathHelper.Angle(new PointF((float)centroidPoint.X, (float)centroidPoint.Y),
//                                    new PointF((float)resampledPoints[0].X, (float)resampledPoints[0].Y), false);
    	double theta = MathHelper.Angle(centroidPoint, resampledPoints[0], false);

        Point[] rotatedPoints = RotateBy(resampledPoints, -theta);
        //DrawPoints(rotatedPoints);
        Point[] scaledPoints = ScaleTo(rotatedPoints, 250);
        Point[] translatedPoints = TranslateTo(scaledPoints, new Point(0, 0, 0, 0));

        return translatedPoints;
    }
}