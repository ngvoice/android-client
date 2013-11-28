package com.voiceblue.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.SherlockListFragment;
import com.csipsimple.R;

public class VoiceBlueWebFragment extends SherlockListFragment {
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {    	
        View v = inflater.inflate(R.layout.voiceblue_web, container, false);
        
        WebView myWebView = (WebView) v.findViewById(R.id.webView);        
        myWebView.setWebViewClient(new WebViewClient() {
        		
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
            	 view.loadUrl(url);
                 return true;
            }
        });
        
        myWebView.loadUrl("http://m.lebara.co.uk/");
        
        return v;
    }

}
