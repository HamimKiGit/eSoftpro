package com.esoftproindia.esoftpro.status;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import com.esoftproindia.esoftpro.R;

public class ViewStatus extends AppCompatActivity {
    public static final String TAG="ViewStatus";

    private ProgressBar progressBar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_status);

        WebView webView = (WebView) findViewById(R.id.webViewViewStatus);
        progressBar=(ProgressBar)findViewById(R.id.progressBarViewStatus);
        webView.setWebViewClient(new HelpClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if(url.equals("https://m.facebook.com/home.php")){
                    view.loadUrl("https://m.facebook.com/Softprogroupofcompanies");
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                if(url.equals("https://m.facebook.com/home.php")){
                    view.loadUrl("https://m.facebook.com/Softprogroupofcompanies");
                }
            }

        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVerticalScrollBarEnabled(false);
        webView.loadUrl("https://m.facebook.com/Softprogroupofcompanies");
    }

    private class HelpClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return true;
        }
    }
}