package com.elgroup.givvruk;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.tech.givvruk.settings.PrivacyPolicyActivity;
import com.tech.givvruk.settings.TermsServiceActivity;
import com.tech.givvruk.utils.AlertMessage;
import com.tech.givvruk.utils.CallbackInterface;
import com.tech.givvruk.utils.ConnectionDetector;
import com.tech.givvruk.utils.Constants;
import com.tech.givvruk.utils.GlobalVolley_cl;
import com.tech.givvruk.utils.UrlList;
import com.tech.givvruk.utils.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends Activity {

	EditText txtEmail,txtPassword,txtUsername;
	SharedPreferences preference;
	private ConnectionDetector cd;
	Button signupSubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        /*if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}*/
        cd = new ConnectionDetector(this);
        preference= MyApplication.preference;
		txtEmail=(EditText)findViewById(R.id.email_edittext);
		txtPassword=(EditText)findViewById(R.id.password_edittext);
		txtUsername=(EditText)findViewById(R.id.username_edittext);
		signupSubmit = (Button)findViewById(R.id.signup_submit);
    }
    
    public void cancelSignup(View v)
    {
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
    	//finish();
    }
    public void signupSubmit(View v)
    {
    	if(cd.isConnectingToInternet())
    	{	
    		if(txtEmail.getText().toString().isEmpty() || txtPassword.getText().toString().isEmpty())
    		{
    			AlertMessage.showAlertDialog(this, "Alert!", "Please enter valid email & password.", false);
    		}
    		else {
                if(!AlertMessage.isEmailValid(txtEmail.getText().toString())){
                    AlertMessage.showAlertDialog(this, "Alert!", "Please enter valid email.", false);
                    return;
                }
                UserFunctions userFunction = new UserFunctions(getApplicationContext());
                // JSONObject jsonResponse = userFunction.signup(txtEmail.getText().toString(), txtPassword.getText().toString());

                new GlobalVolley_cl(this, UrlList.SIGNUP,userFunction.Vsignup(txtEmail.getText().toString(), txtPassword.getText().toString()),new CallbackInterface() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject jsonResponse=null;
                        try {
                            jsonResponse=new JSONObject(response);
                            {
                                if (jsonResponse.getString("success").contentEquals("1")) {
                                    SharedPreferences.Editor editor = preference.edit();
                                    editor.putString("VERIFY_EMAIL", txtEmail.getText().toString());
                                    editor.putString("user_email", txtEmail.getText().toString());
                                    editor.putString("user_name", txtUsername.getText().toString());
                                    //***
                                    editor.putString("accesstoken", jsonResponse.getString("accesstoken"));

                                    editor.commit();
                                    /*Intent invok=new Intent(getApplicationContext(), VerifyEmailActivity.class);
                                    invok.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(invok);
                                    finish();*/
                                    MyApplication.FLAGCREATEPROFILE = true;
                                    Intent invok = new Intent(getApplicationContext(), CreateProfile.class);
                                    editor.putString("profile_status", "created");
                                    editor.apply();
                                    invok.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(invok);
                                    finish();
                                    //startActivity(invok);
                                    //finish();
                                }else if(jsonResponse.getString("success").contentEquals("0")&& !jsonResponse.getString("msg").contentEquals("")){
                                    Toast tost = Toast.makeText(SignupActivity.this, jsonResponse.getString("msg"), Toast.LENGTH_SHORT);
                                    tost.setGravity(Gravity.CENTER, 0, 0);
                                    tost.show();
                                }else {
                                    Toast tost = Toast.makeText(SignupActivity.this, "No internet!", Toast.LENGTH_SHORT);
                                    tost.setGravity(Gravity.CENTER, 0, 0);
                                    tost.show();
                                }
                            /*else
                            {
                                if(jsonResponse.has("active"))
                                {
                                    SharedPreferences.Editor   editor = preference.edit();
                                    editor.putString("VERIFY_EMAIL",txtEmail.getText().toString());
                                    editor.commit();
                                    Intent invok=new Intent(getApplicationContext(), VerifyEmailActivity.class);
                                    invok.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(invok);
                                    finish();
                                }
                                else
                                {
                                    AlertMessage.showAlertDialog(this, "Alert!", "Email Already Exist.", false);
                                }
                            }*/
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure() {

                    }
                });

            }
        }else{
            Toast tost = Toast.makeText(this, "No internet!",Toast.LENGTH_SHORT);
            tost.setGravity(Gravity.CENTER,0, 0);
            tost.show();
        }
    }
    public void gotoTermsServiceSignup(View v)
    {
    	Intent invok=new Intent(getApplicationContext(), TermsServiceActivity.class);
    	invok.setAction(Constants.Action.ACTION_START_ACTIVITY);
		startActivity(invok);
		overridePendingTransition(R.anim.helpscreen_in_to_out, R.anim.helpscreen_out_to_in);
    }
    
    public void gotoPrivacyPolicySignup(View v)
    {
    	Intent invok=new Intent(getApplicationContext(), PrivacyPolicyActivity.class);
    	invok.setAction(Constants.Action.ACTION_START_ACTIVITY);
		startActivity(invok);
		overridePendingTransition(R.anim.helpscreen_in_to_out, R.anim.helpscreen_out_to_in);
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
