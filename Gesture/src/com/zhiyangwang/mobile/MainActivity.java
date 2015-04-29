package com.zhiyangwang.mobile;

import com.example.gesture.R;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private DrawGesture gesture;
	private Button mResetButton;
	private Button mSaveButton;
	private Button mCalculateButton;
	private Button mShowTemplateButton;
	private Button mClearButton;
	private Button mAuthenticationButton;
	private SeekBar mAuthStrictSeekBar;
	
	private Display mDisplay;
	private Point mSize;
	private LayoutParams mLayout;
	private final int mBottomSpace = 350;
	private int mWidth;
	private int mHeight;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		gesture = new DrawGesture(getApplicationContext());
		
		setContentView(R.layout.activity_main);
		mResetButton = (Button) findViewById(R.id.reset_button);
		mSaveButton = (Button) findViewById(R.id.save_button);
		mCalculateButton = (Button) findViewById(R.id.calculate_button);
		mShowTemplateButton = (Button) findViewById(R.id.show_button);
		mClearButton = (Button) findViewById(R.id.clear_button);
		mAuthenticationButton = (Button) findViewById(R.id.authenticate_button);
		
		if(gesture.getTemplateSize() < 3) {
			mCalculateButton.setEnabled(false);
			mShowTemplateButton.setEnabled(false);
			mAuthenticationButton.setEnabled(false);
		}
		
		mAuthStrictSeekBar = (SeekBar) findViewById(R.id.authStrictSeekBar);
		
		mAuthStrictSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			double progress = 0;

			public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser){
				progress = progressValue;
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
//				Assistance.setThresFactor(progress);
//				Toast.makeText(MainActivity.this,"Anthentication Threshold factor is : " + Assistance.GetThresFactor(), 
//						Toast.LENGTH_SHORT).show();
				
				//For Protractor Authentication
				ProtractorAuthentication.setThreshold(progress);
				Toast.makeText(MainActivity.this,"Anthentication Stringency factor is : " + ProtractorAuthentication.getThreshold(), 
						Toast.LENGTH_SHORT).show();
			}
		});
		
		mResetButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gesture.reset();
			}
		});
		
		mSaveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gesture.save(MainActivity.this);
				if(gesture.getTemplateSize() >= 3) {
					if(!mCalculateButton.isEnabled())
						mCalculateButton.setEnabled(true);
					if(!mShowTemplateButton.isEnabled())
						mShowTemplateButton.setEnabled(true);
					if(!mAuthenticationButton.isEnabled())
						mAuthenticationButton.setEnabled(true);
				}
//				gesture.save();
			}
		});
		
		mCalculateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gesture.calculate();
			}
		});
		
		mShowTemplateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gesture.drawTemplate();
			}
		});
		
		mClearButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gesture.clear();
			}
		});
		
		mAuthenticationButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				gesture.authentication(MainActivity.this);
				
//				RAMsAuthentication auth = new RAMsAuthentication(gesture);
//				auth.authentication(MainActivity.this);
				
				if(gesture.checkTemplateStatus()) {
					ProtractorAuthentication auth = new ProtractorAuthentication(gesture);
					auth.authentication(MainActivity.this);
				} else
					Toast.makeText(MainActivity.this,"Please Calculate Template First!", Toast.LENGTH_SHORT).show();
			}
		});
		
		mDisplay = getWindowManager().getDefaultDisplay();
		mSize = new Point();
		mDisplay.getSize(mSize);
		mWidth = mSize.x;
		mHeight = mSize.y;
		mLayout = new LayoutParams(mWidth, mHeight - mBottomSpace);
//		Log.v("Test", "width " + mWidth);
//		Log.v("Test", "height " + mHeight);
		
		
		addContentView(gesture, mLayout);
	}
}
