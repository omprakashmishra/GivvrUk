package com.tech.givvruk.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.elgroup.givvruk.R;
import com.tech.givvruk.utils.Constants;

public class TermsServiceActivity extends Activity{
	String action;
	private WebView webview;
    ProgressBar progressLoading;
    Context ctx;
@SuppressLint("SetJavaScriptEnabled")
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.terms_service);
	Intent intent= getIntent();
	action = intent.getAction();
	ctx = this;
	webview = (WebView)findViewById(R.id.webViewTerms);
	progressLoading = (ProgressBar) findViewById(R.id.webviewTermsLoading);
	progressLoading.setVisibility(View.VISIBLE);
    WebSettings settings = webview.getSettings();
    settings.setJavaScriptEnabled(true);
    webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
    //http://teamgivvr.com/termsnconditions.php
   // webview.loadUrl("http://teamgivvr.com/privacypolicy.php");
    webview.loadUrl("http://teamgivvr.com/termsnconditions.php");
    webview.setWebViewClient(new WebViewClient() {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        
        public void onPageFinished(WebView view, String url) {
            progressLoading.setVisibility(View.INVISIBLE);
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        	Toast t = Toast.makeText(ctx, "No internet!", Toast.LENGTH_SHORT);
            t.setGravity(Gravity.CENTER, 0, 0);
            t.show();
        }
    });
    
}

public void gotoBackTermsService(View v)
{
	finish();
	if(action.contentEquals(Constants.Action.ACTION_START_ACTIVITY))
	{
		overridePendingTransition(R.anim.helpscreen_in_to_down, R.anim.helpscreen_out_to_down);
	}
	else
	{
	overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
	}
}
@Override
public void onBackPressed() {
	super.onBackPressed();
	if(action.contentEquals(Constants.Action.ACTION_START_ACTIVITY))
	{
		overridePendingTransition(R.anim.helpscreen_in_to_down, R.anim.helpscreen_out_to_down);
	}
	else
	{
	overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
	}
}

}
