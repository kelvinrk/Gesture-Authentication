package com.zhiyangwang.protractor;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.Toast;

import com.zhiyangwang.geometry.Point;
import com.zhiyangwang.gestures.Gesture;
import com.zhiyangwang.rams.RelativeAccuracyMeasures;

public class PotractorRecognizer extends BasicUnistrokeRecognizer {
	private String info;
	private double bestScore;

     public PotractorRecognizer()
     {

     }

     public PotractorRecognizer(List<Point> pointsToRecognize, List<Gesture> knownGestures)
     {
         _gestureToRecognize = new Gesture(pointsToRecognize.toArray(new Point[pointsToRecognize.size()]), "", this._sizeOfResample);
         _knonwGestures = knownGestures;
         bestScore = 0.0;

//         start();
     }
     
//     public void process(Context context) {
//    	 start();
//    	 if(info != null)
//    		 Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
//     }

     @Override
     public void start()
     {
         // Step 1
         //Protractor only needs n = 16 points to perform optimally
         _sizeOfResample = 16;
         Point[] resampledPoints = TransformInputGestures(_gestureToRecognize.Points); 

         //Step2
         // Generate a vector representation for the gesture. The procedure takes two
         // parameters: points are resampled points from Step 1, and oSensitive specifies whether the 
         // gesture should be treated orientation sensitive or invariant. The procedure first translates 
         // the gesture so that its centroid is the origin, and then rotates the gesture to align its 
         // indicative angle with a base orientation. VECTORIZE returns a normalized vector with a 
         // length of 2n.
         List<Double> normalizedVector = VectorizeF(resampledPoints);

         // Step3
         //Match the vector of an unknown gesture against a set of templates. OPTIMALCOSINE-DISTANCE
         //provides a closed-form solution to find the minimum cosine distance 
         //between the vectors of a template and the unknown gesture by only rotating the template
         //once.
         bestScore =  RecognizeT(normalizedVector, _knonwGestures);

//         _gestureToRecognize.Name = "Name :: " + result[1] + "  Score :: " + result[0];
//         _gestureToRecognize.Name = "Score :: " + result[0];
//         info = _gestureToRecognize.Name;
//         return bestScore;
     }
     
     public double getBestScore() {
    	 return bestScore;
     }

     private List<Double> Vectorize(Point[] resampledPoints, boolean oSensitive )
     {
         Point centroid = MathHelper.CalculateCentroid(resampledPoints);
         Point[] translatedPoints = TranslateTo(resampledPoints, centroid);
         double indicativeAngle = Math.atan2(translatedPoints[0].Y, translatedPoints[0].X);

         double delta = 0;
         if (oSensitive)
         {
            // var baseOrientation = (Math.PI / 4) * Math.Floor((indicativeAngle + Math.PI / 8) / (Math.PI / 4));
            // delta = baseOrientation - indicativeAngle;
             delta = indicativeAngle;
         }
         else
         {
             delta = indicativeAngle;
         }

         double sum = 0;
         List<Double> vector = new ArrayList<Double>();
         for (int i = 0; i < translatedPoints.length; i++)
         {
             //var newPoint = new Points();
             vector.add( translatedPoints[i].X * Math.cos(delta) - translatedPoints[i].Y * Math.sin(delta));
             vector.add( translatedPoints[i].Y * Math.cos(delta) + translatedPoints[i].X * Math.sin(delta));
            // vector.Add(newPoint);
             sum = sum + translatedPoints[i].X * translatedPoints[i].X + translatedPoints[i].Y * Math.cos(delta) * translatedPoints[i].Y * Math.cos(delta);  
         }

         double magnitude = Math.sqrt(sum);
         for (int i = 0; i < vector.size(); i++ )
         {
//             vector[i] = vector[i] / magnitude;
             vector.set(i, vector.get(i) / magnitude);
         }
         return vector;
     }

     public static List<Double> VectorizeF(Point[] points)
     {
         double sum = 0.0;
         List<Double> vector = new ArrayList<Double>();
         for (int i = 0; i < points.length; i++)
         {
             vector.add(points[i].X);
             vector.add(points[i].Y);
             sum += points[i].X * points[i].X + points[i].Y * points[i].Y;
         }
         double magnitude = Math.sqrt(sum);
         for (int i = 0; i < vector.size(); i++)
         {
        	 vector.set(i, vector.get(i) / magnitude);
         }
         return vector;
     }

     private double RecognizeT(List<Double> normalizedVector, List<Gesture> _knownGestures)
     {
         double best = 0;
//         String gestureName = null;

         for(Gesture each : _knownGestures)
         {
        	 List<Double> knownGestureVector = VectorizeF(TransformInputGestures(each.Points));
             double distance = OptimalCosineDistance(knownGestureVector, normalizedVector);
             double score = 1 / distance;

             if (score > best)
             {
            	 best = score;
//                 gestureName = each.Name;
             }
         }
//         String[] res = new String[2];
//         res[0] = Double.toString(bestScore);
//         res[1] = gestureName;
//         return new String[]{Double.toString(bestScore), gestureName};
         return best;
     }

     private double OptimalCosineDistance(List<Double> knownGestureVector, List<Double> normalizedVector)
     {
         double a = 0;
         double b = 0;

         for (int i = 0; i < Math.min(knownGestureVector.size(), normalizedVector.size()); i = i + 2)
         {
             a += knownGestureVector.get(i) * normalizedVector.get(i) + knownGestureVector.get(i + 1) * normalizedVector.get(i + 1);
             b += knownGestureVector.get(i) * normalizedVector.get(i + 1) - knownGestureVector.get(i + 1) * normalizedVector.get(i);
         }
         double angle = Math.atan(b / a);
         double distance = Math.acos( a * Math.cos(angle) + b * Math.sin(angle));

         return distance;
     }
}
