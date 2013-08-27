package com.kpi.android.activity;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

/**
 * A custom StartupActivity to workaround a known Android issue with two
 * application instances. The issue is described <a
 * href="http://code.google.com/p/android/issues/detail?id=2373">here</a> and <a
 * href="http://code.google.com/p/android/issues/detail?id=5277">here</a>.
 * 
 * @author Vitaliy Khudenko (<a
 *         href="https://github.com/cleverua/android_startup_activity"
 *         >android_startup_activity</a>)
 * 
 */
public class StartupActivity extends Activity {

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (needStartApp()) {
      final Intent i = new Intent(StartupActivity.this, KPIActivity.class);
      //final Intent i = new Intent(StartupActivity.this, mainPageActivity.class);
      startActivity(i);
    }

    finish();
  }

  @Override
  public void onConfigurationChanged(final Configuration newConfig) {
    // this prevents StartupActivity recreation on Configuration changes
    // (device orientation changes or hardware keyboard open/close).
    // just do nothing on these changes:
    super.onConfigurationChanged(null);
  }

  private boolean needStartApp() {
    final ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    final List<RunningTaskInfo> tasksInfo = am.getRunningTasks(1024);

    if (!tasksInfo.isEmpty()) {
      final String ourAppPackageName = getPackageName();
      RunningTaskInfo taskInfo;
      final int size = tasksInfo.size();
      for (int i = 0; i < size; i++) {
        taskInfo = tasksInfo.get(i);
        if (ourAppPackageName.equals(taskInfo.baseActivity.getPackageName())) {
          // continue application start only if there is the only Activity in
          // the task
          // (BTW in this case this is the StartupActivity)
          return taskInfo.numActivities == 1;
        }
      }
    }

    return true;
  }
}
