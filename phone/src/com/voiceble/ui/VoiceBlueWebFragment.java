package com.voiceble.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.SherlockListFragment;
import com.csipsimple.R;
import com.csipsimple.ui.SipHome.ViewPagerVisibilityListener;
import com.csipsimple.ui.favorites.FavAdapter;

public class VoiceBlueWebFragment extends SherlockListFragment /*implements ViewPagerVisibilityListener*/ {
	private FavAdapter mAdapter;
    private boolean mDualPane;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void attachAdapter() {
        if(getListAdapter() == null) {
            if(mAdapter == null) {
                mAdapter = new FavAdapter(getActivity(), null);
            }
            setListAdapter(mAdapter);
        }
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

/*	@Override
	public void onVisibilityChanged(boolean visible) {
		// TODO Auto-generated method stub
		
	} */
}
