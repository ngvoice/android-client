package com.voiceblue.custom.config;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.Profile;

import com.voiceblue.phone.api.SipConfigManager;
import com.voiceblue.phone.api.SipProfile;
import com.voiceblue.phone.db.DBProvider;
import com.voiceblue.phone.utils.Log;
import com.voiceblue.phone.utils.PreferencesWrapper;

public class ConfigLoader {

	private static final String TAG = "ConfigLoader";	
	private static VoiceBlueAccount mLoadedAccount;
	
	public static VoiceBlueAccount loadFromResult(ConfigDownloaderResult result) {
		
		try {
			JSONObject config = result.getObjectResult();			
			
			JSONArray jsonAcccounts = config.getJSONArray("accounts");
			JSONObject jsonSettings	= config.getJSONObject("settings");
			VoiceBlueAccount account = null;
			
			// TODO fix this
			for (int i = 0; i < 1; i++) {
				account = new VoiceBlueAccount();
				
				account.setUsername(jsonAcccounts.getJSONObject(i).getString("username"));
				account.setPassword(jsonAcccounts.getJSONObject(i).getString("data"));
				account.setDisplayName(jsonAcccounts.getJSONObject(i).getString("display_name"));
				account.setRegURI(jsonAcccounts.getJSONObject(i).getString("reg_uri"));
				account.setAccID(jsonAcccounts.getJSONObject(i).getString("acc_id"));
				account.setProxy(jsonAcccounts.getJSONObject(i).getString("proxy"));
				account.setRealm(jsonAcccounts.getJSONObject(i).getString("realm"));
				account.setRegUseProxy(jsonAcccounts.getJSONObject(i).getString("reg_use_proxy"));								
			}
								
			account.setCustomerCareURL(jsonSettings.getString("customer_care_url"));
			account.setMyAccURL(jsonSettings.getString("myaccount_url"));
			account.setWebURL(jsonSettings.getString("web_url"));
			account.setTopUpURL(jsonSettings.getString("topup_url"));

			mLoadedAccount = account;
			return mLoadedAccount;
		}
		catch(Exception e) {
			e.printStackTrace();			
			return null;
		}		
	}

	public static VoiceBlueAccount getLoadedAccount() {			
		return mLoadedAccount;
	}
	
	public static VoiceBlueAccount loadFromDatabase(Context ctx) {	
		Cursor c = ctx.getContentResolver().query(SipProfile.ACCOUNT_URI, DBProvider.ACCOUNT_FULL_PROJECTION, 
				SipProfile.FIELD_ACTIVE + "=?", new String[] {"1"}, null);
		
		if (c != null) {
			try {				
				if(c.getCount() > 0) {
					
					// load only the first
					// TODO load every account and register all of them
					c.moveToFirst();
    				SipProfile account = new SipProfile(c);
    				mLoadedAccount = new VoiceBlueAccount();
    				
    				mLoadedAccount.setUsername(account.getSipUserName());
    				mLoadedAccount.setPassword(account.getPassword());
    				mLoadedAccount.setDisplayName(account.getDisplayName());
    				mLoadedAccount.setRegURI(account.reg_uri);
    				mLoadedAccount.setAccID(account.acc_id);
    				mLoadedAccount.setProxy(account.getProxyAddress());
    				mLoadedAccount.setRealm(account.realm);
    				mLoadedAccount.setRegUseProxy(Integer.toString(account.reg_use_proxy));
    				
    				return mLoadedAccount;
				}
			} catch (Exception e) {
				Log.e(TAG, "Error on looping over sip profiles", e);
			} finally {
				c.close();
			}					
		}
		
		return null;
	}
	
	public static void setupDefaultPreferences(Context ctx) {
		String g722CodecWb = SipConfigManager.getCodecKey("G722/16000/1", SipConfigManager.CODEC_WB),
			   pcmaCodecWb = SipConfigManager.getCodecKey("PCMA/8000/1", SipConfigManager.CODEC_WB),
			   pcmuCodecWb = SipConfigManager.getCodecKey("PCMU/8000/1", SipConfigManager.CODEC_WB),
			   isacCodecWb = SipConfigManager.getCodecKey("ISAC/16000/1", SipConfigManager.CODEC_WB),
			   gsmCodecWb  = SipConfigManager.getCodecKey("GSM/8000/1", SipConfigManager.CODEC_WB),
			   silk24CodecWb  = SipConfigManager.getCodecKey("SILK/24000/1", SipConfigManager.CODEC_WB),
			   h264CodecWb  = SipConfigManager.getCodecKey("H264/97", SipConfigManager.CODEC_WB),
			   h263CodecWb  = SipConfigManager.getCodecKey("H263-1998/96", SipConfigManager.CODEC_WB),
			   vp8CodecWb =  SipConfigManager.getCodecKey("VP8/102", SipConfigManager.CODEC_WB);
				
		String gsmCodecNb  = SipConfigManager.getCodecKey("GSM/8000/1", SipConfigManager.CODEC_NB),
			   isacCodecNb = SipConfigManager.getCodecKey("ISAC/16000/1", SipConfigManager.CODEC_NB),
			   pcmaCodecNb = SipConfigManager.getCodecKey("PCMA/8000/1", SipConfigManager.CODEC_NB),			   
			   pcmuCodecNb = SipConfigManager.getCodecKey("PCMU/8000/1", SipConfigManager.CODEC_NB),
			   ilbcCodecNb =  SipConfigManager.getCodecKey("iLBC/8000/1", SipConfigManager.CODEC_NB),
			   g722CodecNb = SipConfigManager.getCodecKey("G722/16000/1", SipConfigManager.CODEC_NB),
			   silk24CodecNb  = SipConfigManager.getCodecKey("SILK/24000/1", SipConfigManager.CODEC_NB),
			   silk8CodecNb = SipConfigManager.getCodecKey("SILK/8000/1", SipConfigManager.CODEC_NB),
			   h264CodecNb  = SipConfigManager.getCodecKey("H264/97", SipConfigManager.CODEC_NB),
			   h263CodecNb  = SipConfigManager.getCodecKey("H263-1998/96", SipConfigManager.CODEC_NB),
			   vp8CodecNb =  SipConfigManager.getCodecKey("VP8/102", SipConfigManager.CODEC_NB);
		
		
		SipConfigManager.setPreferenceBooleanValue(ctx, SipConfigManager.INTEGRATE_WITH_DIALER, true);
		SipConfigManager.setPreferenceBooleanValue(ctx, SipConfigManager.INTEGRATE_WITH_CALLLOGS, true);
		
	    SipConfigManager.setPreferenceBooleanValue(ctx, SipConfigManager.USE_3G_IN, false);
		SipConfigManager.setPreferenceBooleanValue(ctx, SipConfigManager.USE_3G_OUT, true);
		SipConfigManager.setPreferenceBooleanValue(ctx, SipConfigManager.USE_GPRS_IN, false);
		SipConfigManager.setPreferenceBooleanValue(ctx, SipConfigManager.USE_GPRS_OUT, false);
		SipConfigManager.setPreferenceBooleanValue(ctx, SipConfigManager.USE_EDGE_IN, false);
		SipConfigManager.setPreferenceBooleanValue(ctx, SipConfigManager.USE_EDGE_OUT, false);
		
		SipConfigManager.setPreferenceBooleanValue(ctx, SipConfigManager.USE_WIFI_IN, true);
		SipConfigManager.setPreferenceBooleanValue(ctx, SipConfigManager.USE_WIFI_OUT, true);
		
		SipConfigManager.setPreferenceBooleanValue(ctx, SipConfigManager.USE_OTHER_IN, false);
		SipConfigManager.setPreferenceBooleanValue(ctx, SipConfigManager.USE_OTHER_OUT, false);
		
		SipConfigManager.setPreferenceBooleanValue(ctx, SipConfigManager.LOCK_WIFI, false);			
				
		// wide band
		SipConfigManager.setPreferenceStringValue(ctx, g722CodecWb, "300");
		SipConfigManager.setPreferenceStringValue(ctx, pcmaCodecWb, "250");
		SipConfigManager.setPreferenceStringValue(ctx, pcmuCodecWb, "240");
		SipConfigManager.setPreferenceStringValue(ctx, isacCodecWb, "240");
		
		SipConfigManager.setPreferenceStringValue(ctx, gsmCodecWb, "0");
		SipConfigManager.setPreferenceStringValue(ctx, silk24CodecWb, "0");
		
		// narrow band
		SipConfigManager.setPreferenceStringValue(ctx, ilbcCodecNb, "300");
		SipConfigManager.setPreferenceStringValue(ctx, isacCodecNb, "250");
		SipConfigManager.setPreferenceStringValue(ctx, gsmCodecNb, "200");
		
		SipConfigManager.setPreferenceStringValue(ctx, pcmuCodecNb, "0");
		SipConfigManager.setPreferenceStringValue(ctx, pcmaCodecNb, "0");
		SipConfigManager.setPreferenceStringValue(ctx, g722CodecNb, "0");
		SipConfigManager.setPreferenceStringValue(ctx, silk8CodecNb, "0");
		SipConfigManager.setPreferenceStringValue(ctx, silk24CodecNb, "0");
		
		// wide band video codecs
		SipConfigManager.setPreferenceStringValue(ctx, h264CodecWb, "200");
		SipConfigManager.setPreferenceStringValue(ctx, h263CodecWb, "255");
		SipConfigManager.setPreferenceStringValue(ctx, vp8CodecWb, "0");		
		
		// narrow band video codecs
		SipConfigManager.setPreferenceStringValue(ctx, h264CodecNb, "200");
		SipConfigManager.setPreferenceStringValue(ctx, h263CodecNb, "255");
		SipConfigManager.setPreferenceStringValue(ctx, vp8CodecNb, "0");
	}
}
