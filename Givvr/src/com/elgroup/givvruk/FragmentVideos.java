package com.elgroup.givvruk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.flurry.android.FlurryAgent;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.tech.givvruk.utils.ConnectionDetector;
import com.tech.givvruk.utils.Constants;
import com.tech.givvruk.utils.ScrollViewExt;
import com.tech.givvruk.utils.ScrollViewListener;
import com.tech.givvruk.utils.UrlList;
import com.tech.givvruk.utils.UserFunctions;
import com.tech.givvruk.utils.Utilities;
import com.tech.givvruk.utils.VideoControllerView;
import com.tech.givvruk.utils.Videos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

@SuppressLint("ValidFragment")
public class FragmentVideos extends Fragment {

    /* Any number for uniquely distinguish your request */
    public static final int WEBVIEW_REQUEST_CODE = 100;
    public static final int INSTA_CODE = 90;
    private static final String PREF_NAME = "sample_twitter_pref";
    private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    private static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
    private static final String PREF_KEY_FACEBOOK_LOGIN = "is_facebook_loggedin";
    private static final String PREF_USER_NAME = "twitter_user_name";
    public static MediaPlayer mediaPlayer;
    public static RelativeLayout layoutFullscreen;
    public static ImageView imageLike;
    public static Drawable mDrawable, drawableNotLiked;
    public static TextView textviewLike;
    private static Twitter twitter;
    private static RequestToken requestToken;
    private static SharedPreferences mSharedPreferences;
    public ArrayList<Videos> videoList = new ArrayList<Videos>();
    public LoginManager manager;
    DisplayImageOptions options;
    ProgressBar progressVideos;
    Context ctx;
    ConnectionDetector cd;
    String videoDomainUrl = UrlList.VIDEOLINK;
    String videoFormat = ".mp4";
    String videoThumbnailUrl = UrlList.VIDEO_THUMBNAIL_LINK;
    String thumbnailFormat = ".jpg";
    int xpos;
    int[] videoCounter;
    String success;
    ListView listVideos;
    String projectColor, projectId, projectName, projectTag, projectDescription, lARGE_IMAGE_URL;
    LinearLayout layoutVideos;
    LayoutInflater inflator;
    ScrollViewExt scrollView;
    boolean FLAG_SHOW_DIALOG = true, flagFullscreen = false;
    ViewTreeObserver vto;
    ImageView noMoreVideos;
    DisplayImageOptions options1;
    int count = 0;
    TextView textFB, textTW, textFBCount, textTWCount;
    View viewFBCount, viewTWCount;
    String cost = "0", shareonFb,amount;
    SharedPreferences preference;
    private ShareDialog shareDialog;
    private String consumerKey = "wjrLCp02VlyQJ3gnf0QHCl7ka";
    private String consumerSecret = "GB2AsKrKuCA0UmlXpsajk1QRm474lHYSm6ob3CMnV0UAdChGTU";
    private String callbackUrl = "oauth://t4jsample";
    private String oAuthVerifier = null;
    private boolean isPreparing = false;
    private CallbackManager callbackManager;
    private int videoViewHeight = -1;
    private boolean mFullScreen = false;

    @SuppressLint("ValidFragment")
    public FragmentVideos(Context ctx, String projectId, String projectColor, String projectName, String projectTag, String projectDescription, String lARGE_IMAGE_URL) {
        this.projectColor = projectColor;
        this.projectId = projectId;
        this.projectName = projectName;
        this.projectTag = projectTag;
        this.projectDescription = projectDescription;
        this.lARGE_IMAGE_URL = lARGE_IMAGE_URL;
        this.ctx = ctx;
        mediaPlayer = MyApplication.player;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getActivity());
        View view = inflater.inflate(R.layout.videos, container, false);
        inflator = getActivity().getLayoutInflater();
        preference = MyApplication.preference;

        shareDialog = new ShareDialog(getActivity());


        //ctx = getActivity();
        cd = new ConnectionDetector(ctx);
        layoutFullscreen = (RelativeLayout) getActivity().findViewById(R.id.layoutFullscreen);
        initTwitterConfigs();

        mSharedPreferences = MyApplication.preference;
        mDrawable = ctx.getResources().getDrawable(R.drawable.like_color);
        drawableNotLiked = ctx.getResources().getDrawable(R.drawable.like);
        mDrawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor(projectColor), Mode.MULTIPLY));
        if (android.os.Build.VERSION.SDK_INT > 8) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        count = mSharedPreferences.getInt("pref_facebook_friends_count", 0);

        options1 = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(170))
                .build();
        progressVideos = (ProgressBar) view.findViewById(R.id.progressVideos);
        scrollView = (ScrollViewExt) view.findViewById(R.id.scrollView1);
        layoutVideos = (LinearLayout) view.findViewById(R.id.layout_videos);
        noMoreVideos = (ImageView) view.findViewById(R.id.noMoreVideos);


        if (cd.isConnectingToInternet()) {
            new GetVideosData().execute();
        } else {
            Toast tost = Toast.makeText(ctx, "No internet!", Toast.LENGTH_SHORT);
            tost.setGravity(Gravity.CENTER, 0, 0);
            tost.show();
        }
        boolean isLoggedIn = mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);

		/*  if already logged in, then hide login layout and show share layout */
        if (!isLoggedIn) {

            Uri uri = ((Activity) ctx).getIntent().getData();

            if (uri != null && uri.toString().startsWith(callbackUrl)) {

                String verifier = uri.getQueryParameter(oAuthVerifier);

                try {

					/* Getting oAuth authentication token */
                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

					/* Getting user id form access token */
                    long userID = accessToken.getUserId();
                    final User user = twitter.showUser(userID);
                    final String username = user.getName();

					/* save updated token */
                    saveTwitterInfo(accessToken);

                } catch (Exception e) {
                    Log.e("Failed to login Twitter", e.getMessage());
                }
            }

        }
        return view;
    }

    private void saveTwitterInfo(AccessToken accessToken) {

        long userID = accessToken.getUserId();

        User user;
        try {
            user = twitter.showUser(userID);
            int count = user.getFollowersCount();
            String username = user.getName();
            float shareCount = Float.parseFloat(cost) * count;
            BigDecimal bdTW = new BigDecimal(Float.toString(shareCount));
            bdTW = bdTW.setScale(2, BigDecimal.ROUND_UP);
            //buttonTw.setBackgroundResource(R.drawable.share_tw_count);
            //buttonTw.setText("Tweet to raise up to");
            viewTWCount.setVisibility(View.VISIBLE);
            textTW.setText("Tweet to raise up to");
            int bigCount = (int) bdTW.floatValue();
            if (bigCount >= 1000) {
                float shareCountTWK = (float) (bigCount * .001);
                BigDecimal bdTWK = new BigDecimal(Float.toString(shareCountTWK));
                bdTWK = bdTWK.setScale(2, BigDecimal.ROUND_UP);
                textTWCount.setText("£" + bigCount);
            } else {
                textTWCount.setText("£" + bigCount);
            }

			/* Storing oAuth tokens to shared preferences */
            Editor e = mSharedPreferences.edit();
            e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
            e.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
            e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
            e.putString(PREF_USER_NAME, username);
            e.putInt("pref_twitter_follwers_count", count);
            e.commit();

        } catch (TwitterException e1) {
            e1.printStackTrace();
        }
    }

    private void initTwitterConfigs() {
        consumerKey = getString(R.string.twitter_consumer_key);
        consumerSecret = getString(R.string.twitter_consumer_secret);
        callbackUrl = getString(R.string.twitter_callback);
        oAuthVerifier = getString(R.string.twitter_oauth_verifier);
    }

    private void loginToTwitter(TextView textTw, TextView textTWCount, View viewTWCount, String cost) {

        this.textTW = textTw;
        this.textTWCount = textTWCount;
        this.viewTWCount = viewTWCount;
        this.cost = cost;
        final ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(consumerKey);
        builder.setOAuthConsumerSecret(consumerSecret);

        final Configuration configuration = builder.build();
        final TwitterFactory factory = new TwitterFactory(configuration);
        twitter = factory.getInstance();

        try {
            requestToken = twitter.getOAuthRequestToken(callbackUrl);

            final Intent intent = new Intent(ctx, WebViewActivity.class);
            intent.putExtra(WebViewActivity.EXTRA_URL, requestToken.getAuthenticationURL());
            startActivityForResult(intent, WEBVIEW_REQUEST_CODE);

        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    public void addVideoLayout() {
        videoCounter = new int[videoList.size()];
     /*	Collections.sort(videoList, new Comparator<Videos>() {

			@Override
			public int compare(Videos v1, Videos v2) {
				String id1 = ((Videos) v1).getVIDEO_UNIT_VALUE();
				String id2 = ((Videos) v2).getVIDEO_UNIT_VALUE();

				// descending  order
				return id2.compareTo(id1);
			}
		});
		*/

        layoutVideos.removeAllViews();
        for (int i = 0; i < videoList.size(); i++) {
            final RelativeLayout myRelView = (RelativeLayout) inflator.inflate(R.layout.video_test, null);
            myRelView.setId(i);
            // Button companyName = (Button) myRelView.findViewById(R.id.imageViewSponsorLink);
            RelativeLayout layout_frame_video = (RelativeLayout) myRelView.findViewById(R.id.layout_frame_video);
            LinearLayout videoLike = (LinearLayout) myRelView.findViewById(R.id.imageViewLikeVideo);
            final ImageView imageLike = (ImageView) myRelView.findViewById(R.id.imageViewLike);
            final TextView textviewLike = (TextView) myRelView.findViewById(R.id.textViewLike);
            ImageView imageSponsorLogo = (ImageView) myRelView.findViewById(R.id.imageSponsorLogo);
            final View blackOverlay = (View) myRelView.findViewById(R.id.blackOverlay);
            final LinearLayout layoutVideoOverlayText = (LinearLayout) myRelView.findViewById(R.id.layoutVideoOverlayText);
            final ImageButton playButton = (ImageButton) myRelView.findViewById(R.id.play_button);
            final ImageButton playVideo = (ImageButton) myRelView.findViewById(R.id.play_video);
            TextView sponsorName = (TextView) myRelView.findViewById(R.id.textViewsponsorName);
            TextView textViewCost = (TextView) myRelView.findViewById(R.id.textViewCost);
            TextView textVideoOverlayCount = (TextView) myRelView.findViewById(R.id.textVideoOverlayCount);
            textVideoOverlayCount.setTextColor(Color.parseColor(projectColor));
            final ImageView thumbnail = (ImageView) myRelView.findViewById(R.id.thumnail_image);
            final ProgressBar progressLoading = (ProgressBar) myRelView.findViewById(R.id.progressBarVideo);
            final LinearLayout layoutShare = (LinearLayout) myRelView.findViewById(R.id.layoutShare);
            final LinearLayout buttonFB = (LinearLayout) myRelView.findViewById(R.id.buttonFB);
            final LinearLayout buttonTW = (LinearLayout) myRelView.findViewById(R.id.buttonTW);
            final Button buttonEM = (Button) myRelView.findViewById(R.id.buttonEm);
            final LinearLayout buttonMSG = (LinearLayout) myRelView.findViewById(R.id.buttonMSG);
            final TextView textFB = (TextView) myRelView.findViewById(R.id.textFB);
            final TextView textTW = (TextView) myRelView.findViewById(R.id.textTW);

            final View viewCountFB = (View) myRelView.findViewById(R.id.viewCountFB);
            final View viewCountTW = (View) myRelView.findViewById(R.id.viewCountTW);

            final TextView textFBCount = (TextView) myRelView.findViewById(R.id.textFBCount);
            final TextView textTWCount = (TextView) myRelView.findViewById(R.id.textTWCount);
            final TextView textMSGCount = (TextView) myRelView.findViewById(R.id.textMSGCount);


            final int position = i;

            if (videoList.get(position).getLIKED().contains("1")) {
                imageLike.setImageDrawable(mDrawable);
                textviewLike.setTextColor(Color.parseColor(projectColor));
            }
            //final LinearLayout layoutVideo = (LinearLayout) myRelView.findViewById(R.id.layout_video);
            //final ImageButton pauseButton = (ImageButton) myRelView.findViewById(R.i1d.pause);
            //final ImageButton fullscreenButton = (ImageButton) myRelView.findViewById(R.id.fullscreen);

            //final TextView timeTicker = (TextView) myRelView.findViewById(R.id.timeTicker);
            //timeTicker.setVisibility(View.INVISIBLE);


            Utilities.downloadImage(videoList.get(position).getLOGO_URL(), imageSponsorLogo, options1, null);
            videoCounter[position] = Integer.parseInt(videoList.get(position).getVIDEO_FREQ());
            Utilities.downloadImage(videoList.get(position).getTHUMNAIL(), thumbnail, options, null);

            String sponsor = videoList.get(position).getCOMPANY_NAME();
            String firstLetter = sponsor.substring(0, 1);
            String capitalFirst = firstLetter.toUpperCase();
            String sponsorSub = sponsor.substring(1);
            sponsorName.setText(capitalFirst + sponsorSub);

            float x = Float.parseFloat(videoList.get(position).getVIDEO_COST());
            final int x1 = (int) x;
            if (x1 < 100) {
                textViewCost.setText("Watch the video to raise £" + x );
                textVideoOverlayCount.setText("£" + videoList.get(position).getVIDEO_COST());
            } else {
                textViewCost.setText("Watch the video to raise " + "" + videoList.get(position).getVIDEO_COST());
                textVideoOverlayCount.setText("£" + videoList.get(position).getVIDEO_COST());
            }
            //layoutVideo.setOnTouchListener(new OnSwipeTouchListener(ctx, false));
            playButton.setBackground(Utilities.MyPlayButtonLayer(ctx, projectColor));

            videoLike.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    likeVideo(videoList.get(position).getVIDEO_ID());
                    imageLike.setImageDrawable(mDrawable);
                    textviewLike.setTextColor(Color.parseColor(projectColor));

					/*FragmentVideos.imageLike = imageLike;
                    FragmentVideos.textviewLike = textviewLike;*/
                    /*Intent intent = new Intent(getActivity(), LikeVideoActivity.class);
                    intent.putExtra("LIKE_OBJECT_ID", videoList.get(position).getFB_LIKE_URL());
					intent.putExtra("VIDEO_ID", videoList.get(position).getVIDEO_ID());
					intent.putExtra("COLOR", projectColor);
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);*/
                }
            });
              /* companyName.setOnClickListener(new OnClickListener() {
                    @Override
					public void onClick(View v) {
						String webLink = videoList.get(position).getCOMPANY_WEBSITE();
						if(webLink.isEmpty())
						{

						}
						else
						{
							if(!webLink.contains("http"))
							{
								webLink = "http://"+webLink;
							}
						sendClicknLikeEvents(videoList.get(position).getVIDEO_ID(), "clicked");
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse(webLink));
						ctx.startActivity(i);
						}
					}
				});*/
               /*shareVideo.setOnClickListener(new OnClickListener() {
                @Override
				public void onClick(View v) {*/

            String userId = mSharedPreferences.getString("user_id", "0");
            final String videoShareUrl = "http://givvr.co.uk/share.php?project_id=" + projectId + "&video_id=" + videoList.get(position).getVIDEO_ID() + "&user_id=" + userId;

					/*if(layoutShare.getVisibility()==View.GONE)
                    {
						layoutShare.setVisibility(View.VISIBLE);*/
            int countContacts = getNumber(getActivity().getContentResolver());
            float shareCount = Float.parseFloat(videoList.get(position).getVIDEO_COST()) * countContacts;
            BigDecimal bd = new BigDecimal(Float.toString(shareCount));
            bd = bd.setScale(2, BigDecimal.ROUND_UP);
            textMSGCount.setText("£" + bd.floatValue());
            //buttonMSG.setText("Share to raise         $"+bd.floatValue());
                    /*}
                    else
					{
						layoutShare.setVisibility(View.GONE);
					}*/

            //count = mSharedPreferences.getInt("pref_facebook_friends_count", 0);

            if (count != 0) {
                float shareCountFB = Float.parseFloat(videoList.get(position).getVIDEO_COST()) * count;
                BigDecimal bdFB = new BigDecimal(Float.toString(shareCountFB));
                bdFB = bdFB.setScale(2, BigDecimal.ROUND_UP);
                //+bdFB.floatValue()
                viewCountFB.setVisibility(View.VISIBLE);
                textFB.setText("Share to raise up to");
                //textFB.setText("Connect to raise more");
                //textFBCount.setText("$"+bdFB.floatValue());
                int bigCount = (int) bdFB.floatValue();
                if (bigCount >= 1000) {
                    float shareCountFBK = (float) (bigCount * .001);
                    BigDecimal bdFBK = new BigDecimal(Float.toString(shareCountFBK));
                    bdFBK = bdFBK.setScale(2, BigDecimal.ROUND_UP);
                    textFBCount.setText("£" + bigCount );
                } else {
                    textFBCount.setText("£" + bigCount );
                }
                //buttonFB.setText("Share to raise up to");
            }
                    /*else
                    {
						connectFacebook();
					}*/

            boolean isLoggedIn = mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
            int twCount = mSharedPreferences.getInt("pref_twitter_follwers_count", 1);
            if (isLoggedIn) {
                float shareCountTW = Float.parseFloat(videoList.get(position).getVIDEO_COST()) * twCount;
                BigDecimal bdTW = new BigDecimal(Float.toString(shareCountTW));
                bdTW = bdTW.setScale(2, BigDecimal.ROUND_UP);

                viewCountTW.setVisibility(View.VISIBLE);
                textTW.setText("Tweet to raise up to");

                int bigCount = (int) bdTW.floatValue();
                if (bigCount >= 1000) {
                    float shareCountTWK = (float) (bigCount * .001);
                    BigDecimal bdTWK = new BigDecimal(Float.toString(shareCountTWK));
                    bdTWK = bdTWK.setScale(2, BigDecimal.ROUND_UP);
                    textTWCount.setText("£" + bigCount);
                } else {
                    textTWCount.setText("£" + bigCount);
                }
                //buttonTW.setText("Tweet to raise up to");
            }
            buttonFB.setOnClickListener(new OnClickListener() {
                //facebook sare video click...***
                @Override
                public void onClick(View v) {
                    final boolean isLoggedInFB = mSharedPreferences.getBoolean(PREF_KEY_FACEBOOK_LOGIN, false);
                    shareonFb = videoShareUrl;
                    if (isLoggedInFB) {
                        String desc = "";
                        if (x1 < 100) {
                            desc = Constants.FACEBOOK_SHARE_VIDEO_1 + projectName + Constants.FACEBOOK_SHARE_VIDEO_2 + videoList.get(position).getCOMPANY_NAME() + " donates " + x1 + " ₹" + " to " + projectName + Constants.FACEBOOK_SHARE_VIDEO_3;
                        } else {
                            desc = Constants.FACEBOOK_SHARE_VIDEO_1 + projectName + Constants.FACEBOOK_SHARE_VIDEO_2 + videoList.get(position).getCOMPANY_NAME() + " donates " + "₹" + videoList.get(position).getVIDEO_COST() + " to " + projectName + Constants.FACEBOOK_SHARE_VIDEO_3;
                        }
                        shareOnFb(projectName, videoShareUrl, desc, videoShareUrl, lARGE_IMAGE_URL);
                    } else {
                        connectFacebook(textFB, textFBCount, viewCountFB, videoList.get(position).getVIDEO_COST());
                    }
                }
            });
            buttonTW.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    boolean isLoggedIn = mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
                    if (!isLoggedIn) {
                        loginToTwitter(textTW, textTWCount, viewCountTW, videoList.get(position).getVIDEO_COST());
                    } else {
                        try {
                            Intent tweetIntent = new Intent(Intent.ACTION_SEND);
                            tweetIntent.setType("text/plain");
                            tweetIntent.putExtra(Intent.EXTRA_TEXT, videoShareUrl + '\n' + Constants.TW_TEXT_BODY_1 + videoList.get(position).getCOMPANY_NAME() + " donates ₹" + videoList.get(position).getVIDEO_COST() + " to " + projectName + "!");

                            PackageManager pm = ctx.getPackageManager();
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
                            Toast.makeText(ctx, "You don't seem to have twitter installed on this device", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            buttonEM.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent email = new Intent(Intent.ACTION_SEND);
                    //********
                    email.setData(Uri.parse("mailto:"));
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
                    email.putExtra(Intent.EXTRA_SUBJECT, "Givvr - Help Donate to Charities by Givvng your time!");

                    String desc = "";
                    if (x1 < 100) {
                        desc = Constants.FACEBOOK_SHARE_VIDEO_1 + projectName + Constants.FACEBOOK_SHARE_VIDEO_2 + videoList.get(position).getCOMPANY_NAME() + " donates " + x1 + " cents" + " to " + projectName + Constants.FACEBOOK_SHARE_VIDEO_3;
                    } else {
                        desc = Constants.FACEBOOK_SHARE_VIDEO_1 + projectName + Constants.FACEBOOK_SHARE_VIDEO_2 + videoList.get(position).getCOMPANY_NAME() + " donates " + "£" + videoList.get(position).getVIDEO_COST() + " to " + projectName + Constants.FACEBOOK_SHARE_VIDEO_3;
                    }
                    email.putExtra(Intent.EXTRA_TEXT, desc + '\n' + '\n' + videoShareUrl);
                    // email.setType("message/rfc822");
                    //*********
                    email.setType("text/plain");
                    startActivity(email);
                }
            });
            buttonMSG.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.setData(Uri.parse("sms:"));
                           /* if(x1<100)
                            {
					        sendIntent.putExtra(Intent.EXTRA_TEXT, videoShareUrl+'\n'+Constants.TW_TEXT_BODY_1+"a Charity"+Constants.TW_TEXT_BODY_2+x1+" cents!");
							}
					        else
					        {*/
                    sendIntent.putExtra("sms_body", videoShareUrl + '\n' + Constants.TW_TEXT_BODY_1 + "a Charity" + Constants.TW_TEXT_BODY_2 + "\u20B9" + videoList.get(position).getVIDEO_COST() + "!");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, videoShareUrl + '\n' + Constants.TW_TEXT_BODY_1 + "a Charity" + Constants.TW_TEXT_BODY_2 + "\u20B9" + videoList.get(position).getVIDEO_COST() + "!");
                    startActivity(sendIntent);
                }
            });
                    /*
                    }
			    });*/
            playVideo.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {


                    isPreparing = false;
                    playButton.setVisibility(View.INVISIBLE);
                    playVideo.setVisibility(View.INVISIBLE);
                    progressLoading.setVisibility(View.VISIBLE);

                   // Home.dataUpdate = true;

                    final VideoControllerView controller = new VideoControllerView(ctx, projectColor);
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                    mediaPlayer.reset();
                    startScroll(layoutVideoOverlayText, blackOverlay, playVideo, playButton, 1, thumbnail, controller, progressLoading);
                    final SurfaceView surfaceView = (SurfaceView) myRelView.findViewById(R.id.videoSurface);
                    // videoViewHeight = surfaceView.getHeight();
                    videoViewHeight = myRelView.getId();

                    final SurfaceHolder surfaceHolder = surfaceView.getHolder();
                    surfaceHolder.addCallback(new SurfaceHolder.Callback() {

                        @Override
                        public void surfaceDestroyed(SurfaceHolder holder) {
                            mediaPlayer.pause();
                            Log.i("Mydata", "surface destroyed");
                        }

                        @Override
                        public void surfaceCreated(SurfaceHolder holder) {
                            mediaPlayer.setDisplay(holder);

                        }

                        @Override
                        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                                   int height) {
                            // TODO Auto-generated method stub
                            Log.i("Mydata", "surface changed");
                             amount="update";
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    if (videoViewHeight > -1) {
                                        //Toast.makeText(getActivity(), "Video Completed", 1000).show();
                                        sendVideoResponse(videoList.get(position).getVIDEO_ID(), projectId);
                                        videoCounter[position]--;
                                        //playVideo.setVisibility(View.VISIBLE);
                                        //playButton.setVisibility(View.VISIBLE);
                                        thumbnail.setVisibility(View.VISIBLE);
                                        blackOverlay.setVisibility(View.VISIBLE);
                                        layoutVideoOverlayText.setVisibility(View.VISIBLE);
                                        ProjectActivity.updateRaised();
                                        startScroll(layoutVideoOverlayText, blackOverlay, playVideo, playButton, videoCounter[position], thumbnail, controller, progressLoading);
                                        amount="updated";
                                        mp.reset();
                                    }
                                }
                            });
                        }
                    });

                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setDisplay(surfaceHolder);
                    mediaPlayer.setScreenOnWhilePlaying(true);
                         /*try {
                             // mediaPlayer.setDataSource(videoList.get(position).getVIDEO_URL());
				    		  mediaPlayer.prepare();
				    		 } catch (IllegalArgumentException e) {
				    		  // TODO Auto-generated catch block
				    		  e.printStackTrace();
				    		 } catch (IllegalStateException e) {
				    		  // TODO Auto-generated catch block
				    		  e.printStackTrace();
				    		 } catch (IOException e) {
				    		  // TODO Auto-generated catch block
				    		  e.printStackTrace();
				    		 }*/
                  /*  MediaController mediaController = new
                            MediaController(getActivity());
                    mediaController.setAnchorView(mediaPlayer);
                    mediaPlayer.setMediaController(mediaController);*/
                    // controller.setAnchorView(mediaPlayer);
                    mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            controller.setMediaPlayer(new VideoControllerView.MediaPlayerControl() {



                                @Override
                                public void start() {
                                    // TODO Auto-generated method stub
                                    mediaPlayer.start();
                                    mFullScreen = true;
                                    playVideo.setVisibility(View.INVISIBLE);
                                    playButton.setVisibility(View.INVISIBLE);
                                    progressLoading.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void pause() {
                                    // TODO Auto-generated method stub
                                    mediaPlayer.pause();
                                    playVideo.setVisibility(View.VISIBLE);
                                    playButton.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public int getDuration() {
                                    // TODO Auto-generated method stub
                                    return mediaPlayer.getDuration();
                                }

                                @Override
                                public int getCurrentPosition() {
                                    // TODO Auto-generated method stub
                                    return mediaPlayer.getCurrentPosition();
                                }

                                @Override
                                public void seekTo(int pos) {
                                    // TODO Auto-generated method stub
                                    mediaPlayer.seekTo(pos);
                                }

                                @Override
                                public boolean isPlaying() {
                                    // TODO Auto-generated method stub
                                    return mediaPlayer.isPlaying();
                                }

                                @Override
                                public int getBufferPercentage() {
                                    // TODO Auto-generated method stub
                                    return 0;
                                }

                                @Override
                                public boolean canPause() {
                                    // TODO Auto-generated method stub
                                    return true;
                                }

                                @Override
                                public boolean canSeekBackward() {
                                    // TODO Auto-generated method stub
                                    return false;
                                }

                                @Override
                                public boolean canSeekForward() {
                                    // TODO Auto-generated method stub
                                    return false;
                                }

                                @Override
                                public boolean isFullScreen() {
                                    if (mFullScreen) {
                                        return false;
                                    } else {
                                        return true;
                                    }
                                }

                                @Override
                                public void toggleFullScreen() {
                                    flagFullscreen = true;
                                    setFullScreen(isFullScreen());
                                }

                            });
                            int surfaceView_Width = surfaceView.getWidth();
                            int surfaceView_Height = surfaceView.getHeight();

                            float video_Width = mediaPlayer.getVideoWidth();
                            float video_Height = mediaPlayer.getVideoHeight();

                            float ratio_width = surfaceView_Width / video_Width;
                            float ratio_height = surfaceView_Height / video_Height;
                            float aspectratio = video_Width / video_Height;

                            LayoutParams layoutParams = surfaceView.getLayoutParams();

                            if (ratio_width > ratio_height) {
                                layoutParams.width = (int) (surfaceView_Height * aspectratio);
                                layoutParams.height = surfaceView_Height;
                            } else {
                                layoutParams.width = surfaceView_Width;
                                layoutParams.height = (int) (surfaceView_Width / aspectratio);
                            }

                            surfaceView.setLayoutParams(layoutParams);

                            controller.setAnchorView((RelativeLayout) myRelView.findViewById(R.id.videoSurfaceContainer));

                            controller.show();
                            mediaPlayer.start();


                            mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {

                                @Override
                                public void onVideoSizeChanged(MediaPlayer mp, int arg1, int arg2) {
                                    // TODO Auto-generated method stub

                                    thumbnail.setVisibility(View.INVISIBLE);
                                    progressLoading.setVisibility(View.INVISIBLE);
                                    mp.start();
                                }
                            });
                        }
                    });

                    try {
                        mediaPlayer.setDataSource(getActivity(), Uri.parse(videoList.get(position).getVIDEO_URL()));
                        System.out.println("vid url " + videoList.get(position).getVIDEO_URL());
                        mediaPlayer.prepareAsync();
                        isPreparing = true;
                    } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (SecurityException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    surfaceView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mediaPlayer.isPlaying()) {
                                //controller.show();

                            }
                        }
                    });

                }
            });
            layoutVideos.addView(myRelView);
        }
    }


    public void startScroll(final LinearLayout layoutVideoOverlayText, final View blackOverlay, final ImageButton playVideo, final ImageButton playButton, final int videoCounter2, ImageView thumbnail, final VideoControllerView controller, final ProgressBar progressBar) {
        thumbnail.setVisibility(View.VISIBLE);

        scrollView.setScrollViewListener(new ScrollViewListener() {
            @Override
            public void onScrollChanged(ScrollViewExt scrollView, int x, int y,
                                        int oldx, int oldy) {
                /*int diffy = Math.abs(oldy - y);
                if (diffy > 70) {

                    if (videoCounter2 > 0) {
                        layoutVideoOverlayText.setVisibility(View.INVISIBLE);
                        blackOverlay.setVisibility(View.INVISIBLE);
                        playVideo.setVisibility(View.VISIBLE);
                        playButton.setVisibility(View.VISIBLE);
                    } else if (videoCounter2 == 0) {
                        layoutVideoOverlayText.setVisibility(View.VISIBLE);
                        blackOverlay.setVisibility(View.VISIBLE);
                    }
                    if (mediaPlayer.isPlaying() || isPreparing) {
                        if (progressBar != null)
                            progressBar.setVisibility(View.GONE);
                        mediaPlayer.pause();
                        controller.hide();
                        playVideo.setVisibility(View.VISIBLE);
                        playButton.setVisibility(View.VISIBLE);
                    }

                }*/

                if (videoViewHeight != -1) {
                    View view = scrollView.findViewById(R.id.layout_videos).findViewById(videoViewHeight).findViewById(R.id.videoSurface);
                    ;
                    if (view != null) {

                        Rect scrollBounds = new Rect();
                        scrollView.getHitRect(scrollBounds);


                        if (!view.getLocalVisibleRect(scrollBounds)) {
                            if (videoCounter2 > 0) {
                                layoutVideoOverlayText.setVisibility(View.INVISIBLE);
                                blackOverlay.setVisibility(View.INVISIBLE);
                                playVideo.setVisibility(View.VISIBLE);
                                playButton.setVisibility(View.VISIBLE);
                            }/* else if (videoCounter2 == 0) {
                                layoutVideoOverlayText.setVisibility(View.VISIBLE);
                                blackOverlay.setVisibility(View.VISIBLE);
                            }*/
                            if (mediaPlayer.isPlaying() || isPreparing) {

                                layoutVideoOverlayText.setVisibility(View.INVISIBLE);
                                blackOverlay.setVisibility(View.INVISIBLE);
                                if (progressBar != null)
                                    progressBar.setVisibility(View.GONE);
                                mediaPlayer.pause();
                                mediaPlayer.stop();
                                mediaPlayer.reset();
                                controller.hide();
                                playVideo.setVisibility(View.VISIBLE);
                                playButton.setVisibility(View.VISIBLE);
                            }

                            videoViewHeight = -1;
                        }
                    }
                }
            }
        });
    }

    public void sendVideoResponse(String vId, String projectId) {
        UserFunctions userFunction = new UserFunctions(ctx);
       //****
        if(amount.equals("update")) {
            userFunction.videoPlayed(vId, projectId);
        }
    }

    public void sendClicknLikeEvents(String vId, String event) {
        UserFunctions userFunction = new UserFunctions(ctx);
        userFunction.sendVideoEvents(vId, event);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!flagFullscreen) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
        }
        FlurryAgent.onEndSession(getActivity());
    }

    public void shareVideo(String projectName, String projectTag, String projectDescription, String lARGE_IMAGE_URL) {
        Intent intent = new Intent(getActivity(), ShareProjectActivity.class);
        intent.putExtra(Constants.Extra.POST_LINK_NAME, projectName);
        intent.putExtra(Constants.Extra.POST_LINK_CAPTION, projectTag);
        intent.putExtra(Constants.Extra.POST_LINK_DESCRIPTION, projectDescription);
        intent.putExtra(Constants.Extra.POST_PICTURE_URL, lARGE_IMAGE_URL);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void shareOnFb(String name, String caption, String description, String link, String pictureUrl) {
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
	        new WebDialog.FeedDialogBuilder(getActivity(),
	            session,
	            params))
	        .setOnCompleteListener(new OnCompleteListener() {

	            @Override
	            public void onComplete(Bundle values,
	                FacebookException error) {
	                if (error == null) {
	                    final String postId = values.getString("post_id");
	                    if (postId != null) {
	                        Toast.makeText(ctx,"Video Shared Successfully",Toast.LENGTH_SHORT).show();

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
	                        "Error posting video",
	                        Toast.LENGTH_SHORT).show();
	                }
	            }

	        })
	        .build();
	    feedDialog.show();
	    }
	    else
		{
			String urlToShare = link;
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_TEXT, urlToShare);
			boolean facebookAppFound = false;
			List<ResolveInfo> matches = getActivity().getPackageManager().queryIntentActivities(intent, 0);
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
		else
		{*/

        if(android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP){
             String urlToShare = link;
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, urlToShare);
            boolean facebookAppFound = false;
            List<ResolveInfo> matches = getActivity().getPackageManager().queryIntentActivities(intent, 0);
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

    public void setFullScreen(boolean fullScreen) {
        mFullScreen = false;
        Intent intent = new Intent(ctx, FullscreenActivity.class);
        intent.putExtra("project_color", projectColor);
        startActivityForResult(intent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            flagFullscreen = false;
            if (mediaPlayer.isPlaying()) {
            }
        } else if (requestCode == WEBVIEW_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String verifier = data.getExtras().getString(oAuthVerifier);
                try {
                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

                    saveTwitterInfo(accessToken);

                } catch (Exception e) {
                    Log.e("Twitter Login Failed", e.getMessage());
                }
            }
        } else if (callbackManager != null)
            callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(getActivity(), Constants.FLURRY_ID);
        //connectFacebook();
    }

    public void connectFacebook(TextView textFB, TextView textFBCount, View viewFBCount, String cost) {
        this.textFB = textFB;
        this.cost = cost;
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile, email, user_birthday", "user_location"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        getMyInformation(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
        //Session.openActiveSession((Activity) ctx, true, statusCallback);
    }

    public int getNumber(ContentResolver cr) {
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        int count = phones.getCount();
        phones.close();// close cursor
        return count;
    }
     /*private Session.StatusCallback statusCallback =
             new SessionStatusCallback();*/
			/* private class SessionStatusCallback implements Session.StatusCallback {
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
										JSONObject data = json1.getJSONObject("summary");
										count = Integer.parseInt(data.getString("total_count"));
										Editor e = mSharedPreferences.edit();
										e.putBoolean(PREF_KEY_FACEBOOK_LOGIN, true);
										e.putInt("pref_facebook_friends_count", count);
										e.commit();
										
									    	float shareCount = Float.parseFloat(cost)*count;
											BigDecimal bd = new BigDecimal(Float.toString(shareCount));
										    bd = bd.setScale(2, BigDecimal.ROUND_UP);
										    //buttonFB.setBackgroundResource(R.drawable.share_fb_count);
											//buttonFB.setText("Share to raise up to");
										    viewFBCount.setVisibility(View.VISIBLE);
										    textFB.setText("Share to raise up to");
										    //textFBCount.setText("$"+bd.floatValue());
										    int bigCount = (int) bd.floatValue();
										    if(bigCount>=1000)
										    {
										    	float shareCountFBK = (float) (bigCount*.001);
										    	BigDecimal bdFBK = new BigDecimal(Float.toString(shareCountFBK));
											    bdFBK = bdFBK.setScale(2, BigDecimal.ROUND_UP);
											    textFBCount.setText("$"+bdFBK.floatValue()+"K");
										    }
										    else
										    {
										    	textFBCount.setText("$"+bd.floatValue());
										    }
										
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
			 	    		        }
			 	    		    }
			 	    		).executeAsync();
			 	    	}
			     	else
			     	{
			    			String urlToShare = shareonFb;
			    			Intent intent = new Intent(Intent.ACTION_SEND);
			    			intent.setType("text/plain");
			    			intent.putExtra(Intent.EXTRA_TEXT, urlToShare);
			    			boolean facebookAppFound = false;
			    			List<ResolveInfo> matches = getActivity().getPackageManager().queryIntentActivities(intent, 0);
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
			 }*/

    private void likeVideo(String vId) {
        new UserFunctions(ctx).likeVideo(vId);
        System.out.println("like response" + " :: " + mSharedPreferences.getString("accesstoken", "") + " :: " + new UserFunctions(ctx).likeVideo(vId));
    }

    private void getMyInformation(com.facebook.AccessToken accessToken) {
        //dialog.show();
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {

            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                JSONObject obj = object;
                try {
                    final String userId = obj.getString("id");
                    final String name = obj.getString("name");
                    final String email = obj.getString("email");
                    final String dob = obj.getString("birthday");
                    if (dob != null) {
                        String[] convertDob = dob.split("/");
                        final String myDob = convertDob[2] + "/" + convertDob[0] + "/" + convertDob[1];
                        final String gender = obj.getString("gender");
                        String currentLocation = "";
                        try {
                            currentLocation = obj.getString("location");
                        } catch (Exception e) {
                            currentLocation = "";
                        }
                        //******
                      //  new facebookSignUpTask().execute(email, name, gender, myDob, currentLocation, userId);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender, birthday,location");
        request.setParameters(parameters);
        request.executeAsync();


        new GraphRequest(
                com.facebook.AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        //int count = 0;
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

                        if (cd.isConnectingToInternet()) {
                            new GetVideosData().execute();
                        }
                        // response.getRawResponse();
                    }
                }
        ).executeAsync();


    }

    public class GetVideosData extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressVideos.setVisibility(View.VISIBLE);
            videoList.clear();
        }

        @Override
        protected JSONObject doInBackground(String... arg) {
            UserFunctions userFunction = new UserFunctions(ctx);
            JSONObject result = userFunction.getVideos(projectId);
            Log.i("Mydata video", ""+result);
            JSONArray jsonarray;
            try {
                jsonarray = result.getJSONArray("videos");
                for (int i = 0; i < jsonarray.length(); i++) {
                    result = jsonarray.getJSONObject(i);
							  /*  float x = Float.parseFloat(result.getString("cost"))*100;
								int cents = (int)x;
								String duration = result.getString("duration");
								String[] time = duration.split(":");
								int hours = Integer.parseInt(time[0]);
								int minutes = Integer.parseInt(time[1]);
								int seconds = Integer.parseInt(time[2]);
								int totalSeconds = hours*60*60+minutes*60+seconds;
								long value = cents/totalSeconds;
								String unitValue =  ""+value;
								Log.i("Mydata", "Cents: "+cents+" time "+hours+"min "+minutes+"sec "+seconds+"unit value "+unitValue);
								*/
                    videoList.add(new Videos(videoDomainUrl + result.getString("video_url") + videoFormat,
                            result.getString("company"),
                            result.getString("facebook"),
                            result.getString("website"),
                            result.getString("video_id"),
                            result.getString("cost"),
                            videoThumbnailUrl + result.getString("video_url") + thumbnailFormat,
                            result.getString("freq"),
                            result.getString("maxd"),
                            UrlList.SPONSORIMAGELINK + result.getString("logo"),
                            result.getString("liked")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            progressVideos.setVisibility(View.INVISIBLE);

            if (videoList.size() == 0) {
                noMoreVideos.setVisibility(View.VISIBLE);
            } else {
                addVideoLayout();
            }
        }
    }

    class facebookSignUpTask extends AsyncTask<String, String, JSONObject> {
        SharedPreferences.Editor editor = preference.edit();
        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("LOADING...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String facebook = "true", email = params[0], name = params[1], gender = params[2], myDob = params[3], currentLocation = params[4], userid = params[5];
            UserFunctions userFunction = new UserFunctions(getActivity());
            editor.putString("user_email", email);
            editor.putString("facebook_id", userid);
            JSONObject json = userFunction.facebookLogin(email, facebook, name, myDob, gender, currentLocation,userid);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            String profileStatus = "";
            try {
                profileStatus = result.getString("profile");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (profileStatus.contentEquals("1")) {

                editor.putString("profile_status", "created");
                editor.apply();

            }
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            //  Session.openActiveSession(StartActivity.this, true, statusCallback);
        }

    }
}
