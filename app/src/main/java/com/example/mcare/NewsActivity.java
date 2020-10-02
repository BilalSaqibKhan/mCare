package com.example.mcare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class NewsActivity extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);


        webView = findViewById(R.id.WebView);
        webView.loadUrl("https://moment360.000webhostapp.com/category/health/");
        webView.setWebViewClient(new WebViewClient());

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

    }


    @Override
    public void onBackPressed() {

        if(webView.canGoBack())
        {
            webView.goBack();
        }
        else
        {
            super.onBackPressed();
        }
    }


}
