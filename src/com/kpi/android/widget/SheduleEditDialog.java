package com.kpi.android.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.kpi.android.R;
import com.kpi.android.activity.KPIActivity;
import com.kpi.android.activity.MainPageActivity;
import com.kpi.android.spedule.SheduleItem;




public class SheduleEditDialog extends Dialog implements OnClickListener {
    private MainPageActivity mContext;
    private TextView titleText;
    private Button okButton;
    private EditText timeStart;
    private EditText timeEnd;
    private EditText name;
    private EditText teacher;
    private EditText place;
    private CheckBox window;
    private CheckBox sameOn2;
    private CheckBox break5min;
    private KPIActivity parent;
    private SheduleItem item;
    private int index;
    
    
	public SheduleEditDialog(Context context, KPIActivity cParent, String title, int cIndex) {
		super(context);
		mContext = (MainPageActivity)context;
		
		parent=cParent;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.shedule_edit_row_dialog);
		
		okButton = (Button) findViewById(R.id.okButton);
		timeStart = (EditText) findViewById(R.id.timeStart);
		timeEnd = (EditText) findViewById(R.id.timeEnd);
		name = (EditText) findViewById(R.id.name);
		teacher = (EditText) findViewById(R.id.teacher);
		place = (EditText) findViewById(R.id.place);
		window = (CheckBox) findViewById(R.id.window);
		sameOn2 = (CheckBox) findViewById(R.id.sameOn2);
		break5min = (CheckBox) findViewById(R.id.break5min);
		
		index=cIndex;

		okButton.setOnClickListener(this);
		
		if(index!=-1){
			item=mContext.pairs[index];
		}else{
			item=mContext.getDefaultShedule();
		}
		
		timeStart.setText(item.start);
		timeEnd.setText(item.end);
		name.setText(item.name);
		teacher.setText(item.teacher);
		place.setText(item.place);
	}
	public Boolean canEdit() {
		if(timeStart.getText().toString().split(":").length!=2){
			mContext.errorDialog(0);
			return false;
		}
		if(timeEnd.getText().toString().split(":").length!=2){
			mContext.errorDialog(1);
			return false;
		}
		if(place.getText().toString().split("-").length!=2){
			mContext.errorDialog(2);
			return false;
		}
		return true;
	}
	@Override
	public void onClick(View v) {
		if (v == okButton){
			if(!canEdit()){
				return;
			}
			if(index!=-1){
				mContext.editSheducle(index,timeStart.getText().toString(),timeEnd.getText().toString(),name.getText().toString(),teacher.getText().toString(),place.getText().toString(),window.isChecked(),sameOn2.isChecked(),break5min.isChecked());
			}else{
				mContext.addRaw(timeStart.getText().toString(),timeEnd.getText().toString(),name.getText().toString(),teacher.getText().toString(),place.getText().toString(),window.isChecked(),sameOn2.isChecked(),break5min.isChecked());
			}
			dismiss();
		}
	}


}