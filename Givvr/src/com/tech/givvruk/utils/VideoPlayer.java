package com.tech.givvruk.utils;

/**
 * Created by Mangal on 10/6/2015.
 */

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.io.IOException;

public class VideoPlayer implements MediaPlayer.OnBufferingUpdateListener {

    SurfaceView videoSurface;
    MediaPlayer player;
    VideoControllerViewNew controller;
    ProgressBar progressBar1;
    int duration = 0;
    Context context;
    RelativeLayout surfaceViewContainer;


    public void playVideo(Context context, SurfaceView surfaceView, ProgressBar progressBar, RelativeLayout surfaceViewContainer, String URL) {
        this.context = context;
        progressBar1 = progressBar;
        this.videoSurface = surfaceView;
        this.surfaceViewContainer = surfaceViewContainer;
        controller = new VideoControllerViewNew(context);

        SurfaceHolder videoHolder = videoSurface.getHolder();
        videoHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                player.setDisplay(holder);
                player.prepareAsync();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
        player = new MediaPlayer();

        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnBufferingUpdateListener(this);

            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    controller.setMediaPlayer((new VideoControllerViewNew.MediaPlayerControl() {
                        @Override
                        public void start() {

                        }

                        @Override
                        public void pause() {

                        }

                        @Override
                        public int getDuration() {
                            return 0;
                        }

                        @Override
                        public int getCurrentPosition() {
                            return 0;
                        }

                        @Override
                        public void seekTo(int pos) {

                        }

                        @Override
                        public boolean isPlaying() {
                            return false;
                        }

                        @Override
                        public int getBufferPercentage() {
                            return 0;
                        }

                        @Override
                        public boolean canPause() {
                            return false;
                        }

                        @Override
                        public boolean canSeekBackward() {
                            return false;
                        }

                        @Override
                        public boolean canSeekForward() {
                            return false;
                        }

                        @Override
                        public boolean isFullScreen() {
                            return false;
                        }

                        @Override
                        public void toggleFullScreen() {

                        }
                    }));
                    controller.setAnchorView(VideoPlayer.this.surfaceViewContainer);
                    mp.seekTo(duration);
                    mp.start();
                    mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {

                        @Override
                        public void onVideoSizeChanged(MediaPlayer mp, int arg1, int arg2) {
                            // TODO Auto-generated method stub

                            progressBar1.setVisibility(View.GONE);
                            mp.start();
                        }
                    });
                }
            });

            player.setDataSource(context, Uri.parse(URL));
            player.prepare();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    // Implement SurfaceHolder.Callback

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        int progress = (int) ((float) mp.getDuration() * ((float) percent / (float) 100));
        if (controller.getSeekBar() != null)
            controller.getSeekBar().setSecondaryProgress(percent);
    }

}
