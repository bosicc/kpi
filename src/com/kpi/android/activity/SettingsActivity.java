package com.kpi.android.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.kpi.android.R;


public class SettingsActivity extends PreferenceActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
}