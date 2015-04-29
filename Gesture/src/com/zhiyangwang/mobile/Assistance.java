package com.zhiyangwang.mobile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.zhiyangwang.gestures.CentroidGesture;
import com.zhiyangwang.gestures.Gesture;
import com.zhiyangwang.rams.RelativeAccuracyMeasures;

public class Assistance {

	public static int strokeID = 0;
	private static double resultData[];
	
	public static double shapeError;
	public static double shapeVariability;
	public static double lengthError;
	public static double sizeError;
	public static double bendingError;
	public static double bendingVariability;
	public static double timeError;
	public static double timeVariability;
	public static double velocityError;
	public static double velocityVariability;
	public static double strokeError;
	public static double strokeOrderError;
	
	public static double thresholds[];
	private static double thresFactor = 1.5;
	private static final double timeFactor = 0.5;
	private static final double orderFactor = 1.5;
	
	public Assistance() {
		
	}
	
	public static double getTimeStamp() {
		//Keep last 7 digits
		double timeStamp = (double)(System.currentTimeMillis() % 100000000);
//		Log.v("TimeStamp", "timeStamp " + timeStamp);
		return timeStamp;
	}
	
	public static int getStokeID() {
		return strokeID;
	}
	
	public static void resetStokeID() {
		strokeID = 0;
	}
	
	public static void computeRelativeAccuracyMeasures(Gesture gesture, CentroidGesture taskAxis, boolean computeAsPercentage) {
		resultData = new double[12];
		
//		resultData[0] = round(RelativeAccuracyMeasures.shapeError(gesture, taskAxis), 3);
//		resultData[1] = round(RelativeAccuracyMeasures.shapeVariability(gesture, taskAxis), 3);
//		resultData[2] = round(RelativeAccuracyMeasures.lengthError(gesture, taskAxis, computeAsPercentage), 3);
//		resultData[3] = round(RelativeAccuracyMeasures.sizeError(gesture, taskAxis, computeAsPercentage), 3);
//		resultData[4] = round(RelativeAccuracyMeasures.bendingError(gesture, taskAxis), 3);
//		resultData[5] = round(RelativeAccuracyMeasures.bendingVariability(gesture, taskAxis), 3);
//		resultData[6] = round(RelativeAccuracyMeasures.timeError(gesture, taskAxis, computeAsPercentage), 3);
//		resultData[7] = round(RelativeAccuracyMeasures.timeVariability(gesture, taskAxis), 3);
//		resultData[8] = round(RelativeAccuracyMeasures.velocityError(gesture, taskAxis), 3);
//		resultData[9] = round(RelativeAccuracyMeasures.velocityVariability(gesture, taskAxis), 3);
//		resultData[10] = RelativeAccuracyMeasures.strokeError(gesture, taskAxis);
//		resultData[11] = round(RelativeAccuracyMeasures.strokeOrderError(gesture, taskAxis), 3);
		
		resultData[0] = RelativeAccuracyMeasures.shapeError(gesture, taskAxis);
		resultData[1] = RelativeAccuracyMeasures.shapeVariability(gesture, taskAxis);
		resultData[2] = RelativeAccuracyMeasures.lengthError(gesture, taskAxis, computeAsPercentage);
		resultData[3] = RelativeAccuracyMeasures.sizeError(gesture, taskAxis, computeAsPercentage);
		resultData[4] = RelativeAccuracyMeasures.bendingError(gesture, taskAxis);
		resultData[5] = RelativeAccuracyMeasures.bendingVariability(gesture, taskAxis);
		resultData[6] = RelativeAccuracyMeasures.timeError(gesture, taskAxis, computeAsPercentage);
		resultData[7] = RelativeAccuracyMeasures.timeVariability(gesture, taskAxis);
		resultData[8] = RelativeAccuracyMeasures.velocityError(gesture, taskAxis);
		resultData[9] = RelativeAccuracyMeasures.velocityVariability(gesture, taskAxis);
		resultData[10] = RelativeAccuracyMeasures.strokeError(gesture, taskAxis);
		resultData[11] = RelativeAccuracyMeasures.strokeOrderError(gesture, taskAxis);
		
		shapeError = resultData[0];
		shapeVariability = resultData[1];
		lengthError = resultData[2];
		sizeError = resultData[3];
		bendingError = resultData[4];
		bendingVariability = resultData[5];
		timeError = resultData[6];
		timeVariability = resultData[7];
		velocityError = resultData[8];
		velocityVariability = resultData[9];
		strokeError = resultData[10];
		strokeOrderError = resultData[11];
	}
	
	private static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	public static double[] getResult() {
		if(resultData != null || resultData.length != 0)
			return resultData;
		return null;
	}
	
	public static void showLog() {
		Log.v("Test", "shapeError " + Assistance.shapeError);
		Log.v("Test", "shapeVariability " + Assistance.shapeVariability);
		Log.v("Test", "lengthError " + Assistance.lengthError + " Percentage");
		Log.v("Test", "sizeError " + Assistance.sizeError + " Percentage");
		Log.v("Test", "bendingError " + Assistance.bendingError);
		Log.v("Test", "bendingVariability " + Assistance.bendingVariability);
		Log.v("Test", "timeError " + Assistance.timeError + " Percentage");
		Log.v("Test", "timeVariability " + Assistance.timeVariability);
		Log.v("Test", "velocityError " + Assistance.velocityError);
		Log.v("Test", "velocityVariability " + Assistance.velocityVariability);
		Log.v("Test", "strokeError " + Assistance.strokeError);
		Log.v("Test", "strokeOrderError " + Assistance.strokeOrderError);
	}
	
	public static void calculateThreshold(List<Gesture> mGestures, CentroidGesture taskAxis, boolean computeAsPercentage) {
		thresholds = new double[12];
		for(Gesture each : mGestures) {
			thresholds[0] += RelativeAccuracyMeasures.shapeError(each, taskAxis);
			thresholds[1] += RelativeAccuracyMeasures.shapeVariability(each, taskAxis);
			thresholds[2] += RelativeAccuracyMeasures.lengthError(each, taskAxis, computeAsPercentage);
			thresholds[3] += RelativeAccuracyMeasures.sizeError(each, taskAxis, computeAsPercentage);
			thresholds[4] += RelativeAccuracyMeasures.bendingError(each, taskAxis);
			thresholds[5] += RelativeAccuracyMeasures.bendingVariability(each, taskAxis);
			thresholds[6] += RelativeAccuracyMeasures.timeError(each, taskAxis, computeAsPercentage);
			thresholds[7] += RelativeAccuracyMeasures.timeVariability(each, taskAxis);
			thresholds[8] += RelativeAccuracyMeasures.velocityError(each, taskAxis);
			thresholds[9] += RelativeAccuracyMeasures.velocityVariability(each, taskAxis);
//			thresholds[10] += RelativeAccuracyMeasures.strokeError(each, taskAxis);
			thresholds[11] += RelativeAccuracyMeasures.strokeOrderError(each, taskAxis);
		}
		
		for(int i = 0; i < thresholds.length; i++)
			thresholds[i] /= mGestures.size();
	}
	
	public static boolean authentication(Context context) {
		if(Assistance.strokeError != 0) {
			Toast.makeText(context, "Authentication FAILED! Geature stroke number Error!", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if (Math.abs(Assistance.shapeError - Assistance.thresholds[0]) > thresFactor * Assistance.thresholds[1]) {
			Toast.makeText(context, "Authentication FAILED! Geature Shape Error!", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if (Assistance.lengthError > thresFactor * Assistance.thresholds[2]) {
			Toast.makeText(context, "Authentication FAILED! Geature Length Error!", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if (Assistance.sizeError > thresFactor * Assistance.thresholds[3]) {
			Toast.makeText(context, "Authentication FAILED! Geature Size Error!", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if (Math.abs(Assistance.bendingError - Assistance.thresholds[4]) > thresFactor * Assistance.thresholds[5]) {
			Toast.makeText(context, "Authentication FAILED! Geature Bending Error!", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if (Math.abs(Assistance.timeError - Assistance.thresholds[6]) > timeFactor * thresFactor * Assistance.thresholds[7]) {
			Toast.makeText(context, "Authentication FAILED! Geature Time Error!", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if (Math.abs(Assistance.velocityError - Assistance.thresholds[8]) > thresFactor * Assistance.thresholds[9]) {
			Toast.makeText(context, "Authentication FAILED! Geature Velocity Error!", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if (Assistance.strokeOrderError > orderFactor * thresFactor * Assistance.thresholds[11]) {
			Toast.makeText(context, "Authentication FAILED! Geature Stroke Order Error!", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			Toast.makeText(context, "Authentication SUCCESSFUL!!!", Toast.LENGTH_SHORT).show();
			return true;
		}
	}
	
	public static void setThresFactor(double index) {
		thresFactor = -0.1 * index + 2.5;
	}
	
	public static double GetThresFactor() {
		return thresFactor;
	}
}
