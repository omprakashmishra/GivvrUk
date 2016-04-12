package com.elgroup.givvruk;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.flurry.android.FlurryAgent;
import com.tech.givvruk.utils.ConnectionDetector;
import com.tech.givvruk.utils.Constants;
import com.tech.givvruk.utils.ListViewAdapter;
import com.tech.givvruk.utils.OnSwipeTouchListener;
import com.tech.givvruk.utils.UrlList;
import com.tech.givvruk.utils.UserFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SponsorsActivity extends Activity {
	 
		JSONObject jsonobject;
	    JSONArray jsonarray;
	    ListView listview;
	    ListViewAdapter adapter;
	    ArrayList<HashMap<String, String>> arraylist;	    
	    public static String SPONSORCOMPANY = "company";
	    public static String SPONSORDESCRIPTION = "description";
	    public static String SPONSORLOGO = "logo";
	    public static String FBLIKEPAGEURL = "facebooklink";
	    public static String WEBSITE = "website";
	    String IMAGE_URL=UrlList.SPONSORIMAGELINK;
	    private ConnectionDetector cd;
	    ProgressBar sponsorLoading;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.sponsors_page);

        sponsorLoading = (ProgressBar) findViewById(R.id.sponsorLoading);
        cd = new ConnectionDetector(this);
        if (cd.isConnectingToInternet()) {
        new DownloadJSON().execute();
        }
        else
        {
        	Toast tost = Toast.makeText(this, "No internet!",Toast.LENGTH_SHORT);
			tost.setGravity(Gravity.CENTER,0, 0);
			tost.show();
        }
    }
    
    public void gotoBackSponsors(View v)
    {
    	finish();
    	overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
    }
    
    private class DownloadJSON extends AsyncTask<Void, Void, Void> {
    	 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            sponsorLoading.setVisibility(View.VISIBLE);
        }
 
        @Override
        protected Void doInBackground(Void... params) {
            arraylist = new ArrayList<HashMap<String, String>>();
            UserFunctions userFunctions=new UserFunctions(SponsorsActivity.this);
            jsonobject =  userFunctions.getSponsors();
            try {
                jsonarray = jsonobject.getJSONArray("sponsors");
                for (int i = 0; i < jsonarray.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    jsonobject = jsonarray.getJSONObject(i);
                    map.put("company", jsonobject.getString("company"));
                    map.put("description", jsonobject.getString("bio"));
                    map.put("logo", IMAGE_URL+jsonobject.getString("logo"));
                    map.put("facebooklink", jsonobject.getString("facebook"));
                    map.put("website", jsonobject.getString("website"));
                    arraylist.add(map);
                }
            } catch (JSONException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void args) {
        	sponsorLoading.setVisibility(View.INVISIBLE);
            listview = (ListView) findViewById(R.id.listSponsors);
            adapter = new ListViewAdapter(SponsorsActivity.this, arraylist);
            listview.setAdapter(adapter); 
            listview.setOnTouchListener(new OnSwipeTouchListener(SponsorsActivity.this, false));
        }
    }
    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
    	super.onActivityResult(arg0, arg1, arg2);
    	//LikeView.handleOnActivityResult(this, arg0, arg1, arg2);
    }
    @Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
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