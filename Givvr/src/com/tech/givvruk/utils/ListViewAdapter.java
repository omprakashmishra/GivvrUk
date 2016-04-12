package com.tech.givvruk.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.elgroup.givvruk.R;
import com.elgroup.givvruk.SponsorsActivity;
import com.facebook.share.widget.LikeView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;
import java.util.HashMap;

public class ListViewAdapter extends BaseAdapter {

   Context context;
   LayoutInflater inflater;
   ArrayList<HashMap<String, String>> data;
   HashMap<String, String> resultp = new HashMap<String, String>();
   DisplayImageOptions options;
   public ListViewAdapter(Context context,
           ArrayList<HashMap<String, String>> arraylist) {
       this.context = context;
       data = arraylist;
       //Settings.sdkInitialize(context);
       options = new DisplayImageOptions.Builder()
		.resetViewBeforeLoading(true)
		.cacheOnDisk(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.considerExifParams(true)
		.displayer(new RoundedBitmapDisplayer(170))
		.build();
   }

   @Override
   public int getCount() {
       return data.size();
   }

   @Override
   public Object getItem(int position) {
       return null;
   }

   @Override
   public long getItemId(int position) {
       return 0;
   }

   public View getView(final int position, View itemView, ViewGroup parent) {
       TextView sponsorName;
       TextView sponsorDescription;
       ImageView sponsorImage;
       
       inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       if(itemView==null)
       itemView = inflater.inflate(R.layout.sponsors_list_layout, parent, false);
       resultp = data.get(position);
       sponsorName = (TextView) itemView.findViewById(R.id.sponsorName);
       sponsorDescription = (TextView) itemView.findViewById(R.id.sponsorDescription);
       LikeView likeView = (LikeView) itemView.findViewById(R.id.like_view);
       String object_id = resultp.get(SponsorsActivity.FBLIKEPAGEURL);
       if(!object_id.contains("http"))
		{
		 	object_id = "http://"+object_id;
		}
	   likeView.setObjectIdAndType(object_id, LikeView.ObjectType.OPEN_GRAPH);
	   likeView.setLikeViewStyle(LikeView.Style.BUTTON);
       sponsorImage = (ImageView) itemView.findViewById(R.id.sponsorImage);
       sponsorName.setText(resultp.get(SponsorsActivity.SPONSORCOMPANY));
       sponsorDescription.setText(resultp.get(SponsorsActivity.SPONSORDESCRIPTION));
       Utilities.downloadImage(resultp.get(SponsorsActivity.SPONSORLOGO), sponsorImage, options, null);

       sponsorImage.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View view) {
               String website = data.get(position).get(SponsorsActivity.WEBSITE);
               if(!website.contains("http"))
               {
                   website = "http://"+website;
               }
               Intent i = new Intent(Intent.ACTION_VIEW);
               i.setData(Uri.parse(website));
               context.startActivity(i);
           }
       });

       sponsorName.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			
			String website = data.get(position).get(SponsorsActivity.WEBSITE);
			if(!website.contains("http"))
			{
				website = "http://"+website;
			}
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(website));
			context.startActivity(i);
		}
	});  
       return itemView;
   }
}