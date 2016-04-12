package com.elgroup.givvruk;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.flurry.android.FlurryAgent;
import com.tech.givvruk.utils.ConnectionDetector;
import com.tech.givvruk.utils.Constants;
import com.tech.givvruk.utils.UserFunctions;
import com.tech.givvruk.utils.Utilities;

import org.json.JSONObject;

public class SplashActivity extends Activity {
	protected boolean _active = true;
	private Thread splashTread;
	SharedPreferences preference;
	String userEmail,userProfile,flagLoginType,flagLoginGoogle;
	Boolean isInternetPresent = false;
	ConnectionDetector cd;
	UserFunctions userFunction;
	protected int _splashTime = 5000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.landing_page);
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
		WebView webview = (WebView)findViewById(R.id.webView1);
		WebSettings settings = webview.getSettings();
		settings.setJavaScriptEnabled(true);
		webview.loadUrl("file:///android_asset/www/htmltest.html");
		cd = new ConnectionDetector(this);
		isInternetPresent = cd.isConnectingToInternet();
		userFunction= new UserFunctions(this);
		  if (isInternetPresent) {
	        	preference = MyApplication.preference;
	        	userEmail = preference.getString("user_email", "");
	   		 	userProfile = preference.getString("profile_status", "");
	   		 	flagLoginType = preference.getString("facebook_id", "");
	   		 	flagLoginGoogle = preference.getString("google_login", "");
		splashTread = new Thread() {
			@Override
			public void run() {
				try {
					int waited = 0;
					while (_active && (waited < _splashTime)) {
						sleep(100);
						if (_active) {
							waited += 100;
						}
					}
				} catch (InterruptedException e) {
					finish();
				} finally {
					try {

                        //if access token is not empty---->Home activity else StartActivity
                        // editor.putString("accesstoken", ACCESS_TOKEN);
						if(userEmail.isEmpty())
		    			{
		            		Intent invok=new Intent(getApplicationContext(), StartActivity.class);
		    				invok.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    				startActivity(invok);
		    				finish();
		    			}
		    			else if(userProfile.isEmpty())
		    			{
		    				String facebook,google;
		    				if(flagLoginType.contentEquals("")||flagLoginGoogle.contentEquals("yes"))
		    				{
		    					facebook = "0";
		    					google = "1";
		    				}
		    				else
		    				{
		    					facebook = "1";
		    					google = "0";
		    				}
		    				JSONObject json = userFunction.loginUser(userEmail,facebook,google);
			    			if(json==null)
			    			{
			    				Utilities ultilities = new Utilities();
								ultilities.displayMessageAndExit(SplashActivity.this, "Alert!","Sorry, unable to process. Please check your network connection or try again later.");
			    			}
			    			else
			    			{
		    				Intent invok=new Intent(getApplicationContext(), CreateProfile.class);
		    				invok.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    				startActivity(invok);
		    				finish();

                                /*Intent invok=new Intent(getApplicationContext(), StartActivity.class);
                                invok.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(invok);
                                finish();*/
			    			}
		    			}
		    			else
		    			{
		    				String facebook,google;
		    				if(flagLoginGoogle.contentEquals("yes"))
		    				{
		    					google = "1";
		    				}
		    				else
		    				{
		    					google = "0";
		    				}
		    				if(flagLoginType.contentEquals(""))
		    				{
		    					facebook = "0";
		    				}
		    				else
		    				{
		    					facebook = "1";
		    				}
		    				JSONObject json = userFunction.loginUser(userEmail,facebook,google);
		    				
		    				if(json==null)
			    			{
			    				Utilities ultilities = new Utilities();
								ultilities.displayMessageAndExit(SplashActivity.this, "Alert!","Sorry, unable to process. Please check your network connection or try again later.");
			    			}
			    			else
			    			{
		    				Intent i = new Intent(getApplicationContext(), Home.class);
		    				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    				startActivity(i);
		    				finish();
			    			}
		    			}

					} catch (Exception e2) {
						
					}
				}
			}
		};
		splashTread.start();
	}
		  else {
			  Intent i = new Intent(getApplicationContext(), StartActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				finish();
		        /* Utilities ultilities = new Utilities();
					ultilities.displayMessageAndExit(this, "No internet!","Sorry, unable to process. Please check your network connection or try again later.");*/
		       }	
	}

	@Override
	public void onBackPressed() {
		splashTread.interrupt();
		super.onBackPressed();
	}
	@Override
	protected void onStart() {
		NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(GCMIntentService.PUSH_NOTIFICATION);
		super.onStart();
		FlurryAgent.onStartSession(this, Constants.FLURRY_ID);
		
	}
	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
}
