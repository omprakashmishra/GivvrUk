package com.elgroup.givvruk;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.flurry.android.FlurryAgent;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.tech.givvruk.utils.ConnectionDetector;
import com.tech.givvruk.utils.Constants;
import com.tech.givvruk.utils.CustomArrayAdapter;
import com.tech.givvruk.utils.Friends;
import com.tech.givvruk.utils.HorizontalListView;
import com.tech.givvruk.utils.OnSwipeTouchListener;
import com.tech.givvruk.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentProjectProfile extends Fragment {

    String projectName, projectTag,
            projectRaised, projectGoal,
            projectDescription, lARGE_IMAGE_URL, projectID, facebookID, projectColor;
    SharedPreferences preference;
    HorizontalListView listView;
    DisplayImageOptions options;
    String daysLeft;
    ImageView imgGivvrUsers;
    ArrayList<Friends> friendsList = new ArrayList<Friends>();
    private ConnectionDetector cd;
    private CallbackManager callbackManager;
    private AppInviteDialog mInvititeDialog;

    public FragmentProjectProfile(){
        super();
    }
    @SuppressLint({"NewApi", "ValidFragment"})
    public FragmentProjectProfile(String projectName, String projectTag,
                                  String projectRaised, String projectGoal,
                                  String projectDescription, String lARGE_IMAGE_URL, String projectID, String daysLeft, String projectColor) {
        super();
        this.projectName = projectName;
        this.projectTag = projectTag;
        this.projectRaised = projectRaised;
        this.projectGoal = projectGoal;
        this.projectDescription = projectDescription;
        this.lARGE_IMAGE_URL = lARGE_IMAGE_URL;
        this.projectID = projectID;
        this.daysLeft = daysLeft;
        this.projectColor = projectColor;

    }

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.one_project_page, container, false);

        InitFacebook();


        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        cd = new ConnectionDetector(getActivity());
        preference = MyApplication.preference;
        facebookID = preference.getString("facebook_id", "");


        SeekBar projectProgress = (SeekBar) view.findViewById(R.id.projectProgress);
        TextView pDescription = (TextView) view.findViewById(R.id.projectDesc);
        TextView textProjectTagHeading = (TextView) view.findViewById(R.id.textProjectTagHeading);
        TextView textGoalProjectProfile = (TextView) view.findViewById(R.id.textGoalProjectProfile);
        TextView textRaisedPercent = (TextView) view.findViewById(R.id.textRaisedPercent);
        TextView textDays = (TextView) view.findViewById(R.id.textDays);
        imgGivvrUsers = (ImageView) view.findViewById(R.id.imgGivvrUsers);
        listView = (HorizontalListView) view.findViewById(R.id.listFriends);
        ImageView projectLargeImage = (ImageView) view.findViewById(R.id.projectLargeImage);
        /*Button shareProject = (Button) view.findViewById(R.id.share_project_one);*/
        Button inviteMoreFriends = (Button) view.findViewById(R.id.invite_more_friends);
        LinearLayout layoutSwipe = (LinearLayout) view.findViewById(R.id.layout_swipe);
        //shareProject.setBackgroundDrawable(Utilities.MyButtonLayer(getActivity(), projectColor));
		/*shareProject.setBackground(Utilities.MyButtonLayer(getActivity(), projectColor));*/
        inviteMoreFriends.setBackground(Utilities.MyButtonLayer(getActivity(), projectColor));

        pDescription.setText(projectDescription);
        textProjectTagHeading.setText(projectTag);
        Utilities.downloadImage(lARGE_IMAGE_URL, projectLargeImage, options, null);
        projectProgress.setMax(Integer.parseInt(projectGoal) * 100);
        projectProgress.setProgressDrawable(Utilities.MyProgressLayer(getActivity(), projectColor));
        projectProgress.setProgress(getConvertedValue(Float.parseFloat(projectRaised)));

        textGoalProjectProfile.setText("£" + projectGoal);
        float raisePercent = ((Float.parseFloat(projectRaised) * 100) / Integer.parseInt(projectGoal));
        int d = (int) Math.floor(raisePercent);
        String value = String.valueOf(d);
        textRaisedPercent.setText(value);
        textDays.setText(daysLeft);

        String friendListPref = preference.getString("facebook_friends_array","");

        try {
            JSONArray array = new JSONArray(friendListPref);
            for (int i=0;i<array.length();i++){
                JSONObject jsonFriend = array.getJSONObject(i);
                friendsList.add(new Friends(jsonFriend.getString("name"), jsonFriend.getString("id")));

            }
            CustomArrayAdapter adapter = new CustomArrayAdapter(getActivity(), friendsList);
            listView.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        layoutSwipe.setOnTouchListener(new OnSwipeTouchListener(getActivity(), false));
        if (facebookID.isEmpty()) {
            listView.setVisibility(View.GONE);
            imgGivvrUsers.setVisibility(View.VISIBLE);
        } else {
            if (cd.isConnectingToInternet()) {
                readFriendList();
            }
        }
	   /* shareProject.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				shareProject();
			}
		});*/
        inviteMoreFriends.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestDialog();
            }
        });
        return view;

    }

    private void InitFacebook() {


        callbackManager = CallbackManager.Factory.create();

        mInvititeDialog = new AppInviteDialog(getActivity());
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

    public int getConvertedValue(float floatVal) {
        int intVal = 0;
        intVal = (int) (floatVal * 100);
        return intVal;
    }

    public void sendRequestDialog() {

        Bundle params = new Bundle();
        params.putString("message", "Help Donate to Charities by Givvng your time!");


        if (AppInviteDialog.canShow()) {
            AppInviteContent content = new AppInviteContent.Builder()
                    .setApplinkUrl("https://fb.me/1550078068615694").build();

            AppInviteDialog.show(getActivity(), content);

        }

	  /*  Session session =Session.getActiveSession();
	AccessToken.getCurrentAccessToken();
		if(session!=null)
		{
	    if(session.isOpened())
	    {

	    WebDialog requestsDialog = (
	        new WebDialog.Builder(getActivity(),).RequestsDialogBuilder(getActivity(),
	        		session,
	            params))
	            .setOnCompleteListener(new WebDialog.OnCompleteListener() {

	                public void onComplete(Bundle values,
	                    FacebookException error) {
	                    if (error != null) {
	                        if (error instanceof FacebookOperationCanceledException) {
	                            Toast.makeText(getActivity(), 
	                                "Request cancelled", 
	                                Toast.LENGTH_SHORT).show();
	                        } else {
	                            Toast.makeText(getActivity(), 
	                                "Request cancelled", 
	                                Toast.LENGTH_SHORT).show();
	                        }
	                    } else {
	                        final String requestId = values.getString("request");
	                        if (requestId != null) {
	                            Toast.makeText(getActivity(), 
	                                "Request sent",  
	                                Toast.LENGTH_SHORT).show();
	                        } else {
	                            Toast.makeText(getActivity(), 
	                                "Request cancelled", 
	                                Toast.LENGTH_SHORT).show();
	                        }
	                    }   
	                }

	            })
	            .build();
	    requestsDialog.show();
	    }
		}
		else
		{
			AlertMessage.showAlertDialog(getActivity(), "Login Failed!", "Please login with facebook to invite more friends.", false);
		}*/
    }

    public void shareProject() {
        Intent intent = new Intent(getActivity(), ShareProjectActivity.class);
        intent.putExtra(Constants.Extra.POST_LINK_NAME, projectName);
        intent.putExtra(Constants.Extra.POST_LINK_CAPTION, projectTag);
        intent.putExtra(Constants.Extra.POST_LINK_DESCRIPTION, projectDescription);
        intent.putExtra(Constants.Extra.POST_PICTURE_URL, lARGE_IMAGE_URL);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /*private Session.StatusCallback statusCallback =
    new SessionStatusCallback();
    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            if(session.getState().isOpened()){
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
                                    JSONArray array = json1.getJSONArray("data");
                                    if(array.length()<0)
                                    {
                                        listView.setVisibility(View.GONE);
                                        imgGivvrUsers.setVisibility(View.VISIBLE);
                                    }
                                    else
                                    {
                                        friendsList.clear();
                                    for(int i = 0 ; i<array.length(); i++)
                                    {
                                    JSONObject jsonFriend = array.getJSONObject(i);
                                    friendsList.add(new Friends(jsonFriend.getString("name"), jsonFriend.getString("id")));
                                    }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                 if(MyApplication.FLAG_READ_FRIENDS)
                                 {
                                     CustomArrayAdapter adapter = new CustomArrayAdapter(getActivity(), friendsList);
                                     listView.setAdapter(adapter);
                                 }
                            }
                        }
                    ).executeAsync();
                }
        }
    }*/
    private void readFriendList() {
        //Session.openActiveSession(getActivity(), true, statusCallback);
    }

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(getActivity(), Constants.FLURRY_ID);
    }

    @Override
    public void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(getActivity());
    }
}
