package com.tech.givvruk.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.elgroup.givvruk.MyApplication;
import com.facebook.AccessToken;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserFunctions {

    private static String loginURL = UrlList.LOGIN;
    private static String FB_loginURL = UrlList.FBLOGIN;
    private static String registerURL = UrlList.REGISTER;
    private static String profileURL = UrlList.PROFILE;
    private static String logoutURL = UrlList.LOGOUT;
    private static String signupURL = UrlList.SIGNUP;
    private static String projectsURL = UrlList.PROJECTS;
    private static String getProjectURL = UrlList.PROJECT;
    private static String getVideosURL = UrlList.VIDEOS;
    private static String getSponsorsURL = UrlList.SPONSORS;
    private static String videoPlayedViewUrl = UrlList.VIEW;
    private static String videoLikeUrl = UrlList.LIKE;
    private static String forgotPasswordUrl = UrlList.FORGOT_PASSWORD;
    StringBuffer sb;
    String line;
    InputStream is;
    JSONObject jObj = null;
    String json = "";
    private JSONParser jsonParser;
    static SharedPreferences preference;
    private String accesstoken;

    public UserFunctions(Context ctx) {
        jsonParser = new JSONParser(ctx);

        preference = MyApplication.preference;
        accesstoken = preference.getString("accesstoken", "");
    }

    public JSONObject getSponsors() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("accesstoken", accesstoken));
        JSONObject json = jsonParser.getJSONFromUrl(getSponsorsURL, params);
        return json;
    }

    //***********************
    public Map return_map(List<NameValuePair> arraylist) {
        HashMap<String, String> params = new HashMap<>();
        for (NameValuePair nameValuePair : arraylist) {
            String key = nameValuePair.getName();
            String value = nameValuePair.getValue();
            params.put(key,value);
        }
        return params;
    }
   /* public String AccessTokenR(){
      return accesstoken;
    }*/

    public JSONObject getProjects() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("accesstoken", accesstoken));
        JSONObject json = jsonParser.getJSONFromUrl(projectsURL, params);
        return json;
    }

    public JSONObject logoutUser() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject json = jsonParser.getJSONFromUrl(logoutURL, params);
        return json;
    }

    public JSONObject getVideos(String projectId) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("project_id", projectId));
        params.add(new BasicNameValuePair("accesstoken", accesstoken));
        JSONObject json = jsonParser.getJSONFromUrl(getVideosURL, params);

        return json;
    }

    public JSONObject loginUser(String email, String flagLoginType, String flagLoginGoogle) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("session_email", email));
        params.add(new BasicNameValuePair("facebook", flagLoginType));
        params.add(new BasicNameValuePair("google", flagLoginGoogle));
        JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
        return json;
    }

    public JSONObject videoPlayed(String vId, String projectId) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("video_id", vId));
        params.add(new BasicNameValuePair("project_id", projectId));
        params.add(new BasicNameValuePair("accesstoken", accesstoken));
        JSONObject json = jsonParser.getJSONFromUrl(videoPlayedViewUrl, params);
        return json;
    }

    public JSONObject likeVideo(String vId) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("video_id", vId));
        params.add(new BasicNameValuePair("event", "liked"));
        params.add(new BasicNameValuePair("accesstoken", accesstoken));
        JSONObject json = jsonParser.getJSONFromUrl(videoLikeUrl, params);
        return json;
    }

    public JSONObject addProject(String projectID, String userID) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("project_id", projectID));
        params.add(new BasicNameValuePair("user_id", userID));
        params.add(new BasicNameValuePair("accesstoken", accesstoken));
        JSONObject json = jsonParser.getJSONFromUrl(projectsURL, params);
        return json;
    }

    public JSONObject signup(String email, String password) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonParser.getJSONFromUrl(signupURL, params);
        return json;
    }

    public Map<String, String> Vsignup(String email, String password) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("password", password);
        return params;
    }

    public JSONObject googleLogin(String email, String google) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("google_login", google));
        JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
        return json;
    }

    public JSONObject facebookLogin(String email, String facebook, String name, String dob, String gender, String city, String fbid) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("facebook_login", facebook));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("dob", dob));
        params.add(new BasicNameValuePair("gender", gender));
        params.add(new BasicNameValuePair("city", city));
        params.add(new BasicNameValuePair("fbid", fbid));
        JSONObject json = jsonParser.getJSONFromUrl(FB_loginURL, params);
        return json;
    }

    public Map VfacebookLogin(String email, String facebook, String name, String dob, String gender, String city, String fbid) {
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("facebook_login", facebook);
        params.put("name", name);
        params.put("dob", dob);
        params.put("gender", gender);
        params.put("city", city);
        params.put("fbid", fbid);
        return params;
    }

    public JSONObject loginMe(String email, String password) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
        return json;
    }

    public Map VloginMe(String email, String password) {
        // Building Parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("password", password);
        // JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
        return params;
    }
    //*** Ranking
   /* public JSONObject getRanking(String accesstoken) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("accesstoken", accesstoken));
        /*/

    /**
     * *******
     * JSONObject json = jsonParser.getJSONFromUrl(profileURL, params);
     * return json;
     * }
     */


    public JSONObject createProfile(String name, String gender, String dob, String city, String imageString, String accesstoken) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("gender", gender));
        params.add(new BasicNameValuePair("dob", dob));
        params.add(new BasicNameValuePair("city", city));
        params.add(new BasicNameValuePair("image", imageString));
        params.add(new BasicNameValuePair("accesstoken", accesstoken));
        // editor.putString("accesstoken", jsonResponse.getString("accesstoken"));
        JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
        return json;
    }

    public Map VcreateProfile(String name, String gender, String dob, String city, String imageString) {
        HashMap<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("gender", gender);
        params.put("dob", dob);
        params.put("city", city);
        params.put("image", imageString);
        params.put("accesstoken", accesstoken);

        return params;
    }

    public JSONObject getProfile(String accesstoken) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("accesstoken", accesstoken));
        //**********
        JSONObject json = jsonParser.getJSONFromUrl(profileURL, params);
        ;
        return json;
    }

    public JSONObject getProject(String projectID) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("project_id", projectID));
        params.add(new BasicNameValuePair("accesstoken", accesstoken));
        JSONObject json = jsonParser.getJSONFromUrl(getProjectURL, params);
        return json;
    }

    public JSONObject forgotPassword(String email) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email", email));
        // params.add(new BasicNameValuePair("accesstoken", accesstoken));
        JSONObject json = jsonParser.getJSONFromUrl(forgotPasswordUrl, params);
        return json;
    }

    //----------------------testing...
    public Map<String, String> VforgotPassword(String email) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        return params;
    }

    public JSONObject verifyCode(String email, String code) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("code", code));
        JSONObject json = jsonParser.getJSONFromUrl(UrlList.VERIFY_EMAIL, params);
        return json;
    }

    public JSONObject resetPassword(String email, String password) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonParser.getJSONFromUrl(UrlList.FORGOT_PASSWORD_RESET, params);
        return json;
    }

    public JSONObject registerGCM(String appKey, String deviceType) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("device_id", appKey));
        params.add(new BasicNameValuePair("device_type", deviceType));
        JSONObject json = jsonParser.getJSONFromUrl(Constants.SERVER_URL, params);
        return json;
    }

    public JSONObject verifyEmail(String email, String code) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("code", code));
        JSONObject json = jsonParser.getJSONFromUrl(UrlList.VERIFY_EMAIL, params);
        return json;
    }

    public JSONObject getProjectHistory() {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("accesstoken", accesstoken));
        JSONObject json = jsonParser.getJSONFromUrl(UrlList.PROJECTS_HISTORY, params);
        return json;
    }

    public JSONObject sendVideoEvents(String videoId, String event) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("video_id", videoId));
        params.add(new BasicNameValuePair("event", event));
        JSONObject json = jsonParser.getJSONFromUrl(UrlList.LIKE, params);
        return json;
    }

    public JSONObject unregisterGCM(String flag) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("unregister", flag));
        JSONObject json = jsonParser.getJSONFromUrl(Constants.SERVER_URL, params);
        return json;
    }

    public JSONObject sendFeedback(String name, String email, String message) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("message", message));
        JSONObject json = jsonParser.getJSONFromUrl(UrlList.FEEDBACK, params);
        return json;
    }

    public JSONObject ratingApp() {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("accesstoken", accesstoken));
        JSONObject json = jsonParser.getJSONFromUrl(UrlList.RATING, params);
        return json;
    }

    public JSONObject rateTheApp(String rated) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("rated", rated));
        params.add(new BasicNameValuePair("accesstoken", accesstoken));
        JSONObject json = jsonParser.getJSONFromUrl(UrlList.RATING, params);
        return json;
    }

    public boolean isLogedIn() {
        return AccessToken.getCurrentAccessToken() != null;
    }
}