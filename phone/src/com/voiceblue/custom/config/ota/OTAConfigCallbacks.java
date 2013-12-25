package com.voiceblue.custom.config.ota;

public interface OTAConfigCallbacks {
	void onOTAConfigRegistrationResult(boolean registered);
	void onOTAConfigMessageReceived(OTAConfigMessage message);
}
