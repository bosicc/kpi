package com.kpi.android.widget;

import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.kpi.android.R;
import com.kpi.android.activity.MainPageActivity;




public class SheduleChangeDay extends Dialog implements OnClickListener {
    private MainPageActivity mContext;
    private Button okButton;
    private Spinner daySpinner;
    private Spinner weekSpinner;
    private int day;
    private int week;
    
	public SheduleChangeDay(MainPageActivity context, int cDay, int cWeek) {
		super(context);
		mContext = context;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.shedule_chande_day);
		
		okButton = (Button) findViewById(R.id.okButton);
		daySpinner = (Spinner) findViewById(R.id.daySpinner);
		weekSpinner = (Spinner) findViewById(R.id.weekSpinner);
		
		day=cDay;
		week=cWeek;

		
		okButton.setOnClickListener(this);
		daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		        //Object item = parent.getItemAtPosition(pos);
		    	day=pos;
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    }
		});
		weekSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		        //Object item = parent.getItemAtPosition(pos);
		    	week=pos;
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    }
		});
		
		daySpinner.setSelection(day);
		weekSpinner.setSelection(week);
	}
	@Override
	public void onClick(View v) {
		if (v == okButton){
			mContext.changeDay(day, week);
			Log.d("", day+";"+week);
			
			dismiss();
		}
	}


}
