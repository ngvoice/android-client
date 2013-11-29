package com.voiceblue.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.Window;
import android.widget.TextView;

import com.csipsimple.R;

public class SplashScreen extends Activity {
	private final int SPLASH_DURATION = 2500;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_splash_screen);
		
		TextView label = (TextView) findViewById(R.id.textView1);
		label.setText(Html.fromHtml("<b><font color=#000000>voice</font><font color=#3333FF>blue</font><font color=#000000>.com</font></b>"));
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					
					Thread.sleep(SPLASH_DURATION);
					
					Intent login = new Intent(SplashScreen.this, LoginActivity.class);
					login.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					startActivity(login);
					finish();
				}
				catch(Exception e) { /* do nothing */  }			
				
			}
		}).start();
		/*
		new Handler().post(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(5000);
					
				//	Intent home = new Intent(SplashScreen.this, SipHome.class);
			     //   startActivity(home);
			      //  finish();
				}
				catch(Exception ex) { }
				
		        
			}
		});	
		*/		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.splash_screen, menu);
				
		return true;
	}

}
