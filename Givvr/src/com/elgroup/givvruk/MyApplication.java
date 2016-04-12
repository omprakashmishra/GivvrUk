package com.elgroup.givvruk;

import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.gcm.GCMRegistrar;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.tech.givvruk.utils.Constants;
import com.tech.givvruk.utils.UserFunctions;

import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;

public class MyApplication extends Application {
	public static  SharedPreferences preference;
	public static  DefaultHttpClient httpclient;
	public static  MediaPlayer player;
	public static  boolean FLAGCREATEPROFILE=false,FLAG_GOOGLE_LOGIN = false;
	public static  boolean flagVolume=false, FLAG_RATEUS_TIMER = false;
	public static  AudioManager am ;
	public static  boolean FLAG_VIDEO_PLAYED = false,FLAG_LAYOUT_FEEDBACK = false,FLAG_LAYOUT_RATEUS = false;
	public static  boolean FLAG_PROJECT_ADDED=false,FLAG_READ_FRIENDS=false;
	public static  Context ctx;
	public static  int numMessage=0;
	public static  String currentRaise;
	public static  StringBuffer sb = new StringBuffer(5);
	public static  String[] videoUrls;
	@SuppressWarnings("unused")
	@Override
	public void onCreate() {

		/*if (Constants.Config.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
		}*/
		FlurryAgent.init(this, Constants.FLURRY_ID);
		ctx=this;
		super.onCreate();
		preference = getSharedPreferences("Givvr App",MODE_PRIVATE);

		httpclient = new DefaultHttpClient();
		player= new MediaPlayer();
		am = (AudioManager)getSystemService(AUDIO_SERVICE);
		initImageLoader(ctx);
	}

	 public static void gotoRateUs(Context context)
	    {
	    	    Uri uri = Uri.parse("market://details?id=" + ctx.getPackageName());
	    	    Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
	    	    try {
	    	    	context.startActivity(myAppLinkToMarket);
	    	    } catch (ActivityNotFoundException e) {
	    	        Toast.makeText(context, " unable to find market app", Toast.LENGTH_LONG).show();
	    	    }
	    }
	
	public void initImageLoader(Context context) {
		File cacheDir = new File(this.getCacheDir(), "Givvr");
	    if (!cacheDir.exists())
	        cacheDir.mkdir();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.MIN_PRIORITY + 3)
				.denyCacheImageMultipleSizesInMemory()
				.threadPoolSize(5)
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.memoryCache(new FIFOLimitedMemoryCache(1))
				.diskCache(new UnlimitedDiscCache(cacheDir)) // default
				.diskCacheSize(100 * 1024 * 1024) // 100 Mb
				.tasksProcessingOrder(QueueProcessingType.FIFO)
				.imageDownloader(new BaseImageDownloader(ctx)) // default
				.imageDecoder(new BaseImageDecoder(true)) // default
        		.defaultDisplayImageOptions(DisplayImageOptions.createSimple()) 
				.writeDebugLogs() // Remove for release app
				.build();
		ImageLoader.getInstance().init(config);
	}
	
	public static void register(final String regId) {
         
			UserFunctions user = new UserFunctions(ctx);
			user.registerGCM(regId, "android");
	        GCMRegistrar.setRegisteredOnServer(ctx, true);
	    }
	 
	public static void unregister( final String flag) {
         
	        UserFunctions user = new UserFunctions(ctx);
			user.unregisterGCM(flag);
	        GCMRegistrar.setRegisteredOnServer(ctx, false);
	    }
	   
	   static void displayMessageOnScreen(Context context, String message) {
	          
	        Intent intent = new Intent(Constants.DISPLAY_MESSAGE_ACTION);
	        intent.putExtra(Constants.EXTRA_MESSAGE, message);
	        context.sendBroadcast(intent);
	    }    
	    private static PowerManager.WakeLock wakeLock;
	     
	    public static void acquireWakeLock(Context context) {
	        if (wakeLock != null) wakeLock.release();
	        PowerManager pm = (PowerManager) 
	                          context.getSystemService(Context.POWER_SERVICE);
	        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
	                PowerManager.ACQUIRE_CAUSES_WAKEUP |
	                PowerManager.ON_AFTER_RELEASE, "WakeLock");
	        wakeLock.acquire();
	    }
	 
	    public static  void releaseWakeLock() {
	        if (wakeLock != null) wakeLock.release(); wakeLock = null;
	    }
	}