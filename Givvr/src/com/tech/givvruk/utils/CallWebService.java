package com.tech.givvruk.utils;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Mangal on 11/7/2015.
 */
public class CallWebService {
    private static Context mContext;
    private static CallWebService callWebService = null;
    private CallbackInterface callbackInterface = null;

    public static CallWebService getInstance(Context context) {
        mContext = context;
        if (callWebService == null)
            callWebService = new CallWebService();
        return callWebService;

    }

    private void CallbackInterface() {

    }

    public void callGetWevService(String Url, CallbackInterface callbackInterface) {
        this.callbackInterface = callbackInterface;
        new HitGetAsyncTask().execute(Url);
    }

    private HttpResponse usingGet(String url) {
        String responseData = "";
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        HttpResponse response = null;
        try {
            response = client.execute(request);

        } catch (IOException e) {
            e.printStackTrace();
        }

        /*UserFunctions userFunction = new UserFunctions(mContext);
        JSONObject json = userFunction.getRanking("");
        return json;*/
        return response;
    }

    public class HitGetAsyncTask extends AsyncTask<String, Void, HttpResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(final HttpResponse response) {
            super.onPostExecute(response);

            HttpResponse resp = response;
            BufferedReader rd = null;

            if (resp.getStatusLine().getStatusCode() == 200) {

                try {
                    rd = new BufferedReader(
                            new InputStreamReader(resp.getEntity().getContent()));
                    StringBuffer result = new StringBuffer();
                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                    String responseData = result.toString();

                    callbackInterface.onSuccess(responseData);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                callbackInterface.onFailure();
            }


        }

        @Override
        protected HttpResponse doInBackground(String... params) {


            String url = params[0];

            return usingGet(url);
        }
    }


}
