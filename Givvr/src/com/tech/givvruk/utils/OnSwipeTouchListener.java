package com.tech.givvruk.utils;

import android.app.Activity;
import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.elgroup.givvruk.Home;
import com.elgroup.givvruk.R;

public class OnSwipeTouchListener implements OnTouchListener
{ 
	Context ctx;
	boolean flag_swipe;
	private final GestureDetector gestureDetector; 
	public OnSwipeTouchListener(Context context,boolean FLAG_ACTIVITY) 
	{ 
		ctx = context;
		flag_swipe = FLAG_ACTIVITY;
		gestureDetector = new GestureDetector(context, new GestureListener());
		
	} 
	
	public void onSwipeLeft() 
	{
		
	} 
	public void onSwipeRight() 
	{ 
		if(flag_swipe)
		{
			Home.toggleMenu();
		}
		else
		{
			((Activity)ctx).finish();
			((Activity)ctx).overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
		}
		
	}
	
	public boolean onTouch(View v, MotionEvent event) 
	{ 
		return gestureDetector.onTouchEvent(event); 
	} 
	
	
	private final class GestureListener extends SimpleOnGestureListener implements OnGestureListener 
	
	{ 
		
		private static final int SWIPE_DISTANCE_THRESHOLD = 100; 
		private static final int SWIPE_VELOCITY_THRESHOLD = 100; 
		
		@Override public boolean onDown(MotionEvent e) 
		
		{ 
			return true; 
			}
		@Override 
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) 
		
		{ 
			float distanceX = e2.getX() - e1.getX(); 
			float distanceY = e2.getY() - e1.getY(); 
			if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) 
			
			{ 
				
				if (distanceX > 0) onSwipeRight(); 
				
				else onSwipeLeft(); 
				
				return true; 
				
			} return false; 
			} 
		} 
	
}