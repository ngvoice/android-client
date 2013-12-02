package com.voiceblue.ui;

import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.csipsimple.R;
import com.csipsimple.api.SipConfigManager;
import com.csipsimple.api.SipProfile;
import com.csipsimple.ui.SipHome;
import com.voiceblue.config.AccessInformation;
import com.voiceblue.config.ConfigDownloaderCallbacks;
import com.voiceblue.config.ConfigDownloaderResult;
import com.voiceblue.config.ConfigDownloaderTask;
import com.voiceblue.config.ConfigLoader;
import com.voiceblue.config.VoiceBlueAccount;
import com.voiceblue.config.ota.OTAConfig;

public class LoginActivity extends Activity implements ConfigDownloaderCallbacks {

	private ProgressDialog mProgressDialog;	

	private EditText mTxtUsername;
	private EditText mTxtPassword;
	private String mUsername;	
	private String mPassword;
	
	private final String TAG = "LoginActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		boolean reloadConfig = forceConfigurationReload();
		boolean firstTimeConfig = isFirstTimeConfiguration();
		
		if (reloadConfig && !firstTimeConfig) {
			
			mUsername = SipConfigManager.getPreferenceStringValue(this, AccessInformation.USERNAME_FIELD_KEY);
			mPassword = SipConfigManager.getPreferenceStringValue(this, AccessInformation.PASSWORD_FIELD_KEY);
						
			if (mUsername == null || mPassword == null)
				showError(getString(R.string.voiceblue_usrpwd_empty));
			else {
				// remove all previous accounts, a new one is going to be fetched 
				getContentResolver().delete(SipProfile.ACCOUNT_URI, null, null);

				setContentView(R.layout.activity_splash_screen);
				
				new ConfigDownloaderTask(LoginActivity.this)
						.execute(new AccessInformation(mUsername, mPassword));
								
				return;
			}
		}
		else if (!firstTimeConfig) {
			showMainScreen();
			return;
		}
		//else
		//	return;
		
		setupLoginScreen();
	}
	
	private void setupLoginScreen() {
		setContentView(R.layout.activity_login);
		
		Button btn = (Button) findViewById(R.id.btnLogin);
		TextView lnkPasswordForgotten = (TextView) findViewById(R.id.lnkPasswordForgotten);		
		TextView lnkNewAccount = (TextView) findViewById(R.id.lnkNewAccount);
		
		mTxtUsername = (EditText) findViewById(R.id.txtUsername);
		mTxtPassword = (EditText) findViewById(R.id.txtPassword);			
		
		lnkPasswordForgotten.setMovementMethod(LinkMovementMethod.getInstance());
		lnkNewAccount.setMovementMethod(LinkMovementMethod.getInstance());
		
		lnkPasswordForgotten.setText(Html.fromHtml(getString(R.string.voiceblue_password_forgotten)));
		lnkNewAccount.setText(Html.fromHtml(getString(R.string.voiceblue_create_account)));
		//
		//getContentResolver().delete(SipProfile.ACCOUNT_URI, null, null);
		
		btn.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View arg0) {
				try {
					if (mTxtUsername.getText().length() == 0)
						throw new Exception(getString(R.string.voiceblue_username_empty));
					
					if (mTxtPassword.getText().length() == 0)
						throw new Exception(getString(R.string.voiceblue_password_empty));
					
					mUsername = mTxtUsername.getText().toString();
					mPassword = mTxtPassword.getText().toString();
					
					new ConfigDownloaderTask(LoginActivity.this)
						.execute(new AccessInformation(mUsername, mPassword));
				}
				catch (Exception e) {
					if (mProgressDialog != null)
						mProgressDialog.dismiss();
					
					showError(e.getMessage());
				}
			}
		});
	}
	
	private void showError(String message) {
		AlertDialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setTitle("Oops")
				.setIcon(R.drawable.ic_login_error)
				.setMessage(message);
		
		builder.setPositiveButton("OK", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
			
		dialog = builder.create();
		dialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public void onPreDownloading() {
		showDownloadingProgress();
	}

	@Override
	public void onDownladingCancelled() {
		if (mProgressDialog != null)
			mProgressDialog.dismiss();
		
		Log.e(TAG, "Configuration fetch was cancelled");
		showError("Configuration fetch was cancelled");
		
	}

	private boolean isFirstTimeConfiguration() {
		Cursor c = getContentResolver().query(SipProfile.ACCOUNT_URI, 
				new String[] { SipProfile.FIELD_ID }, 
				null, 
				null, 
				null);

		return (c.getCount() == 0);
	}
	
	@Override
	public void onConfigDownloaded(ConfigDownloaderResult result) {
		try {
			if (!result.operationSuccessful()) 
				throw new Exception(getString(R.string.voiceblue_invalid_credential));
						
			VoiceBlueAccount acc = ConfigLoader.loadFromResult(result);
			
			// update username/password of the web portal access
			SipConfigManager.setPreferenceStringValue(this, 
														AccessInformation.USERNAME_FIELD_KEY, 
														mUsername);
			SipConfigManager.setPreferenceStringValue(this, 
														AccessInformation.PASSWORD_FIELD_KEY, 
														mPassword);
			
			// update common preferences
			SipConfigManager.setPreferenceStringValue(this, 
														VoiceBlueAccount.CUSTOMER_CARE_KEY, 
														acc.getCustomerCareURL());
			SipConfigManager.setPreferenceStringValue(this, 
														VoiceBlueAccount.MY_ACCOUNT_KEY, 
														acc.getMyAccURL());
			SipConfigManager.setPreferenceStringValue(this, 
														VoiceBlueAccount.TOP_UP_URL_KEY, 
														acc.getTopUpURL());
			SipConfigManager.setPreferenceStringValue(this, 
														VoiceBlueAccount.WEB_URL_KEY, 
														acc.getWebURL());
			
			if (acc == null)
				throw new Exception("Something went wrong parsing your config");			
			
			if (isFirstTimeConfiguration()) {
			
				ContentValues values =  new ContentValues();
				values.put(SipProfile.FIELD_ACTIVE, "1");
				values.put(SipProfile.FIELD_PROXY, acc.getProxy());
				values.put(SipProfile.FIELD_DISPLAY_NAME, acc.getDisplayName());
				values.put(SipProfile.FIELD_ACC_ID, acc.getAccID());
				values.put(SipProfile.FIELD_USERNAME, acc.getUsername());
				values.put(SipProfile.FIELD_DATA, acc.getPassword());
				values.put(SipProfile.FIELD_REG_URI, acc.getRegURI());
				values.put(SipProfile.FIELD_REALM, acc.getRealm());
				values.put(SipProfile.FIELD_REG_USE_PROXY, acc.getRegUseProxy());
				
				getContentResolver().insert(SipProfile.ACCOUNT_URI, values);
			}	
					
			showMainScreen();
			
			ConfigLoader.setupDefaultPreferences(this);
		
		}
		catch(JSONException e) {
			if (mProgressDialog != null)
				mProgressDialog.dismiss();
			
			showError("Something is wrong on the server side. Please contact support");
			setupLoginScreen();
		}
		catch(Exception e) {
			if (mProgressDialog != null)
				mProgressDialog.dismiss();
			String errorMsg = e.getMessage();
			
			if (errorMsg == null || errorMsg.equals(""))
				errorMsg = "Something bad happened. Please try again and/or contact support.";
			
			showError(errorMsg);
			
			setupLoginScreen();
		}
	}
	
	private void showMainScreen() {
		Intent home = new Intent(LoginActivity.this, SipHome.class);
		home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(home);
		
		if (mProgressDialog != null)
			mProgressDialog.dismiss();			
		
		finish();
	}
	
	private void showDownloadingProgress() {
		mProgressDialog = ProgressDialog.show(LoginActivity.this, 
				null, 
				getString(R.string.voiceblue_fetching_config));
	}
	
	private boolean forceConfigurationReload() {
		String value = SipConfigManager.getPreferenceStringValue(this, OTAConfig.RELOAD_CONFIG_KEY);
		
		if (value != null && value.equals("yes")) {
			SipConfigManager.setPreferenceStringValue(this, OTAConfig.RELOAD_CONFIG_KEY, "no");
			return true;
		}
				
		return false;
	}
}
