package com.tech.givvruk.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.elgroup.givvruk.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Calendar;

public class Utilities {
	
	public static LayerDrawable MyProgressLayer(Context context,String colorString)
	{
			BitmapDrawable shape = (BitmapDrawable) context.getResources().getDrawable(R.drawable.stripe);
			ClipDrawable clip2 = new ClipDrawable(shape, Gravity.LEFT,ClipDrawable.HORIZONTAL);
	       
	       GradientDrawable shape2 = new GradientDrawable(Orientation.BOTTOM_TOP, new int[]{Color.parseColor(colorString),Color.parseColor(colorString)});
	       shape2.setCornerRadius(50);                     
	       ClipDrawable clip = new ClipDrawable(shape2, Gravity.LEFT,ClipDrawable.HORIZONTAL);
	       GradientDrawable shape1 = new GradientDrawable(Orientation.BOTTOM_TOP, new int[]{Color.WHITE, context.getResources().getColor(R.color.white)});
	       shape1.setCornerRadius(50);//change the corners of the rectangle    
	       InsetDrawable d1=  new InsetDrawable(shape1,0,0,0,0);//the padding u want to use
	       LayerDrawable mylayer = new LayerDrawable(new Drawable[]{d1,clip,clip2});
	       
	       return mylayer;
	}
	
	public static LayerDrawable MyButtonLayer(Context context,String colorString)
	{
		
	       GradientDrawable shape1 = new GradientDrawable(Orientation.BOTTOM_TOP, new int[]{Color.parseColor(colorString),Color.parseColor(colorString)});
	       shape1.setCornerRadius(100);//change the corners of the rectangle
	       InsetDrawable d1=  new InsetDrawable(shape1,0,0,0,0);//the padding u want to use  
	    
	       LayerDrawable mylayer = new LayerDrawable(new Drawable[]{d1});
	       return mylayer;
	}
	public static LayerDrawable MyPlayButtonLayer(Context context,String colorString)
	{
		   
	       GradientDrawable shape1 = new GradientDrawable(Orientation.BOTTOM_TOP, new int[]{Color.parseColor(colorString),Color.parseColor(colorString)});
	       shape1.setCornerRadius(200);//change the corners of the rectangle    
	       shape1.setStroke(6, Color.WHITE);
	       shape1.setAlpha(230);
	       InsetDrawable d1=  new InsetDrawable(shape1,0,0,0,0);//the padding u want to use
	       
	       LayerDrawable mylayer = new LayerDrawable(new Drawable[]{d1});
	       
	       return mylayer;
	}
	
	public void displayMessageAndExit(Activity activity, String tiltleMassage,
			String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(tiltleMassage);
		builder.setMessage(message);
		builder.setPositiveButton("Ok", new FinishListener(activity));
		builder.setOnCancelListener(new FinishListener(activity));
		builder.setIcon(R.drawable.ic_action_warning);
		builder.setCancelable(false);
		builder.show();
	}

	public static void downloadImage(String IMAGE_URL,ImageView imageProjectHome,DisplayImageOptions options,final ProgressBar spinner)
	{
 		com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(IMAGE_URL, imageProjectHome, options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				if(spinner!=null)
				{
				spinner.setVisibility(View.VISIBLE);
				}
			}
			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				String message = null;
				switch (failReason.getType()) {
					case IO_ERROR:
						message = "Image not found";
						break;
					case DECODING_ERROR:
						message = "Image can't be decoded";
						break;
					case NETWORK_DENIED:
						message = "Downloads are denied";
						break;
					case OUT_OF_MEMORY:
						message = "Out Of Memory error";
						break;
					case UNKNOWN:
						message = "Unknown error";
						break;
				}
				if(spinner!=null)
				{
				spinner.setVisibility(View.GONE);
				}
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				if(spinner!=null)
				{
				spinner.setVisibility(View.GONE);
				}
			}
		});
	}
	public static void adjustAspectRatio(int videoWidth, int videoHeight,TextureView mTextureView) {
        int viewWidth = mTextureView.getWidth();
        int viewHeight = mTextureView.getHeight();
        double aspectRatio = (double) videoHeight / videoWidth;

        int newWidth, newHeight;
        if (viewHeight > (int) (viewWidth * aspectRatio)) {
            // limited by narrow width; restrict height
            newWidth = viewWidth;
            newHeight = (int) (viewWidth * aspectRatio);
        } else {
            // limited by short height; restrict width
            newWidth = (int) (viewHeight / aspectRatio);
            newHeight = viewHeight;
        }
        int xoff = (viewWidth - newWidth) / 2;
        int yoff = (viewHeight - newHeight) / 2;
        Log.v("Mydata", "video=" + videoWidth + "x" + videoHeight +
                " view=" + viewWidth + "x" + viewHeight +
                " newView=" + newWidth + "x" + newHeight +
                " off=" + xoff + "," + yoff);

        Matrix txform = new Matrix();
        mTextureView.getTransform(txform);
        txform.setScale((float) newWidth / viewWidth, (float) newHeight / viewHeight);
        //txform.postRotate(10);          // just for fun
        txform.postTranslate(xoff, yoff);
        mTextureView.setTransform(txform);
    }
	public static void createScheduledNotification(Context context,int days)
    {
    
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(System.currentTimeMillis());
    
    calendar.add(Calendar.HOUR_OF_DAY, days * 24);
    
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    
    int id = (int) System.currentTimeMillis();
    Intent intent = new Intent(context, AlarmReceiver.class);

    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

}
