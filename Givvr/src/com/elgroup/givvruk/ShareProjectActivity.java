package com.elgroup.givvruk;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.AppInviteDialog;
import com.facebook.share.widget.ShareDialog;
import com.flurry.android.FlurryAgent;
import com.tech.givvruk.utils.Constants;
import com.tech.givvruk.utils.UserFunctions;

import java.util.List;

public class ShareProjectActivity extends Activity {
	
	Context ctx;
	String name,caption,description,link,pictureUrl;
	private CallbackManager callbackManager;
	private AppInviteDialog mInvititeDialog;
	private ShareDialog shareDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_project);
		ctx = this;

		FacebookSdk.sdkInitialize(this);
		callbackManager = CallbackManager.Factory.create();
		shareDialog = new ShareDialog(this);

		Intent intent = getIntent();
		name = intent.getStringExtra(Constants.Extra.POST_LINK_NAME);
		caption = intent.getStringExtra(Constants.Extra.POST_LINK_CAPTION);
		description = intent.getStringExtra(Constants.Extra.POST_LINK_DESCRIPTION);
		pictureUrl = intent.getStringExtra(Constants.Extra.POST_PICTURE_URL);
		link = "http://www.givvr.com/";
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void shareDone(View v)
	{
		finish();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}
	
	public void shareOnFb(View v)
	{
		Bundle params = new Bundle();
	    params.putString("name", name);
	    params.putString("caption", caption);
	    params.putString("description", description);
	    params.putString("link", link);
	    params.putString("picture", pictureUrl);


		if (new UserFunctions(ctx).isLogedIn()) {
			ShareLinkContent content = new ShareLinkContent.Builder()
					.setContentDescription(description)
					.setContentTitle(caption)
					.setImageUrl(Uri.parse(pictureUrl))
					.setContentUrl(Uri.parse(link)).build();

			shareDialog.show(content);

		}

	    /*Session session =Session.getActiveSession();
		if(session!=null)
		{
	    if(session.isOpened())
	    {
	    WebDialog feedDialog = (
	        new WebDialog.FeedDialogBuilder(this,
	            session,
	            params))
	        .setOnCompleteListener(new OnCompleteListener() {

	            @Override
	            public void onComplete(Bundle values,
	                FacebookException error) {
	                if (error == null) {
	                    final String postId = values.getString("post_id");
	                    if (postId != null) {
	                        Toast.makeText(ctx,"Project Successfully Shared",Toast.LENGTH_SHORT).show();
	                        
	                    } else {
	                        Toast.makeText(ctx, 
	                            "Publish cancelled", 
	                            Toast.LENGTH_SHORT).show();
	                    }
	                } else if (error instanceof FacebookOperationCanceledException) {
	                    Toast.makeText(ctx, 
	                        "Publish cancelled", 
	                        Toast.LENGTH_SHORT).show();
	                } else {
	                    Toast.makeText(ctx, 
	                        "Error posting project", 
	                        Toast.LENGTH_SHORT).show();
	                }
	            }

	        })
	        .build();
	    feedDialog.show();
	    }
		}*/
		else
		{
			String urlToShare = link;
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_TEXT, urlToShare);
			boolean facebookAppFound = false;
			List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
			for (ResolveInfo info : matches) {
			    if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana")) {
			        intent.setPackage(info.activityInfo.packageName);
			        facebookAppFound = true;
			        break;
			    }
			}
			if (!facebookAppFound) {
			    String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + urlToShare;
			    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
			}
			startActivity(intent);
		}
		
	}
	public void shareTwitter(View v)
	{
		try {
	        Intent tweetIntent = new Intent(Intent.ACTION_SEND);
	        tweetIntent.setType("text/plain");
	        tweetIntent.putExtra(Intent.EXTRA_TEXT, Constants.EMAIL_TEXT_BODY_2+name+" on GIVVR."+'\n'+"Join me and download the GIVVR app now. Thanks!"+'\n'+Constants.EMAIL_TEXT_BODY_5);
	        PackageManager pm = getPackageManager();
	        List<ResolveInfo> lract = pm.queryIntentActivities(tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);
	        boolean resolved = false;
	        for (ResolveInfo ri : lract) {
	            if (ri.activityInfo.name.contains("twitter")) {
	                tweetIntent.setClassName(ri.activityInfo.packageName,
	                        ri.activityInfo.name);
	                resolved = true;
	                break;
	            }
	        }
	        startActivity(resolved ?
	                tweetIntent :
	                Intent.createChooser(tweetIntent, "Choose one"));
	    } catch (final ActivityNotFoundException e) {
	        Toast.makeText(this, "You don't seem to have twitter installed on this device", Toast.LENGTH_SHORT).show();
	    }
	}
	public void shareEmail(View v)
	{
		Intent email = new Intent(Intent.ACTION_SEND);
		email.putExtra(Intent.EXTRA_EMAIL, new String[] { "abc@gmail.com" });
		email.putExtra(Intent.EXTRA_SUBJECT, "Givvr - Help Donate to Charities by Givvng your time!");
		email.putExtra(Intent.EXTRA_TEXT, "Hi,"+'\n'+Constants.EMAIL_TEXT_BODY_1+'\n'+'\n'+Constants.EMAIL_TEXT_BODY_2+name+Constants.EMAIL_TEXT_BODY_3+name+"."+Constants.EMAIL_TEXT_BODY_4+'\n'+'\n'+Constants.EMAIL_TEXT_BODY_5);
		email.setType("message/rfc822");
		startActivity(email);

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

	private void InitFacebook() {


		callbackManager = CallbackManager.Factory.create();

		mInvititeDialog = new AppInviteDialog(this);
		mInvititeDialog.registerCallback(callbackManager,
				new FacebookCallback<AppInviteDialog.Result>() {

					@Override
					public void onSuccess(AppInviteDialog.Result result) {

					}

					@Override
					public void onCancel() {
						Log.d("Result", "Cancelled");

					}

					@Override
					public void onError(FacebookException exception) {
						Log.d("Result", "Error " + exception.getMessage());

					}
				});

	}
}