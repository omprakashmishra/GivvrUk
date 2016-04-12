package com.tech.givvruk.utils;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.elgroup.givvruk.R;

import java.util.Map;

import dmax.dialog.SpotsDialog;

/**
 * Created by windows 7 on 12/11/2015.
 */
public class GlobalVolley_cl  {
    private Context mycontext;
    private String murl;
    private Map<String,String> map_vlaue;
    private CallbackInterface myListener;
    //private ProgressDialog progressbar;
    private SpotsDialog MyDialog;

    public GlobalVolley_cl(Context context ,String url, Map<String,String> map,CallbackInterface Listener) {
       super();
        this.mycontext=context;
        this.murl=url;
        this.map_vlaue=map;
        this.myListener=Listener;

        MyDialog = new SpotsDialog(mycontext, R.style.Custom);
        MyDialog.show();


       /* progressbar = new ProgressDialog(mycontext);
        progressbar.setMessage("Loading...");
        progressbar.setCancelable(false);
        progressbar.show();*/

        webservices();
    }

    private void webservices() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, murl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //-------------------------------------------------------------------------->Response Listener
                        MyDialog.dismiss();
                      //  progressbar.dismiss();
                        myListener.onSuccess(response);

                       /* if(response.trim().equals("success")){
                           // openProfile();
                        }else{
                           // Toast.makeText(getApplication(), response, Toast.LENGTH_LONG).show();
                        }*/

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //-------------------------------------------------------------------------->error Listener
                        MyDialog.dismiss();
                       // progressbar.dismiss();
                        myListener.onFailure();
                      //  Toast.makeText(getApplication(),error.toString(),Toast.LENGTH_LONG ).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
               // map_vlaue = new HashMap<String,String>();
                return map_vlaue;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mycontext);
        requestQueue.add(stringRequest);
    }

}

