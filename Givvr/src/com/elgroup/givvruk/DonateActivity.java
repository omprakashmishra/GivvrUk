package com.elgroup.givvruk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tech.givvruk.utils.CallWebService;
import com.tech.givvruk.utils.CallbackInterface;
import com.tech.givvruk.utils.ImageLoading;
import com.tech.givvruk.utils.UrlList;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mangal on 11/9/2015.
 */
public class DonateActivity extends Activity implements CallbackInterface {

    private ImageLoading loading;
    private ProgressDialog progressDialog;
    private View view;
    private boolean isCalled = false;
    private ImageView donateIMG;
    private TextView txtAmountValue;
    private TextView txtTotalAmountValue;
    private ProgressBar donateProgressBar;
    static SharedPreferences preference;
    private String accesstoken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donate_activity);

        preference=MyApplication.preference;
        InitViews();
    }

    private void InitViews() {

        accesstoken = preference.getString("accesstoken", "");

        donateIMG = (ImageView) findViewById(R.id.donateIMG);
        txtAmountValue = (TextView) findViewById(R.id.txtAmountValue);
        txtTotalAmountValue = (TextView) findViewById(R.id.txtTotalAmountValue);
        donateProgressBar = (ProgressBar) findViewById(R.id.donateProgressBar);
        loading = new ImageLoading(this);
        CallWebService.getInstance(this).callGetWevService(UrlList.DONATE_DATA+"?accesstoken="+accesstoken, this);

    }

    @Override
    public void onSuccess(String response) {

        try {
            JSONObject object = new JSONObject(response);
            String img ="http://teamgivvr.co.uk/donateimage/" + object.getString("bgimage");
            String amountValue = object.getString("totaldonation");
            String totalAmountValue = object.getString("target");

            txtAmountValue.setText(Html.fromHtml("<b>" + amountValue + "</b> " + " so far"));
            txtTotalAmountValue.setText(totalAmountValue);

            loading.LoadImage(img, donateIMG, null);

            donateProgressBar.setMax(Integer.parseInt(String.valueOf(Math.round(Double.parseDouble(totalAmountValue)))));
            donateProgressBar.setProgress(Integer.parseInt((String.valueOf(Math.round(Double.parseDouble(amountValue))))));

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onFailure() {

    }

    public void goback(View v) {
        onBackPressed();
    }
    public void openWeb(View v){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://givvr.co.uk/payment.php?accesstoken="+accesstoken));
       // Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://givvr.com/payment.php"));
        startActivity(browserIntent);
    }
}
