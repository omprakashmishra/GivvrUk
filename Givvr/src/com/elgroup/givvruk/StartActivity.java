package com.elgroup.givvruk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.tech.givvruk.settings.PrivacyPolicyActivity;
import com.tech.givvruk.settings.TermsServiceActivity;
import com.tech.givvruk.utils.CallbackInterface;
import com.tech.givvruk.utils.ConnectionDetector;
import com.tech.givvruk.utils.Constants;
import com.tech.givvruk.utils.GlobalVolley_cl;
import com.tech.givvruk.utils.UrlList;
import com.tech.givvruk.utils.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class StartActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener, OnClickListener {

    private static final int RC_SIGN_IN = 0;
    private static GoogleApiClient mGoogleApiClient;
    String profileStatus,ACCESS_TOKEN;
    private SharedPreferences preference;
    private ConnectionDetector cd;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private Button btnSignIn;
    private ConnectionResult mConnectionResult;
    private CallbackManager callbackManager;
    /*private Session.StatusCallback statusCallback =
            new SessionStatusCallback();*/

    public static void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            //Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            //mGoogleApiClient.disconnect();
            //mGoogleApiClient.connect();
            Games.signOut(mGoogleApiClient);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);

        setContentView(R.layout.start);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        cd = new ConnectionDetector(this);
        preference = MyApplication.preference;

        //*****
        //createKeyHash();

        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) findViewById(R.id.loginButtonFacebook);
        loginButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        loginButton.setBackgroundResource(R.drawable.button_fb_selector);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_location", "user_friends"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getMyInformation(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(),"Facebook login was canceled",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(getApplicationContext(),"Facebook login failed"+ e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        btnSignIn = (Button) findViewById(R.id.loginButtonGoogle);
        btnSignIn.setOnClickListener(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this).addOnConnectionFailedListener(this)
                .addApi(Plus.API, new Plus.PlusOptions.Builder().build()) // note the options
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
        //signOutFromGplus();
       /* Session session = Session.getActiveSession();
        if (session != null) {
            session.closeAndClearTokenInformation();
        }
        loginButton.setOnErrorListener(new LoginButton.OnErrorListener() {
            @Override
            public void onError(FacebookException error) {

            }
        });*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        //mGoogleApiClient.connect();
        FlurryAgent.onStartSession(this, Constants.FLURRY_ID);
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        FlurryAgent.onEndSession(this);
    }

    private void getMyInformation(AccessToken accessToken) {
        //dialog.show();
        final String act=accessToken.toString();
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {

            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                JSONObject obj = object;
                try {

                    System.out.println("jobj " + obj);
                    final String userId = obj.optString("id");
                    final String name = obj.optString("name");
                    final String email = obj.optString("email");
                    final String gender = obj.optString("gender");


                    // final String dob = obj.getString("birthday");

                    if (!email.isEmpty()) {
                        // String[] convertDob = dob.split("/");
                        // final String myDob = convertDob[2] + "/" + convertDob[0] + "/" + convertDob[1];

                        String currentLocation = "";
                        try {
                            currentLocation = obj.optString("location");
                        } catch (Exception e) {
                            currentLocation = "";
                        }
                        //******
                        System.out.println("act------->"+String.valueOf(act));

                        SharedPreferences.Editor e = preference.edit();
                        e.putString("accesstoken", userId);
                        e.commit();

                       // new facebookSignUpTask().execute(email, name, gender, "", currentLocation, userId);
                        facebookSignUpTask(email, name, gender, "", currentLocation, userId);
                    }else{
                        disconnectFromFacebook();
                        // Toast.makeText(getApplicationContext(),"---------------->",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
               /* facebookId = MyJsonParser.getString(object.toString(), "id");
                email = MyJsonParser.getString(object.toString(), "email");
                gender = MyJsonParser.getString(object.toString(), "gender");
                firstName = MyJsonParser.getString(object.toString(), "first_name");
                lastName = MyJsonParser.getString(object.toString(), "last_name");

                next();*/
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender, birthday,location");
        request.setParameters(parameters);
        request.executeAsync();


        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        int count = 0;
                        String friendListArray = "";
                        try {

                            count = Integer.parseInt(response.getJSONObject().getJSONObject("summary").getString("total_count"));
                            friendListArray = response.getJSONObject().getJSONArray("data").toString();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            count = 0;
                        }

                        SharedPreferences.Editor e = preference.edit();
                        e.putBoolean("is_facebook_loggedin", true);
                        e.putInt("pref_facebook_friends_count", count);
                        e.putString("facebook_friends_array", friendListArray);
                        e.commit();
                        // response.getRawResponse();
                    }
                }
        ).executeAsync();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (cd.isConnectingToInternet()) {
            if (mSignInClicked) {
                if (requestCode == RC_SIGN_IN) {
                    if (resultCode != RESULT_OK) {
                        mSignInClicked = false;
                    }

                    mIntentInProgress = false;

                    if (!mGoogleApiClient.isConnecting()) {
                        mGoogleApiClient.connect();
                    }
                }
            } else {
                //Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
                callbackManager.onActivityResult(requestCode, resultCode, data);
                // loginToFacebook();
            }
        } else {
            Toast tost = Toast.makeText(this, "No internet!", Toast.LENGTH_SHORT);
            tost.setGravity(Gravity.CENTER, 0, 0);
            tost.show();
        }
    }

    //*********
    public void disconnectFromFacebook() {

        //------------------------------------
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm");
        builder.setMessage("Due to facebook privacy we can't get your email would u like continue Sign up");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                dialog.dismiss();
            }

        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
                //-------------------------------------------------Logout
                if (AccessToken.getCurrentAccessToken() == null) {
                    return; // already logged out
                }

                new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                        .Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {

                        LoginManager.getInstance().logOut();

                    }
                }).executeAsync();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
        //------------------------------------
    }

    public void gotoTermsServiceStart(View v) {
        Intent invok = new Intent(getApplicationContext(), TermsServiceActivity.class);
        invok.setAction(Constants.Action.ACTION_START_ACTIVITY);
        startActivity(invok);
        overridePendingTransition(R.anim.helpscreen_in_to_out, R.anim.helpscreen_out_to_in);
    }

    public void gotoPrivacyPolicyStart(View v) {
        Intent invok = new Intent(getApplicationContext(), PrivacyPolicyActivity.class);
        invok.setAction(Constants.Action.ACTION_START_ACTIVITY);
        startActivity(invok);
        overridePendingTransition(R.anim.helpscreen_in_to_out, R.anim.helpscreen_out_to_in);
    }

    public void gotoSignin(View v) {
        Intent invok = new Intent(getApplicationContext(), SigninActivity.class);
        invok.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(invok);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

    }

    public void gotoSignup(View v) {
        Intent invok = new Intent(getApplicationContext(), SignupActivity.class);
        invok.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(invok);
        finish();
        overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
    }

    public void loginToFacebook() {
        /*final Session session = Session.getActiveSession();
        if (session.isOpened()) {
            Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        final String userId = user.getId();
                        final String name = user.getName();
                        final String email = (String) response.getGraphObject().getProperty("email");
                        final String dob = user.getBirthday();
                        if (dob != null) {
                            String[] convertDob = dob.split("/");
                            final String myDob = convertDob[2] + "/" + convertDob[0] + "/" + convertDob[1];
                            final String gender = (String) response.getGraphObject().getProperty("gender");
                            final String currentLocation = (String) user.getLocation().asMap().get("name");
                            String[] City = new String[]{"", ""};
                            if (currentLocation.contains(",")) {
                                City = currentLocation.split(",");
                            } else {
                                City[0] = currentLocation;
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });
                            new facebookSignUpTask().execute(email, name, gender, myDob, City[0], userId);
                        }
                    }
                }
            }).executeAsync();
        }*/
    }

    @Override
    public void onConnected(Bundle arg0) {
        // TODO Auto-generated method stub
        mSignInClicked = false;
        getProfileInformation();
    }

    private void getProfileInformation() {
        SharedPreferences.Editor editor = preference.edit();
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                Log.i("Mydata", personPhotoUrl);
                int gender;
                if (currentPerson.hasGender()) {
                    gender = currentPerson.getGender();
                } else {
                    gender = 0;
                }
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                editor.putString("user_email", email);
                editor.putString("google_login", "yes");
                editor.putString("person_google_photo_url", personPhotoUrl);
                UserFunctions user = new UserFunctions(this);
                JSONObject json = user.googleLogin(email, "1");
                Log.i("Mydata", "" + json);
                String profileStatus = json.getString("profile");
                if (profileStatus.contentEquals("1")) {
                    editor.putString("profile_status", "created");
                    editor.apply();
                    editor.commit();
                    Intent intent = new Intent(this, Home.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                } else {
                    MyApplication.FLAG_GOOGLE_LOGIN = true;
                    Intent intent = new Intent(this, CreateProfile.class);
                    intent.putExtra(Constants.Extra.GOOGLE_LOGIN_USER_NAME, personName);
                    intent.putExtra(Constants.Extra.GOOGLE_LOGIN_USER_EMAIL, email);
                    intent.putExtra(Constants.Extra.GOOGLE_LOGIN_USER_GENDER, gender);
                    intent.putExtra(Constants.Extra.GOOGLE_LOGIN_USER_PROFILE_PIC, personPhotoUrl);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                }
            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        // TODO Auto-generated method stub
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }
        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    public void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            //Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
            //resolveSignInError();
            mGoogleApiClient.connect();
        }
    }

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginButtonGoogle:
                // Signin button clicked
                signInWithGplus();
                break;
        }
    }

    public void createKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.elgroup.givvruk", PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

public void facebookSignUpTask (String...  params){
    final SharedPreferences.Editor editor = preference.edit();
    String facebook = "true", email = params[0], name = params[1], gender = params[2], myDob = params[3], currentLocation = params[4], userid = params[5];
    UserFunctions userFunction = new UserFunctions(getApplicationContext());

    editor.putString("gender", gender);
    editor.putString("user_email", email);
    editor.putString("facebook_id", userid);

           new GlobalVolley_cl(this, UrlList.FBLOGIN,userFunction.VfacebookLogin(email, facebook, name, myDob, gender, currentLocation, userid), new CallbackInterface() {
               @Override
               public void onSuccess(String response) {
                    JSONObject result=null;
                       try {
                           result=new JSONObject(response);
                           //---------------------------------------------
                           profileStatus = result.getString("profile");
                           ACCESS_TOKEN= result.getString("accesstoken");
                           System.out.println("profilestatus "+profileStatus);

                       } catch (JSONException e) {
                           e.printStackTrace();
                       }catch (Exception e){
                           e.printStackTrace();
                       }
                   if (profileStatus.contentEquals("1")) {
                       Intent invok = new Intent(getApplicationContext(), Home.class);
                       editor.putString("profile_status", "created");
                       editor.putString("accesstoken", ACCESS_TOKEN);
                       editor.apply();
                       invok.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                       startActivity(invok);
                       finish();
                   }
               }
               @Override
               public void onFailure() {

               }

           });
}
//---------------------------------------------

    class facebookSignUpTask extends AsyncTask<String, String, JSONObject> {
        SharedPreferences.Editor editor = preference.edit();
        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(StartActivity.this);
            mProgressDialog.setMessage("LOADING...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String facebook = "true", email = params[0], name = params[1], gender = params[2], myDob = params[3], currentLocation = params[4], userid = params[5];
            UserFunctions userFunction = new UserFunctions(getApplicationContext());

            editor.putString("gender", gender);
            editor.putString("user_email", email);
            editor.putString("facebook_id", userid);
            JSONObject json = userFunction.facebookLogin(email, facebook, name, myDob, gender, currentLocation,userid);
            System.out.println("fbjson "+json);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            try {
                profileStatus = result.getString("profile");
                ACCESS_TOKEN= result.getString("accesstoken");
                System.out.println("profilestatus "+profileStatus);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (profileStatus.contentEquals("1")) {
                Intent invok = new Intent(getApplicationContext(), Home.class);
                editor.putString("profile_status", "created");
                editor.putString("accesstoken", ACCESS_TOKEN);
                editor.apply();
                invok.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(invok);
                finish();
            }
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            //  Session.openActiveSession(StartActivity.this, true, statusCallback);
        }

    }

    /*private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {

            if (session.getState().isOpened()) {
                new Request(
                        session,
                        "/me/friends",
                        null,
                        HttpMethod.GET,
                        new Request.Callback() {
                            public void onCompleted(Response response) {

                                GraphObject json = response.getGraphObject();
                                JSONObject json1 = json.getInnerJSONObject();
                                try {
                                    JSONObject data = json1.getJSONObject("summary");
                                    int count = Integer.parseInt(data.getString("total_count"));

                                    Editor e = preference.edit();
                                    e.putBoolean("is_facebook_loggedin", true);
                                    e.putInt("pref_facebook_friends_count", count);
                                    e.commit();

                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                            }
                        }
                ).executeAsync();
            }
        }
    }*/


}
