package com.voiceblue.config;

import android.content.Context;

import com.csipsimple.api.SipConfigManager;

public class AccessInformation {
	public static final String USERNAME_FIELD_KEY = "vb_username";
	public static final String PASSWORD_FIELD_KEY = "vb_password";
	
	private String mURL	= VoiceBlueURL.CLIENT_CONFIG;
	private String mUsername;
	private String mPassword;
	
	public AccessInformation() { }
	
	public AccessInformation(String username, String password, String url) {
		mUsername = username;
		mPassword = password;
		mURL = url;
	}
	
	public AccessInformation(String url) {
		mURL = url;
	}
	
	public AccessInformation(String username, String password) {
		mUsername = username;
		mPassword = password;
	}
	
	public AccessInformation loadCredentialFromConfigurationFile(Context ctxt) {
		mUsername = SipConfigManager.getPreferenceStringValue(ctxt, USERNAME_FIELD_KEY);
		mPassword = SipConfigManager.getPreferenceStringValue(ctxt, PASSWORD_FIELD_KEY);
		
		return this;
	}
	
	public String getURL() {
		return mURL;
	}
	public void setURL(String url) {
		this.mURL = url;
	}
	public String getUsername() {
		return mUsername;
	}
	
	public void setUsername(String username) {
		this.mUsername = username;
	}
	public String getPassword() {
		return mPassword;
	}
	public void setPassword(String password) {
		this.mPassword = password;
	}
	
}
