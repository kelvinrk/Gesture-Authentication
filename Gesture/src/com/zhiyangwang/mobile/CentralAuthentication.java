package com.zhiyangwang.mobile;

import android.content.Context;

public class CentralAuthentication {
	private RAMsAuthentication mRAMsAuthentication;
	private ProtractorAuthentication mProtractorAuthentication;
	private boolean ramsResult;
	private boolean protResult;
	
	public CentralAuthentication(DrawGesture mDraw, Context context, boolean useProtractor) {
		
		mRAMsAuthentication = new RAMsAuthentication(mDraw);
		ramsResult = mRAMsAuthentication.authentication(context);
		
		mProtractorAuthentication = new ProtractorAuthentication(mDraw);
		protResult = mProtractorAuthentication.authentication(context);
	}
}
