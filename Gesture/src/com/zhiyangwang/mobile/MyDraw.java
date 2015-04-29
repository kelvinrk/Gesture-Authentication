package com.zhiyangwang.mobile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

public class MyDraw extends View {

	public Point downPoint, movePoint, upPoint;
	public Paint paint;
	public Canvas canvas;
	public Bitmap bitmap;
	public int downState;
	public int moveState;

	public MyDraw(Context context) {
		super(context);

		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		int width = displayMetrics.widthPixels;
		int height = displayMetrics.heightPixels;

		paint = new Paint(Paint.DITHER_FLAG);//Create a paint
		bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888); // Setup Bitmap
		canvas = new Canvas(bitmap);
		
		// Configure Paint
		paint.setStyle(Style.STROKE);// Stroke Type
		paint.setStrokeWidth(10);// Paint Width is 5 pixels
		paint.setColor(Color.RED);// Red Color
		paint.setAntiAlias(true);

		downPoint = new Point();
		movePoint = new Point();
		upPoint = new Point();

		Log.i("MyDraw", "bitmap::::::::::::::::::" + bitmap);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(bitmap, 0, 0, null);
	}

}
