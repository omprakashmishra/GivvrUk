package com.elgroup.givvruk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.elgroup.givvruk.Models.RankingModel;
import com.tech.givvruk.utils.CallWebService;
import com.tech.givvruk.utils.CallbackInterface;
import com.tech.givvruk.utils.ImageLoading;
import com.tech.givvruk.utils.UrlList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mangal on 11/7/2015.
 */
public class Ranking extends Activity implements CallbackInterface {
    private ListView listView;
    private int i = 0;
    private View listViewHeader = null;
    private RankingAdapter adapter;
    SharedPreferences preference;
    static String acc;
    // private DisplayImageOptions options;
    private ImageLoading loading;
    private ProgressDialog progressDialog;
    private View view;
    private boolean isCalled = false;
    private View footerView;
    private ProgressBar footerProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking);

        InitViews();
    }

    private void InitViews() {

        preference = MyApplication.preference;
        acc=preference.getString("accesstoken", "");

        listView = (ListView) findViewById(R.id.listView);
        loading = new ImageLoading(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        footerView = LayoutInflater.from(this).inflate(R.layout.footer_view, null);
        footerProgressBar = (ProgressBar) footerView.findViewById(R.id.progressBar);

        listView.addFooterView(footerView);
      /*  options = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.profile_no_photo)
                .showImageOnLoading(getResources().getDrawable(R.drawable.profile_no_photo))
                .showStubImage(R.drawable.profile_no_photo)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(false)
                .cacheInMemory(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(300))
                .build();*/
        adapter = new RankingAdapter(this, R.layout.ranking_list_item);
        listView.setAdapter(adapter);

        //******for bottom view
        setValueinPBottomView();
    }

    private void setValueinPBottomView() {

       final TextView txtRankCircle = (TextView) findViewById(R.id.txtRankCircle);
        CircleImageView imgProfile = (CircleImageView) findViewById(R.id.imgProfile);
        TextView txtUserName = (TextView) findViewById(R.id.txtUserName);
        TextView txtAddress = (TextView) findViewById(R.id.txtAddress);
       final TextView txtAmount = (TextView) findViewById(R.id.txtAmount);
        txtUserName.setText(getIntent().getStringExtra("name"));
        txtAddress.setText(getIntent().getStringExtra("city"));
        txtRankCircle.setText(getIntent().getStringExtra("your_rank"));
        txtAmount.setText("£" + " " + getIntent().getStringExtra("your_total"));

        //*************
        String  fbid = preference.getString("facebook_id", "");
        String primg;
        if(!fbid.isEmpty()){
            String img_value = "http://graph.facebook.com/" + fbid + "/picture?type=large";
            primg=img_value;
        }else{
            primg=getIntent().getStringExtra("PROFILE_IMAGE_URL");
        }
        // loading.LoadImage(getIntent().getStringExtra("PROFILE_IMAGE_URL"), imgProfile, null);
        loading.LoadImage(primg, imgProfile, null);

        //profile data get url==UrlList.PROFILE;
        String aaa=UrlList.PROFILE+"?accesstoken="+preference.getString("accesstoken", "");
        CallWebService.getInstance(this).callGetWevService(aaa, new CallbackInterface() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject Object=new JSONObject(response);
                    txtRankCircle.setText(Object.optString("your_rank"));
                    txtAmount.setText("£" + " " + Object.optString("your_total"));
                    //InitViews();
                    CallWebService.getInstance(Ranking.this).callGetWevService(createUrl(), Ranking.this);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure() {

            }
        });
       //----------------
    }

    @Override
    public void onSuccess(String response) {
        progressDialog.dismiss();
        try {
            JSONArray array = new JSONArray(response);
            if (array.length() > 0) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    RankingModel model = new RankingModel();
                    String firstName = object.optString("name");
                    try {
                        firstName = firstName.substring(0, firstName.indexOf(" "));
                    } catch (Exception e) {
                    }
                    model.setName(firstName);
                    model.setCity(object.optString("city"));
                    model.setProfile_img_url(UrlList.domainURL + object.optString("profile_img_url"));
                    model.setFb_id(object.optString("fb_id"));
                    model.setUsertotal("£" + " " + new DecimalFormat("##.##").format(Double.parseDouble(object.optString("usertotal"))));

                    adapter.add(model);
                    // adapter.notifyDataSetChanged();

                }

                if (!isCalled) {
               /* view = findViewById(R.id.includedLayout);
                view.setVisibility(View.GONE);*/
                    //  createListViewHeader();
                    listView.addHeaderView(createListViewHeader());

                    isCalled = true;
                }
               // setValueinPBottomView();
            } else {
                adapter.notifyNoMoreItems();
            }
        } catch (Exception e) {
            e.printStackTrace();
            adapter.notifyNoMoreItems();

        }
    }

    @Override
    public void onFailure() {
        progressDialog.dismiss();
        adapter.notifyNoMoreItems();
    }

    public String createUrl() {
//http://api.teamgivvr.co.uk/topraisedusersnew.php?count=12&accesstoken=
        String url = UrlList.RANKING_URL + String.valueOf(i)+"&accesstoken="+acc;
        System.out.println("url-------------->"+url);
        i = i + 10;
        return url;

    }

    private View createListViewHeader() {

        LayoutInflater inflater = LayoutInflater.from(this);
        listViewHeader = inflater.inflate(R.layout.ranking_header, null);

        CircleImageView imgFirstCircle = (CircleImageView) listViewHeader.findViewById(R.id.imgFirstCircle);
        CircleImageView imgSecondCircle = (CircleImageView) listViewHeader.findViewById(R.id.imgSecondCircle);
        CircleImageView imgThirdCircle = (CircleImageView) listViewHeader.findViewById(R.id.imgThirdCircle);

        TextView txtNameFirst = (TextView) listViewHeader.findViewById(R.id.txtNameFirst);
        TextView txtNameSecond = (TextView) listViewHeader.findViewById(R.id.txtNameSecond);
        TextView txtNameThird = (TextView) listViewHeader.findViewById(R.id.txtNameThird);

        TextView txtAmountFirst = (TextView) listViewHeader.findViewById(R.id.txtAmountFirst);
        TextView txtAmountSecond = (TextView) listViewHeader.findViewById(R.id.txtAmountSecond);
        TextView txtAmountThird = (TextView) listViewHeader.findViewById(R.id.txtAmountThird);

        TextView txtFirstCircle=(TextView) listViewHeader.findViewById(R.id.txtFirstCircle);
        TextView txtThirdCircle=(TextView) listViewHeader.findViewById(R.id.txtThirdCircle);


        String imgOne,imgzero,imgtwo;

        if(adapter.getCount()>0){

            imgzero=adapter.getItem(0).getProfile_img_url();
            if(!adapter.getItem(0).getFb_id().isEmpty()){
                imgzero="http://graph.facebook.com/"+adapter.getItem(0).getFb_id()+"/picture?type=large";
            }
            loading.LoadImage(imgzero, imgSecondCircle, null);
            txtNameSecond.setText(adapter.getItem(0).getName());
            txtAmountSecond.setText(adapter.getItem(0).getUsertotal());
            adapter.remove(adapter.getItem(0));

        }else {
            adapter.notifyNoMoreItems();
        }


        if(adapter.getCount()>0){
            imgzero=adapter.getItem(0).getProfile_img_url();
            if(!adapter.getItem(0).getFb_id().isEmpty()){
                imgzero="http://graph.facebook.com/"+adapter.getItem(0).getFb_id()+"/picture?type=large";
            }
            loading.LoadImage(imgzero, imgFirstCircle, null);
            txtNameFirst.setText(adapter.getItem(0).getName());
            txtAmountFirst.setText(adapter.getItem(0).getUsertotal());
            adapter.remove(adapter.getItem(0));
        }else {
            txtFirstCircle.setVisibility(View.GONE);
            imgFirstCircle.setVisibility(View.GONE);
            txtNameFirst.setVisibility(View.GONE);
            txtAmountFirst.setVisibility(View.GONE);

            adapter.notifyNoMoreItems();
        }

        if(adapter.getCount()>0){
            imgzero=adapter.getItem(0).getProfile_img_url();
            if(!adapter.getItem(0).getFb_id().isEmpty()){
                imgzero="http://graph.facebook.com/"+adapter.getItem(0).getFb_id()+"/picture?type=large";
            }
            loading.LoadImage(imgzero, imgThirdCircle, null);
            txtNameThird.setText(adapter.getItem(0).getName());
            txtAmountThird.setText(adapter.getItem(0).getUsertotal());
            adapter.remove(adapter.getItem(0));

           // adapter.notifyNoMoreItems();
        }else {
            txtThirdCircle.setVisibility(View.GONE);
            imgThirdCircle.setVisibility(View.GONE);
            txtNameThird.setVisibility(View.GONE);
            txtAmountThird.setVisibility(View.GONE);
           adapter.notifyNoMoreItems();
        }

       /* imgOne=adapter.getItem(1).getProfile_img_url();

        imgtwo=adapter.getItem(2).getProfile_img_url();
        //for facebook img
        //"http://graph.facebook.com/"+id+"/picture?type=large"
        if(!adapter.getItem(1).getFb_id().isEmpty()){
             imgOne="http://graph.facebook.com/"+adapter.getItem(1).getFb_id()+"/picture?type=large";

        } if(!adapter.getItem(2).getFb_id().isEmpty()){
            imgtwo="http://graph.facebook.com/"+adapter.getItem(2).getFb_id()+"/picture?type=large";
        }
        loading.LoadImage(imgOne, imgFirstCircle, null);

        loading.LoadImage(imgtwo, imgThirdCircle, null);

        *//*loading.LoadImage(adapter.getItem(1).getProfile_img_url(), imgFirstCircle, null);
        loading.LoadImage(adapter.getItem(0).getProfile_img_url(), imgSecondCircle, null);
        loading.LoadImage(adapter.getItem(2).getProfile_img_url(), imgThirdCircle, null);*//*

        txtNameFirst.setText(adapter.getItem(1).getName());

        txtNameThird.setText(adapter.getItem(2).getName());

        txtAmountFirst.setText(adapter.getItem(1).getUsertotal());

        txtAmountThird.setText(adapter.getItem(2).getUsertotal());

        for (int i = 0; i < 3; i++)

        //adapter.remove(adapter.getItem(0));
        // adapter.remove(adapter.getItem(1));
        // adapter.remove(adapter.getItem(2));*/

        return listViewHeader;
    }

/*
        Ranking Adapter for showing listview Data
 */

    public void goback(View v) {
        onBackPressed();
    }


    //adapter
    public class RankingAdapter extends ArrayAdapter<RankingModel> {
        boolean mHasMoreItems = true;

        private Context mContext = null;
        private LayoutInflater inflater;


        public RankingAdapter(Context context, int resource) {
            super(context, resource);
            mContext = context;
            inflater = LayoutInflater.from(mContext);

        }

        public void notifyNoMoreItems() {
            mHasMoreItems = false;
            footerProgressBar.setVisibility(View.GONE);
        }


        @Override
        public int getCount() {
            System.out.println("count-------->"+super.getCount());
            return super.getCount();
        }

        @Override
        public RankingModel getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            System.out.println("getView " + position + " " + convertView);

            if (position == getCount() - 1 && mHasMoreItems) {

                CallWebService.getInstance(mContext).callGetWevService(createUrl(), Ranking.this);

            }
            View view = convertView;
            ViewHolder holder = null;

            if (convertView == null) {
                view = inflater.inflate(R.layout.ranking_list_item, null);
                holder = new ViewHolder();
                holder.txtRankCircle = (TextView) view.findViewById(R.id.txtRankCircle);
                holder.imgProfile = (CircleImageView) view.findViewById(R.id.imgProfile);
                holder.txtUserName = (TextView) view.findViewById(R.id.txtUserName);
                holder.txtAddress = (TextView) view.findViewById(R.id.txtAddress);
                holder.txtAmount = (TextView) view.findViewById(R.id.txtAmount);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            RankingModel model = getItem(position);
            if (model != null) {

                holder.txtRankCircle.setText(String.valueOf(position + 4));
                holder.txtUserName.setText(model.getName());
                holder.txtAddress.setText(model.getCity());
                holder.txtAmount.setText(model.getUsertotal());

                String userIMG="";
                if(!model.getFb_id().isEmpty()){
                   //http://graph.facebook.com/"+id+"/picture?type=large
                    userIMG="http://graph.facebook.com/"+model.getFb_id()+"/picture?type=large";
                }else{
                    userIMG=model.getProfile_img_url();
                }

                loading.LoadImage(userIMG, holder.imgProfile, null);
            }


            return view;
        }
    }

    class ViewHolder {
        TextView txtRankCircle, txtUserName, txtAddress, txtAmount;
        CircleImageView imgProfile;
    }
}
