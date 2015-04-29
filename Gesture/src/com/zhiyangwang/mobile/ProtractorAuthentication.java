package com.zhiyangwang.mobile;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.Toast;

import com.zhiyangwang.geometry.Point;
import com.zhiyangwang.gestures.Gesture;
import com.zhiyangwang.protractor.PotractorRecognizer;

public class ProtractorAuthentication extends Authentication{

	private int resampleRate;
	private PotractorRecognizer protractorAuthen;
	private List<Gesture> template;
	private double bestScore;
	private static double threshold = 10;
	
	public ProtractorAuthentication(DrawGesture mDraw) {
		super(mDraw);
		resampleRate = mDraw.getResampleRate();
		template = new ArrayList<Gesture>();
		template.add(taskAxis);
		bestScore = 0.0;
//		potractorAuthen = new PotractorRecognizer(mPoints, templates);
		
		//Only one template, which is the template from rams
		protractorAuthen = new PotractorRecognizer(mPoints, template);
	}
	@Override
	public boolean authentication(Context context) {
		protractorAuthen.start();
		bestScore = protractorAuthen.getBestScore();
//		Toast.makeText(context, "Score :: " + bestScore, Toast.LENGTH_SHORT).show();
		if(bestScore >= threshold) {
			Toast.makeText(context, "Authentication SUCCESS!", Toast.LENGTH_SHORT).show();
			//Update Template
			Gesture gesture = new Gesture(mPoints.toArray(new Point[mPoints.size()]), "", this.resampleRate);
			mDraw.addGestures(gesture);
			mDraw.calculate();
		} else {
			Toast.makeText(context, "Authentication FAILED!", Toast.LENGTH_SHORT).show();
		}
		mDraw.clear();
		return result;
	}
	
	public static void setThreshold(double index) {
		threshold = index + 5.0;
	}
	
	public static double getThreshold() {
		return threshold;
	}
}
