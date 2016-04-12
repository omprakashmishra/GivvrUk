package com.elgroup.givvruk;

import com.flurry.android.FlurryAgent;
import com.tech.givvruk.utils.Constants;
import com.tech.givvruk.utils.VideoControllerView;
import com.tech.givvruk.utils.VideoControllerView.MediaPlayerControl;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class FullscreenActivity extends Activity implements Callback, MediaPlayerControl{
	VideoControllerView controller;
	SurfaceView videoSurface;
	MediaPlayer player;
	int curVolume ;
	LinearLayout layoutVolume;
	Boolean flagVolume=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_fullscreen);
		player = FragmentVideos.mediaPlayer;
		
		 videoSurface = (SurfaceView)findViewById(R.id.videoSurface);
	        SurfaceHolder videoHolder = videoSurface.getHolder();
	        videoHolder.addCallback(this);
	        
	        controller = new VideoControllerView(this,getIntent().getStringExtra("project_color"));
	        videoSurface.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					
					return false;
				}
			});
	        player.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mPlayer) {
					player.seekTo(0);
					finish();
				}
			});
	}
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		player.start();
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		player.setDisplay(holder);
		controller.setMediaPlayer(this);
		controller.setAnchorView((RelativeLayout)findViewById(R.id.videoSurfaceContainer));
		controller.show();
	}
    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return player.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public void pause() {
        player.pause();
        controller.hide();
    }

    @Override
    public void seekTo(int i) {
        player.seekTo(i);
    }

    @Override
    public void start() {
        player.start();
    }
    private boolean mFullScreen = true;
    @Override
    public boolean isFullScreen() {
    	 if(mFullScreen){
    	        return false;
    	    }else{      
    	        return true;
    	    } 
    }

    @Override
    public void toggleFullScreen() {
    	setFullScreen(isFullScreen());
    }
    
    public void setFullScreen(boolean fullScreen){
        fullScreen = false;

        if (mFullScreen)
        {
        	setResult(2);
        	finish();
        }
        else
        {
          
        }
    }
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Constants.FLURRY_ID);
		
	}
	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
}
