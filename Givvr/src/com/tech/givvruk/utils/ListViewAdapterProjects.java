package com.tech.givvruk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.elgroup.givvruk.MyApplication;
import com.elgroup.givvruk.ProjectActivity;
import com.elgroup.givvruk.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.tech.givvruk.settings.ContactUsActivity;

import java.util.ArrayList;

public class ListViewAdapterProjects extends BaseAdapter implements Filterable {

    Context context;

    ArrayList<Project> data = new ArrayList<Project>();
    ArrayList<Project> orig;
    DisplayImageOptions options, options2;
    Boolean FLAG_PROJECT_NAVIGATION = false;
    SharedPreferences preference;
    int pos_rate = 0;

    public ListViewAdapterProjects(Context context,
                                   ArrayList<Project> arraylist) {
        this.context = context;
        //data = arraylist;
        preference = MyApplication.preference;

        if (!preference.getBoolean("LAYOUT_RATEUS", false)) {
            data = arraylist;
        } else if (arraylist.size() >= 3) {
            int dataSize = arraylist.size() + 1;
            for (int i = 0; i < dataSize; i++) {
                if (i == 3) {
                    data.add(i, arraylist.get(0));
                } else if (i > 3) {
                    int pos = i - 1;
                    data.add(i, arraylist.get(pos));
                } else {
                    data.add(i, arraylist.get(i));
                }
            }
        } else if (arraylist.size() == 2) {
            int dataSize = arraylist.size() + 1;
            for (int i = 0; i < dataSize; i++) {
                if (i == 2) {
                    data.add(i, arraylist.get(0));
                }
               /*else if(i<2)
               {
        		   int pos = i-1;
        	   data.add(i, arraylist.get(pos));
        	   }*/
                else {
                    data.add(i, arraylist.get(i));
                }
            }
        } else if (arraylist.size() == 1) {
            int dataSize = arraylist.size() + 1;
            for (int i = 0; i < dataSize; i++) {
                data.add(i, arraylist.get(0));
            }
        } else {
            data = arraylist;
    	  /* int dataSize = arraylist.size()+1; 
           for(int i=0;i<dataSize;i++)
           {   
        	   if(i==2)
        	   {
        		   data.add(i, arraylist.get(0));
        	   }
        	   else
        	   {
        		   data.add(i, arraylist.get(i));
        	   }
           }*/
        }
        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        options2 = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(10))
                .considerExifParams(true)
                .build();

        String FLAG_CLASS_NAME = context.getClass().getSimpleName();
        if (FLAG_CLASS_NAME.contentEquals("ProjectsActivity")) {
            FLAG_PROJECT_NAVIGATION = true;
        }
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

    public View getView(final int position, View itemView, ViewGroup parent) {

        TextView projectName;
        TextView projectGoal;
        ImageView projectSmallImage, projectLargeImage;
        SeekBar projectProgress;
        RelativeLayout layoutProjectImage;
        TextView textProjectDaysLeft;
        TextView textSupporters;
        TextView textviewSupporters;
        TextView textViewProjectTag;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        if (preference.getBoolean("LAYOUT_RATEUS", false)) {
            if (data.size() == 2) {
                pos_rate = 1;
            } else if (data.size() == 3) {
                pos_rate = 2;
            } else if (data.size() > 3) {
                pos_rate = 3;
            }
        }
        if (position == pos_rate && preference.getBoolean("LAYOUT_RATEUS", false)) {
            itemView = inflater.inflate(R.layout.layout_rate_us, parent, false);
            final TextView textRateUsMessage = (TextView) itemView.findViewById(R.id.textRateUsMessage);
            final Button button1 = (Button) itemView.findViewById(R.id.button1);
            final Button button2 = (Button) itemView.findViewById(R.id.button2);
            button1.setText("Not really");
            button2.setText("YES!");
            textRateUsMessage.setText("Enjoying Givvr?");

            button1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (button1.getText().toString().contentEquals("Not really")) {
                        MyApplication.FLAG_LAYOUT_FEEDBACK = true;
                        textRateUsMessage.setText("Would you mind giving us feedback?");
                        button1.setText("No, thanks");
                        button2.setText("Ok, sure");
                    } else if (button1.getText().toString().contentEquals("No, thanks")) {
                        SharedPreferences.Editor editor = preference.edit();
                        editor.putBoolean("LAYOUT_RATEUS", false);
                        editor.putBoolean("RATED", true);
                        editor.apply();
                        editor.commit();
                        data.remove(pos_rate);
                        notifyDataSetChanged();
                    }
                }
            });
            button2.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (button2.getText().toString().contentEquals("YES!")) {
                        textRateUsMessage.setText("How about a rating, then?");
                        button1.setText("No, thanks");
                        button2.setText("Ok, sure");
                    } else if (button2.getText().toString().contentEquals("Ok, sure")) {
                        if (MyApplication.FLAG_LAYOUT_FEEDBACK) {
                            Intent in = new Intent(context, ContactUsActivity.class);
                            context.startActivity(in);
                            MyApplication.FLAG_LAYOUT_FEEDBACK = false;
                            SharedPreferences.Editor editor = preference.edit();
                            editor.putBoolean("LAYOUT_RATEUS", false);
                            editor.apply();
                            editor.commit();
                            data.remove(pos_rate);
                            notifyDataSetChanged();
                        } else {
                            MyApplication.gotoRateUs(context);
                            SharedPreferences.Editor editor = preference.edit();
                            editor.putBoolean("LAYOUT_RATEUS", false);
                            editor.putBoolean("RATED", true);
                            editor.apply();
                            editor.commit();
                            data.remove(pos_rate);
                            notifyDataSetChanged();
                            new ratingAsyn().execute();
                        }
                    }
                }
            });
        } else {
            itemView = inflater.inflate(R.layout.projects_list_layout, parent, false);
            projectName = (TextView) itemView.findViewById(R.id.titleProject);
            projectGoal = (TextView) itemView.findViewById(R.id.textProjectRaised);
            textProjectDaysLeft = (TextView) itemView.findViewById(R.id.textDaysLeft);
            textSupporters = (TextView) itemView.findViewById(R.id.textSupporters);
            textviewSupporters = (TextView) itemView.findViewById(R.id.textViewSupporters);
            projectProgress = (SeekBar) itemView.findViewById(R.id.projectProgress);
            projectSmallImage = (ImageView) itemView.findViewById(R.id.projectSmallImage);
            layoutProjectImage = (RelativeLayout) itemView.findViewById(R.id.layoutrl);
            projectLargeImage = (ImageView) itemView.findViewById(R.id.projectLargeImage);
            textViewProjectTag = (TextView) itemView.findViewById(R.id.projectTag);

            // final String projectTitle=data.get(position).getPROJECTNAME();
            // final String raised=data.get(position).getPROJECTRAISED();
            // final String goal=data.get(position).getPROJECTGOAL();
            // final String projectDescription=data.get(position).getPROJECTDESCRIPTION();
            // final String projectTag=data.get(position).getPROJECTTAG();
            // final String projectLargeImg=data.get(position).getPROJECTLARGEIMG();
            //final String projectSmallImg=data.get(position).getPROJECTSMALLIMG();
            // final String projectID=data.get(position).getPROJECTID();
            //final String charityID=data.get(position).getCHARITYID();
            //final String projectLENGTH = data.get(position).getPROJECTDAYSLEFT();
            //final String projectCOLORCODE = data.get(position).getPROJECTCOLORCODE();
            //final String projectSUPPORTERS = data.get(position).getPROJECTSUPPORTERS();

            textViewProjectTag.setText("\"" + data.get(position).getPROJECTTAG() + "\"");
            layoutProjectImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    ImageLoader.getInstance().clearMemoryCache();
                    //ImageLoader.getInstance().clearDiskCache();
                    Intent invok = new Intent(context, ProjectActivity.class);
                    invok.putExtra("project_title", data.get(position).getPROJECTNAME());
                    invok.putExtra("project_tag", data.get(position).getPROJECTTAG());
                    invok.putExtra("project_raised", data.get(position).getPROJECTRAISED());
                    invok.putExtra("project_goal", data.get(position).getPROJECTGOAL());
                    invok.putExtra("project_decription", data.get(position).getPROJECTDESCRIPTION());
                    invok.putExtra("project_large_image", data.get(position).getPROJECTLARGEIMG());
                    invok.putExtra("project_small_image", data.get(position).getPROJECTSMALLIMG());
                    invok.putExtra("project_id", data.get(position).getPROJECTID());
                    invok.putExtra("charity_id", data.get(position).getCHARITYID());
                    invok.putExtra("project_length", data.get(position).getPROJECTDAYSLEFT());
                    invok.putExtra("project_color", data.get(position).getPROJECTCOLORCODE());
                    context.startActivity(invok);
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                }
            });

            int numSupporter = Integer.parseInt(data.get(position).getPROJECTSUPPORTERS());
            if (numSupporter == 1) {
                textviewSupporters.setText(" supporter");
            }
            if (numSupporter >= 1000) {
                textSupporters.setText("1K");
            } else {
                textSupporters.setText(data.get(position).getPROJECTSUPPORTERS());
            }

            projectName.setText(data.get(position).getPROJECTNAME());
            String hexColor = /*"#"+*/data.get(position).getPROJECTCOLORCODE();
            if (!hexColor.contains("#"))
                hexColor = "#" + data.get(position).getPROJECTCOLORCODE();
            projectName.setTextColor(Color.parseColor(hexColor));
            textProjectDaysLeft.setText(data.get(position).getPROJECTDAYSLEFT());
            projectProgress.setMax(Integer.parseInt(data.get(position).getPROJECTGOAL()) * 100);
            projectGoal.setText("£" + data.get(position).getPROJECTRAISED());
            int progressValue = getConvertedValue(Float.parseFloat(data.get(position).getPROJECTRAISED()));
            projectProgress.setProgressDrawable(Utilities.MyProgressLayer(context, hexColor));
            projectProgress.setProgress(progressValue);
            Utilities.downloadImage(data.get(position).getPROJECTLARGEIMG(), projectLargeImage, options, null);
            Utilities.downloadImage(data.get(position).getPROJECTSMALLIMG(), projectSmallImage, options2, null);
        }
        return itemView;
    }

    public int getConvertedValue(float floatVal) {
        int intVal = 0;
        intVal = (int) (floatVal * 100);
        return intVal;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<Project> results = new ArrayList<Project>();
                if (orig == null)
                    orig = data;
                if (constraint != null) {
                    if (orig != null && orig.size() > 0) {
                        for (final Project g : orig) {
                            if (g.getPROJECTNAME().toLowerCase().contains(constraint.toString()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                data = (ArrayList<Project>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    class ratingAsyn extends AsyncTask<String, String, String> {
        SharedPreferences.Editor editor = preference.edit();

        @Override
        protected String doInBackground(String... params) {

            UserFunctions user = new UserFunctions(context);
            user.rateTheApp("1");
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

        }
    }
}