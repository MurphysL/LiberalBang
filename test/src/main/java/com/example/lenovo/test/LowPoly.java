package com.example.lenovo.test;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * LowPoly
 *
 * @author: lenovo
 * @time: 2016/8/24 17:47
 */

public class LowPoly extends Activity {
    @Bind(R.id.webview)
    WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.low_poly);
        ButterKnife.bind(this);

        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }
}
