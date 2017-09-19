package com.kabirkang.habitgrove.app;

import android.app.Application;

import com.kabirkang.habitgrove.sync.FirebaseUtils;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseUtils.setOfflineModeEnabled(true);
    }

}