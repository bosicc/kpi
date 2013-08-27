package com.kpi.android.content;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kpi.android.rss.Message;
import com.kpi.android.spedule.SheduleItem;

public class DBHelper extends SQLiteOpenHelper {

  /** Database version. */
  private static final int dbVersion = 5; //NOTO: add only +1
  /** Database name. */
  private static final String DB_NAME = "androidKPIMain.db";

  public DBHelper(final Context context) {
    super(context, DB_NAME, null, dbVersion);
  }

  private void dropTables(final SQLiteDatabase db) {
    db.execSQL("DROP TABLE IF EXISTS " + Message.Contract.TABLE_NAME);
    db.execSQL("DROP TABLE IF EXISTS " + SheduleItem.Contract.TABLE_NAME);
  }
  
  private void createTables(final SQLiteDatabase db) {
    db.execSQL(Message.Contract.SQL_CREATE);
    db.execSQL(SheduleItem.Contract.SQL_CREATE);
  }
  
  @Override
  public void onCreate(final SQLiteDatabase db) {
    createTables(db);
  }
  
  @Override
  public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
    dropTables(db);
    createTables(db);
  }
}
