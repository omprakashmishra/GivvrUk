package com.tech.givvruk.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.elgroup.givvruk.MyApplication;

import android.content.Context;

public class JSONParser {
	 
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    Context ctx;
    String line = null;
  public JSONParser(Context ctx) {
	  this.ctx=ctx;
    }
    public JSONObject getJSONFromUrl(String url, List<NameValuePair> params) {
  		DefaultHttpClient httpClient = MyApplication.httpclient;
  		HttpPost httpPost = new HttpPost(url);
         try {
  			httpPost.setEntity(new UrlEncodedFormEntity(params));
  			HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is= httpEntity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            jObj = new JSONObject(json);
            return jObj;
  		} catch (UnsupportedEncodingException e1) {
  			e1.printStackTrace();
  		} catch (ClientProtocolException e) {
  			e.printStackTrace();
  		} catch (IOException e) {
  			e.printStackTrace();
  		} catch (JSONException e) {
         }
         return jObj;
    }
}