package com.kpi.android.activity;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.TextView;

import com.kpi.android.R;

/**
 * Base class for activities.
 * @author Olexandr Tereshchuk - rivne2@gmail.com
 */
public abstract class BaseActivity extends Activity {
  
  /** Label indicates loading process. */
  TextView loadingLabel;
  /** Staturs bar.*/
  private View statusBar;
  /** Progress image.*/
  private View progressImage;
  /** Status messages.*/
  private TextView statusMessage;
  
  
  protected void onCreate(final android.os.Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    statusBar = findViewById(R.id.statusBar);
    progressImage = findViewById(R.id.progressBar);
    statusMessage = (TextView)findViewById(R.id.statusMessageLabel);
//    loadingLabelBottom = (TextView)findViewById(R.id.extra_loading);
  }
  
  public void showProgress(final int message) {
    if (getMainView() == null) { return; }
    getMainView().setVisibility(View.GONE);
    statusBar.setVisibility(View.VISIBLE);
    statusMessage.setText(message);
//    loadingLabelBottom.setVisibility(View.VISIBLE);
//    loadingLabelBottom.setText(message);
    progressImage.setVisibility(View.VISIBLE);
  }
  
  public void showError(final int message) {
    if (getMainView() == null) { return; }
    getMainView().setVisibility(View.GONE);
    statusBar.setVisibility(View.VISIBLE);
    statusMessage.setText(message);
//    loadingLabelBottom.setVisibility(View.VISIBLE);
//    loadingLabelBottom.setText(message);
    progressImage.setVisibility(View.GONE);
  }
  
  public void showMain() {
    if (getMainView() == null) { return; }
    getMainView().setVisibility(View.VISIBLE);
    statusBar.setVisibility(View.GONE);
//    loadingLabelBottom.setVisibility(View.GONE);
  }
  
  protected View getMainView() { return null; };
  
  public boolean isOnline() {
    final ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    final NetworkInfo nInfo = cm.getActiveNetworkInfo();
    if (nInfo != null && nInfo.isConnected()) {
      return true;
    } else {
      return false;
    }
  }

}
