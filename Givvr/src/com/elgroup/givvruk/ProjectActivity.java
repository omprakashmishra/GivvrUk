package com.elgroup.givvruk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.tech.givvruk.utils.ConnectionDetector;
import com.tech.givvruk.utils.Constants;
import com.tech.givvruk.utils.UrlList;
import com.tech.givvruk.utils.UserFunctions;
import com.tech.givvruk.utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

public class ProjectActivity extends FragmentActivity {

    static Context ctx;
    static TextView txtGoalProjectVideos;
    static String LARGE_IMAGE_URL, IMAGE_URL, projectColor, ProjectID, givvrTotal, yourTotal, PROFILE_TAG, projectDescription, projectGoal, projectRaised, projectName, projectTag, profileImageUrl, projectLength, projectCreatedAt;
    DisplayImageOptions options;
    String videoDomainUrl = UrlList.VIDEOLINK;
    String videoFormat = ".mp4";
    ConnectionDetector cd;
    Animation animFadeOut;
    FrameLayout fragmentProject;
    FragmentManager fragmentManager;
    int daysLeft;
    MediaPlayer mediaPlayer;
    TextView textTabVideo, textTabProfile;

    public static void updateRaised() {
        UserFunctions userFunction = new UserFunctions(ctx);

        JSONObject jsonforProject = userFunction.getProject(ProjectID);
        try {
            txtGoalProjectVideos.setText("£" + jsonforProject.getString("raised"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_page);

        //Settings.sdkInitialize(this);
        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(10))
                .considerExifParams(true)
                .build();
        animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);

        ctx = this;
        Intent intent = getIntent();
        ImageView imageSmall = (ImageView) findViewById(R.id.imageViewSmall);
        TextView textProjectName = (TextView) findViewById(R.id.textProjectName);
        TextView textProjectTag = (TextView) findViewById(R.id.textProjectTag);
        txtGoalProjectVideos = (TextView) findViewById(R.id.txtGoalProjectVideos);
        textTabVideo = (TextView) findViewById(R.id.textTabVideo);
        textTabProfile = (TextView) findViewById(R.id.textTabProfile);
        mediaPlayer = MyApplication.player;

        fragmentProject = (FrameLayout) findViewById(R.id.fragmentProject);
        fragmentManager = getSupportFragmentManager();

        projectName = intent.getStringExtra("project_title");
        projectTag = intent.getStringExtra("project_tag");
        projectRaised = intent.getStringExtra("project_raised");
        projectGoal = intent.getStringExtra("project_goal");
        projectDescription = intent.getStringExtra("project_decription");
        LARGE_IMAGE_URL = intent.getStringExtra("project_large_image");
        IMAGE_URL = intent.getStringExtra("project_small_image");
        ProjectID = intent.getStringExtra("project_id");
        projectLength = intent.getStringExtra("project_length");


        projectColor = intent.getStringExtra("project_color");
        if (!projectColor.contains("#"))
            projectColor = "#" + intent.getStringExtra("project_color");

        textProjectName.setText(intent.getStringExtra("project_title"));
        textProjectTag.setText(intent.getStringExtra("project_tag"));
        txtGoalProjectVideos.setText("£" + intent.getStringExtra("project_raised"));
        String image_Url = intent.getStringExtra("project_small_image");
        Utilities.downloadImage(image_Url, imageSmall, options, null);
        cd = new ConnectionDetector(this);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        FragmentVideos fragmentVideo = new FragmentVideos(this, ProjectID, projectColor, projectName, projectTag, projectDescription, LARGE_IMAGE_URL);
        fragmentTransaction.add(R.id.fragmentProject, fragmentVideo);
        fragmentTransaction.commit();
        textTabVideo.setTextColor(Color.parseColor(projectColor));
        textProjectName.setTextColor(Color.parseColor(projectColor));
    }

    public void gotofragmentVideo(View v) {
        if("0".equals(ifany_empty(ProjectID, projectColor, projectName, projectTag, projectDescription, LARGE_IMAGE_URL))){
            return;
        }
        MyApplication.FLAG_READ_FRIENDS = false;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        FragmentVideos fragmentVideo = new FragmentVideos(this, ProjectID, projectColor, projectName, projectTag, projectDescription, LARGE_IMAGE_URL);
        fragmentTransaction.replace(R.id.fragmentProject, fragmentVideo);
        fragmentTransaction.commit();
        textTabProfile.setTextColor(Color.GRAY);
        textTabVideo.setTextColor(Color.parseColor(projectColor));
    }

    public void gotoOneProject(View v) {
        if ("0".equals(ifany_empty(projectName, projectTag, projectRaised, projectGoal, projectDescription, LARGE_IMAGE_URL, ProjectID, projectLength, projectColor))) {
            return;
        }

        MyApplication.FLAG_READ_FRIENDS = true;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        FragmentProjectProfile fragmentVideo = new FragmentProjectProfile(projectName, projectTag, projectRaised, projectGoal, projectDescription, LARGE_IMAGE_URL, ProjectID, projectLength, projectColor);
        fragmentTransaction.replace(R.id.fragmentProject, fragmentVideo);
        fragmentTransaction.commit();
        textTabProfile.setTextColor(Color.parseColor(projectColor));
        textTabVideo.setTextColor(Color.GRAY);
    }

    public String ifany_empty(String... value){
        String check="1";
        for(int k = 0; k < value.length; k++){
            // something
            System.out.println("--------------?"+k);
            if(value[k]==null){
                check="0";
                return check;
            }
        }
        return check;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, Constants.FLURRY_ID);

    }

    public void layoutHome(View v) {
    }

    public void goBackVideos(View v) {
        finish();
        overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
    }

    @Override
    public void onBackPressed() {

        if (FragmentVideos.layoutFullscreen.getVisibility() == View.VISIBLE) {
            FragmentVideos.layoutFullscreen.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
            finish();
            overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //  LikeView.handleOnActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyApplication.FLAG_READ_FRIENDS = false;
        FlurryAgent.onEndSession(this);
    }
}
