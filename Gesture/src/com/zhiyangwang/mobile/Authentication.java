package com.zhiyangwang.mobile;

import java.util.List;

import android.content.Context;

import com.zhiyangwang.geometry.Point;
import com.zhiyangwang.gestures.CentroidGesture;
import com.zhiyangwang.gestures.Gesture;

public class Authentication {
	protected List<Gesture> templates;
	protected CentroidGesture taskAxis;
	protected DrawGesture mDraw;
	protected boolean result;
	protected List<Point> mPoints;
	
	public Authentication(DrawGesture mDraw) {
		this.templates = mDraw.getmGestures();
		this.taskAxis = mDraw.getTaskAxis();
		this.mPoints = mDraw.getPoints();
		this.mDraw = mDraw;
		this.result = false;
	}
	
	public boolean authentication(Context context) {
		return result;
	}
}
