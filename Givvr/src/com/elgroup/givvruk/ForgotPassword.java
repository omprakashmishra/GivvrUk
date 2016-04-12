package com.elgroup.givvruk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.tech.givvruk.utils.AlertMessage;
import com.tech.givvruk.utils.Constants;
import com.tech.givvruk.utils.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPassword extends Activity{

	EditText codeEditText,passwordEdit,rePasswordEdit;
	UserFunctions userFunction;
    TextView textMessage;
	Button buttonSubmit;
	String email;
    boolean check=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgot_password);
		codeEditText=(EditText)findViewById(R.id.code_edittext);
        textMessage=(TextView)findViewById(R.id.textMessage);
		passwordEdit=(EditText)findViewById(R.id.enterPassword_edittext);
		rePasswordEdit=(EditText)findViewById(R.id.againPassword_edittext);
		buttonSubmit = (Button)findViewById(R.id.verify_submit);
		userFunction = new UserFunctions(this);

        Intent invok = getIntent();
		email = invok.getStringExtra("email");
	}

	public void cancelButtonPwd(View v)
	{
        Intent forgetpass=new Intent(this,SigninActivity.class);
        if(check){
            //reset password
            forgetpass.putExtra("activity","SignIn");
        }else{
          //forget password
            forgetpass.putExtra("activity","ForgetPass");
        }
        startActivity(forgetpass);
		finish();
	}
	public void verifyButton(View v)
	{
		if(buttonSubmit.getText().equals("VERIFY"))
		{
		JSONObject json = userFunction.verifyCode(email,codeEditText.getText().toString());
		try {
			String success = json.getString("success");
			if(success.contentEquals("1"))
			{
				codeEditText.setVisibility(View.GONE);
                textMessage.setVisibility(View.GONE);
                textMessage.setVisibility(View.GONE);
				passwordEdit.setVisibility(View.VISIBLE);
				rePasswordEdit.setVisibility(View.VISIBLE);
				buttonSubmit.setText("RESET");
                check=true;
			}else{
               //****
                AlertMessage.showAlertDialog(this, "Alert!", "Please enter correct verification code", false);
                return;
            }
		} catch (JSONException e) {
			e.printStackTrace();
		}
		}
		else if(buttonSubmit.getText().equals("RESET"))
		{

            if(passwordEdit.getText().toString().isEmpty() || rePasswordEdit.getText().toString().isEmpty() ){
                AlertMessage.showAlertDialog(this, "Alert!", "Please enter password", false);
            }else if(passwordEdit.getText().toString().equals(rePasswordEdit.getText().toString())){
				String password = passwordEdit.getText().toString();
				JSONObject json = userFunction.resetPassword(email,password);
				try {
					if(json.getString("success").contentEquals("1"))
					{
                        Toast t = Toast.makeText(this, "Password Successfully Changed ", Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.CENTER, 0, 0);
                        t.show();

					Intent invok=new Intent(getApplicationContext(), SigninActivity.class);
					invok.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(invok);
					finish();
					overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else{
				/*Toast t = Toast.makeText(this, "Password didn't Match!", 100);
				t.setGravity(Gravity.CENTER, 0, 0);
				t.show();*/
                AlertMessage.showAlertDialog(this, "Alert!", "Password didn't Match!", false);
			}
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
