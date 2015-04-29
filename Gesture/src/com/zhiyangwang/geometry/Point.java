package com.zhiyangwang.geometry;

/**
 * Implements a point with (X, Y) coordinates, timestamp (T), and stroke identifier (StrokeId).
 */
public class Point {
	public double X, Y;         // 2-D coordinates
    public double T;            // time
    public int ID;        // the ID of the stroke (e.g., 0, 1, 2, ...) 
    
    public Point()
	{	
		this.createPoint(0, 0, 0, 0);
	}
    
    /**
     * Creates a point with (x, y, t, strokeID) coordinates
     */
    public Point(double x, double y, double t, int strokeID)
    {
        this.createPoint(x, y, t, strokeID);
    }

    /**
     * Creates a point with the argument point
     */
	public Point(Point p)
	{
		this.createPoint(p.X, p.Y, p.T, p.ID);
	}
	
	/**
     * Implements a private instance constructor
     */
    private void createPoint(double x, double y, double t, int strokeID)
	{
		X = x;
		Y = y;
		T = t;
        ID = strokeID;
	}

    /**
     * Returns p1 + p2 on each coordinate and copies the timestamp T and StrokeID of p1
     */
	public static Point plus(Point p1, Point p2)
	{
		return new Point(p1.X + p2.X, p1.Y + p2.Y, p1.T, p1.ID);
	}

	/**
     * Returns p1 - p2 on each coordinate and copies the timestamp T and StrokeID of p1
     */
	public static Point minus(Point p1, Point p2)
	{
		return new Point(p1.X - p2.X, p1.Y - p2.Y, p1.T, p1.ID);
	}

	/**
     * Returns p / scalar on each coordinate.
     */
    public static Point divide(Point p, double scalar)
	{
		return new Point(p.X / scalar, p.Y / scalar, p.T, p.ID);
	}

    /**
     * Returns scalar * p on each coordinate
     */
    public static Point multiply(double scalar, Point p)
	{
		return new Point(scalar * p.X, scalar * p.Y,p.T, p.ID);
	}
    

    /**
     * Returns the point with minimum coordinates on X and Y axes
     */
    public static Point absoluteMin() {
    	return new Point(Double.MIN_VALUE, Double.MIN_VALUE, 0, 0);
    }

    /**
     * Returns the point with maximum coordinates on X and Y axes
     */
    public static Point absoluteMax() {
    	return new Point(Double.MAX_VALUE, Double.MAX_VALUE, 0, 0);
    }
  
    /**
     *  Returns the max value of the X and Y coordinates of this point
     */
    public double maxXY() {
    	return Math.max(X, Y);
    }
}
