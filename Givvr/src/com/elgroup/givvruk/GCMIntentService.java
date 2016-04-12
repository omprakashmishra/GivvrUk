package com.elgroup.givvruk;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import com.google.android.gcm.GCMBaseIntentService;
import com.tech.givvruk.utils.Constants;

public class GCMIntentService extends GCMBaseIntentService {
	static SharedPreferences preference;
	public static int PUSH_NOTIFICATION = 0;
    public GCMIntentService() {
        super(Constants.GOOGLE_SENDER_ID);
        preference = MyApplication.preference;
    }
    @Override
    protected void onRegistered(Context context, String registrationId) {
        SharedPreferences.Editor   editor = preference.edit();
   	    editor.putString("GCM_REGISTERED_ID", registrationId);
        editor.commit();
        MyApplication.register(registrationId);
    }
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        MyApplication.unregister("unregister");
    }
    @Override
    protected void onMessage(Context context, Intent intent) {
        String message = intent.getExtras().getString("price");
        displayNotification(context, message);
    }
    @Override
    protected void onDeletedMessages(Context context, int total) {
    }
    @Override
    public void onError(Context context, String errorId) {
    }
    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        return super.onRecoverableError(context, errorId);
    }
    protected void displayNotification(Context context, String message) {
        NotificationCompat.Builder  mBuilder = 
        new NotificationCompat.Builder(this);	
        String title = context.getString(R.string.app_name);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(message);
        mBuilder.setTicker("New Message Alert!");
        mBuilder.setSmallIcon(R.drawable.icon);

        //mBuilder.setNumber(++MyApplication.numMessage);

        Intent resultIntent = new Intent(this, SplashActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(SplashActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(PUSH_NOTIFICATION, mBuilder.build());
     }
}
