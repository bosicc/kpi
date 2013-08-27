package com.kpi.android.activity;


import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kpi.android.Application;
import com.kpi.android.R;

/**
 * WebView activity.
 * @author Olexandr Tereshchuk - rivne2@gmail.com
 */
public class WebViewActivity extends BaseActivity {

  /** WebView. */
  private WebView webView;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    setContentView(R.layout.web);
    super.onCreate(savedInstanceState);

    webView = (WebView) findViewById(R.id.webView);
    
    webView.setWebChromeClient(new ChromeClient());
    webView.setWebViewClient(new GeneralWebViewClient());
    final WebSettings settings = webView.getSettings();
    settings.setJavaScriptEnabled(true);
    settings.setSavePassword(true);
    settings.setSaveFormData(true);
    settings.setSupportZoom(false);

//    showMain();
    
    if (savedInstanceState == null) {
      display();
    } else {
      webView.restoreState(savedInstanceState);
    }
  }
  
  protected void display() {
    webView.loadUrl(getIntent().getDataString());
  }
  
  @Override
  protected View getMainView() { return webView; }

  @Override
  protected void onSaveInstanceState(final Bundle outState) {
    super.onSaveInstanceState(outState);
    webView.saveState(outState);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    webView.setWebViewClient(null);
    webView.setWebChromeClient(null);
    webView.stopLoading();
    webView.destroy();
  }
  
  public WebView getWebView() { return webView; }

  @Override
  public boolean onKeyDown(final int keyCode, final KeyEvent event) {
    // Check if the key event was the BACK key and if there's history
    if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
      webView.goBack();
      return true;
    }
    // If it wasn't the BACK key or there's no web page history, bubble up to the default
    // system behavior (probably exit the activity)
    return super.onKeyDown(keyCode, event);
  }
  
  @Override
  public boolean onCreateOptionsMenu(final Menu menu) { return false; }

  /**
   * This creates a WebViewClient that will load any URL selected from this
   * WebView into the same WebView.
   *
   * @author Olexandr Tereshchuk - Stanfy (http://www.stanfy.com)
   */
  protected class GeneralWebViewClient extends WebViewClient {

    @Override
    public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
      super.onPageStarted(view, url, favicon);
      showProgress(R.string.common_loading);
    }

    @Override
    public void onPageFinished(final WebView view, final String url) {
      super.onPageFinished(view, url);
      showMain();
    }
    
    @Override
    public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
      if (url.equals(getIntent().getDataString())) {
        view.loadUrl(url);
        return true; 
      }
      startActivity(Application.displayIntent(Uri.parse(url)));
      return true;
    }
  }

  /**
   * Chrome client.
   */
  static class ChromeClient extends WebChromeClient { }
  
  public String getUrl() { return getIntent().getDataString(); }

}
