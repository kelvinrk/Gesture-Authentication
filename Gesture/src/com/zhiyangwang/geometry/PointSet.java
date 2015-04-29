package com.zhiyangwang.geometry;

/**
 * Contains utility functions for point sets.
 */
public class PointSet {
	/**
     * Returns the center of gravity for a set of points
     */
    public static Point centroid(Point[] points)
    {
        if (points == null || points.length == 0)
            return new Point();

        Point center = new Point();
        for(Point point : points)
            center = Point.plus(center, point);
		center = Point.divide(center, points.length);
        return center;
    }

    /**
     * Returns a point that has minimum values on the X and Y axes in the point set.
     */
    public static Point min(Point[] points)
    {
        if (points == null || points.length == 0)
            return new Point();

        Point min = Point.absoluteMax();
        for(Point point : points)
        {
            if (min.X > point.X) 
            	min.X = point.X;
            if (min.Y > point.Y) 
            	min.Y = point.Y;
        }
        return min;
    }

    /**
     * Returns a point that has maximum values on the X and Y axes in the point set.
     */
    public static Point max(Point[] points)
    {
    	if (points == null || points.length == 0)
            return new Point();

        Point max = Point.absoluteMin();
        for(Point point : points)
        {
            if (max.X < point.X) 
            	max.X = point.X;
            if (max.Y < point.Y) 
            	max.Y = point.Y;
        }
        return max;
    }

    /**
     * Returns the bounding box of the set of points.
     */
    public static Box boundingBox(Point[] points)
    {
        return new Box(min(points), max(points));
    }

    /**
     * Return path length.
     */
    public static double pathLength(Point[] points)
    {
        return computePathLength(points, 0, points.length - 1);
    }

    /**
     * Computes path length.
     */
    public static double computePathLength(Point[] points, int p, int q)
    {
        if (points == null || points.length == 0 || p >= q)
            return 0;

        double length = 0;
        for (int i = p + 1; i <= q; i++)
            if (points[i].ID == points[i - 1].ID)
                length += Measurements.euclideanDistance(points[i], points[i - 1]);
        return length;
    }

    
    
    /**
     * Scales a set of points with shape preservation.
     */
    public static Point[] scaleTo(Point[] points)
    {
        Point min = PointSet.min(points);
        Point max = PointSet.max(points);
        double scaleFactor = 1.0 / Point.minus(max, min).maxXY();

        // scale points
        Point[] newPoints = new Point[points.length];
        if (!Double.isInfinite(scaleFactor) && Math.abs(scaleFactor) > 1e-10)
        {
            for (int i = 0; i < points.length; i++)
                newPoints[i] = Point.multiply(scaleFactor, Point.minus(points[i], min));
        }
        else
        {
            for (int i = 0; i < points.length; i++)
                newPoints[i] = new Point(points[i]);
        }
        return newPoints;
    }

    /**
     * Translate points by offset.
     * If offset coincides with the center of gravity, the new center will be (0, 0).
     */
    public static Point[] translateOffset(Point[] points, Point offset)
    {
        Point[] newPoints = new Point[points.length];
        for (int i = 0; i < newPoints.length; i++)
            newPoints[i] = Point.minus(points[i], offset);
        return newPoints;
    }

    /**
     * Resample the point set uniformly into a custom number of points n.
     */
    public static Point[] resample(Point[] points, int n)
    {
        if (points.length == 0 || n <= 0)
            return new Point[0];

        Point[] newPoints = new Point[n];
        newPoints[0] = new Point(points[0]);
        if (points.length == 1)
        {
            for (int i = 1; i < newPoints.length; i++)
                newPoints[i] = new Point(points[0]);
            return newPoints;
        }

        int numPoints = 1;
        double I = pathLength(points) / (n - 1); // computes interval length
        double D = 0;
        for (int i = 1; i < points.length; i++)
            if (points[i].ID == points[i - 1].ID){
                double d = Measurements.euclideanDistance(points[i - 1], points[i]);
                if (D + d >= I){
                    Point firstPoint = points[i - 1];
					while (D + d >= I) {
						// add interpolated point
						double t = Math.min(Math.max((I - D) / d, 0.0), 1.0);
						if (Double.isNaN(t))
							t = 0.5;
						newPoints[numPoints++] = new Point((1.0 - t) * firstPoint.X  + t * points[i].X, 
								(1.0 - t) * firstPoint.Y + t * points[i].Y, 
								(1.0 - t) * firstPoint.T + t * points[i].T, 
								points[i].ID);

						// update partial length
						d = D + d - I;
						D = 0;
                        firstPoint = newPoints[numPoints - 1];
                    }
                    D = d;
                }
                else D += d;
            }

        if (numPoints == n - 1) // sometimes we fall a rounding-error short of adding the last point, so add it if so
            newPoints[numPoints++] = new Point(points[points.length - 1]);
        return newPoints;
    }
}
