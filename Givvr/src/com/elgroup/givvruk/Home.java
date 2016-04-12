package com.elgroup.givvruk;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.elgroup.givvruk.Models.SharedP_Result;
import com.flurry.android.FlurryAgent;
import com.google.android.gcm.GCMRegistrar;
import com.navdrawer.SimpleSideDrawer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.tech.givvruk.settings.SettingsActivity;
import com.tech.givvruk.utils.AlertMessage;
import com.tech.givvruk.utils.ConnectionDetector;
import com.tech.givvruk.utils.Constants;
import com.tech.givvruk.utils.ListViewAdapterProjects;
import com.tech.givvruk.utils.OnSwipeTouchListener;
import com.tech.givvruk.utils.Project;
import com.tech.givvruk.utils.UrlList;
import com.tech.givvruk.utils.UserFunctions;
import com.tech.givvruk.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Home extends Activity implements OnQueryTextListener {

    static SimpleSideDrawer slide_me;
    static SharedPreferences preference;
    static String Your_Rank, Name, Age, City, givvrTotal, yourTotal, PROFILE_TAG, projectDescription, projectGoal, projectRaised, projectName, projectTag, profileImageUrl;
    static RelativeLayout rl;
    private static ConnectionDetector cd;
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MyApplication.acquireWakeLock(getApplicationContext());
            MyApplication.releaseWakeLock();
        }
    };
    JSONObject jsonobject;
    JSONArray jsonarray;
    ListView listview;
    ListViewAdapterProjects adapter;
    ArrayList<Project> arrayProject = new ArrayList<Project>();
    String LARGE_IMAGE_URL = UrlList.LARGEIMAGEURL;
    String SMALL_IMAGE_URL = UrlList.SMALLIMAGEURL;
    Animation animSideDown, animSideUp;
    TextView profileName;
    View view;
    ImageButton btnProfile, btnSettings, btnSponsors;
    RelativeLayout rankingRL, donateRL, homeRL;
    ImageView profileImage, projectHeading;
    DisplayMetrics metrics;
    int panelWidth;
    ProgressBar progressBarHome;
    LinearLayout.LayoutParams menuPanelParameters;
    Context ctx;
    String id = null, personPhotoUrl, UserId = "";
    String img_value = null;
    DisplayImageOptions options;
    boolean flag_activity_running = false;
    private SearchView searchView;
    SharedP_Result sharedP;
    public static Boolean dataUpdate = false;
    public static void toggleMenu() {
        slide_me.toggleLeftDrawer();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        cd = new ConnectionDetector(this);
        ctx = this;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.profile)
                .showImageOnFail(R.drawable.profile)
                .resetViewBeforeLoading(true)
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new RoundedBitmapDisplayer(170))
                .build();
        preference = MyApplication.preference;

        //*******
         sharedP = new SharedP_Result(ctx);

        slide_me = new SimpleSideDrawer(this);
        listview = (ListView) findViewById(R.id.listProjects);
        view = slide_me.setLeftBehindContentView(R.layout.left_menu);
        btnProfile = (ImageButton) findViewById(R.id.menu_option_profile);
        btnSettings = (ImageButton) findViewById(R.id.menu_option_settings);
        btnSponsors = (ImageButton) findViewById(R.id.menu_option_sponsors);
        rankingRL = (RelativeLayout) findViewById(R.id.rankingRL);
        donateRL = (RelativeLayout) findViewById(R.id.donateRL);
        homeRL = (RelativeLayout) findViewById(R.id.homeRL);
        profileImage = (ImageView) findViewById(R.id.profilepic);
        profileName = (TextView) findViewById(R.id.textName);
        searchView = (SearchView) findViewById(R.id.searchView1);
        progressBarHome = (ProgressBar) findViewById(R.id.progressBarHome);


        int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageResource(R.drawable.search);
        projectHeading = (ImageView) findViewById(R.id.imageView1);
        LinearLayout layoutSwipe = (LinearLayout) findViewById(R.id.layoutSwipe);
        layoutSwipe.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext(), true));
        if (cd.isConnectingToInternet()) {
            new fetchHomeData().execute();
        }
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        panelWidth = (int) ((metrics.widthPixels) * 0.67);
        rl = (RelativeLayout) findViewById(R.id.left_menu);
        menuPanelParameters = (LinearLayout.LayoutParams) rl.getLayoutParams();
        menuPanelParameters.width = panelWidth;
        rl.setLayoutParams(menuPanelParameters);
        rl.setFocusableInTouchMode(false);

        homeRL.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                slide_me.closeLeftSide();
            }
        });
        btnProfile.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent invok = new Intent(getApplicationContext(), ProfileActivity.class);
                invok.putExtra("name", Name);
                invok.putExtra("age", Age);
                invok.putExtra("city", City);
                invok.putExtra("givvr_total", givvrTotal);
                invok.putExtra("your_total", yourTotal);
                invok.putExtra("PROFILE_IMAGE_URL", profileImageUrl);
                startActivity(invok);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });
        rankingRL.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent invok = new Intent(getApplicationContext(), Ranking.class);
                invok.putExtra("name", Name);
                invok.putExtra("age", Age);
                invok.putExtra("city", City);
                invok.putExtra("your_total", yourTotal);
                invok.putExtra("your_rank", Your_Rank);
                invok.putExtra("PROFILE_IMAGE_URL", profileImageUrl);
                startActivity(invok);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });
        btnSettings.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
                i.putExtra("user_name", Name);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });
        btnSponsors.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(getApplicationContext(), SponsorsActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });
        donateRL.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DonateActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });

    }



    public void layoutHome(View v) {

    }

    @Override
    public void onBackPressed() {
        if (projectHeading.getVisibility() == View.INVISIBLE) {
            searchView.setIconified(true);
            projectHeading.setVisibility(View.VISIBLE);
        } else if (slide_me.isClosed()) {
            super.onBackPressed();
        } else {
            slide_me.toggleLeftDrawer();
        }
    }

    public void openMenu(View v) {
        slide_me.toggleLeftDrawer();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (cd.isConnectingToInternet()) {
            new DownloadJSON().execute();
        } else {
            Toast tost = Toast.makeText(this, "No internet!", Toast.LENGTH_SHORT);
            tost.setGravity(Gravity.CENTER, 0, 0);
            tost.show();
        }

        long currentTime = System.currentTimeMillis();
        long time = preference.getLong("TIME_STAMP_RATE_US", currentTime + 3 * 5 * 24 * 60 * 60 * 100);

        long x = time - currentTime;
        if (x <= 0) {
            if (preference.getBoolean("RATED", false)) {

            } else {
                SharedPreferences.Editor editor = preference.edit();
                editor.putBoolean("LAYOUT_RATEUS", true);
                editor.apply();
            }
        }

        FlurryAgent.onStartSession(this, Constants.FLURRY_ID);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    public void startPushNotification() {
        new StartNotificationAsyn().execute();
    }

    public void stopPushNotification() {
        new StopNotificationAsyn().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageLoader.getInstance().clearMemoryCache();
        //ImageLoader.getInstance().clearDiskCache();
        System.exit(0);

    }

    private void setupSearchView() {
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search Here");
        searchView.setOnSearchClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (projectHeading.getVisibility() == View.VISIBLE) {
                    projectHeading.setVisibility(View.INVISIBLE);
                } else {
                    projectHeading.setVisibility(View.VISIBLE);
                }
            }
        });
        searchView.setOnCloseListener(new OnCloseListener() {

            @Override
            public boolean onClose() {
                if (projectHeading.getVisibility() == View.VISIBLE) {
                    projectHeading.setVisibility(View.INVISIBLE);
                } else {
                    projectHeading.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            listview.clearTextFilter();
        } else {
            listview.setFilterText(newText);
        }
        return true;
    }

    private class DownloadJSON extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            arrayProject.clear();
            if (flag_activity_running) {
                adapter.notifyDataSetChanged();
            }
            progressBarHome.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            UserFunctions userFunctions = new UserFunctions(Home.this);
            jsonobject = userFunctions.getProjects();
            try {
                jsonarray= jsonobject.getJSONArray("projects");
                for (int i = 0; i < jsonarray.length(); i++) {
                    jsonobject = jsonarray.getJSONObject(i);
                    int daysLeft = 0;
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date date = formatter.parse(jsonobject.getString("created_at"));
                        Date dt2 = Calendar.getInstance().getTime();
                        int diffInDays = (int) ((dt2.getTime() - date.getTime()) / (1000 * 60 * 60 * 24));
                        daysLeft = Integer.parseInt(jsonobject.getString("length")) - diffInDays;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    arrayProject.add(new Project(jsonobject.getString("name"),
                            jsonobject.getString("goal"),
                            jsonobject.getString("tag"),
                            jsonobject.getString("raised"),
                            jsonobject.getString("disc"),
                            SMALL_IMAGE_URL + jsonobject.getString("small_img"),
                            LARGE_IMAGE_URL + jsonobject.getString("large_img"),
                            jsonobject.getString("project_id"),
                            jsonobject.getString("charity_id"),
                            jsonobject.getString("length"),
                            jsonobject.getString("created_at"),
                            jsonobject.getString("color"),
                            "" + daysLeft,
                            jsonobject.getString("supporter")));
                }
            } catch (JSONException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (arrayProject.isEmpty()) {
                Toast.makeText(ctx, "No projects!", Toast.LENGTH_SHORT).show();
            }
            adapter = new ListViewAdapterProjects(Home.this, arrayProject);
            listview.setAdapter(adapter);
            progressBarHome.setVisibility(View.INVISIBLE);
            listview.setTextFilterEnabled(true);
            setupSearchView();
            flag_activity_running = true;
            boolean firstRun = preference.getBoolean("FIRST_LAUNCH", true);
            if (firstRun) {
                SharedPreferences.Editor editor = preference.edit();
                editor.putBoolean("FIRST_LAUNCH", false);
                editor.putBoolean("NOTIFICATION_STATUS", true);
                editor.putString("user_id", UserId);
                long s = System.currentTimeMillis() + 3 * 5 * 24 * 60 * 60 * 100;
                editor.putLong("TIME_STAMP_RATE_US", s);
                editor.apply();
                startPushNotification();
                AlertMessage.showAlertDialog(ctx, "Info", Constants.WELCOME_MESSAGE, true);
                //Utilities.createScheduledNotification(ctx, 5);
            }
        }
    }

    public class fetchHomeData extends AsyncTask<String, String, JSONObject> {
        UserFunctions userFunction = new UserFunctions(ctx);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            //JSONObject jsonforProfile = userFunction.getProfile();
            JSONObject jsonforProfile = userFunction.getProfile(preference.getString("accesstoken", ""));
            Log.i("Homedata ", jsonforProfile.toString());
            return jsonforProfile;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);

            //*******
            sharedP.save(result.toString());

            if (result == null) {
                Utilities ultilities = new Utilities();
                ultilities.displayMessageAndExit((Activity) ctx, "Alert!", "Sorry, unable to process. Please check your network connection or try again later.");
            } else {

                try {
                    Name = result.getString("name");
                    Age = result.getString("age");
                    City = result.getString("city");
                    UserId = result.getString("user_id");
                    profileImageUrl = UrlList.domainURL + result.getString("profile_image");
                    givvrTotal = result.getString("givvr_total");
                    yourTotal = result.getString("your_total");
                    Your_Rank = result.getString("your_rank");
                    //********
                    saveinPriference(Name,result.getString("dob"),Age,City,result.getString("gender"),result.getString("profile_image"),givvrTotal,yourTotal,Your_Rank);
                   // String name, String dob,String Age, String city, String gender, String profile_img_url, String givvrTotal, String yourTotal,String Your_Rank
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                profileName.setText(Name);
                id = preference.getString("facebook_id", "");
                personPhotoUrl = preference.getString("person_google_photo_url", "");
                if (!personPhotoUrl.isEmpty()) {
                    img_value = personPhotoUrl;
                } else if (id.isEmpty()) {
                    img_value = profileImageUrl;
                } else {
                    img_value = "http://graph.facebook.com/" + id + "/picture?type=large";
                }
                Utilities.downloadImage(img_value, profileImage, options, null);
            }
        }


    }

    class StartNotificationAsyn extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String registrationId = preference.getString("GCM_REGISTERED_ID", "");
            if (registrationId.isEmpty()) {
                try {
                    if (cd.isConnectingToInternet()) {

                        GCMRegistrar.checkDevice(ctx);
                        GCMRegistrar.checkManifest(ctx);

                        registerReceiver(mHandleMessageReceiver, new IntentFilter(
                                Constants.DISPLAY_MESSAGE_ACTION));
                        final String regId = GCMRegistrar.getRegistrationId(ctx);
                        if (regId.equals("")) {
                            GCMRegistrar.register(ctx, Constants.GOOGLE_SENDER_ID);
                        } else {
                            SharedPreferences.Editor editor = preference.edit();
                            editor.putString("GCM_REGISTERED_ID", regId);
                            editor.commit();
                            if (GCMRegistrar.isRegisteredOnServer(ctx)) {
                                MyApplication.register(regId);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                MyApplication.register(registrationId);
            }
            return null;
        }
    }

    public void saveinPriference( String name, String dob,String Age, String city, String gender, String profile_img_url, String givvrTotal, String yourTotal,String Your_Rank){
        SharedPreferences.Editor editor = preference.edit();
        editor.putString("name", name);
        editor.putString("dob", dob);
        editor.putString("Age", Age);
        editor.putString("city", city);
        editor.putString("gender", gender);
        editor.putString("profile_img_url", profile_img_url);
        editor.putString("givvrTotal", givvrTotal);
        editor.putString("yourTotal", yourTotal);
        editor.putString("Your_Rank", Your_Rank);

        editor.commit();
        //editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(dataUpdate) {
            new fetchHomeData().execute();
            dataUpdate = false;
        }       Log.d("Home", "The onResume() event");
    }

    class StopNotificationAsyn extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                MyApplication.unregister("unregister");
            } catch (Exception e) {
                //  Log.e("UnRegister Receiver Error","> " + e.getMessage());
            }
            return null;
        }

    }
}
