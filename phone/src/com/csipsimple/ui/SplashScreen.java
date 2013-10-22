package com.csipsimple.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Menu;
import android.view.Window;
import android.widget.TextView;

import com.csipsimple.R;
//import com.actionbarsherlock.app.SherlockFragmentActivity;
//import com.actionbarsherlock.view.Window;

public class SplashScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_splash_screen);
		
		TextView label = (TextView) findViewById(R.id.textView1);
		label.setText(Html.fromHtml("<b><font color=#000000>Voice</font><font color=#3333FF>blue</font><font color=#000000>.com</font></b>"));
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					
					Thread.sleep(3000);
					
					Intent login = new Intent(SplashScreen.this, LoginActivity.class);
					startActivity(login);
					finish();
				}
				catch(Exception ex) { }
				
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
		System.out.print("in splash screen");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash_screen, menu);
				
		return true;
	}

}
