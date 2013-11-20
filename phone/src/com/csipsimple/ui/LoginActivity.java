package com.csipsimple.ui;

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
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.csipsimple.R;
import com.csipsimple.api.SipProfile;
import com.voiceblue.config.AccessInformation;
import com.voiceblue.config.ConfigDownloaderCallbacks;
import com.voiceblue.config.ConfigDownloaderResult;
import com.voiceblue.config.ConfigDownloaderTask;
import com.voiceblue.config.ConfigLoader;
import com.voiceblue.config.VoiceBlueAccount;

public class LoginActivity extends Activity implements ConfigDownloaderCallbacks {

	ProgressDialog mProgressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		if (!isFirstTimeConfiguration()) {
			showMainScreen();
			return;
		}
		
		setContentView(R.layout.activity_login);
		
		Button btn = (Button) findViewById(R.id.btnLogin);
		TextView lnkPasswordForgotten = (TextView) findViewById(R.id.lnkPasswordForgotten);		
		TextView lnkNewAccount = (TextView) findViewById(R.id.lnkNewAccount);
		
		final EditText txtUsername = (EditText) findViewById(R.id.txtUsername);
		final EditText txtPassword = (EditText) findViewById(R.id.txtPassword);
		
		lnkPasswordForgotten.setMovementMethod(LinkMovementMethod.getInstance());
		lnkNewAccount.setMovementMethod(LinkMovementMethod.getInstance());
		
		lnkPasswordForgotten.setText(Html.fromHtml(getString(R.string.voiceblue_password_forgotten)));
		lnkNewAccount.setText(Html.fromHtml(getString(R.string.voiceblue_create_account)));
		//
		//getContentResolver().delete(SipProfile.ACCOUNT_URI, null, null);
		
		btn.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View arg0) {				
				
				mProgressDialog = ProgressDialog.show(LoginActivity.this, 
														null, 
														getString(R.string.voiceblue_fetching_config));
				mProgressDialog.show();
				
				try {
					if (txtUsername.getText().length() == 0)
						throw new Exception(getString(R.string.voiceblue_username_empty));
					
					if (txtPassword.getText().length() == 0)
						throw new Exception(getString(R.string.voiceblue_password_empty));
					
					new ConfigDownloaderTask(LoginActivity.this)
						.execute(new AccessInformation(txtUsername.getText().toString(), 
														txtPassword.getText().toString()));
					
				}
				catch (Exception e) {
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDownladingCancelled() {
		mProgressDialog.dismiss();
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
			/*else {
				mProgressDialog.dismiss();
				showError("> 0!");
			}*/				
					
			showMainScreen();
		}
		catch(Exception e) {
			showError(e.getMessage());
		}
		finally {
			mProgressDialog.dismiss();
		}
	
	}
	
	private void showMainScreen() {

		Intent home = new Intent(LoginActivity.this, SipHome.class);
		home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(home);
		finish();
	}

}
