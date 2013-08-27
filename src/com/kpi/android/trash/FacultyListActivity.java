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

package com.kpi.android.trash;

//Need the following import to get access to the app resources, since this
//class is in a sub-package.
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ExpandableListActivity;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ExpandableListAdapter;
import android.widget.SimpleExpandableListAdapter;

import com.kpi.android.R;

/**
 * A list view example where the data comes from a custom ListAdapter
 */
public class FacultyListActivity extends ExpandableListActivity {

	private static final String NAME = "NAME";
	private static final String IS_EVEN = "IS_EVEN";

	private ExpandableListAdapter mAdapter;

	List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
	List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
	InputStream is;
	boolean addDepartment = true;
	boolean addFaculty;
	HashMap<String, String> FacultyMap = new HashMap<String, String>();
	HashMap<String, String> DepartmentMap = new HashMap<String, String>();
	int count = 0;
	int count1 = -1;
	int count2 = 0;
	List<String> DepartmentCount = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Reading data from text file to inout stream
		is = this.getResources().openRawResource(R.raw.facultydepartment);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		try {
			String line = "";
			while ((line = reader.readLine()) != null) {
				String[] strings = line.split("-");
				String ctry = strings[0].trim();
				String st = strings[1].trim();
				/*
				 * loading country and state to two separate hash tables
				 */
				if (FacultyMap.containsValue(ctry)) {
					addFaculty = false;
					if (!addDepartment) {
						DepartmentMap.put("Department" + count1 + count2, st);
						count2++;
					}
				} else {
					addFaculty = !addFaculty;
					count1++;
					// adding no. of states for a country to a list
					DepartmentCount.add("" + count2);
					if (addFaculty) {
						count2 = 0;
						FacultyMap.put("Faculty" + count, ctry);
						DepartmentMap.put("Department" + count1 + count2, st);
						addDepartment = false;
					}
					count2++;
					count++;
				}
			}
		} catch (Exception e) {
		}
		// re-arranging the state count list
		DepartmentCount.add("" + count2);
		DepartmentCount.remove(0);
		for (int i = 0; i < FacultyMap.size(); i++) {
			Map<String, String> curGroupMap = new HashMap<String, String>();
			groupData.add(curGroupMap);
			String ctry = FacultyMap.get("Faculty" + i);
			curGroupMap.put(NAME, ctry);
			curGroupMap.put(IS_EVEN, "Faculty " + i);
			List<Map<String, String>> children = new ArrayList<Map<String, String>>();
			int k = Integer.parseInt(DepartmentCount.get(i));
			for (int j = 0; j < k; j++) {
				Map<String, String> curChildMap = new HashMap<String, String>();
				children.add(curChildMap);
				curChildMap.put(NAME, DepartmentMap.get("Department" + i + j));
				curChildMap.put(IS_EVEN, "Department " + j);
			}
			childData.add(children);
		}

	     // Example how to read String array from Resources
	     Resources r = getResources();
	     String[] faculties = r.getStringArray(R.array.faculty);
	        

		// Set up our adapter
		mAdapter = new SimpleExpandableListAdapter(this, groupData,
				android.R.layout.simple_expandable_list_item_1, new String[] {
						NAME, IS_EVEN }, new int[] { android.R.id.text1,
						android.R.id.text2 }, childData,
				android.R.layout.simple_expandable_list_item_2, new String[] {
						NAME, IS_EVEN }, new int[] { android.R.id.text1,
						android.R.id.text2 });
		setListAdapter(mAdapter);

	    }
	   }