package com.voiceblue.config;

import java.io.ByteArrayOutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;

import com.voiceblue.connection.HttpsClient;

import android.app.DownloadManager;
import android.os.AsyncTask;
import android.util.Log;

public class ConfigDownloaderTask extends AsyncTask<AccessInformation, Void, ConfigDownloaderResult>{

	ConfigDownloaderCallbacks mCaller;
		
	public ConfigDownloaderTask(ConfigDownloaderCallbacks caller) {
		mCaller	= caller;
	}
	
	public static ConfigDownloaderResult runInServer(String url, String username, String password) {
		
		ConfigDownloaderResult result = new ConfigDownloaderResult();
		
		try {
			HttpClient client = HttpsClient.getClient(username, password);			
			
			HttpResponse response	= client.execute(new HttpGet(url));
									
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
				result.setQueryResult(ConfigDownloaderResult.VOICEBLUE_RES_OK);			
			else {
				result.setQueryResult(ConfigDownloaderResult.VOICEBLUE_RES_NOK);
				Log.d("ConfigDownloaderTask", response.getStatusLine().getReasonPhrase());
			}
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			response.getEntity().writeTo(out);
            out.close();
            String jsonResult	= out.toString(); 
            
            if (jsonResult.length() > 0 && jsonResult.charAt(0) == '[')
            	result.setArrayResult(new JSONArray(jsonResult));
            else if (jsonResult.length() > 0 && jsonResult.charAt(0) == '{') 
            	result.setObjectResult(new JSONObject(jsonResult));
            else if (jsonResult.toLowerCase().equals("no configuration available"))
            	result.setQueryResult(ConfigDownloaderResult.VOICEBLUE_RES_NOK);
            else
            	throw new Exception("Invalid response: " + jsonResult);            
		} 
		catch (Exception e) {

			result.setQueryResult(ConfigDownloaderResult.VOICEBLUE_RES_UNKNOWN_ERROR);
			e.printStackTrace();
		}			
		
        return result;
	}
	
	@Override
	protected ConfigDownloaderResult doInBackground(AccessInformation... params) {
		return runInServer(params[0].getURL(), params[0].getUsername(), params[0].getPassword());
	}

	@Override
	protected void onCancelled(ConfigDownloaderResult result) {
		mCaller.onDownladingCancelled();
	}

	@Override
	protected void onPostExecute(ConfigDownloaderResult result) {
		mCaller.onConfigDownloaded(result);
	}

	@Override
	protected void onPreExecute() {		
		mCaller.onPreDownloading();
	}
}
