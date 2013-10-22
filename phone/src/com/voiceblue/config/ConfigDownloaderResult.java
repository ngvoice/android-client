package com.voiceblue.config;

import org.json.JSONArray;
import org.json.JSONObject;

public class ConfigDownloaderResult {
	public static final int VOICEBLUE_RES_NOK = 1;
	public static final int VOICEBLUE_RES_OK = 2;
	public static final int VOICEBLUE_RES_UNKNOWN_ERROR = 3;
	
	private JSONArray mArrayResult;
	private JSONObject mObjectResult;
	private int mQueryResult;
	
	public ConfigDownloaderResult() { }	
	
	public void setArrayResult(JSONArray mResult) {
		this.mArrayResult = mResult;
	}	
	
	public JSONArray getArrayResult() {
		return mArrayResult;
	}
	
	public void setObjectResult(JSONObject mResult) {
		this.mObjectResult = mResult;
	}
	
	public JSONObject getObjectResult() {
		return mObjectResult;
	}
	
	public int getQueryResult() {
		return mQueryResult;
	}
	public void setQueryResult(int mQueryResult) {
		this.mQueryResult = mQueryResult;
	}
	
	public boolean operationSuccessful() {
		return mQueryResult == VOICEBLUE_RES_OK;
	}
}
