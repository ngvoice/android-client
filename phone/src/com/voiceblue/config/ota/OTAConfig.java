package com.voiceblue.config.ota;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.voiceblue.config.ConfigLoader;
import com.voiceblue.config.VoiceBlueAccount;
import com.voiceblue.config.VoiceBlueURL;

public class OTAConfig {
	
	public final static String RELOAD_CONFIG_KEY = "cfg_reload";
	
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";    
    
    private static final String SENDER_ID = "132908227927";
    
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private final static String TAG = "OTAConfig";
	
	private static GoogleCloudMessaging mGCM;
	private static String mRegID;
	private static OTAConfigCallbacks mCaller;
	//private static AtomicInteger mMsgId = new AtomicInteger(1);
	
    public static boolean checkPlayServices(Activity activity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
            	GooglePlayServicesUtil.getErrorDialog(resultCode, activity, PLAY_SERVICES_RESOLUTION_REQUEST).show();
                return true;
            } 
            else 
            	return false;
        }
        
        return true;
    }
    
    public static void registerApp(Context ctx) {    	
    	    
    	mGCM = GoogleCloudMessaging.getInstance(ctx);
    	mCaller = (OTAConfigCallbacks) ctx;
    	mRegID = getRegistrationId(ctx);
    	
    	if (mRegID.isEmpty())
    		registerToGCM(ctx);
    	else
    		registerToVoiceBlue(ctx);
    }
    
    private static String getRegistrationId(Context ctx) {
    	SharedPreferences prefs = ctx.getSharedPreferences(OTAConfig.class.getSimpleName(), Context.MODE_PRIVATE);
    	
    	return prefs.getString(PROPERTY_REG_ID, "");
    }
    
    private static void registerToGCM(final Context ctx) {
    	
    	new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected void onPostExecute(Boolean result) {
				if (!result) {
					Log.e(TAG, "Error registering to GCM");
					return;
				}
				
				registerToVoiceBlue(ctx);
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					
					if (mGCM == null) 
						mGCM = GoogleCloudMessaging.getInstance(ctx);
					
					mRegID = mGCM.register(SENDER_ID);
					storeRegistrationId(ctx, mRegID);	
					
					return true;
				}
				catch(Exception e) {
					e.printStackTrace();
					Log.e(TAG, e.getMessage());
				}
				
				return false;
			}
    	}.execute();
    }
    
    private static void storeRegistrationId(Context ctx, String regId) {
        final SharedPreferences prefs = ctx.getSharedPreferences(OTAConfig.class.getSimpleName(), Context.MODE_PRIVATE);        
        Log.i(TAG, "Saving regId on app: " + regId);
        
        SharedPreferences.Editor editor = prefs.edit();
        
        editor.putString(PROPERTY_REG_ID, regId);
        
        editor.commit();
    }
    
    public static void messageReceived(Context ctx, String message) {
    	OTAConfigMessage config = new OTAConfigMessage(message);
//    	if (config.isRegister())
//    		SipConfigManager.setPreferenceBooleanValue(ctx, SipConfigManager.USE_3G_IN, true);
    	getCaller().onOTAConfigMessageReceived(config);    	
    }
    
    public static void registerToVoiceBlue(final Context ctx) {
    	new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				
				try {
					
					VoiceBlueAccount account = ConfigLoader.getLoadedAccount();
					if (account == null)
						account = ConfigLoader.loadFromDatabase(ctx);
					
					if (account == null)
						throw new Exception("Couldn't load current voiceblue account!");
					
					String URL = new StringBuilder(VoiceBlueURL.REGISTER_INSTANCE)
												.append("?username=")
												.append(ConfigLoader.getLoadedAccount().getUsername())
												.append("&gcm_key=")
												.append(mRegID)
												.toString();
					
					HttpClient httpClient = new DefaultHttpClient();
					HttpResponse response	= httpClient.execute(new HttpGet(URL));
						
					return (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK);									
				} 
				catch (Exception e) {
					System.err.println(e.getMessage());
					e.printStackTrace();
					return false;
				}			
			}
    		
			@Override
			protected void onPostExecute(Boolean result) {
				getCaller().onOTAConfigRegistrationResult(result);
			}
    		
    	}.execute();
    }

    public static void setCaller(OTAConfigCallbacks caller) {
    	mCaller = caller;
    }
	public static OTAConfigCallbacks getCaller() {
		return mCaller;
	}

}
