package com.voiceblue.custom.config;

public class VoiceBlueAccount {
	
	public static final String CONFIGURED_BY_QR = "configured_by_qr";
	public static final String CUSTOMER_CARE_KEY = "customer_care_url";
	public static final String MY_ACCOUNT_KEY = "myaccount_url";
	public static final String WEB_URL_KEY = "web_url";
	public static final String TOP_UP_URL_KEY = "topup_url";
	
	private String mUsername;
	private String mPassword;
	private String mDisplayName;
	private String mRegURI;
	private String mAccID;
	private String mProxy;
	private String mRealm;
	private String mRegUseProxy;
	private CodecSelection mCodecSelection;
	
	private String mCustomerCareURL;
	private String mMyAccURL;
	private String mWebURL;
	private String mTopUpURL;
	

	public CodecSelection getCodecSelection() {
		return mCodecSelection;
	}
	public void setCodecSelection(CodecSelection selection) {
		this.mCodecSelection = selection;
	}
	
	public String getUsername() {
		return mUsername;
	}
	public void setUsername(String mUsername) {
		this.mUsername = mUsername;
	}
	public String getPassword() {
		return mPassword;
	}
	public void setPassword(String mPassword) {
		this.mPassword = mPassword;
	}
	public String getDisplayName() {
		return mDisplayName;
	}
	public void setDisplayName(String mDisplayName) {
		this.mDisplayName = mDisplayName;
	}
	public String getRegURI() {
		return mRegURI;
	}
	public void setRegURI(String mRegURI) {
		this.mRegURI = mRegURI;
	}
	public String getAccID() {
		return mAccID;
	}
	public void setAccID(String mAccID) {
		this.mAccID = mAccID;
	}
	public String getProxy() {
		return mProxy;
	}
	public void setProxy(String mProxy) {
		this.mProxy = mProxy;
	}
	public String getRealm() {
		return mRealm;
	}
	public void setRealm(String mRealm) {
		this.mRealm = mRealm;
	}
	public String getRegUseProxy() {
		return mRegUseProxy;
	}
	public void setRegUseProxy(String mRegUseProxy) {
		this.mRegUseProxy = mRegUseProxy;
	}
	public String getCustomerCareURL() {
		return mCustomerCareURL;
	}
	public void setCustomerCareURL(String mCustomerCareURL) {
		this.mCustomerCareURL = mCustomerCareURL;
	}
	public String getMyAccURL() {
		return mMyAccURL;
	}
	public void setMyAccURL(String mMyAccURL) {
		this.mMyAccURL = mMyAccURL;
	}
	public String getWebURL() {
		return mWebURL;
	}
	public void setWebURL(String mWebURL) {
		this.mWebURL = mWebURL;
	}
	public String getTopUpURL() {
		return mTopUpURL;
	}
	public void setTopUpURL(String mTopUpURL) {
		this.mTopUpURL = mTopUpURL;
	}
}
