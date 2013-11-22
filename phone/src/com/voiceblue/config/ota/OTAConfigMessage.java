package com.voiceblue.config.ota;

public class OTAConfigMessage {

	public static final int MSG_REGISTER = 1;
	public static final int MSG_UNREGISTER = 2;
	public static final int MSG_CONFIG_RELOAD = 3;
	public static final int MSG_KEEP_ALIVE = 4;
	
	public static final int MSG_UNKNOWN = 99;
	
	private int mMessageType = MSG_UNKNOWN;
	private String mContent;
	
	public OTAConfigMessage() { }
	public OTAConfigMessage(String message) { 
		determineType(message);
	}
	
	public void determineType(String message) {
		
		mContent = message;
		
		if (message.equals("register"))
			setMessageType(OTAConfigMessage.MSG_REGISTER);
    	else if (message.equals("unregister"))
    		setMessageType(OTAConfigMessage.MSG_UNREGISTER);
    	else if (message.equals("reload"))
    		setMessageType(OTAConfigMessage.MSG_CONFIG_RELOAD);
    	else if (message.equals("keep-alive"))
    		setMessageType(OTAConfigMessage.MSG_KEEP_ALIVE);
    	else
    		setMessageType(OTAConfigMessage.MSG_UNKNOWN);    	
	}
	
	public String getMessageContent() {
		return mContent;
	}

	public int getMessageType() {
		return mMessageType;
	}

	private void setMessageType(int mMessageType) {
		this.mMessageType = mMessageType;
	}
	
	public boolean isRegister() {
		return mMessageType == MSG_REGISTER;
	}
	
	public boolean isUnregister() {
		return mMessageType == MSG_UNREGISTER;
	}
	
	public boolean isConfigReload() {
		return mMessageType == MSG_CONFIG_RELOAD;
	}
	
	public boolean isKeepAlive() {
		return mMessageType == MSG_KEEP_ALIVE;
	}
	
	public boolean isUnknown() {
		return mMessageType == MSG_UNKNOWN;
	}
}
