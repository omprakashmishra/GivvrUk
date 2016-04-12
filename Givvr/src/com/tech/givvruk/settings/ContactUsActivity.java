package com.tech.givvruk.settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.elgroup.givvruk.MyApplication;
import com.elgroup.givvruk.R;
import com.tech.givvruk.utils.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

public class ContactUsActivity extends Activity{
	
	SharedPreferences preference;
	String userEmail;
	TextView edit_name,edit_email;
	EditText edit_message;
	int RESULT_OK = 200;
	Context ctx;
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.contact_us);
	ctx = this;
	preference = MyApplication.preference;
	userEmail = preference.getString("user_email", "");
	edit_name=(TextView)findViewById(R.id.editName);
	edit_email=(TextView)findViewById(R.id.editEmail);
	edit_message=(EditText)findViewById(R.id.editMessage);
	edit_name.setText(getIntent().getStringExtra("user_name"));
	edit_email.setText(userEmail);
	}
	
	public void gotoBackContact(View v)
	{
		finish();
		overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
	}
	public void contactSubmit(View v)
	{
		//ClientResponse response = Utilities.SendSimpleMessage();
		new SendingMessageAsyn().execute(edit_name.getText().toString(),edit_email.getText().toString(),edit_message.getText().toString());
		
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
	}
	
	class SendingMessageAsyn extends AsyncTask<String, String, JSONObject>
	{
		private ProgressDialog Dialog = new ProgressDialog(ctx);

		@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    		 Dialog.setMessage("SENDING...");
    		 Dialog.setCancelable(false);
    		 Dialog.setCanceledOnTouchOutside(false);
             Dialog.show();
    	}

		@Override
		protected JSONObject doInBackground(String... params) {
			UserFunctions user = new UserFunctions(ctx);
			JSONObject response = user.sendFeedback(params[0], params[1], params[2]);
			return response;
		}
		
		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			if(Dialog.isShowing())
    		{
				Dialog.dismiss();
    		}
			Log.i("Mydata", ""+result);
            try {
                if(result.getString("success").contentEquals("1")){
                    Toast tost = Toast.makeText(ContactUsActivity.this, "Successfully Posted", Toast.LENGTH_SHORT);
                    tost.setGravity(Gravity.CENTER, 0, 0);
                    tost.show();
                }else {
                    Toast tost = Toast.makeText(ContactUsActivity.this, "No internet!", Toast.LENGTH_SHORT);
                    tost.setGravity(Gravity.CENTER, 0, 0);
                    tost.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            finish();
			overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
		}
		
	}
}
