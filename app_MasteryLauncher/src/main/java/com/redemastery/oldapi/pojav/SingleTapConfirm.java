package com.redemastery.oldapi.pojav;

import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class SingleTapConfirm extends SimpleOnGestureListener {
	@Override
	public boolean onSingleTapUp(MotionEvent event) {
		return true;
	}
}
