package com.zhiyangwang.protractor;

import java.util.ArrayList;
import java.util.List;

import android.graphics.PointF;

import com.zhiyangwang.geometry.Box;
import com.zhiyangwang.geometry.Point;
import com.zhiyangwang.geometry.PointSet;

public class MathHelper
{
    public static double CalculatePathLength(Point[] points)
    {
        return PointSet.pathLength(points);
    }
    public static double CalculatePointsDistance(Point p1, Point p2)
    {
        double powX = (p1.X - p2.X) * (p1.X - p2.X);
        double powY = (p1.Y - p2.Y) * (p1.Y - p2.Y);

        return Math.sqrt(powX + powY);
    }
    public static double DegreeToRadian(double angle)
    {
        return Math.PI * angle / 180.0;
    }
    
    public static Point CalculateCentroid(Point[] points)
    {
        return PointSet.centroid(points);
    }
    public static Point CalculateCentroidBody(Point[] points)
    {
        double x = 0;
        double y = 0;
        int c = 0;
        for (int i = 0; i < points.length; i++)
        {
                x += points[i].X;
                y += points[i].Y;
                c++;
        }
        return new Point(x / c, y / c, 0.0, 0);
    }
    public static Box CalculateBoundingBox(Point[] points)
    {
    	return PointSet.boundingBox(points);
    }
    
    public static List<Point> GetPointsFromRectangle(Box rect)
    {
    	List<Point> rectPoints = new ArrayList<Point>();

        for (int j = 0; j < rect.getWidth(); j++)
        {
            rectPoints.add(new Point(rect.TopLeft.X + j, rect.TopLeft.Y, 0, 0));
            rectPoints.add(new Point(rect.TopLeft.X + j, rect.TopLeft.Y + rect.getHeight(), 0, 0));
        }

        for (int j = 0; j < rect.getHeight(); j++)
        {
            rectPoints.add(new Point(rect.TopLeft.X, rect.TopLeft.Y + j, 0, 0));
            rectPoints.add(new Point(rect.TopLeft.X + rect.getWidth(), rect.TopLeft.Y + j, 0, 0));
        }
        return rectPoints;
    }
    
    public static double Angle(Point start, Point end, boolean positiveOnly)
    {
        double radians = 0.0;
        if (start.X != end.X)
        {
            radians = Math.atan2(end.Y - start.Y, end.X - start.X);
        }
        else // pure vertical movement
        {
            if (end.Y < start.Y)
                radians = -Math.PI / 2.0; // -90 degrees is straight up
            else if (end.Y > start.Y)
                radians = +Math.PI / 2.0; // 90 degrees is straight down
        }
        if (positiveOnly && radians < 0.0)
        {
            radians += Math.PI * 2.0;
        }
        return radians;
    }
}
