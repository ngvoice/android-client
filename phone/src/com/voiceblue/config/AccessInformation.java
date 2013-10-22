package com.voiceblue.config;

public class AccessInformation {
	public final String DEFAULT_URL = "https://clientconfig.ng-voice.com/";
	
	private String mURL	= DEFAULT_URL;
	private String mUsername;
	private String mPassword;
	
	public AccessInformation() { }
	public AccessInformation(String username, String password) {
		mUsername = username;
		mPassword = password;
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
