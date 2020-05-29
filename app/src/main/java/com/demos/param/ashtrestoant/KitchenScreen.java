package com.demos.param.ashtrestoant;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import okhttp3.OkHttpClient;

public class KitchenScreen extends AppCompatActivity  {
    public WebView web;

    private String WebAddress = "http://jaishreekrishna.me/rresto/public/Kichansceen";
    private String sharew = "share";
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchenscreen);
        getSupportActionBar().hide();
        web = (WebView) findViewById(R.id.webview01);
        web.loadUrl("http://jaishreekrishna.me/rresto/public/Kichansceen");
        web.getSettings().setJavaScriptEnabled(true);
        web.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        web.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.e("WebView", consoleMessage.message());
                return true;
            }

        });

        swipeRefreshLayout=findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                web.loadUrl(WebAddress);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


}
