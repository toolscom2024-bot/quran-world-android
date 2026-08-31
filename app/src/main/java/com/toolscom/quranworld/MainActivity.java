package com.toolscom.quranworld;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final String HOME = "https://alseraj-almuneer.toolscom2024.chatgpt.site";
    private WebView webView;
    private ProgressBar progress;

    @SuppressLint("SetJavaScriptEnabled")
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(7,60,50));
        getWindow().setNavigationBarColor(Color.rgb(7,60,50));

        FrameLayout root = new FrameLayout(this);
        webView = new WebView(this);
        progress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progress.setMax(100);
        root.addView(webView, new FrameLayout.LayoutParams(-1,-1));
        root.addView(progress, new FrameLayout.LayoutParams(-1,8));
        setContentView(root);

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        s.setUserAgentString(s.getUserAgentString() + " QuranWorldAndroid/1.0");

        webView.setWebChromeClient(new WebChromeClient(){
            @Override public void onProgressChanged(WebView view, int value){
                progress.setProgress(value);
                progress.setVisibility(value < 100 ? View.VISIBLE : View.GONE);
            }
        });
        webView.setWebViewClient(new WebViewClient(){
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest req){
                Uri uri=req.getUrl();
                String host=uri.getHost()==null?"":uri.getHost();
                if(host.endsWith("chatgpt.site") || host.endsWith("qurango.net") || host.endsWith("radiojar.com")) return false;
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
                return true;
            }
            @Override public void onReceivedError(WebView v, WebResourceRequest r, android.webkit.WebResourceError e){
                if(r.isForMainFrame()) showOffline();
            }
        });
        if(state==null) webView.loadUrl(HOME); else webView.restoreState(state);
    }

    private void showOffline(){
        TextView t=new TextView(this); t.setText("تعذر الاتصال بالإنترنت\nتحقق من الشبكة ثم اضغط للمحاولة مجددًا");
        t.setTextColor(Color.rgb(20,41,35)); t.setTextSize(18); t.setGravity(17); t.setPadding(40,40,40,40);
        t.setBackgroundColor(Color.rgb(243,239,229)); t.setOnClickListener(v->{ ((FrameLayout)t.getParent()).removeView(t); webView.loadUrl(HOME); });
        ((FrameLayout)webView.getParent()).addView(t,new FrameLayout.LayoutParams(-1,-1));
    }
    @Override public void onBackPressed(){ if(webView!=null && webView.canGoBack()) webView.goBack(); else super.onBackPressed(); }
    @Override protected void onSaveInstanceState(Bundle out){ webView.saveState(out); super.onSaveInstanceState(out); }
    @Override protected void onDestroy(){ if(webView!=null) webView.destroy(); super.onDestroy(); }
}
