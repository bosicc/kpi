package com.kpi.android.activity;

import android.app.Dialog;
import android.app.TabActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;

import com.flurry.android.FlurryAgent;
import com.kpi.android.Application;
import com.kpi.android.R;
import com.kpi.android.widget.CustomizeAboutDialog;

public class KPIActivity extends TabActivity {

  private TabHost tabHost;
  
  public static final String FLURRY_KEY = "4F4LPFR9PWQAHTH8NSXH";

  /** Tab tags. */
  public static final String TAB_MENU = "menu", TAB_NEWS = "news",
      TAB_FACLIST = "facList", TAB_MAP = "map", TAB_GMAP = "googleMap";
  
  /** Extra keys. */
  private static final String KEY_ACTIVE_TAB = "kpiActiveTab";
  
  /** Dialogs. */
  private static final int DIALOG_ABOUT = 1;

  public int[] naviPoint = { 50449440, 30457600, 17 };
  public int mapItemType;
  public String mapItemIndex;
  public int mapTask = 0;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.maintab);

    tabHost = getTabHost();
    // tabHost.setOnTabChangedListener(this);

    setTabs(savedInstanceState == null ? 0 : savedInstanceState.getInt(KEY_ACTIVE_TAB, 0));
  }
  
  @Override
  protected void onSaveInstanceState(final Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(KEY_ACTIVE_TAB, tabHost.getCurrentTab());
  }

  private void setTabs(final int activeTab) {
//    tabHost.addTab(tabHost
//        .newTabSpec(TAB_MENU)
//        .setIndicator(getText(R.string.tab_schedule),
//            getResources().getDrawable(R.drawable.tab_schedule))
//        .setContent(Application.mainPageIntent(this)));

    tabHost.addTab(tabHost
        .newTabSpec(TAB_NEWS)
        .setIndicator(getText(R.string.tab_news),
            getResources().getDrawable(R.drawable.tab_news))
        .setContent(Application.newsListIntent(this)));

    tabHost.addTab(tabHost
        .newTabSpec(TAB_MAP)
        .setIndicator(getText(R.string.tab_map),
            getResources().getDrawable(R.drawable.tab_map))
        .setContent(Application.kpiMapIntent(this)));
    tabHost.addTab(tabHost
        .newTabSpec(TAB_GMAP)
        .setIndicator(getText(R.string.tab_google_map),
            getResources().getDrawable(R.drawable.tab_googlemap))
        .setContent(Application.googleMapIntent(this)));

    
    tabHost.setCurrentTab(activeTab);
  }

  public void navigateToPoint(final int itemType, final String itemIndex) {
    mapTask = 1;
    mapItemType = itemType;
    mapItemIndex = itemIndex;
    tabHost.setCurrentTabByTag(TAB_GMAP);
  }

  /**
   * On options menu creation.
   */
  @Override
  public boolean onCreateOptionsMenu(final Menu menu) {
    final MenuInflater i = getMenuInflater();
    i.inflate(R.menu.main, menu);
    menu.removeItem(R.id.menu_refresh);
    return true;
  }

  /**
   * On options menu item selection.
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {

    case R.id.menu_settings:
      startActivity(Application.settingsIntent(this));
      return true;
      
    case R.id.menu_faclist:
        startActivity(Application.facultiesIntent(this));
        return true;

    case R.id.menu_about:
      showDialog(DIALOG_ABOUT);
      return true;
      
    default:
    }

    return super.onOptionsItemSelected(item);
  }
  
  @Override
  protected Dialog onCreateDialog(final int id) {
    if (DIALOG_ABOUT == id) { return new CustomizeAboutDialog(this); }
    return super.onCreateDialog(id);
  }
  
  @Override
	protected void onStart() {
		super.onStart();
		//FlurryAgent.setReportLocation(false);
		FlurryAgent.onStartSession(this, FLURRY_KEY);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
  
  @Override
  protected void onDestroy() {
    super.onDestroy();
    try {
      removeDialog(DIALOG_ABOUT);
    } catch (final Throwable t) { /* old API hack */ }
  }

}

