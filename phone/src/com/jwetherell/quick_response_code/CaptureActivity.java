/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jwetherell.quick_response_code;

import java.util.EnumSet;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.voiceblue.custom.ui.LoginActivity;
import com.voiceblue.phone.R;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.client.result.ParsedResultType;
import com.jwetherell.quick_response_code.result.ResultHandler;
import com.jwetherell.quick_response_code.result.ResultHandlerFactory;

/**
 * Example Capture Activity.
 * 
 * @author Justin Wetherell (phishman3579@gmail.com)
 */
public class CaptureActivity extends DecoderActivity {

	public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    
    private static final String KEY 	= "abc123abc123abc1";    
    
    private static final String TAG = CaptureActivity.class.getSimpleName();
    private static final Set<ResultMetadataType> DISPLAYABLE_METADATA_TYPES = EnumSet.of(ResultMetadataType.ISSUE_NUMBER, ResultMetadataType.SUGGESTED_PRICE,
            ResultMetadataType.ERROR_CORRECTION_LEVEL, ResultMetadataType.POSSIBLE_COUNTRY);

    private TextView statusView = null;
    private View resultView = null;
    private boolean inScanMode = false;
    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.qr_capture);
        Log.v(TAG, "onCreate()");

        resultView = findViewById(R.id.result_view);
        statusView = (TextView) findViewById(R.id.status_view);

        inScanMode = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (inScanMode)
                finish();
            else
                onResume();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void handleDecode(Result rawResult, Bitmap barcode) {
        drawResultPoints(barcode, rawResult);

        ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(this, rawResult);
        handleDecodeInternally(rawResult, resultHandler, barcode);
    }

    protected void showScanner() {
        inScanMode = true;
        resultView.setVisibility(View.GONE);
        statusView.setText(R.string.msg_default_status);
        statusView.setVisibility(View.VISIBLE);
        viewfinderView.setVisibility(View.VISIBLE);
    }

    protected void showResults() {
        inScanMode = false;
        statusView.setVisibility(View.GONE);
        viewfinderView.setVisibility(View.GONE);
        resultView.setVisibility(View.VISIBLE);
    }
    
    private void handleDecodeInternally(Result rawResult, ResultHandler resultHandler, Bitmap barcode) {
    	onPause();
    	try {
    		
    		Intent scannedActivity = new Intent(this, LoginActivity.class);
    		
	    	if (resultHandler.getType() != ParsedResultType.TEXT)
	    		throw new Exception(getString(R.string.voiceblue_invalid_qr_code));
	            	
	    	scannedActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    		
    		String b64String = resultHandler.getDisplayContents().toString();
    		String creds	= new String(decrypt(Base64.decode(b64String.getBytes(), Base64.DEFAULT)), "UTF-8");
    		
    		String credsArray[] = creds.split("\n");
    		
    		if (credsArray.length != 2) 
    			throw new Exception(getString(R.string.voiceblue_invalid_qr_code));
    		
    		if (credsArray[0].length() == 0 || credsArray[1].length() == 0)
    			throw new Exception(getString(R.string.voiceblue_invalid_qr_code));
    		
    		scannedActivity.putExtra(USERNAME, credsArray[0]);
        	scannedActivity.putExtra(PASSWORD, credsArray[1]);
        	
        	startActivity(scannedActivity);
        	finish();
        	return;
    	}
    	catch(BadPaddingException ex) {
    		ex.printStackTrace();
    		showError(getString(R.string.voiceblue_invalid_qr_code));    		
    	}
    	catch(Exception ex) {
    		ex.printStackTrace();    		
    		showError("Unknown error: " + ex.getMessage());
    	}
    	
    	
    	
    	//showResults();
    }
    
    private byte[] decrypt(byte[] encrypted) throws Exception {
    	SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
    	
    	Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    	cipher.init(Cipher.DECRYPT_MODE, keySpec);
    	
    	byte[] decrypted = cipher.doFinal(encrypted);

    	return decrypted;
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
				finish();
			}
		});
			
		dialog = builder.create();
		dialog.show();
	}

}
