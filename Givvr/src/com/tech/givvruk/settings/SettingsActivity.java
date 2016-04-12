package com.tech.givvruk.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.elgroup.givvruk.Home;
import com.elgroup.givvruk.MyApplication;
import com.elgroup.givvruk.R;
import com.elgroup.givvruk.StartActivity;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.tech.givvruk.utils.ConnectionDetector;
import com.tech.givvruk.utils.Constants;
import com.tech.givvruk.utils.UserFunctions;

import java.io.File;

public class SettingsActivity extends Activity implements OnCheckedChangeListener {
	SharedPreferences preference;
	private ConnectionDetector cd;
	//Session session;
	CheckBox checkNotification;
	LoginManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.settings_page);
        preference = MyApplication.preference;
        cd = new ConnectionDetector(this);
       // session = Session.getActiveSession();
		//manager.
        checkNotification = (CheckBox) findViewById(R.id.checkNotification);
        checkNotification.setOnCheckedChangeListener(this);
       if(preference.getBoolean("NOTIFICATION_STATUS", false))
    	{
        	checkNotification.setChecked(true);
    	}
    }
    public void gotoBackSettings(View v)
    {
    	finish();
    	overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
    } 
    public void logout(View v)
    {
        if(cd.isConnectingToInternet())
        {
            UserFunctions userFunction = new UserFunctions(this);
            //userFunction.logoutUser();
            deleteCache(this);
            LoginManager.getInstance().logOut();
		/*if(session!=null)
		{
		session.closeAndClearTokenInformation();
		}*/
            SharedPreferences.Editor   editor = preference.edit();
            editor.clear();
            editor.commit();
            new Home().stopPushNotification();
            Intent invok=new Intent(this, StartActivity.class);
            invok.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(invok);
            finish();
        }
        else
        {
            Toast tost = Toast.makeText(this, "No internet!",Toast.LENGTH_SHORT);
            tost.setGravity(Gravity.CENTER,0, 0);
            tost.show();
        }
    }
    public void gotoContactUs(View v)
    {
    	/*	Intent email = new Intent(Intent.ACTION_SEND);
    		email.putExtra(Intent.EXTRA_EMAIL, new String[] { "contact@givvr.com" });
    		email.putExtra(Intent.EXTRA_SUBJECT, "Givvr App");
    		email.putExtra(Intent.EXTRA_TEXT, "Hi, Givvr App is Superb! \n Thanks for such a nice app.");
    		email.setType("message/rfc822");
    		startActivity(Intent.createChooser(email, "Choose an Email client"));
		*/
    	Intent invok=new Intent(getApplicationContext(), ContactUsActivity.class);
    	invok.putExtra("user_name", getIntent().getStringExtra("user_name"));
    	startActivity(invok);
    	overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void gotoPrivacyPolicy(View v)
    {
    	Intent invok=new Intent(getApplicationContext(), PrivacyPolicyActivity.class);
    	invok.setAction(Constants.Action.ACTION_SETTINGS_ACTIVITY);
		startActivity(invok);
    	overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }
    public void gotoTermsService(View v)
    {
    	Intent invok=new Intent(getApplicationContext(), TermsServiceActivity.class);
    	invok.setAction(Constants.Action.ACTION_SETTINGS_ACTIVITY);
		startActivity(invok);
    	overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    } 
    public void gotoRateUs(View v)
    {
    	MyApplication.gotoRateUs(this);
    }
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(isChecked)
		{
			SharedPreferences.Editor   editor = preference.edit();
			editor.putBoolean("NOTIFICATION_STATUS", true);
			editor.apply();
			new Home().startPushNotification();
		}
		else
		{
			SharedPreferences.Editor   editor = preference.edit();
			editor.putBoolean("NOTIFICATION_STATUS", false);
			editor.apply();
			new Home().stopPushNotification();
		}
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
	}
	public static void deleteCache(Context context) {
	    try {
	        File dir = context.getCacheDir();
	        if (dir != null && dir.isDirectory()) {
	            deleteDir(dir);
	        }
	    } catch (Exception e) {}
	}

	public static boolean deleteDir(File dir) {
	    if (dir != null && dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i = 0; i < children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }
	    return dir.delete();
	}
}