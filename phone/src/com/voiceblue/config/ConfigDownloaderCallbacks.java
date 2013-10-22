package com.voiceblue.config;

public interface ConfigDownloaderCallbacks {
	
	void onPreDownloading();
	void onDownladingCancelled();
	void onConfigDownloaded(ConfigDownloaderResult result);	

}
