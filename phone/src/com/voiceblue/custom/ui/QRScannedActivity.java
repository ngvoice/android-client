package com.voiceblue.custom.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.voiceblue.custom.config.AccessInformation;
import com.voiceblue.custom.config.ConfigDownloaderCallbacks;
import com.voiceblue.custom.config.ConfigDownloaderResult;
import com.voiceblue.custom.config.ConfigDownloaderTask;
import com.voiceblue.custom.config.ConfigLoader;
import com.voiceblue.custom.config.VoiceBlueAccount;
import com.voiceblue.phone.R;
import com.voiceblue.phone.ui.SipHome;
import com.jwetherell.quick_response_code.CaptureActivity;

public class QRScannedActivity extends Activity implements ConfigDownloaderCallbacks {

	private ProgressDialog mProgressDialog;		
	private Button mBtnScanAgain;
	private Button mBtnLoadProfile;
	private TextView mTxtProfile;
	ImageView mBarcodeImageView;
	VoiceBlueAccount mAccount = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qr_scanned_activity);
	
//		getWindow().setTitle(getString(R.string.voiceblue_qr_auto_config));
		
		mBarcodeImageView = (ImageView) findViewById(R.id.imgBarcode);
		
		mTxtProfile = (TextView) findViewById(R.id.txtProfile);
        mBtnScanAgain = (Button) findViewById(R.id.btnScanAgain);
        mBtnLoadProfile = (Button) findViewById(R.id.btnLoad);
        
        //Bitmap image = (Bitmap) getIntent().getParcelableExtra(CaptureActivity.QR_CODE);
        Button btnExit = (Button) findViewById(R.id.btnExit);
        boolean isURL = false; //= getIntent().getBooleanExtra(CaptureActivity.IS_URL, false);
        String content = "";// = getIntent().getStringExtra(CaptureActivity.CONTENT);
        
        mBtnScanAgain.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent scan = new Intent(QRScannedActivity.this, CaptureActivity.class);
				startActivity(scan);
			}
		});
        
        mBtnLoadProfile.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				showTaskProgress("Setting up account");
				
				ConfigLoader.setupAccount(QRScannedActivity.this, mAccount);
								
				Intent home = new Intent(QRScannedActivity.this, SipHome.class);
				home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(home);
					
				if (mProgressDialog != null)
					mProgressDialog.dismiss();			
				
				finish();
			}
		});
        
        btnExit.setOnClickListener(new View.OnClickListener() {
			
    			@Override
    			public void onClick(View v) {    				 
    				 android.os.Process.killProcess(android.os.Process.myPid());
    				 System.exit(0);
    			}
    		});
        
        mBarcodeImageView.setImageResource(isURL ? R.drawable.check : R.drawable.wrong);
        
        if (!isURL) {            
        	mTxtProfile.setText(R.string.voiceblue_invalid_qr_code);
        	mBtnLoadProfile.setEnabled(false);
            return;
        } 

        new ConfigDownloaderTask(this)
        	.execute(new AccessInformation(content));
	}
	
	@Override
	public void onPreDownloading() {
		showDownloadingProgress();
	}

	@Override
	public void onDownladingCancelled() {
		if (mProgressDialog != null)
			mProgressDialog.dismiss();
	}

	@Override
	public void onConfigDownloaded(ConfigDownloaderResult result) {
		try {
			if (!result.operationSuccessful()) 
				throw new Exception(getString(R.string.voiceblue_invalid_credential));
						
			mAccount = ConfigLoader.loadFromResult(result);
			
			mTxtProfile.setText(mAccount.getDisplayName());
			
		}		
		catch(Exception e) {
			if (mProgressDialog != null)
				mProgressDialog.dismiss();
			
			mTxtProfile.setText(e.getMessage());
			mBarcodeImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wrong));
			mBtnLoadProfile.setEnabled(false);
		}
		finally {
			if (mProgressDialog != null)
				mProgressDialog.dismiss();
		}
	}
	
	private void showTaskProgress(String text) {
		mProgressDialog = ProgressDialog.show(QRScannedActivity.this, 
				null, 
				text);
	}
	
	private void showDownloadingProgress() {
		showTaskProgress(getString(R.string.voiceblue_fetching_config));
	}
}
