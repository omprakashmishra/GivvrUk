package com.elgroup.givvruk;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.facebook.FacebookSdk;
import com.facebook.share.widget.LikeView;
import com.flurry.android.FlurryAgent;
import com.tech.givvruk.utils.Constants;
import com.tech.givvruk.utils.UserFunctions;

public class LikeVideoActivity extends Activity{

	String video_id;
	LikeView likeView;
	String object_id,color;
	SharedPreferences preferences;
	SharedPreferences.Editor   editor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.like_dialog);
		FacebookSdk.sdkInitialize(this);
		video_id = getIntent().getStringExtra("VIDEO_ID");
		object_id = getIntent().getStringExtra("LIKE_OBJECT_ID");
		color = getIntent().getStringExtra("COLOR");
		likeView = (LikeView) findViewById(R.id.likeViewDialog);
		if(!object_id.contains("http"))
		{
			object_id = "http://"+object_id;
		}
		
		preferences = MyApplication.preference;
		likeView.setObjectIdAndType(object_id, LikeView.ObjectType.OPEN_GRAPH);
		likeView.setLikeViewStyle(LikeView.Style.BUTTON);
	}
	public void likeDone(View v)
	{
		finish();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		    super.onActivityResult(requestCode, resultCode, data);
		    editor = preferences.edit();
		 /*   LikeView.handleOnActivityResult(this, requestCode, resultCode, data);
		    LikeActionController.getControllerForObjectId(this, object_id, new CreationCallback() {

				@Override
				public void onComplete(LikeActionController likeActionController) {
					if (likeActionController.isObjectLiked()) {
						sendClicknLikeEvents(video_id, "fb_like");
						FragmentVideos.imageLike.setImageDrawable(FragmentVideos.mDrawable);
						FragmentVideos.textviewLike.setTextColor(Color.parseColor(color));
					} else {
						sendClicknLikeEvents(video_id, "fb_like");
						FragmentVideos.imageLike.setImageDrawable(FragmentVideos.drawableNotLiked);
						FragmentVideos.textviewLike.setTextColor(Color.parseColor("#2f2f2f"));
					}

				}
			});*/
	  }
	  public void sendClicknLikeEvents(String vId,String event)
		{
			UserFunctions userFunction = new UserFunctions(this);
			userFunction.sendVideoEvents(vId, event);  	
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
