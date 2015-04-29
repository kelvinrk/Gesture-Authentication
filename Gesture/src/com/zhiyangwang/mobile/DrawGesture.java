package com.zhiyangwang.mobile;

import java.util.ArrayList;
import java.util.List;

import com.zhiyangwang.geometry.Point;
import com.zhiyangwang.gestures.CentroidGesture;
import com.zhiyangwang.gestures.Gesture;
import com.zhiyangwang.gestures.PointAlignmentType;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

public class DrawGesture extends MyDraw {

	private Path mPath;
	private float mX, mY;
	private List<Gesture> mGestures;
	private List<Point> mPoints;
	private Point current;
	private CentroidGesture taskAxis;
	
	private final PointAlignmentType alignmentType = PointAlignmentType.Chronological;
	private final boolean useNearestNeighbor = false;
	private final int resampleRate = 64;
	private double rams[];
	private final float shift = 400;
	private final boolean computeAsPercentage = true;
	
	public DrawGesture(Context context) {
		super(context);
		mPath = new Path();
		mPoints = new ArrayList<Point>();
		mGestures = new ArrayList<Gesture>();
		taskAxis = null;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawPath(mPath, paint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			onTouchDown(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			onTouchMove(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			onTouchUp(x, y);
			invalidate();
			break;
		default:
		}
		return true;
	}

	private void onTouchDown(float x, float y) {
		current = new Point(x, y, Assistance.getTimeStamp(), Assistance.getStokeID());
		mPoints.add(current);
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
	}

	private void onTouchMove(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx > 0 || dy > 0) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		} else if (dx == 0 || dy == 0) {
			mPath.quadTo(mX, mY, (x + 1 + mX) / 2, (y + 1 + mY) / 2);
			mX = x + 1;
			mY = y + 1;
		}
		current = new Point(x, y, Assistance.getTimeStamp(), Assistance.getStokeID());
		mPoints.add(current);
	}

	private void onTouchUp(float x, float y) {
		canvas.drawPath(mPath, paint);
		mPath.reset();
		Assistance.strokeID++;
	}

	/**
	 * Reset all stored value, including task axis
	 */
	public void reset() {
		this.clear();
		mGestures = new ArrayList<Gesture>();
		taskAxis = null;
	}
	
	/**
	 * Clear current drawing on the canvas, also clear memory
	 */
	public void clear() {
		mPoints = new ArrayList<Point>();
		mPath.reset();
		Assistance.resetStokeID();
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		invalidate();
	}
	
	/**
	 * Save current gesture, then clear, prepare for next step
	 * @param context
	 */
	public void save(Context context) {
		if(!strokeCheck(mGestures))
			Toast.makeText(context, "Caution: Stoke number error! This gesture is invalid!", Toast.LENGTH_SHORT).show();
		else{
			mGestures.add(new Gesture(mPoints.toArray(new Point[mPoints.size()]), "", this.resampleRate));
			if(mGestures != null) 
				this.drawGesture(mGestures.get(mGestures.size() - 1));
		}
		this.clear();
		Log.v("Test", "Template array size " + mGestures.size());
	}
	
	/**
	 * Calculate Task Axis or called Average Axis or called Template
	 */
	public void calculate() {
		PointAlignmentType alignmentType = this.alignmentType;
		boolean useNearestNeighbor = this.useNearestNeighbor;
		taskAxis = new CentroidGesture(mGestures.toArray(new Gesture[mGestures.size()]), alignmentType, useNearestNeighbor);
//		Log.v("Test", "taskAxis sampling rate is " + taskAxis.samplingRate());
		if(taskAxis != null)
			this.drawGesture(taskAxis);
	}
	
	/**
	 * Draw template gesture
	 */
	public void drawTemplate() {
		if(taskAxis != null)
			this.drawGesture(taskAxis);
	}
	
	/**
	 * Draw a gesture on canvas
	 * @param gesture
	 */
	private void drawGesture(Gesture gesture) {
		this.clear();
		Point[] points = gesture.Points;
		float x, y;
		int length = gesture.samplingRate();
		mPath.reset();
		x = (float)points[0].X + shift;
		y = (float)points[0].Y + shift;
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
		
		for(int i = 1; i < length; i++) {
			x = (float)points[i].X + shift;
			y = (float)points[i].Y + shift;
			
//			Log.v("Test", x + " " + y + "  " + points[i].ID);
			float dx = Math.abs(x - mX);
			float dy = Math.abs(y - mY);
			
			if (dx > 0 || dy > 0) {
				mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
				mX = x;
				mY = y;
			} else if (dx == 0 || dy == 0) {
				mPath.quadTo(mX, mY, (x + 1 + mX) / 2, (y + 1 + mY) / 2);
				mX = x + 1;
				mY = y + 1;
			}
		}
		canvas.drawPath(mPath, paint);
		invalidate();
		mPath.reset();
	}
	
	/**
	 * Check if the number of Strokes is valid
	 * @param mGestures
	 * @return
	 */
	private boolean strokeCheck(List<Gesture> mGestures) {
		if(mGestures == null || mGestures.size() == 0)
			return true;
		else{
			Gesture lastGesture = mGestures.get(mGestures.size() - 1);
			int lastGestureLastPoint = lastGesture.samplingRate() - 1;
			int lastStrokeNumber = lastGesture.Points[lastGestureLastPoint].ID + 1;
			Log.v("Test", "lastStrokeNumber is " + lastStrokeNumber);
			if(Assistance.getStokeID() != lastStrokeNumber) {
				Log.v("Test", "Current StrokeNumber is " + Assistance.getStokeID());
				return false;
			}
			else
				return true;
		}
	}
	
	/**
	 * RAMs Authentication Process
	 * @param context
	 */
	public void authentication(Context context) {
			Gesture gesture = new Gesture(mPoints.toArray(new Point[mPoints.size()]), "", this.resampleRate);
			Assistance.computeRelativeAccuracyMeasures(gesture, taskAxis, this.computeAsPercentage);
			rams = Assistance.getResult();
			boolean pass = false;
			
			Assistance.calculateThreshold(mGestures, taskAxis, computeAsPercentage);
			
			if(rams != null && rams.length != 0) {
//				Toast.makeText(context, "Rams SUCCESS!", Toast.LENGTH_SHORT).show();
				Assistance.showLog();
				pass = Assistance.authentication(context);
				//Make template adaptive, update the template when authentication successful.
				if(pass) {
					mGestures.add(gesture);
					calculate();
				}
				this.clear();
				Log.v("Test", "Template array size " + mGestures.size());
			}
			else {
				Toast.makeText(context, "Rams FAILED!", Toast.LENGTH_SHORT).show();
			clear();
		}
	}

	public List<Gesture> getmGestures() {
		if(mGestures.size() != 0)
			return mGestures;
		return null;
	}
	
	public int getTemplateSize() {
		if(mGestures != null)
			return mGestures.size();
		return 0;
	}
	
	public void addGestures(Gesture gesture) {
		mGestures.add(gesture);
	}

	public CentroidGesture getTaskAxis() {
		if(taskAxis != null)
			return taskAxis;
		return null;
	}
	
	public int getResampleRate() {
		return resampleRate;
	}
	
	public List<Point> getPoints() {
		if(mPoints.size() != 0)
			return mPoints;
		return null;
	}
	
	public boolean checkTemplateStatus() {
		return taskAxis != null;
	}
}
