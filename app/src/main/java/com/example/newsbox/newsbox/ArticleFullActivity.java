package com.example.newsbox.newsbox;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class ArticleFullActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_full);

        WebView webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        Uri data;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String isOnline = extras.getString("EXTRA_ONLINE_MODE");
            if (isOnline.equals("OFFLINE"))
            {
                String dataOffline = extras.getString("EXTRA_CACHED_CONTENT");
                webView.loadData(dataOffline, "text/html; charset=UTF-8", null);
            }
            else
            {
                data = Uri.parse(extras.getString("EXTRA_URI"));
                webView.loadUrl(data.toString());
            }
        }
    }
}
