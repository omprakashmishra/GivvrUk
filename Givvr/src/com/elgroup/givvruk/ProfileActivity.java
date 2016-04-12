package com.elgroup.givvruk;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.tech.givvruk.utils.ConnectionDetector;
import com.tech.givvruk.utils.Constants;
import com.tech.givvruk.utils.ListViewAdapterProjects;
import com.tech.givvruk.utils.Project;
import com.tech.givvruk.utils.UrlList;
import com.tech.givvruk.utils.UserFunctions;
import com.tech.givvruk.utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileActivity extends Activity {

	TextView txtName,txtAge,txtCity,txtGivvrTotal,txtYourTotal,edit;
	ConnectionDetector cd;
	ImageView profile_dp;
	 String id=null,personPhotoUrl;
	 DisplayImageOptions options;
	 SharedPreferences preference;
	 String img_value = null;
	 ArrayList<Project> arrayProject = new ArrayList<Project>();
	 ListViewAdapterProjects adapter;
    private String update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);
        cd = new ConnectionDetector(this);
        preference = MyApplication.preference;
        options = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.profile)
                .resetViewBeforeLoading(true)
                .cacheInMemory(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(170))
                .build();
        
         Intent data=getIntent();

		 txtName=(TextView)findViewById(R.id.textUserName);
		 txtAge=(TextView)findViewById(R.id.textUserAge);
		 txtCity=(TextView)findViewById(R.id.textUserCity);
		 txtGivvrTotal=(TextView)findViewById(R.id.textGivvrTotal);
		 txtYourTotal=(TextView)findViewById(R.id.textYourTotal);
		 profile_dp =  (ImageView)findViewById(R.id.profile_dp);
		 edit=(TextView) findViewById(R.id.edit);

		txtName.setText(data.getStringExtra("name"));
        String a=data.getStringExtra("age");
                if(data.getStringExtra("age")!=null && !data.getStringExtra("age").equals(""))
                {
                    txtAge.setText(data.getStringExtra("age")+" YEARS");
                }
		txtCity.setText(data.getStringExtra("city"));
                if(data.getStringExtra("givvr_total")!=null)
                {
                txtGivvrTotal.setText("£"+data.getStringExtra("givvr_total"));
                txtYourTotal.setText("£"+data.getStringExtra("your_total"));
                }
                 id = preference.getString("facebook_id", "");
                 personPhotoUrl = preference.getString("person_google_photo_url", "");
                 if(!personPhotoUrl.isEmpty())
                 {
                     img_value = personPhotoUrl;
                     edit.setVisibility(View.GONE);
                 }
                 else if(id.isEmpty())
                {
                    img_value = data.getStringExtra("PROFILE_IMAGE_URL");
                }
                else
                {
                 edit.setVisibility(View.GONE);
                 img_value = "http://graph.facebook.com/"+id+"/picture?type=large";
                }
	    Utilities.downloadImage(img_value, profile_dp, options, null);
      //********
        update="";
        new fetchHomeData().execute();
    }
    public void gotoBackProfile(View v)
    {
    	finish();
    	overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
    }
    public void editProfile(View v)
    {
    	MyApplication.FLAGCREATEPROFILE=true;
    	if(id.isEmpty())
    	{
    	Intent invok=new Intent(getApplicationContext(), CreateProfile.class);
            Intent data=getIntent();
            //***********
            invok.putExtra("from","ProfileActivity");
            /*invok.putExtra("name",txtName.getText());
            invok.putExtra("age",data.getStringExtra("age"));
            invok.putExtra("city",txtCity.getText());*/
            invok.putExtra("image_value",img_value);
		startActivity(invok);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    	}
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	if(MyApplication.FLAGCREATEPROFILE)
    	{
    		if (cd.isConnectingToInternet()) 
    		{
    		new fetchHomeData().execute();
    		}
    		else
    		{
    		}
    		FlurryAgent.onStartSession(this, Constants.FLURRY_ID);
    	}
    }
	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}


    public class fetchHomeData extends AsyncTask<String, String, JSONObject>
    {
		
    	UserFunctions userFunction = new UserFunctions(getApplicationContext());
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    	}
    	
		@Override
		protected JSONObject doInBackground(String... params) {
			
			JSONObject jsonforProfile = userFunction.getProfile(preference.getString("accesstoken", ""));
			return jsonforProfile;
		}
    	@Override
    	protected void onPostExecute(JSONObject result) {
    		super.onPostExecute(result);
    		try {
                if(update.equals("all")){
                    txtName.setText(result.getString("name"));
                    if(!result.getString("age").equals("")){
                        txtAge.setText(result.getString("age")+" YEARS");
                    }if(id.isEmpty()){
                        img_value = UrlList.domainURL+result.getString("profile_image");
                    }
                    txtCity.setText(result.getString("city"));
                    txtGivvrTotal.setText("£"+result.getString("givvr_total"));
                    txtYourTotal.setText("£"+result.getString("your_total"));
                    Utilities.downloadImage(img_value, profile_dp, options, null);
                }else{
                    txtGivvrTotal.setText("£"+result.getString("givvr_total"));
                    txtYourTotal.setText("£"+result.getString("your_total"));
                }

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		MyApplication.FLAGCREATEPROFILE=false;
    	}
    }
    @Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
	}

    @Override
    protected void onResume() {
        super.onResume();
        update="all";
        if (Home.dataUpdate) {
            new fetchHomeData().execute();
        }
    }
}
