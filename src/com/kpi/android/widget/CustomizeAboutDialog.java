package com.kpi.android.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.kpi.android.R;

public class CustomizeAboutDialog extends Dialog implements OnClickListener {
	Button okButton;
    private ExpandableListView mList;
    private ExpandableListAdapter mAdapter;
    
    
    private int mTitleColor;
    private int mInfoColor;
    private Context mContext;
    
	private static final String SEND_MAIL = "Send mail...";
	private static final String PLAIN_TEXT = "plain/text";
    
	public CustomizeAboutDialog(Context context) {
		super(context);
		
		mContext = context;

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.about);
		okButton = (Button) findViewById(R.id.OkButton);
		okButton.setOnClickListener(this);

		ImageView image = (ImageView) findViewById(R.id.about_icon);
		image.setImageResource(R.drawable.about_icon);

		Resources r = context.getResources();
		mTitleColor = r.getColor(R.color.about_list_title);
		mInfoColor =  r.getColor(R.color.white);
		
		mList = (ExpandableListView) findViewById(android.R.id.list);
		mAdapter = new ExpandabAboutAdapter();
        mList.setAdapter(mAdapter);
        mList.setOnChildClickListener(new OnChildClickListener(){

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
	        	switch(groupPosition){
	        	case 0: // Developer 1 - Borys Pratsiuk
	        		switch(childPosition){
	        		case 0:
//	        			dismiss();
//	        			new AlertDialog.Builder(mContext)
//	            		.setIcon(R.drawable.icon)
//	            		.setTitle(R.string.Dev_info1)
//	                	.setMessage(R.string.Dev_info1_1)
//	                	.setPositiveButton("ok", null)
//	                	.show();
	        			break;
	        		case 1:
	        			mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.biowave.org.ua")));
	        			break;
	        		case 2:
	        			mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.bosicc.com")));
	        			break;
	        		case 3:
	        			String[] to = {"info@bosicc.com"};
	 		        	
		   	        	 final Intent emailIntent = new Intent(
		   	     				android.content.Intent.ACTION_SEND);
		   	     		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, to);
		   	     		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "��: ");
		   	     		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, mContext.getText(R.string.About));
		   	     		
		   	     		emailIntent.setType(PLAIN_TEXT);
		   	     		mContext.startActivity(Intent.createChooser(emailIntent, SEND_MAIL));
		   	     		
	        			break;
	        		}
	        		break;
	        	case 1:
	        		break;
	        	}
				return false;
			}
        });
        
        
	}
	
	public class ExpandabAboutAdapter extends BaseExpandableListAdapter {
        // Sample data set.  children[i] contains the children (String[]) for groups[i].
		int [] groups = {R.string.Dev_name_prat, R.string.Dev_name_enot, R.string.Dev_name_tere, R.string.Dev_name_serg, R.string.Dev_name_avilov};
		int [][] children = {
				{R.string.Dev_info_prat_1,R.string.Dev_info_prat_2, R.string.Dev_info_prat_3, R.string.Dev_info_prat_4 },
				{R.string.Dev_info_enot},
				{R.string.Dev_info_tere},
				{R.string.Dev_info_serg_1,R.string.Dev_info_serg_2},
				{R.string.Dev_info_avilov_1,R.string.Dev_info_avilov_2}
		};
        public Object getChild(int groupPosition, int childPosition) {
            return children[groupPosition][childPosition];
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public int getChildrenCount(int groupPosition) {
            return children[groupPosition].length;
        }

        public TextView getGenericView() {
            // Layout parameters for the ExpandableListView
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, 64);

            TextView textView = new TextView(getContext());
            textView.setLayoutParams(lp);
            // Center the text vertically
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            // Set the text starting position
            textView.setPadding(36, 0, 0, 0);
            return textView;
        }
        
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                View convertView, ViewGroup parent) {
            TextView textView = getGenericView();
            //textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            textView.setText(children[groupPosition][childPosition]);
            textView.setTextColor(mInfoColor);
            return textView;
        }

        public Object getGroup(int groupPosition) {
            return groups[groupPosition];
        }

        public int getGroupCount() {
            return groups.length;
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                ViewGroup parent) {
            TextView textView = getGenericView();
            textView.setText(groups[groupPosition]);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            textView.setTextColor(mTitleColor);
            return textView;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public boolean hasStableIds() {
            return true;
        }

    }
	
	public void onClick(View v) {

		if (v == okButton)

			dismiss();

	}

}
