package com.kpi.android.activity;

import android.os.Bundle;

import com.kpi.android.R;

public class NewsReaderActivity extends WebViewActivity {

  /** Extra tags. */
  public static final String MES_DESC = "mesDesc";
  public static final String MES_TITLE = "mesTitle";
  public static final String MES_LINK = "mesLink";
  
  @Override
  protected void display() {
    final Bundle extras = getIntent().getExtras();
    final String text = "<b>" + extras.getString(MES_TITLE)
        + "</b><br>" + extras.getString(MES_DESC)
        + "<br><center><a href=\"" + extras.getString(MES_LINK) + "\">" 
        + getText(R.string.goToURL) +"</a></center>";
    getWebView().loadDataWithBaseURL(null, text, "text/html", "utf-8", null);
  }
}
