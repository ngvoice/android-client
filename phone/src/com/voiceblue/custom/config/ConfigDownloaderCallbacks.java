package com.voiceblue.custom.config;

public interface ConfigDownloaderCallbacks {
	
	void onPreDownloading();
	void onDownladingCancelled();
	void onConfigDownloaded(ConfigDownloaderResult result);	

}
