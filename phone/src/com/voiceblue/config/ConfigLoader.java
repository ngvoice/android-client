package com.voiceblue.config;

import org.json.JSONArray;
import org.json.JSONObject;

import com.csipsimple.backup.SipProfileJson;

public class ConfigLoader {

	public static VoiceBlueAccount loadFromResult(ConfigDownloaderResult result) {
		
		try {
			JSONObject config = result.getObjectResult();			
			
			JSONArray jsonAcccounts = config.getJSONArray("accounts");
			VoiceBlueAccount account = null;
			
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

			return account;
		}
		catch(Exception e) {
			e.printStackTrace();			
			return null;
		}		
	}
	
}
