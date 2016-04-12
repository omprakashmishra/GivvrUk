package com.elgroup.givvruk;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.tech.givvruk.utils.AlertMessage;
import com.tech.givvruk.utils.Constants;
import com.tech.givvruk.utils.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

public class VerifyEmailActivity extends Activity implements OnClickListener{
	
	SharedPreferences preference;
	TextView forgotPasswordTitle;
	Button verifySubmit;
	EditText codeEditext;
	String emailToVerify;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgot_password);
		preference = MyApplication.preference;
		forgotPasswordTitle = (TextView) findViewById(R.id.forgot_password_title);
		forgotPasswordTitle.setText("Verify Code");
		verifySubmit = (Button) findViewById(R.id.verify_submit);
		verifySubmit.setOnClickListener(this);
		codeEditext = (EditText) findViewById(R.id.code_edittext);
		emailToVerify = preference.getString("VERIFY_EMAIL", "");
	}
	@Override
	public void onClick(View v) {
		if(v == verifySubmit)
		{
			UserFunctions user = new UserFunctions(this);
			JSONObject jsonResponse = user.verifyEmail(emailToVerify, codeEditext.getText().toString());
			try {
				if(jsonResponse.getString("success").contentEquals("1"))
				{
					SharedPreferences.Editor   editor = preference.edit();
				    editor.putString("VERIFY_EMAIL","");
				    editor.apply();
				    editor.commit();
					Intent invok=new Intent(getApplicationContext(), SigninActivity.class);
			 		invok.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 		startActivity(invok);
			 		finish();
				}
				else
				{
					AlertMessage.showAlertDialog(this, "Alert!", "Code Entered Wrong! Please check mail and verify.", false);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
	}
	public void cancelButtonPwd(View v)
	{
		finish();
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
