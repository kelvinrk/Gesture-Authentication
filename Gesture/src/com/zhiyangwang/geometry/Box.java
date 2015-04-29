package com.zhiyangwang.geometry;

/**
 * Implements a rectangle in 2-D.
 */
public class Box {
	public Point TopLeft = new Point();
	public Point BottomRight = new Point();

	/**
     * Constructs a rectangle with top left and bottom right corners.
     */
	public Box(Point topLeft, Point bottomRight) {
		this.TopLeft = new Point(topLeft);
		this.BottomRight = new Point(bottomRight);
	}

	/**
     * Return width of the box
     */
	public double getWidth() {
		return Math.abs(BottomRight.X - TopLeft.X);
	}

	/**
     * Return height of the box
     */
	public double getHeight() {
		return Math.abs(BottomRight.Y - TopLeft.Y);
	}

	/**
     * Return area of the box
     */
	public double getArea() {
		return this.getWidth() * this.getHeight();
	}
}
