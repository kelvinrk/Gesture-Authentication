package com.zhiyangwang.geometry;

/**
 * Implements a 2-D vector.
 */
public class Vector {
	private Point vector = new Point();

	/**
     * Defines a vector that points from A to B.
     */
	public Vector(Point a, Point b)
	{
		this.vector = Point.minus(b, a);
	}
	
	/**
     * Returns the length of the vector
     */
    public double getLength()
	{
        return Measurements.euclideanDistance(vector, new Point());
	}

    /**
     * Returns vector coordinates as a point
     */
	public Point getValues()
	{
		return this.vector;
	}

	/**
     * Returns the dot product of the v and u vectors
     */
    public static double dotProduct(Vector v, Vector u)
	{
		return v.getValues().X * u.getValues().X + v.getValues().Y * u.getValues().Y;
	}
}
