package com.kpi.android.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.kpi.android.R;
import com.kpi.android.content.DBHelper;
import com.kpi.android.spedule.SheduleItem;
import com.kpi.android.spedule.Sorter;
import com.kpi.android.widget.SheducleClickDialog;
import com.kpi.android.widget.SheduleChangeDay;
import com.kpi.android.widget.SheduleEditDialog;
import com.kpi.android.widget.SheduleHowEditDialog;

public class MainPageActivity extends Activity implements OnItemClickListener {
	
	private SheduleItem items[];
	public SheduleItem pairs[];
	private SheduleItem breaks[];
	private String dayName[];
	private String weekName[];
	
	private Button editButton; 
	private ListView mList;
  	private TextView event_timer;
  	private TextView event_name;
  	private TextView title;
  	private int eventMode=-1;
  	private int currentEvent;
  	
  	private Boolean initiated = false;
  	
  	public KPIActivity parent;
  	private DBHelper dbHelper;
  	
  	private int cDay=0;
  	private int cWeek=0;
  	
	protected void onCreate(final android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_page);
		
		editButton=(Button) findViewById(R.id.editButton);
		
		event_timer = (TextView) findViewById(R.id.event_timer);
		event_name = (TextView) findViewById(R.id.event_name);
		title= (TextView) findViewById(R.id.title);
		
//		Timer timer = new Timer();
		
		mList = (ListView) findViewById(R.id.scheduList);
	    mList.setOnItemClickListener(this);
	    
	    parent=(KPIActivity)getParent();
	    
	    dbHelper = new DBHelper(this);
	    
	    long localTime= System.currentTimeMillis();
	    
	    cDay=getDayOfTheWeek(localTime);
	    cWeek=getWeekNumber(localTime);
	    
	    Resources r = getResources();
	    dayName=r.getStringArray(R.array.day_long);
	    weekName=r.getStringArray(R.array.week_name);
	
//	    getSheducles();
	    
//	    timer.schedule(timerProcessor(), 0,1000);
	    
	    editButton.setOnClickListener(edit);
	}
	public int getDayOfTheWeek(long time){
		Date date=new Date(time);
		
		return getNormalDay(date.getDay());
	}
	public int getNormalDay(int day){
		if(day!=0){
			return day-1;
		}else{
			return 6;
		}
	}
	public int getWeekNumber(long time){
		GregorianCalendar calendar=new GregorianCalendar();
		
		Date date=new Date(time);
		Date spr1Date=new Date();
		int day=0;
		int month=date.getMonth();
		int year=date.getYear();
		int week=0;
		int i;
		
		spr1Date.setYear(year);
		spr1Date.setMonth(8);
		
		day=getNormalDay(spr1Date.getDay());
		
		if(month>=8){
			for(i=8;i<month;i++){
				day+=getDaysInMonth(i,year);
			}
		}else{
			for(i=8;i<12;i++){
				day+=getDaysInMonth(i,year);
			}
			for(i=0;i<month;i++){
				day+=getDaysInMonth(i,year+1);
			}
		}
		
		day+=calendar.get(Calendar.DAY_OF_MONTH)-1;
		week=(int)Math.floor(day/7);
		if(week==Math.round(week/2)*2){
			return 0;
		}else{
			return 1;
		}
	}
	public int getDaysInMonth(int month, int year){
		switch(month){
			case 0://Январь
			case 2://Март
			case 4://Май 
			case 6://Июль
			case 7://Август
			case 9://Октябрь
			case 11://Декабрь
				return 31;	
			case 3://Апрель
			case 5://Июнь 	
			case 8://Сентябрь
			case 10://Ноябрь
				return 30;
			case 1://Февраль
				if(getLeapYear(year)){
					return 29;
				}else{
					return 28;
				}
		}
		return 30;
	}
	public Boolean getLeapYear(int year){
		Boolean canDvide4=(year==Math.round(year/4)*4);
		Boolean canDvide100=(year==Math.round(year/100)*100);
		Boolean canDvide400=(year==Math.round(year/400)*400);
		
		if (canDvide4) {
			if (canDvide100) {
				if (canDvide400) {
					return true;
		        }else{
		            return false;
		        }
			}else{
		        return true;      	  
			}
		}else{
			 return false;
		}
	}
	public void getSheducles(){
		Resources r = getResources();
		
		loadSheducle();
		
		processSheduleItems();
		Sorter.quickSort(pairs);
		
		mList.setAdapter(new SheduleAdapter(this,pairs));
		
		title.setText(r.getString(R.string.mail_page_sheduleOn)+dayName[cDay]+", "+weekName[cWeek]);
		initiated=false;
	}
	public void errorDialog(int id)
	{
	    int alertMessage;
	    
	    switch (id)
	    {
	        case 0:
	            alertMessage = R.string.alert_invalid_startFormat;
	            break;
	        case 1:
	            alertMessage = R.string.alert_invalid_enFormat;
	            break;
	        case 2:
	            alertMessage = R.string.alert_invalid_placeFormat;
	            break;
	        default:
	            return;
	    }
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
        builder.setMessage(alertMessage)
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton) 
                    {                        
                    }
                })
            .show();
	}
	private OnClickListener edit = new OnClickListener() {
        public void onClick(View v) {
        	howEditDialog();
        }
    };
    public void showSheduleChangeDay(){
	    SheduleChangeDay dialog = new SheduleChangeDay(this, cDay, cWeek);
		dialog.show();
	}
    public void howEditDialog(){
	    SheduleHowEditDialog dialog = new SheduleHowEditDialog(this);
		dialog.show();
	}
    public void addRowDialog(){
    	SheduleEditDialog dialog = new SheduleEditDialog(this,parent,"1",-1);
		dialog.show();
    }
	public TimerTask timerProcessor(){
		return new TimerTask()
		{
			long localTime;
			Date localDate=new Date();
			
			int currentDay;
			int seconds = 0;
			
			@Override
			public void run() {
				localTime = System.currentTimeMillis();
				localDate.setTime(localTime);
				if(!initiated){
					findClosestPair();
					currentDay=localDate.getDay();
					initiated=true;
				}
				if(initiated){
					switch(eventMode){
						case 0:
							seconds=(int)(items[currentEvent].endDate.getTime()-localTime)/1000;
							break;
						case 1:
							seconds=(int)(items[currentEvent+1].startDate.getTime()-localTime)/1000;
							break;
						case 2:
							seconds=(int)(items[currentEvent].startDate.getTime()-localTime)/1000;
					}
				}
				if(eventMode!=3 && seconds<=0){
					initiated=false;
				}
				if(localDate.getDay()!=currentDay){
					initiated=false;
				}
				runOnUiThread(new Runnable()
				{
					public void run() {
						event_name.setText(getEventName(eventMode));
						if(eventMode!=3){
							event_timer.setText(getEventLabel(eventMode)+": "+toNormalTime(seconds));
						}else{
							event_timer.setText(getEventLabel(eventMode));
						}
					}
				}); 
			}
		};
	}
	public String toNormalTime(int seconds){
		int h;
		int m;
		int s;
		
		Resources r = getResources();
		String hTxt=r.getString(R.string.mail_page_how_short);
		String mTxt=r.getString(R.string.mail_page_min_short);
		String sTxt=r.getString(R.string.mail_page_sec_short);
		
		m=(int) Math.floor(seconds/60);
		h=(int) Math.floor(m/60);
		m-=h*60;
		s=seconds-h*3600-m*60;
		
		if(h!=0){
			return h+hTxt+","+m+mTxt+","+s+sTxt;
		}else{
			if(m!=0){
				return m+mTxt+","+s+sTxt;
			}else{
				return s+sTxt;
			}
		}
	}
	public String getEventName(int mode){
		Resources r = getResources();
		
		switch(mode){
			case 0:
				return items[currentEvent].name;
			case 1:
				return r.getString(R.string.mail_page_break);
			case 2:
				return r.getString(R.string.mail_page_morning);
			case 3:
				return r.getString(R.string.mail_page_freeTime);
		}
		return "";
	}
	public String getEventLabel(int mode){
		Resources r = getResources();
		
		switch(mode){
			case 0:
				return r.getString(R.string.mail_page_beforeEndPair);
			case 1:
				return r.getString(R.string.mail_page_beforeEndBreak);
			case 2:
				return r.getString(R.string.mail_page_beforeStart);
			case 3:
				return r.getString(R.string.mail_page_finish);
		}
		return "";
	}
	public void findClosestPair(){
		long localTime = System.currentTimeMillis();
		
		int i;

		if(localTime<items[0].startDate.getTime()){
			eventMode=2;
			currentEvent=0;
			return;
		}
		for(i=0;i<items.length;i++){
			if(localTime>=items[i].startDate.getTime() && localTime<items[i].endDate.getTime()){
				currentEvent=i;
				eventMode=0;
				return;
			}
			if(i<items.length-1 && localTime>=items[i].endDate.getTime() && localTime<items[i+1].startDate.getTime()){
				currentEvent=i;
				eventMode=1;
				return;
			}
		}
		if(localTime>items[items.length-1].endDate.getTime()){
			eventMode=3;
			currentEvent=items.length-1;
			return;
		}
	}
	public void processSheduleItems(){
		List<SheduleItem> cItems=new ArrayList<SheduleItem>();
		
		int i;
		SheduleItem si;
		for(i=0;i<pairs.length;i++){
			
			pairs[i].startDate=SheduleItem.parceToDate(pairs[i].start);
			pairs[i].endDate=SheduleItem.parceToDate(pairs[i].end);
			
			pairs[i].day=cDay;
			pairs[i].week=cWeek;
			
			pairs[i].window=0;
			pairs[i].sameOn2=0;
			pairs[i].break5min=0;
			pairs[i].parcePlace();
			
			//si=new SheduleItem();
			
			//si.id=cItems.size();
			//si.start=pairs[i].start;
			//si.end=pairs[i].end;
			//si.name=pairs[i].name;
			//si.teacher=pairs[i].teacher;
			//si.place=pairs[i].place;
			//si.day=cDay;
			//si.week=cWeek;
			//si.window=0;
			//si.sameOn2=0;
			//si.break5min=0;
			//si.startDate=pairs[i].startDate;
			//si.endDate=pairs[i].endDate;
			//si.parcePlace();
			
			cItems.add(pairs[i]);
		}
		
		items=cItems.toArray(new SheduleItem[cItems.size()]);
	}
	public SheduleItem[] parceToSheduleItem(String[] StrAr){
		SheduleItem siAr[]=new SheduleItem[StrAr.length];
    	
    	int i;
    	String[] s;
    	SheduleItem si;
    	
	    for(i=0;i<StrAr.length;i++){
	    	si=new SheduleItem();
	    	
	    	s=StrAr[i].split(";");
	    	si.start=s[0];
	    	si.end=s[1];
	    	si.name=s[2];
	    	si.teacher=s[3];
	    	si.place=s[4];
	    	
	    	siAr[i]=si;
	    }
    	
    	return siAr;
    }
	@Override
	public void onItemClick(final AdapterView<?> adapter, final View view, final int position, final long id) {
		SheduleItem si = pairs[position];
		SheducleClickDialog dialog = new SheducleClickDialog(this,parent,si.name,""+si.corps, position);
		dialog.show();
	}
	private void savePairs() {
		int i;
		
		for(i=0;i<pairs.length;i++){
			saveSheducle(i);
		}
	}
	private void saveSheducle(int index) {
	    if (dbHelper == null) { return; }
	    final SQLiteDatabase db = dbHelper.getWritableDatabase();

	    if(db.insert(SheduleItem.Contract.TABLE_NAME, null, SheduleItem.Contract.toContentValues(pairs[index], new ContentValues(),false))==-1){
	    	db.update(SheduleItem.Contract.TABLE_NAME,SheduleItem.Contract.toContentValues(pairs[index], new ContentValues(),false),"id="+pairs[index].id,null);
	    }
	    db.close();
	}
	private void addSheducle(SheduleItem si) {
	    if (dbHelper == null) { return; }
	    final SQLiteDatabase db = dbHelper.getWritableDatabase();
	    
	    db.insert(SheduleItem.Contract.TABLE_NAME, null, SheduleItem.Contract.toContentValues(si, new ContentValues(),true));

	    db.close();
	}
	private void deleteSheducle(int index) {
	    if (dbHelper == null) { return; }
	    final SQLiteDatabase db = dbHelper.getWritableDatabase();
	    
	    db.delete(SheduleItem.Contract.TABLE_NAME,"id="+pairs[index].id, null);

	    db.close();
	}
	private Boolean loadSheducle() {
	    if (dbHelper == null) { return false; }
	    final SQLiteDatabase db = dbHelper.getReadableDatabase();
	    
	    //dbHelper.onUpgrade(db, 0, 0);
	    
	    final Cursor cur = db.query(SheduleItem.Contract.TABLE_NAME,
	    		SheduleItem.Contract.COLUMNS, SheduleItem.Contract.COLUMN_DAY+"="+cDay+" AND "+SheduleItem.Contract.COLUMN_WEEK+"="+cWeek,
	    		null, null, null, SheduleItem.Contract.COLUMN_CODE);
	    try {
	    	pairs = new SheduleItem[cur.getCount()];
	      if (cur.moveToFirst()) {
	        do {
	        	pairs[cur.getPosition()]=SheduleItem.Contract.fromCursor(cur, new SheduleItem());
	        } while (cur.moveToNext());
	      }
	    } finally {
	      cur.close();
	      db.close();
	    }
	    if(pairs.length==0){
	    	return false;
	    }else{
	    	return true;
	    }
	}
	public void deleteRaw(int index) {
		deleteSheducle(index);
		getSheducles();
	}
	public void addRaw(String timeStart, String timeEnd, String name, String teacher, String place, Boolean window, Boolean sameOn2, Boolean break5min) {
		SheduleItem si=new SheduleItem();
		
		si.start=timeStart;
		si.end=timeEnd;
		si.name=name;
		si.teacher=teacher;
		si.place=place;
		si.day=cDay;
		si.week=cWeek;
		si.window=BooleanToInt(window);
		si.sameOn2=BooleanToInt(sameOn2);
		si.break5min=BooleanToInt(break5min);
		
		addSheducle(si);
		getSheducles();
	}
	public String getNextTime(Date date){
		Date date2=new Date(date.getTime()+6900000);
		int m=date2.getMinutes();
		int h=date2.getHours();
		String min;
		
		if(m<10){
			min="0"+m;
		}else{
			min=""+m;
		}
		
		return h+":"+min;
	}
	public SheduleItem getDefaultShedule(){
		SheduleItem si=new SheduleItem();
		
		if(pairs.length>0){
			si.start=getNextTime(pairs[pairs.length-1].startDate);
			si.end=getNextTime(pairs[pairs.length-1].endDate);
			si.place=pairs[pairs.length-1].place;
		}else{
			si.start="8:30";
			si.end="10:05";
			si.place="1-1";
		}
		si.name="Math";
		si.teacher="NewName OO";
		si.day=cDay;
		si.week=cWeek;
		si.window=0;
		si.sameOn2=0;
		si.break5min=0;
		
		return si;
	}
	public void editSheducle(int index, String timeStart, String timeEnd, String name, String teacher, String place, Boolean window, Boolean sameOn2, Boolean break5min){
		pairs[index].start=timeStart;
		pairs[index].end=timeEnd;
		pairs[index].name=name;
		pairs[index].teacher=teacher;
		pairs[index].place=place;
		pairs[index].day=cDay;
		pairs[index].week=cWeek;
		pairs[index].window=BooleanToInt(window);
		pairs[index].sameOn2=BooleanToInt(sameOn2);
		pairs[index].break5min=BooleanToInt(break5min);
		
		saveSheducle(index);
		
		mList.setAdapter(new SheduleAdapter(this,pairs));
	}
	public void changeDay(int day, int week){
		cDay=day;
		cWeek=week;
		
		getSheducles();
	}
	public int  BooleanToInt(Boolean b) {
		if(b){
			return 1;
		}else{
			return 0;
		}
	}
	private static final class ListItemHolder {
		public TextView start;
		public TextView end;
		public TextView name;
		public TextView teacher;
		public TextView place;
	}
	private static class SheduleAdapter extends ArrayAdapter<SheduleItem> {
	
		private final Activity context;
		private final SheduleItem[] items;
		 
        public SheduleAdapter(Activity context, SheduleItem[] items) {
        	super(context, R.layout.schedule_row, items);
    		this.context = context;
    		this.items = items;
        }
        
		@Override
		public View getView(final int position, final View view, final ViewGroup parent) {
			View v = view;
			ListItemHolder h = null;
			if (v == null) {
				v = (View) LayoutInflater.from(context).inflate(R.layout.schedule_row,parent, false);
				h = new ListItemHolder();
				
				h.start = (TextView) v.findViewById(R.id.start);
				h.end = (TextView) v.findViewById(R.id.end);
				h.name = (TextView) v.findViewById(R.id.name);
				h.teacher = (TextView) v.findViewById(R.id.teacher);
				h.place = (TextView) v.findViewById(R.id.place);
				
				v.setTag(h);
			} else {
				h = (ListItemHolder) v.getTag();
			}
			
			SheduleItem m = getItem(position);
			
			h.start.setText(m.start);
			h.end.setText(m.end);
			h.name.setText(m.name);
			h.teacher.setText(m.teacher);
			h.place.setText(m.place);
			
			return v;
		}		
	}
}