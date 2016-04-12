package com.tech.givvruk.utils;

import org.json.JSONException;
import org.json.JSONObject;

import com.elgroup.givvruk.MyApplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

public class AlarmReceiver extends BroadcastReceiver{

	SharedPreferences preference;
	Context ctx;
	@Override
	public void onReceive(Context context, Intent intent) {
		
		ctx = context;
		preference = MyApplication.preference;
		if(preference.getBoolean("APP_IS_RATED", false))
		{
			
		}
		else
		{
			new ratingAsyn().execute();
		}
	}

	class ratingAsyn extends AsyncTask<String, String, String>
	{
		String rated;
		SharedPreferences.Editor   editor = preference.edit();
		@Override
		protected String doInBackground(String... params) {
			
			UserFunctions user = new UserFunctions(ctx);
			JSONObject json =  user.ratingApp();
			try {
				rated = json.getString("rated");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return rated;
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(result.contentEquals("0"))
			{
				 editor.putBoolean("LAYOUT_RATEUS", true);
				 editor.apply();
				 editor.commit();
			}
			else
			{
				 editor.putBoolean("LAYOUT_RATEUS", false);
				 editor.putBoolean("APP_IS_RATED", true);
				 editor.apply();
				 editor.commit();
			}
		}
	}
}
