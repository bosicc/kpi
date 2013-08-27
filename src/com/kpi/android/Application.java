package com.kpi.android;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.kpi.android.activity.FacDepListActivity;
import com.kpi.android.activity.KPIActivity;
import com.kpi.android.activity.KpiMapActivity;
import com.kpi.android.activity.MainPageActivity;
import com.kpi.android.activity.MapGoogleActivity;
import com.kpi.android.activity.NewsListActivity;
import com.kpi.android.activity.NewsReaderActivity;
import com.kpi.android.activity.SettingsActivity;
import com.kpi.android.activity.WebViewActivity;

/**
 * Android KPI Application.
 * @author Olexandr Tereshchuk - rivne2@gmail.com
 */
public class Application extends android.app.Application {
  
  
  public static Intent newsListIntent(final Context context) {
    return new Intent(context, NewsListActivity.class);
  }
  public static Intent newsDetailsIntent(final Context context, final String description, final String title, final String link) {
	    return new Intent(context, NewsReaderActivity.class)
	    .putExtra(NewsReaderActivity.MES_DESC, description)
	    .putExtra(NewsReaderActivity.MES_TITLE, title)
	    .putExtra(NewsReaderActivity.MES_LINK, link);
	  }  
  public static Intent kpiIntent(final Context context) {
    return new Intent(context, KPIActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
  }
  
  public static Intent displayIntent(final Uri uri) {
    return new Intent(Intent.ACTION_VIEW, uri);
  }
  
  public static Intent webViewIntent(final Context context, final Uri url) {
    return new Intent(context, WebViewActivity.class).setData(url);
  }
  
  public static Intent settingsIntent(final Context context) {
    return new Intent(context, SettingsActivity.class);
  }
  
  public static Intent facultiesIntent(final Context c) {
    return new Intent(c, FacDepListActivity.class);
  }
  
  public static Intent kpiMapIntent(final Context c) {
    return new Intent(c, KpiMapActivity.class);
  }
  
  public static Intent googleMapIntent(final Context c) {
    return new Intent(c, MapGoogleActivity.class);
  }
  
  public static Intent mainPageIntent(final Context c) {
    return new Intent(c, MainPageActivity.class);
  }

}
