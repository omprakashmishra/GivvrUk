package com.elgroup.givvruk;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.tech.givvruk.utils.AlertMessage;
import com.tech.givvruk.utils.CallbackInterface;
import com.tech.givvruk.utils.ConnectionDetector;
import com.tech.givvruk.utils.Constants;
import com.tech.givvruk.utils.GlobalVolley_cl;
import com.tech.givvruk.utils.UrlList;
import com.tech.givvruk.utils.UserFunctions;
import com.tech.givvruk.utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;

public class CreateProfile extends Activity implements OnClickListener{

    EditText txtName,txtDob,txtCity;
    RadioGroup rGroup;
    RadioButton gender,rbtnFemale,rbtnMale;
    SharedPreferences preference;
    String profileStatus,imageStatus,activityFrom;
    ImageView image;
    private final int TAKE_PICTURE=0;
    private final int SELECT_FILE = 100;
    public static final int DETAILS_SAVED = 2;
    private int mYear;
    private int mMonth;
    private int mDay;
    static final int DATE_DIALOG_ID = 0;
    private ConnectionDetector cd;
    //String upLoadServerUri = UrlList.UPLOADIMAGE;
    static DisplayImageOptions options;
    String imageString = null, googleEmail;
    Context ctx;
    File destination;
    private Uri picUri;
    TextView textCreateProfile;
    private final int PIC_CROP = 201;
    String imageFilePath=null;
    private String accesstoken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_profile);
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        options = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.profile)
                .resetViewBeforeLoading(true)
                .cacheInMemory(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(170))
                .build();

        preference = com.elgroup.givvruk.MyApplication.preference;

        txtName=(EditText)findViewById(R.id.username_edittext);
        txtDob=(EditText)findViewById(R.id.dob_edittext);
        txtCity=(EditText)findViewById(R.id.city_edittext);
        rGroup=(RadioGroup)findViewById(R.id.rgroupGender);
        image=(ImageView)findViewById(R.id.profileImage);
        rbtnFemale = (RadioButton) findViewById(R.id.rbtnFemale);
        rbtnMale = (RadioButton) findViewById(R.id.rbtnMale);
        textCreateProfile=(TextView)findViewById(R.id.text_create_profile);

        String userName = preference.getString("user_name", "");
        accesstoken= preference.getString("accesstoken", "");

        if(userName!=null)
            txtName.setText(userName);

        if(com.elgroup.givvruk.MyApplication.FLAG_GOOGLE_LOGIN)
        {
            txtName.setText(getIntent().getStringExtra(Constants.Extra.GOOGLE_LOGIN_USER_NAME));
            googleEmail = getIntent().getStringExtra(Constants.Extra.GOOGLE_LOGIN_USER_EMAIL);
            int genderValue = getIntent().getIntExtra(Constants.Extra.GOOGLE_LOGIN_USER_GENDER, 0);
            if(genderValue==1)
            {
                rbtnFemale.setChecked(true);
            }
            ImageLoader.getInstance().displayImage(getIntent().getStringExtra(Constants.Extra.GOOGLE_LOGIN_USER_PROFILE_PIC), image, options);
        }

        if(MyApplication.FLAGCREATEPROFILE)
        {
            textCreateProfile.setText("Edit Profile");
        }
        image.setOnClickListener(this);
        cd = new ConnectionDetector(this);

        final Calendar c = Calendar.getInstance();
        txtDob.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                showDialog(DATE_DIALOG_ID);
                return true;
            }
        });
        ctx = this;
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        //****
        filldata();
    }

    //*******
    private void filldata() {
        //comes from create profile
        activityFrom="other";
        ImageButton cancel_button=(ImageButton)findViewById(R.id.cancel_button);
        cancel_button.setVisibility(View.GONE);
        textCreateProfile.setText("Create Profile");
        //comes from ProfileActivity

        Intent data=getIntent();
        if("ProfileActivity".equals(data.getStringExtra("from"))){
            activityFrom="ProfileActivity";
            cancel_button.setVisibility(View.VISIBLE);
            textCreateProfile.setText("Edit Profile");
            //name dob city gender pic
           /* accesstoken = preference.getString("name", "");
            accesstoken = preference.getString("dob", "");
            accesstoken = preference.getString("city", "");
            accesstoken = preference.getString("profile_img_url", "");*/


            String gdr = preference.getString("gender", "");
            imageString=data.getStringExtra("image_value");
            txtName.setText(preference.getString("name", ""));
            txtDob.setText(preference.getString("dob", ""));
            txtCity.setText(preference.getString("city", ""));
            Utilities.downloadImage(imageString, image, options, null);

            if(gdr.equals("male")){
                rbtnMale.setChecked(true);
                rbtnFemale.setChecked(false);
            }else{
                rbtnFemale.setChecked(true);
                rbtnMale.setChecked(false);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, Constants.FLURRY_ID);
    }
    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener()
    {

        public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth)
        {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            updateDisplay();
        } };

    private void updateDisplay() {
        txtDob.setText(new StringBuilder().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay));
    }

    protected Dialog onCreateDialog(int id) {
        switch (id)
        {
            case DATE_DIALOG_ID:
                //**********
                DatePickerDialog dialog = new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
                dialog.getDatePicker().setMaxDate(new Date().getTime());
                return dialog;
            //--------------
            //  return new DatePickerDialog(this,mDateSetListener,mYear, mMonth, mDay);
        }
        return null;
    }

    public void cancelProfile(View v)
    {
        finish();
        //overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
    }

    public void profileSubmit(View v){
        if (cd.isConnectingToInternet()) {
            gender=(RadioButton)findViewById(rGroup.getCheckedRadioButtonId());
            final String gen=gender.getText().toString();
            UserFunctions userFunction = new UserFunctions(this);

            final String name = txtName.getText().toString();
            final String dob = txtDob.getText().toString();
            final String city = txtCity.getText().toString();
            StringBuffer sb = new StringBuffer();

            if(name.length()<2)
            {
                sb.append("Enter your name.\n");
            }
            if(dob.length()<2)
            {
                sb.append("Enter your DOB.\n");
            }
            if(city.length()<2)
            {
                sb.append("Enter your current city.\n");
            }
            if(sb.length()>0)
            {
                AlertMessage.showAlertDialog(ctx, "Alert", sb.toString(), false);
            }

            else
            {
                new GlobalVolley_cl(this, UrlList.REGISTER,userFunction.VcreateProfile(name, gen, dob, city, imageString),new CallbackInterface() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject json=null;
                        try{
                            json=new JSONObject(response);
                            profileStatus=json.getString("create");
                            imageStatus = json.getString("image");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(profileStatus.contentEquals("1")||imageStatus.contentEquals("1")){
                            //			if(MyApplication.FLAGCREATEPROFILE)
                            //			{
                            //			finish();
                            //			}
                            //			else
                            if(activityFrom=="other"){
                                Home.dataUpdate = true;
                                Intent invok=new Intent(getApplicationContext(), Home.class);
                                //Intent invok=new Intent(getApplicationContext(), ProfileActivity.class);
                                SharedPreferences.Editor   editor = preference.edit();
                                editor.putString("profile_status","created");
                                editor.apply();
                                invok.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(invok);
                                finish();

                                overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
                            }else{
                                updatePriference(name,dob,city,gen);

                                Home.dataUpdate = true;
                                SharedPreferences.Editor   editor = preference.edit();
                                editor.putString("profile_status","created");
                                editor.apply();

                                finish();
                            }
                        }
                    }
                    @Override
                    public void onFailure() {
                        AlertMessage.showAlertDialog(CreateProfile.this, "Update Failed!","Sorry, unable to process. Please check your network connection or try again later.", false);
                    }
                });
            }
        }else{
            Toast tost = Toast.makeText(this, "No internet!",Toast.LENGTH_SHORT);
            tost.setGravity(Gravity.CENTER,0, 0);
            tost.show();
        }
    }

    //after update add in shared preference
    public void updatePriference( String name, String dob, String city, String gender){
        SharedPreferences.Editor editor = preference.edit();
        editor.putString("name", name);
        editor.putString("dob", dob);
        editor.putString("city", city);
        editor.putString("gender", gender);

        editor.commit();
        //editor.apply();
    }

    private static final int SELECT_PHOTO = 100;

    @Override
    public void onClick(View v) {
        if (v==image) {
            final Dialog dialog = new Dialog(this);
            dialog.setTitle("Select Option");
            dialog.setContentView(R.layout.custom_dialog);

            Button buttonSdCard = (Button)dialog.findViewById(R.id.btn_sdcard);
            Button buttonCamera = (Button)dialog.findViewById(R.id.btn_camera);
            buttonSdCard.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
                        loadPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE, SELECT_PHOTO);
                    }else{
                        //captureImageFromSdCard();
                        openGallery();
                    }
                    dialog.dismiss();
                }
            });

            buttonCamera.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    captureImageFromCamera();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case SELECT_PHOTO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                }
                break;
            }
        }
    }

    private void loadPermissions(String perm, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                ActivityCompat.requestPermissions(this, new String[]{perm}, requestCode);
            }
        } else {
            switch (requestCode) {

                case SELECT_PHOTO: {
                    openGallery();
                }
                break;
            }
        }
    }

    private void openGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    public static File getOutputMediaFile()
    {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Givvr");
        if(!mediaStorageDir.exists())
        {
            if(!mediaStorageDir.mkdirs())
            {
                return null;
            }
        }
        File mediaFile = new File(mediaStorageDir.getPath()+File.separator+"image.jpg");
        return mediaFile;
    }

    public void captureImageFromSdCard() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_FILE);
    }


    public void captureImageFromCamera()
    {
        Intent invok= new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(invok, TAKE_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == TAKE_PICTURE){
                picUri = data.getData();
                performCrop();
            }
            else if(requestCode == SELECT_FILE)
            {
                picUri = data.getData();
                performCrop();
            }
            else if(requestCode == PIC_CROP){
                Bundle extras = data.getExtras();
                Bitmap thePic = extras.getParcelable("data");
                String newImagPath = saveToInternalSorage(thePic);
                final String imageurl= "file://"+newImagPath;
                ImageLoader.getInstance().displayImage(imageurl, image, options);
                imageString = decodeMyImage(newImagPath);
            }
        }
    }

    public String decodeMyImage(String sdPath)
    {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(sdPath, options);

        options.inSampleSize = calculateInSampleSize(options, 100, 100);

        options.inJustDecodeBounds = false;
        Bitmap bitmapOrg = BitmapFactory.decodeFile(sdPath, options);

        bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 95, bao);
        byte[] ba = bao.toByteArray();
        String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);

        return ba1;
    }
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    private void performCrop(){
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, PIC_CROP);
        }
        catch(ActivityNotFoundException anfe){
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private String saveToInternalSorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("givvr", Context.MODE_PRIVATE);
        File mypath=new File(directory,"profile.jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mypath.getAbsolutePath();
    }
}
