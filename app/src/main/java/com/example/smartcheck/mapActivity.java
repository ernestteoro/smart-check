package com.example.smartcheck;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class mapActivity extends Activity
{
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.map);
	    WebView webview = (WebView) findViewById(R.id.webview);
	
	    WebSettings settings = webview.getSettings();
	    webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
	    webview.setScrollbarFadingEnabled(true);
	
	    settings.setJavaScriptEnabled(true);
	    settings.setLoadWithOverviewMode(true);
	    settings.setUseWideViewPort(true);
	    settings.setBuiltInZoomControls(true);
	    
	    final String dim;
        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT) {
            dim = "height";
        } else {
            dim = "width";
        }
 
        final String map = getIntent().getStringExtra(HomeDashBoardActivity.EXTRA_MAP);
        final String imgSrc = "<img src=\"" + map + "\" " + dim + "=\"100%\">";
        webview.loadDataWithBaseURL("file:///android_asset/", imgSrc,
                "text/html", "utf-8", null);
	}
	
}
