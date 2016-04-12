package com.elgroup.givvruk;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.tech.givvruk.utils.AlertMessage;
import com.tech.givvruk.utils.CallbackInterface;
import com.tech.givvruk.utils.ConnectionDetector;
import com.tech.givvruk.utils.Constants;
import com.tech.givvruk.utils.GlobalVolley_cl;
import com.tech.givvruk.utils.UrlList;
import com.tech.givvruk.utils.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

public class SigninActivity extends Activity {

	SharedPreferences preference;
	String PROFILE_TAG,LOGIN_TAG,ACCESS_TOKEN,SUCCESS_TAG,alert_msg;
	EditText txtEmail,txtPassword;
	TextView txtForgotPwd;
	Button signInSubmit;
	Boolean flagForgotPassword=false;
	private ConnectionDetector cd;
	UserFunctions userFunction ;
    int a=0;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);
        /*if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}*/
        cd = new ConnectionDetector(this);
        preference = MyApplication.preference;
        txtEmail=(EditText)findViewById(R.id.email_edittext);
		txtPassword=(EditText)findViewById(R.id.password_edittext);
		txtForgotPwd=(TextView)findViewById(R.id.forgot_password_selector);
		signInSubmit=(Button)findViewById(R.id.signin_submit);
		userFunction = new UserFunctions(this);


        //****
        //-----from activity-----ForgotPassword...
        alert_msg="Please enter correct email & password";
        Bundle bundle=getIntent().getExtras();
        if (bundle !=null){
            if(bundle.getString("activity").contains("ForgetPass")){
                forgotPassFn();
            }else{

            }
        }
		
	}
    public void forgotPassword(View v)
    {
        forgotPassFn();
    }
    public void forgotPassFn(){
        txtPassword.setText("pass");
        txtPassword.setVisibility(View.GONE);
        txtForgotPwd.setVisibility(View.GONE);
        signInSubmit.setText("Continue");
        flagForgotPassword=true;
        alert_msg="Please enter valid email";
    }
    public void cancelButton(View v)
    {
        if(flagForgotPassword){
            txtPassword.setText("");
            txtPassword.setHint("Password");
            txtPassword.setVisibility(View.VISIBLE);
            txtForgotPwd.setVisibility(View.VISIBLE);
            signInSubmit.setText("SIGN IN");
            flagForgotPassword=false;
            alert_msg="Please enter correct email & password";
        }else{
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
       // finish();
        }
    }
    public void signinButton(View v)
    {
    	if(cd.isConnectingToInternet())
    	{
    		if(txtEmail.getText().toString().isEmpty()||txtPassword.getText().toString().isEmpty())
    		{
    			AlertMessage.showAlertDialog(this, "Alert!", alert_msg, false);
    		}
    		else
    		{

                if(flagForgotPassword){
                    forgotPassword();
                    /*if(SUCCESS_TAG.contentEquals("0")){
                        AlertMessage.showAlertDialog(this, "Alert!", alert_msg, false);
                    }else{
                        Intent invok=new Intent(getApplicationContext(), ForgotPassword.class);
                        invok.putExtra("email", txtEmail.getText().toString());
                        invok.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(invok);
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                    }*/
                }else{
                    signin();
                }
    	    }
    	}
    	else
    	{
    		/*Toast tost = Toast.makeText(this, "No internet!",500);
			tost.setGravity(Gravity.CENTER,0, 0);
			tost.show();*/
    	}
    }

    private void signin() {
        new GlobalVolley_cl(this, UrlList.LOGIN,userFunction.VloginMe(txtEmail.getText().toString(), txtPassword.getText().toString()),new CallbackInterface() {
            @Override
            public void onSuccess(String response) {
                JSONObject json = null;
                    try {
                        json = new JSONObject(response);
                        Log.i("Login---->", json.toString());
                        PROFILE_TAG=json.optString("profile");
                        LOGIN_TAG=json.optString("login");
                        SUCCESS_TAG=json.optString("success");
                        //********
                        ACCESS_TOKEN = json.optString("accesstoken");

                        SharedPreferences.Editor   editor = preference.edit();
                        // editor.putString("User_access_json",json.toString());
                        editor.apply();

                    }catch (Exception e) {
                        e.printStackTrace();
                    }


                if(LOGIN_TAG.contentEquals("0"))
                    {
                        if(json.has("active"))
                        {
                            SharedPreferences.Editor   editor = preference.edit();
                            editor.putString("VERIFY_EMAIL",txtEmail.getText().toString());
                            Intent invok=new Intent(getApplicationContext(), VerifyEmailActivity.class);
                            invok.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(invok);
                            finish();
                        }
                        else
                        {
                            AlertMessage.showAlertDialog(SigninActivity.this, "Alert!", "Wrong email or password ", false);
                        }
                    }
                    if(LOGIN_TAG.contentEquals("1"))
                    {
                        SharedPreferences.Editor   editor = preference.edit();
                        editor.putString("user_email",txtEmail.getText().toString());
                        try {
                            editor.putString("user_id",json.getString("user_id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        editor.apply();
                    }
                    if(LOGIN_TAG.contentEquals("1")&&PROFILE_TAG.contentEquals("0")) {
                        Intent invok=new Intent(getApplicationContext(), CreateProfile.class);
                        invok.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(invok);
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                    }
                    else if(LOGIN_TAG.contentEquals("1")&&PROFILE_TAG.contentEquals("1")) {
                        SharedPreferences.Editor   editor = preference.edit();
                        editor.putString("profile_status","created");
                        if(ACCESS_TOKEN.isEmpty()){
                            Toast.makeText(SigninActivity.this, "Empty accesstoken", Toast.LENGTH_SHORT).show();
                            return;
                        }else{
                            editor.putString("accesstoken", ACCESS_TOKEN);
                        }
                        editor.apply();
                        editor.commit();

                        Intent invok=new Intent(getApplicationContext(), Home.class);
                        invok.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(invok);
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                    }
            }

            @Override
            public void onFailure() {

            }
        });
    }


    private void forgotPassword() {
        if (txtEmail.getText().toString() == "") {
            AlertMessage.showAlertDialog(this, "Alert!", "Please enter valid email", false);
        } else {
           /* JSONObject json = userFunction.forgotPassword(txtEmail.getText().toString());
            SUCCESS_TAG=json.optString("success");
            */
            //userFunction.VforgotPassword(txtEmail.getText().toString());


            new GlobalVolley_cl(this, UrlList.FORGOT_PASSWORD,userFunction.VforgotPassword(txtEmail.getText().toString()),new CallbackInterface() {
                @Override
                public void onSuccess(String response) {

                    JSONObject json = null;
                    try {
                        json = new JSONObject(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    SUCCESS_TAG=json.optString("success");
                    if(SUCCESS_TAG.equals("0")){
                        AlertMessage.showAlertDialog(SigninActivity.this, "Alert!", alert_msg, false);
                    }else{
                        Intent invok=new Intent(getApplicationContext(), ForgotPassword.class);
                        invok.putExtra("email", txtEmail.getText().toString());
                        invok.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(invok);
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                    }
                }
                @Override
                public void onFailure() {

                    AlertMessage.showAlertDialog(SigninActivity.this, "Alert!", alert_msg, false);
                }
            });
        }
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
