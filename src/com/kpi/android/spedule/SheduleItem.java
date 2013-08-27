package com.kpi.android.spedule;

import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;

public class SheduleItem {
	public int id;
	public String start;
	public String end;
	public String name;
	public String teacher;
	public String place;
	
	public int day;
	public int week;
	
	public int window;
	public int sameOn2;
	public int break5min;
	
	public Date startDate;
	public Date endDate;
	
	public int corps;
	public int auditory;
	
	public static Date parceToDate(String str){
		Date date=new Date(System.currentTimeMillis());
		String strParced[]=str.split(":");
		
		date.setHours(Integer.parseInt(strParced[0]));
		date.setMinutes(Integer.parseInt(strParced[1]));
		date.setSeconds(0);
		
		return date;
	}
	public void parcePlace(){
		String strParced[]=place.split("-");
		
		auditory=Integer.parseInt(strParced[0]);
		corps=Integer.parseInt(strParced[1]);
	}
	public static final class Contract {
	    
	    private Contract() { /* hide */ }

	    /** Table name. */
	    public static final String TABLE_NAME = "shedule";
	    
	    /** Column names. */
	    public static final String COLUMN_CODE = "id", COLUMN_START = "start", COLUMN_END = "end",
	    COLUMN_NAME = "name", COLUMN_TEACHER = "teacher", COLUMN_PLACE = "place", COLUMN_DAY = "day", COLUMN_WEEK = "week",
	    COLUMN_WIN = "window", COLUMN_SO2 = "sameOn2", COLUMN_B5M = "break5min";
	    
	    /** Columns. */
	    public static final int CODE = 0, START = 1, END = 2, NAME = 3, TEACHER = 4, PLACE = 5, DAY = 6, WEEK = 7, WIN = 8, SO2 = 9, B5M = 10;
	    
	    /** Columns list. */
	    public static final String[] COLUMNS = new String[] {
	      COLUMN_CODE, COLUMN_START, COLUMN_END, COLUMN_NAME, COLUMN_TEACHER, COLUMN_PLACE, COLUMN_DAY, COLUMN_WEEK,
	      COLUMN_WIN, COLUMN_SO2, COLUMN_B5M
	    };
	    
	    /** SQL code to create a table. */
	    public static final String SQL_CREATE;
	    
	    static {
	      SQL_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
	          +" (" + COLUMN_CODE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
	          + COLUMN_START + " TEXT, "
	          + COLUMN_END + " TEXT, "
	          + COLUMN_NAME + " TEXT, "
	          + COLUMN_TEACHER + " TEXT, "
	          + COLUMN_PLACE + " TEXT, "
	          + COLUMN_DAY + " INTEGER, "
	          + COLUMN_WEEK + " INTEGER, "
	          + COLUMN_WIN + " INTEGER, "
	          + COLUMN_SO2 + " INTEGER, "
	          + COLUMN_B5M +" INTEGER)";
	    }
	    
	    public static ContentValues toContentValues(final SheduleItem instance, final ContentValues values, final Boolean isNew) {
	    	if(!isNew){
	    		values.put(COLUMN_CODE, instance.id);
	    	}
	    	values.put(COLUMN_START, instance.start);
	    	values.put(COLUMN_END, instance.end);
	    	values.put(COLUMN_NAME, instance.name);
	    	values.put(COLUMN_TEACHER, instance.teacher);
	    	values.put(COLUMN_PLACE, instance.place);
	    	values.put(COLUMN_DAY, instance.day);
	    	values.put(COLUMN_WEEK, instance.week);
	    	values.put(COLUMN_WIN, instance.window);
	    	values.put(COLUMN_SO2, instance.sameOn2);
	    	values.put(COLUMN_B5M, instance.break5min);
	      
	    	return values;
	    }
	    public static SheduleItem fromCursor(final Cursor c, final SheduleItem instance) {
	      instance.id=c.getInt(CODE);
	      instance.start=c.getString(START);
	      instance.end=c.getString(END);
	      instance.name=c.getString(NAME);
	      instance.teacher=c.getString(TEACHER);
	      instance.place=c.getString(PLACE);
	      instance.day=c.getInt(DAY);
	      instance.week=c.getInt(WEEK);
	      instance.window=c.getInt(WIN);
	      instance.sameOn2=c.getInt(SO2);
	      instance.break5min=c.getInt(B5M);
	      
	      return instance;
	    }
	  }
}
