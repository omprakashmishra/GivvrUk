package com.tech.givvruk.utils;

import java.util.ArrayList;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.elgroup.givvruk.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomArrayAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    ArrayList<Friends> data = new ArrayList<Friends>();
    static DisplayImageOptions options;
    public CustomArrayAdapter(Context context, ArrayList<Friends> friendsList) {
        super();
        data.clear();
        data=friendsList;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        options = new DisplayImageOptions.Builder()
		.showImageOnFail(R.drawable.profile)
		.resetViewBeforeLoading(true)
		.cacheOnDisk(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.considerExifParams(true)
		.displayer(new RoundedBitmapDisplayer(300))
		.build();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_friends_item_layout, parent, false);
            holder = new Holder();
            holder.textView = (TextView) convertView.findViewById(R.id.txtFriendsName);
            holder.imageFriendsDp= (ImageView) convertView.findViewById(R.id.imageFriendsDp);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.textView.setText(data.get(position).getFRIEND_NAME());
	    String img_value = "http://graph.facebook.com/"+data.get(position).getFRIEND_ID()+"/picture?type=large";
        Utilities.downloadImage(img_value, holder.imageFriendsDp, options, null);
        return convertView;
    }

    private static class Holder {
        public TextView textView;
        public ImageView imageFriendsDp ;
    }

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
