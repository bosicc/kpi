/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kpi.android.activity;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spanned;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kpi.android.R;


public class FacDepListActivity extends ExpandableListActivity {
	
	private static final String TAG = "FacDepListActivity";
	  
	private ExpandableListAdapter mAdapter;
	private ExpandableListView mList;
    private int mTitleColor;
    private int mBgColor;
    private int mInfoColor;
    private String[] groups = new String[27];
    private String[][] children = new String[27][];
    private String[] facrefs = new String [27];
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set up our adapter
		mAdapter = new FacDepListAdapter();
		setListAdapter(mAdapter);
		registerForContextMenu(getExpandableListView());
		
		
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
        
		menu.setHeaderTitle("Menu");
		menu.add(0, 0, 0, R.string.facdep_list_action1);                  
		menu.add(0, 1, 0, R.string.facdep_list_action2);
		menu.add(0, 2, 0, R.string.facdep_list_action3);
		
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		ExpandableListView.ExpandableListContextMenuInfo info =
				(ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
		
		int type =
		ExpandableListView.getPackedPositionType(info.packedPosition);
		int group =
		ExpandableListView.getPackedPositionGroup(info.packedPosition);
		int child =
		ExpandableListView.getPackedPositionChild(info.packedPosition);
		
		//Log.i(TAG, "type="+type+" group="+group+" child="+child);
		 
	  switch (item.getItemId()) {
	  case 0:
		  if (child == -1){
			  Resources r = getResources();
			  facrefs = r.getStringArray(R.array.references_faculties);
			  Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(facrefs[group]));
	          startActivity(intent);
		  }else{
			  Toast.makeText(getApplicationContext(), "Only Faculty website\'s urls available", Toast.LENGTH_LONG).show();
		  }

          finish();
	    return true;
	  case 1:
	    return true;
	  case 2:
		  return true;
	  default:
	    return super.onContextItemSelected(item);
	  }
	}

	public class FacDepListAdapter extends BaseExpandableListAdapter {
		private LayoutInflater inflater;

		public FacDepListAdapter() {
			// Example how to read String array from Resources
			Resources r = getResources();
			children[0] = r.getStringArray(R.array.iasa_dep);
			children[1] = r.getStringArray(R.array.esem_dep);
			children[2] = r.getStringArray(R.array.ime_dep);
			children[3] = r.getStringArray(R.array.ipt_dep);
			children[4] = r.getStringArray(R.array.ipp_dep);
			children[5] = r.getStringArray(R.array.its_dep);
			children[6] = r.getStringArray(R.array.miti_dep);
			children[7] = r.getStringArray(R.array.iscip_dep);
			children[8] = r.getStringArray(R.array.cass_dep);
			children[9] = r.getStringArray(R.array.cam_dep);
			children[10] = r.getStringArray(R.array.cbb_dep);
			children[11] = r.getStringArray(R.array.cce_dep);
			children[12] = r.getStringArray(R.array.cct_dep);
			children[13] = r.getStringArray(R.array.cepea_dep);
			children[14] = r.getStringArray(R.array.ce_dep);
			children[15] = r.getStringArray(R.array.chpe_dep);
			children[16] = r.getStringArray(R.array.cics_dep);
			children[17] = r.getStringArray(R.array.cide_dep);
			children[18] = r.getStringArray(R.array.cl_dep);
			children[19] = r.getStringArray(R.array.cmm_dep);
			children[20] = r.getStringArray(R.array.icme_dep);
			children[21] = r.getStringArray(R.array.cpe_dep);
			children[22] = r.getStringArray(R.array.cpm_dep);
			children[23] = r.getStringArray(R.array.cre_dep);
			children[24] = r.getStringArray(R.array.cssl_dep);
			children[25] = r.getStringArray(R.array.wf_dep);
			children[26] = r.getStringArray(R.array.jfme_dep);
			groups = r.getStringArray(R.array.faculty);
			mTitleColor = r.getColor(R.color.facdeplist_title);
			mInfoColor =  r.getColor(R.color.facdeplist_chield);
			mBgColor =  r.getColor(R.color.facdep_listbg);

			inflater = (LayoutInflater) getBaseContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
		}

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
					ViewGroup.LayoutParams.FILL_PARENT, 70);
			
			TextView textView = new TextView(FacDepListActivity.this);
			textView.setLayoutParams(lp);
			// Center the text vertically
			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			// Set the text starting position
			textView.setPadding(60, 0, 0, 0);
			textView.setBackgroundColor(mBgColor);
			return textView;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView textView = getGenericView();
			textView.setText(getChild(groupPosition, childPosition).toString());
			textView.setPadding(16, 0, 0, 0);
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
		
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView textView = getGenericView();
			Spanned textSpan = android.text.Html.fromHtml(getGroup(groupPosition).toString());
			textView.setText(textSpan);
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
}
