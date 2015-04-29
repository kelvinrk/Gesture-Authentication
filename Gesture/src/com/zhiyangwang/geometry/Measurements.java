package com.zhiyangwang.geometry;

public class Measurements {
	public static final double Epsilon = 4.94066e-324;
	public static double sqrEuclideanDistance(Point a, Point b)
    {
        return (a.X - b.X) * (a.X - b.X) + (a.Y - b.Y) * (a.Y - b.Y);
    }

	/**
     * Returns the Euclidean Distance between two points.
     */
    public static double euclideanDistance(Point a, Point b)
    {
        return Math.sqrt(sqrEuclideanDistance(a, b));
    }

    /**
     * Returns the smallest turning angle between vectors v and u in radians of [0..PI].
     */
    public static double shortAngle(Vector v, Vector u)
	{
        // compute cosine between vectors
        double vLength = v.getLength();
        double uLength = u.getLength();
        if (Math.abs(vLength * uLength) <= Epsilon)
            return 0;
        double cos_angle = Vector.dotProduct(v, u) / (vLength * uLength);
        
        // deal with special cases
        if (cos_angle <= -1.0) 
        	return Math.PI;
		if (cos_angle >= 1.0) 
			return 0;

        // return angle in the interval [0, PI]
        return Math.acos(cos_angle);
	}

    /**
     * Returns the trigonometric turning angle between vectors v and u in radians of [0..2PI]
     */
    public static double angle(Vector v, Vector u)
	{
        double angle = shortAngle(v, u);
        if (!Measurements.trigonometricOrder(v, u))
			angle = Math.PI * 2.0 - angle;
		return angle;
	}

    /**
     * Returns true if vectors v and u are in trigonometric order
     */
    public static boolean trigonometricOrder(Vector v, Vector u)
    {
        return (v.getValues().X * u.getValues().Y - v.getValues().Y * u.getValues().X < 0) ? false : true;
    }
}
