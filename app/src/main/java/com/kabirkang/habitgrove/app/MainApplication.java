package com.kabirkang.habitgrove.app;

import android.app.Application;

import com.kabirkang.habitgrove.analytics.HabitGroveAnalytics;
import com.kabirkang.habitgrove.sync.FirebaseSyncUtils;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        HabitGroveAnalytics.initAnalytics(this);
        HabitGroveAnalytics.logAppOpen();
        FirebaseSyncUtils.setOfflineModeEnabled(true);
    }

}