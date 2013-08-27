package com.kpi.android.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kpi.android.Application;
import com.kpi.android.DialogHandler;
import com.kpi.android.R;
import com.kpi.android.content.DBHelper;
import com.kpi.android.content.MultiSelectListPreference;
import com.kpi.android.rss.FeedParser;
import com.kpi.android.rss.FeedParserFactory;
import com.kpi.android.rss.Message;
import com.kpi.android.rss.ParserType;

/**
 * @author Olexandr Tereshchuk - rivne2@gmail.com
 */
public class NewsListActivity extends BaseLoaderActivity implements
    OnItemClickListener, OnSharedPreferenceChangeListener {

  /** Logging TAG. */
  private static final String TAG = "NewsListActivity";
  
  /** Feed index. */
  private static final String KEY_FEED_INDEX = "kpiFeedIndex";
  
  /** Is first start. */
  private boolean firstStart = true;
  /** Messages. */
  private List<Message> messages;
  /** List view. */
  private ListView mList;

  /** DB helper. */
  private DBHelper dbHelper;
  
  /** Feeds. */
  private String feedUrlList[];
  
  /** Feed index. */
  private int feedIndex = -1;
  
  /** Shared preferences. */
  private SharedPreferences sharedPrefs;
  
  /** Colors for different news */
  private static int [] colorlist;
  
  /** All rss feeds */
  private static String[] allRssFeedslist;
  
  /** All rss feeds labels*/
  private static String[] allRssFeedsLabels;
  
  private Map<String, String> feedMapLabels;
  private Map<String, Integer> feedMapColor;
  
  private DialogHandler progressHandler;
  

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    setContentView(R.layout.news);
    super.onCreate(savedInstanceState);
    mList = (ListView) findViewById(android.R.id.list);
    mList.setOnItemClickListener(this);
    
    progressHandler = new DialogHandler(this, (View)findViewById(R.id.statusBarBottom));
    progressHandler.hideWaitDialog();
    
    showProgress(R.string.common_loading);
    
	int[] colors = {0, 0xFF808080, 0}; // red for the example
	mList.setDivider(new GradientDrawable(Orientation.RIGHT_LEFT, colors));
	mList.setDividerHeight(1);
	
	Resources r  = getBaseContext().getResources();
	colorlist = r.getIntArray(R.array.rssFeed_colors);
	allRssFeedslist = r.getStringArray(R.array.rssFeed_val);
	allRssFeedsLabels = r.getStringArray(R.array.rssFeed_names);
	feedMapLabels = new HashMap();
	feedMapColor = new HashMap();
	for (int i=0; i<allRssFeedslist.length; i++){
		feedMapLabels.put(allRssFeedslist[i], allRssFeedsLabels[i]);
		feedMapColor.put(allRssFeedslist[i], colorlist[i]);
		//Log.i(TAG,"RSS: key="+allRssFeedslist[i] + "val="+allRssFeedsLabels[i]);
	}
    
    dbHelper = new DBHelper(this);
    
    sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    feedUrlList = MultiSelectListPreference
        .parseStoredValue(sharedPrefs.getString("rssFeeds", null));
    
    if (savedInstanceState == null) {
      firstStart = true;
    } else {
      firstStart = false;
      feedIndex = savedInstanceState.getInt(KEY_FEED_INDEX, -1);
    }

    final StateHolder state = (StateHolder) getLastNonConfigurationInstance();
    if (state == null) {
      execute();
    } else {
      messages = state.messages;
      if (messages == null) {
        execute();
      } else {
        callback();
      }
    }
  }
  
  @Override
  public void onSharedPreferenceChanged(final SharedPreferences prefs, final String key) {
//	Log.d(TAG, "Props are changed. key="+key);
    if (key.equals("rssFeeds")) {
    
      feedUrlList = MultiSelectListPreference
          .parseStoredValue(sharedPrefs.getString("rssFeeds", null));
      feedIndex = -1;
      showProgress(R.string.common_loading);
      execute();
    }
  }
  
  @Override
  protected void onStart() {
    super.onStart();
    sharedPrefs.registerOnSharedPreferenceChangeListener(this);
  }
  
  @Override
  protected void onStop() {
    super.onStop();
    sharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
  }
  
  @Override
  protected void onSaveInstanceState(final Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(KEY_FEED_INDEX, feedIndex);
  }
  
  @Override
  public boolean onCreateOptionsMenu(final Menu menu) {
    final MenuInflater i = getMenuInflater();
    i.inflate(R.menu.main, menu);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(final MenuItem item) {
    if (R.id.menu_refresh == item.getItemId()) {
    
    	
      // TODO: Remove this update. It should be done automaticaly
      // but in HTC G1 + android 2.1 it's not work :(
      feedUrlList = MultiSelectListPreference
    	        .parseStoredValue(sharedPrefs.getString("rssFeeds", null));
//      Log.d(TAG, Arrays.toString(feedUrlList));
      
      //Delete old news
      deleteAllNews();
      
      feedIndex = -1;
      firstStart = false;
      execute();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    dbHelper.close();
    dbHelper = null;
  }

  @Override
  public void run() {
    if (firstStart) {
      loadFeedLocal();
      deleteOldFeeds();
    } else {
      loadFeed(ParserType.ANDROID_SAX);
    }
  }

  @Override
  public void callback() {
    if (firstStart) {
      if (messages == null || messages.size() == 0) {
        firstStart = false;
        execute();
      } else {
        render();
      }
    } else {
      boolean loadMore = feedUrlList != null && feedIndex < feedUrlList.length;
      if (messages == null || messages.size() == 0) {
        if (loadMore) {
          execute();
        } else {
          showError(R.string.common_nothingToShow);
        }
      } else {
        render();
        if (loadMore) { execute(); }
      }
    }
  }

  private void render() {
    mList.setAdapter(new NewsAdapter(this, messages));
    showMain();
  }

  @Override
  public Object onRetainNonConfigurationInstance() {
    final StateHolder state = new StateHolder();
    state.messages = messages;
    return state;
  }

  @Override
  protected void retryRequest() {
    showProgress(R.string.common_loading);
    execute();
  }

  @Override
  protected View getMainView() {
    return mList;
  }

  @Override
  public void onItemClick(final AdapterView<?> adapter, final View view,
      final int position, final long id) {
    final Message m = (Message) adapter.getItemAtPosition(position);
    startActivity(Application.newsDetailsIntent(this, m.getDescription(),
        m.getTitle(), m.getLink()));
  }

  /**
   * Used from Thread to load data from RSS feed
   * @param type
   */
  private void loadFeed(final ParserType type) {
    feedIndex++;
//    Log.d(TAG, "Feed index: " + feedIndex);
    
    if (feedUrlList == null || feedIndex >= feedUrlList.length) { 
    	return; 
    }
    
    try {
      final String currentFeed = feedUrlList[feedIndex];
//      Log.d(TAG, "ParserType=" + type.name());
//      Log.d(TAG, "Loading feed: " + currentFeed);

      progressHandler.showWaitDialog(getBaseContext().getResources().getString(R.string.common_loading) + " " + 
      		feedMapLabels.get(currentFeed));
      
      final FeedParser parser = FeedParserFactory.getParser(type, currentFeed);
      long start = System.currentTimeMillis();
      final List<Message> messages = parser.parse();
      
      if (messages != null && messages.size() > 0) {
    	  if (this.messages == null) { 
    		  this.messages = new ArrayList<Message>(); 
    	  }
    	  for (final Message m : messages) {

    	  		m.setSource(feedMapLabels.get(currentFeed));
    	  		m.setFeedsNum(feedMapColor.get(currentFeed));
    	  		
//    	  		Log.i(TAG,"Start 1");
//    	  		for (int i=0;i<allRssFeedslist.length;i++){
//    	  			if (allRssFeedslist[i].equals(currentFeed)){
//    	  				m.setFeedsNum(i);
//    	  				m.setSource(allRssFeedsLabels[i]);
//    	  			}
//    	  		}
//    	  		Log.i(TAG,"---- 2");
//    	  			m.setSource(feedMap.get(currentFeed));
//    	  			m.setFeedsNum(1);
//    	  		Log.i(TAG,"---- 3");
    	  	}
    	  	// store just downloaded data
    	  	saveFeedLocal(messages);
    	  	
    	  	// load sorted data from DB
    	  	loadFeedLocal();
    	  	
//	        if (feedIndex > 0) {
//	          messages.addAll(this.messages);
//	        }
//	        this.messages = messages;
    	  	
    	  	 
      }
      final long duration = System.currentTimeMillis() - start;
//      Log.d(TAG, "Parser duration=" + duration);
    } catch (final Throwable t) {
      Log.e(TAG, "Can't load feed", t);
    }
    
    progressHandler.hideWaitDialog();
  }

  private void deleteOldFeeds() {
    if (dbHelper == null) { return; }
    final SQLiteDatabase db = dbHelper.getWritableDatabase();
    db.delete(Message.Contract.TABLE_NAME, Message.Contract.COLUMN_DATE + " <= ?",
        new String[] {String.valueOf(Message.Contract.getOldFeedDate())});
    db.close();
  }
  
  private void deleteAllNews() {
	    if (dbHelper == null) { return; }
	    final SQLiteDatabase db = dbHelper.getWritableDatabase();
	    db.delete(Message.Contract.TABLE_NAME, null,null);
	    db.close();
	  }
  
  private void saveFeedLocal(final List<Message> messages) {
    if (dbHelper == null) { return; }
    final SQLiteDatabase db = dbHelper.getWritableDatabase();
    for (final Message m : messages) {
      db.insert(Message.Contract.TABLE_NAME, null,
          Message.Contract.toContentValues(m, new ContentValues()));
    }
    db.close();
  }

  private void loadFeedLocal() {
    if (dbHelper == null) { return; }
    final SQLiteDatabase db = dbHelper.getReadableDatabase();
    final Cursor cur = db.query(Message.Contract.TABLE_NAME,
        Message.Contract.COLUMNS, null, null, null, null,
        Message.Contract.COLUMN_DATE + " DESC");
    try {
      messages = new ArrayList<Message>(cur.getCount());
      if (cur.moveToFirst()) {
        do {
          messages.add(Message.Contract.fromCursor(cur, new Message()));
        } while (cur.moveToNext());
      }
    } finally {
      cur.close();
      db.close();
    }
  }

  /**
   * @author Olexandr Tereshchuk - rivne2@gmail.com
   */
  private static final class ListItemHolder {
    public TextView title;
    public TextView date;
    public TextView source;
    public TextView color;
  }

  /**
   * @author Olexandr Tereshchuk - rivne2@gmail.com
   */
  private static class NewsAdapter extends ArrayAdapter<Message> {

    public NewsAdapter(final Context context, final List<Message> messages) {
      super(context, android.R.layout.simple_list_item_1,
          messages == null ? Collections.<Message> emptyList() : messages);
    }

    @Override
    public View getView(final int position, final View view,
        final ViewGroup parent) {
      View v = view;
      ListItemHolder h = null;
      if (v == null) {
        v = (View) LayoutInflater.from(getContext()).inflate(R.layout.newsrow,
            parent, false);
        h = new ListItemHolder();
        h.title = (TextView) v.findViewById(R.id.news_Title);
        h.date = (TextView) v.findViewById(R.id.news_Date);
        h.source = (TextView) v.findViewById(R.id.news_Source);
        h.color = (TextView) v.findViewById(R.id.textColor);
        v.setTag(h);
      } else {
        h = (ListItemHolder) v.getTag();
      }

      final Message m = getItem(position);
      h.title.setText(m.getTitle());
      h.date.setText(m.getDateString());
      h.source.setText(m.getSource());
      //h.color.setBackgroundColor(colorlist[m.getFeedsNum()]);
      h.color.setBackgroundColor(m.getFeedsNum());

      return v;
    }
  }

  /**
   * @author Olexandr Tereshchuk - rivne2@gmail.com
   */
  private static class StateHolder {
    /** Messages. */
    List<Message> messages = null;
  }
}
