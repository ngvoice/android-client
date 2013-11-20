package com.voiceblue.config.ota;

import java.util.ArrayList;
import java.util.List;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.csipsimple.api.ISipService;
import com.csipsimple.service.SipService;
import com.csipsimple.ui.SipHome;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class OTAConfigService extends IntentService {

	private static final String TAG = "OTAConfigService";
	
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    private List<String> mMsgQueue = new ArrayList<String>();
    
    public OTAConfigService() {
        super("OTAConfigService");               
    }

	@Override
	public void onCreate() {
		super.onCreate();
		bindService(new Intent(this, SipService.class), mConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	public void onDestroy() {	
		super.onDestroy();
		unbindService(mConnection);
	}

	@Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.        
        try {
	        String messageType = gcm.getMessageType(intent);               
	        
	        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
	            /*
	             * Filter messages based on message type. Since it is likely that GCM
	             * will be extended in the future with new message types, just ignore
	             * any message types you're not interested in, or that you don't
	             * recognize.
	             */
	            if (GoogleCloudMessaging.
	                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
	                sendNotification("Send error: " + extras.toString());
	            } else if (GoogleCloudMessaging.
	                    MESSAGE_TYPE_DELETED.equals(messageType)) {
	                sendNotification("Deleted messages on server: " +
	                        extras.toString());
	            // If it's a regular GCM message, do some work.
	            } else if (GoogleCloudMessaging.
	                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
	                String message = extras.getString("message");
	                //sendNotification("Received: " + extras.toString());
	                Log.i(TAG, "Received: " + extras.toString());
	                
	                // called when app is stopped or destroyed
	                if (OTAConfig.getCaller() == null) {
	                	if (mSipService == null) {
	                		System.out.println("-------==== adding message to queue!");
	                		mMsgQueue.add(message);
	                		bindService(new Intent(this, SipService.class), mConnection, Context.BIND_AUTO_CREATE);
	                	}
	                	else
	                		processOTAConfigMessageReceived(new OTAConfigMessage(message));
	                }
	                else
	                	OTAConfig.messageReceived(extras.getString("message"));
	            }
	        }
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
        
        OTAConfigBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, SipHome.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        //.setSmallIcon(R.drawable.ic_stat_gcm)
        .setContentTitle("GCM Notification")
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
    
    
    private ISipService mSipService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
        	mSipService = ISipService.Stub.asInterface(arg1);
        	
        	if (mMsgQueue.size() == 0)
        		return;
        	
        	for(String message:mMsgQueue)
        		processOTAConfigMessageReceived(new OTAConfigMessage(message));
        	
        	mMsgQueue.clear();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        	mSipService = null;
        }
    };   
		
	private void processOTAConfigMessageReceived(OTAConfigMessage message) {
		try {
			
			if (mSipService == null)
				throw new Exception("mSipService is null");
			
			mSipService.sipStart();
			
			if (message.isRegister()) {
				mSipService.reAddAllAccounts();
				Log.i(TAG, "Received register request");
			}
			else if (message.isUnregister()) {
				mSipService.removeAllAccounts();
				Log.i(TAG, "Received unregister request");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
