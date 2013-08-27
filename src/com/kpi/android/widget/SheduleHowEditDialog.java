package com.kpi.android.widget;

import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.kpi.android.R;
import com.kpi.android.activity.MainPageActivity;




public class SheduleHowEditDialog extends Dialog implements OnClickListener {
    private MainPageActivity mContext;
    private Button changeDay;
    private Button addRaw;
    private Button close;
    
	public SheduleHowEditDialog(MainPageActivity context) {
		super(context);
		mContext = context;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.shedule_how_edit_dialog);
		
		changeDay = (Button) findViewById(R.id.changeDay);
		addRaw = (Button) findViewById(R.id.addRaw);
		close= (Button) findViewById(R.id.close);
		
		changeDay.setOnClickListener(this);
		addRaw.setOnClickListener(this);
		close.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		if (v == close){
			dismiss();
		}
		if (v == addRaw){
			mContext.addRowDialog();
			dismiss();
		}
		if (v == changeDay){
			mContext.showSheduleChangeDay();
			dismiss();
		}
	}


}
