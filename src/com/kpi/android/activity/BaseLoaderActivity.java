package com.kpi.android.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.kpi.android.R;

/**
 * Base class for activities with info loading.
 * NOTE: This is very simple implementation. Consider moving loading from AsyncTask to service.
 * @author Olexandr Tereshchuk - rivne2@gmail.com
 */
public abstract class BaseLoaderActivity extends BaseActivity implements Runnable {
  
  /** Current loader task. */
  private LoaderTask currentTask = null;
  
  /** Retry button. */
  private View buttonRetry;
  
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    buttonRetry = findViewById(R.id.buttonRetry);
    if (buttonRetry != null) {
      buttonRetry.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(final View view) { retryRequest(); }
      });
    }
  }
  
  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (currentTask != null) { currentTask.cancel(true); }
    currentTask = null;
  }
  
  public void execute() {
    if (currentTask != null) { currentTask.cancel(true); }
    currentTask = new LoaderTask();
    currentTask.execute(this);
  }
  
  protected abstract void retryRequest();
  
  @Override
  public void showProgress(final int message) {
    super.showProgress(message);
    buttonRetry.setVisibility(View.GONE);
  }
  
  @Override
  public void showError(final int message) {
    super.showError(message);
    buttonRetry.setVisibility(View.VISIBLE);
  }
  
  @Override
  public void showMain() {
    super.showMain();
    buttonRetry.setVisibility(View.GONE);
  }
  
  /**
   * Handler callback.
   */
  public abstract void callback();
  
  /**
   * @author Olexandr Tereshchuk (Stanfy - www.stanfy.com)
   */
  static class LoaderTask extends AsyncTask<BaseLoaderActivity, Void, BaseLoaderActivity> {

    @Override
    protected BaseLoaderActivity doInBackground(final BaseLoaderActivity... params) {
      final BaseLoaderActivity a = params[0];
      if (!isCancelled()) { a.run(); }
      return a;
    }
    
    @Override
    protected void onPostExecute(final BaseLoaderActivity a) {
      if (isCancelled()) { return; }
      a.callback();
    }
    
  }
}
