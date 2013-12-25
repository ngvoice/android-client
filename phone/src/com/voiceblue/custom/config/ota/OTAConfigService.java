package com.voiceblue.custom.config.ota;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.voiceblue.phone.api.ISipService;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.voiceblue.custom.ui.SplashScreen;
import com.voiceblue.phone.api.SipConfigManager;
import com.voiceblue.phone.api.SipManager;
import com.voiceblue.phone.service.SipService;
import com.voiceblue.phone.ui.SipHome;

public class OTAConfigService extends IntentService {

	private static final String TAG = "OTAConfigService";
	
	public static final int NOTIFICATION_ID = 1;
	//private NotificationManager mNotificationManager;
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
	                //sendNotification("Send error: " + extras.toString());
	            	;
	            } else if (GoogleCloudMessaging.
	                    MESSAGE_TYPE_DELETED.equals(messageType)) {
	                //sendNotification("Deleted messages on server: " +
	                //        extras.toString());
	            	;
	            // If it's a regular GCM message, do some work.
	            } else if (GoogleCloudMessaging.
	                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
//	                sendNotification("Received: " + extras.toString());
	                String message = extras.getString("message");
	                
	                // called when app is stopped or destroyed
	                if (OTAConfig.getCaller() == null) {
	                	if (mSipService == null) {
	                		mMsgQueue.add(message);
	                		bindService(new Intent(this, SipService.class), mConnection, Context.BIND_AUTO_CREATE);
	                	}
	                	else
	                		processOTAConfigMessageReceived(new OTAConfigMessage(message), 
	                											mSipService,
	                											this);
	                }
	                else
	                	OTAConfig.messageReceived(this, extras.getString("message"));
	            }
	        }
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
        
        OTAConfigBroadcastReceiver.completeWakefulIntent(intent);
    }
	
	/*
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager) 
        						this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, SipHome.class), 0);               

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.vb_logo)
		        .setContentTitle("OTA Config Received")
		        .setContentText(msg);		        
        
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
    */
	
    private ISipService mSipService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
        	mSipService = ISipService.Stub.asInterface(arg1);
        	
        	if (mMsgQueue.size() == 0)
        		return;
        	
        	for(String message:mMsgQueue)
        		processOTAConfigMessageReceived(new OTAConfigMessage(message), mSipService, OTAConfigService.this);
        	
        	mMsgQueue.clear();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        	mSipService = null;
        }
    };   
		
	public static void processOTAConfigMessageReceived(OTAConfigMessage message, ISipService sipService, final Context cxtx) {
		try {
			
			Log.d(TAG, "Message: [" + message.getMessageContent() + "]");
			
			if (sipService == null)
				throw new Exception("SipService is null");				
			
			if (message.isRegister()) {

				startSipService(cxtx);
				sipService.reAddAllAccounts();
				Log.i(TAG, "Received register request");
			}
			else if (message.isUnregister()) {
				
//				sipService.setForceRegistrationOn3g(false);
				//sipService.removeAllAccounts();
				sipService.sipStop();
				Log.i(TAG, "Received unregister request");
			}
			else if (message.isReloadConfig()) {
				Log.i(TAG, "Received reload-config request");
				SipConfigManager.setPreferenceStringValue(cxtx, OTAConfig.RELOAD_CONFIG_KEY, "yes");
				
				sipService.sipStop();
				restart(cxtx);
				return;
			}
			else if (message.isKeepAlive())
				; // do nothing
			else 
				Log.e(TAG, "Unknown message");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void restart(Context cxtx) {
		
	    PendingIntent intent = PendingIntent.getActivity(cxtx, 0, new Intent(cxtx, SplashScreen.class), PendingIntent.FLAG_CANCEL_CURRENT);
	    AlarmManager manager = (AlarmManager) cxtx.getSystemService(Context.ALARM_SERVICE);
	    manager.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, intent);
	    
	    System.exit(0);
	}
	
	 private static void startSipService(final Context cxtx) {
	        Thread t = new Thread("StartSip") {
	            public void run() {
	                Intent serviceIntent = new Intent(SipManager.INTENT_SIP_SERVICE);
	                serviceIntent.putExtra(SipManager.EXTRA_OUTGOING_ACTIVITY, new ComponentName(cxtx, SipHome.class));
	                serviceIntent.putExtra("force_on_3g", true);
	                
	                cxtx.startService(serviceIntent);
	            };
	        };
	        
	        t.start();
	 }
}
