package com.kpi.android.widget;

import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.kpi.android.R;
import com.kpi.android.activity.KPIActivity;
import com.kpi.android.activity.MainPageActivity;




public class SheducleClickDialog extends Dialog implements OnClickListener {
    private MainPageActivity mContext;
    private TextView titleText;
    private Button editButton;
    private Button cancelButton;
    private Button navigateToButton;
    private Button deleteButton;
    private KPIActivity parent;
    private String index;
    private int shIndex;
    
	public SheducleClickDialog(MainPageActivity context, KPIActivity cParent, String title, String cIndex, int cShIndex) {
		super(context);
		mContext = context;
		
		parent=cParent;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.shedule_click_dialog);
		
		titleText = (TextView) findViewById(R.id.titleText);
		cancelButton = (Button) findViewById(R.id.cancelButton);
		navigateToButton = (Button) findViewById(R.id.navigateToButton);
		editButton= (Button) findViewById(R.id.editButton);
		deleteButton= (Button) findViewById(R.id.deleteButton);
		
		index=cIndex;
		shIndex=cShIndex;
		
		titleText.setText(title);
		editButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		navigateToButton.setOnClickListener(this);
		deleteButton.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		if (v == cancelButton)
			dismiss();
		if (v == navigateToButton){
			parent.navigateToPoint(0,index);
			dismiss();
		}
		if (v == deleteButton){
			mContext.deleteRaw(shIndex);
			dismiss();
		}
		if (v == editButton){
			SheduleEditDialog dialog = new SheduleEditDialog(mContext,parent,"1",shIndex);
			dialog.show();
			dismiss();
		}
	}


}
