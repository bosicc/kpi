package com.kpi.android.rss;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class Message implements Comparable<Message>, Serializable {
  /** UID. */
  private static final long serialVersionUID = 5045897543134409311L;
  
  private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("HH:mm dd MMM yyyy", Locale.getDefault());
  private static final SimpleDateFormat PARSER = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ZZZZ", Locale.US);
  
  /** Message contract. */
  public static final class Contract {
    
    private Contract() { /* hide */ }

    /** Table name. */
    public static final String TABLE_NAME = "rss_news";
    
    /** Column names. */
    public static final String COLUMN_CODE = "_id", COLUMN_FEEDSNUM = "feedsnum", COLUMN_TITLE = "title", COLUMN_LINK = "link",
    COLUMN_DESCRIPTION = "description", COLUMN_DATE = "date", COLUMN_SOURCE = "source";
    
    /** Columns. */
    public static final int CODE = 0, FEEDSNUM = 1, TITLE = 2, LINK = 3, DESCRIPTION = 4, DATE = 5, SOURCE = 6;
    
    /** Columns list. */
    public static final String[] COLUMNS = new String[] {
      COLUMN_CODE, COLUMN_FEEDSNUM, COLUMN_TITLE, COLUMN_LINK, COLUMN_DESCRIPTION, COLUMN_DATE, COLUMN_SOURCE
    };
    
    /** SQL code to create a table. */
    public static final String SQL_CREATE;
    
    static {
      SQL_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
          +" (" + COLUMN_CODE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
          + COLUMN_FEEDSNUM +" INTEGER, "
          + COLUMN_TITLE + " TEXT, "
          + COLUMN_LINK + " TEXT, "
          + COLUMN_DESCRIPTION + " TEXT, "
          + COLUMN_DATE +" INTEGER, "
          + COLUMN_SOURCE + " TEXT)";
    }
    
    public static ContentValues toContentValues(final Message instance, final ContentValues values) {
      values.put(COLUMN_FEEDSNUM, instance.feedsnum);
      values.put(COLUMN_TITLE, instance.title);
      values.put(COLUMN_LINK, instance.link);
      values.put(COLUMN_DESCRIPTION, instance.description);
      final Date d = instance.date;
      values.put(COLUMN_DATE, d != null ? d.getTime() / 1000 : 0);
      values.put(COLUMN_SOURCE, instance.source);
      return values;
    }
    
    public static Message fromCursor(final Cursor c, final Message instance) {
      instance.setFeedsNum(c.getInt(FEEDSNUM));
      instance.setTitle(c.getString(TITLE));
      instance.setLink(c.getString(LINK));
      instance.setDescription(c.getString(DESCRIPTION));
      final long t = c.getLong(DATE);
      instance.date = t == 0 ? null : new Date(t * 1000);
      instance.setSource(c.getString(SOURCE));
      return instance;
    }
    
    public static long getOldFeedDate() {
      final Calendar c = Calendar.getInstance();
      c.add(Calendar.WEEK_OF_MONTH, -1);
      return c.getTimeInMillis() / 1000;
    }
  }

  private String title;
  private String link;
  private transient Uri linkUri;
  private String description;
  private Date date;
  private String source;
  private int feedsnum;

  public int getFeedsNum() { return feedsnum; }
  public void setFeedsNum(int feedsnum) { this.feedsnum = feedsnum; }
  
  public String getTitle() { return title; }
  public void setTitle(final String title) { this.title = title.trim(); }
  
  public String getLink() { return link; }
  public void setLink(final String link) { this.link = link; }
  public Uri getLinkUri() {
    if (linkUri == null && link != null) { linkUri = Uri.parse(link); }
    return linkUri;
  }

  public String getDescription() { return description; }
  public void setDescription(final String description) { this.description = description.trim(); }

  public String getDateString() { return FORMATTER.format(this.date); }
  public Date getDateNorm() { return date; }
  public void setDate(final String d) {
    String date = d;
    // pad the date if necessary
    while (!date.endsWith("00")) {
      date += "0";
    }
    try {
      this.date = PARSER.parse(date.trim());
    } catch (final ParseException e) {
      throw new RuntimeException(e);
    }
  }
  
  public String getSource() { return source; }
  public void setSource(final String source) { this.source = source; }
  
  public Message copy() {
    final Message copy = new Message();
    copy.feedsnum = feedsnum;
    copy.title = title;
    copy.link = link;
    copy.description = description;
    copy.date = date;
    copy.source = source;
    return copy;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("FeedNum: ");
    sb.append(feedsnum);
    sb.append("Title: ");
    sb.append(title);
    sb.append('\n');
    sb.append("Date: ");
    sb.append(this.getDateString());
    sb.append('\n');
    sb.append("Link: ");
    sb.append(link);
    sb.append('\n');
    sb.append("Description: ");
    sb.append(description);
    return sb.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((date == null) ? 0 : date.hashCode());
    result = prime * result
        + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((link == null) ? 0 : link.hashCode());
    result = prime * result + ((title == null) ? 0 : title.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Message other = (Message) obj;
    if (date == null) {
      if (other.date != null) return false;
    } else if (!date.equals(other.date)) return false;
    if (description == null) {
      if (other.description != null) return false;
    } else if (!description.equals(other.description)) return false;
    if (link == null) {
      if (other.link != null) return false;
    } else if (!link.equals(other.link)) return false;
    if (title == null) {
      if (other.title != null) return false;
    } else if (!title.equals(other.title)) return false;
    return true;
  }

  public int compareTo(final Message another) {
    if (another == null) return 1;
    // sort descending, most recent first
    return another.date.compareTo(date);
  }
}
