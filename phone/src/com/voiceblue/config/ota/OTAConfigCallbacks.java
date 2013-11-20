package com.voiceblue.config.ota;

public interface OTAConfigCallbacks {
	void onOTAConfigRegistrationResult(boolean registered);
	void onOTAConfigMessageReceived(OTAConfigMessage message);
}
