package com.zhiyangwang.mobile;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.zhiyangwang.geometry.Point;
import com.zhiyangwang.gestures.Gesture;


public class RAMsAuthentication extends Authentication{
	private double rams[];
	private final boolean computeAsPercentage = true;
	private int resampleRate;
	
	public RAMsAuthentication(DrawGesture mDraw) {
		super(mDraw);
		resampleRate = mDraw.getResampleRate();
	}
	
	@Override
	public boolean authentication(Context context) {
		Gesture gesture = new Gesture(mPoints.toArray(new Point[mPoints.size()]), "", this.resampleRate);
		Assistance.computeRelativeAccuracyMeasures(gesture, taskAxis, this.computeAsPercentage);
		rams = Assistance.getResult();
//		boolean pass = false;

		Assistance.calculateThreshold(templates, taskAxis, computeAsPercentage);

		if (rams != null && rams.length != 0) {
			Assistance.showLog();
			result = Assistance.authentication(context);
			// Make template adaptive, update the template when authentication successful.
			if (result) {
				mDraw.addGestures(gesture);
				mDraw.calculate();
			}
			
			Log.v("Test", "Template array size " + templates.size());
		} else {
			Toast.makeText(context, "Rams FAILED!", Toast.LENGTH_SHORT).show();
		}
		mDraw.clear();
		return result;
	}
}
